package com.example.TIUMusic.Libs.Visualizer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.lerp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

fun ensureVisualizerPermissionAllowed(
    activity : ComponentActivity,
    onPermissionAccepted: () -> Unit,
    onFinish: () -> Unit
) {
    val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                VisualizerSettings.VisualizerEnabled = true;
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                VisualizerSettings.VisualizerEnabled = false;
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
            if (VisualizerSettings.VisualizerEnabled)
                onPermissionAccepted();
            onFinish();
        }
    when {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED -> {
            VisualizerSettings.VisualizerEnabled = true;
            onPermissionAccepted();
            onFinish();
            // You can use the API that requires the permission.
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.RECORD_AUDIO) -> {
            VisualizerSettings.VisualizerEnabled = false;
            onFinish();
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
        }
        else -> {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO)
        }
    }
}

// Drawer
@Composable
fun VisualizerCircleRGB(
    modifier : Modifier = Modifier,
    radius : Float = 300f,
    lineHeight: Float = 600f,
    minHertz : Float = 20f,
    maxHertz : Float = 15000f,
    visualizerViewModel: VisualizerViewModel
) {
    if (!VisualizerSettings.VisualizerEnabled)
        return;
    var prevTime = remember { System.currentTimeMillis() };
    var pathLeft by remember { mutableStateOf(Path()) }
    var pathRight by remember { mutableStateOf(Path()) }
    var center = remember { Size(0f, 0f) }
    // INIT sa
    DisposableEffect(Unit) {
        val listener = object : VisualizerListener {
            override fun onChange(newPathLeft: Path, newPathRight: Path) {
                pathLeft = newPathLeft;
                pathRight = newPathRight;

            }
        }
        visualizerViewModel.addVisualizerListener(
            listener = listener,
            radius = radius,
            lineHeight = lineHeight,
            minHertz = minHertz,
            maxHertz = maxHertz
        )
        onDispose {
            visualizerViewModel.removeVisualizerListener(listener);
        }
    }
    Canvas(modifier = modifier.background(Color.Transparent)) {
        // FPS COUNTER
        val deltaTime : Float = 1 / ((System.currentTimeMillis() - prevTime) / 1000f);
        //println("Delta Time : $deltaTime")
        prevTime = System.currentTimeMillis();
        center = size / 2f;

        val resultPath = Path.combine(PathOperation.Union, pathLeft, pathRight);
        val blendMode = BlendMode.Lighten; // BlendMode.Multiply for white background, BlendMode.Lighten for black background
        rotate(degrees = 0.5f) {
            drawPath(
                resultPath,
                Color.Red,
                blendMode = blendMode
            )
        }
        rotate(degrees = -0.5f) {
            drawPath(
                resultPath,
                Color.Green,
                blendMode = blendMode
            )
        }
        rotate(degrees = 0f) {
            drawPath(
                resultPath,
                Color.Blue,
                blendMode = blendMode
            )
        }
    }


}
@Composable
fun VisualizerCircle(
    modifier : Modifier = Modifier,
    radius : Float = 300f,
    barDistance : Float = 100f,
    lineHeight: Float = 600f,
    minHertz : Float = 20f,
    maxHertz : Float = 15000f,
    visualizerViewModel: VisualizerViewModel
) {
    if (!VisualizerSettings.VisualizerEnabled)
        return;
    var prevTime = remember { (System.currentTimeMillis()) };
    var pathLeft by remember { mutableStateOf(Path()) }
    var pathRight by remember { mutableStateOf(Path()) }
    var center by remember { mutableStateOf(Size(0f, 0f)) }
    // INIT sa
    DisposableEffect(Unit) {
        val listener = object : VisualizerListener {
            override fun onChange(newPathLeft: Path, newPathRight: Path) {
                pathLeft = newPathLeft;
                pathRight = newPathRight;

            }
        }
        visualizerViewModel.addVisualizerListener(
            listener = listener,
            radius = radius,
            lineHeight = lineHeight,
            minHertz = minHertz,
            maxHertz = maxHertz
        )
        onDispose {
            visualizerViewModel.removeVisualizerListener(listener);
        }
    }
    Canvas(modifier = modifier.background(Color.Transparent)) {
        // FPS COUNTER
        val deltaTime: Float = 1 / ((System.currentTimeMillis() - prevTime) / 1000f);
        //println("Delta Time : $deltaTime")
        prevTime = System.currentTimeMillis();

        center = size / 2f;
        val brush = radialGradient(
            0.0f to Color(0xFF020024),
            0.5f to Color(0xFF090979),
            1.0f to Color(0xFF00d4ff),
            center = Offset(center.width, center.height),
            radius = radius + lineHeight / 5
        );

        val resultPath = Path.combine(PathOperation.Union, pathLeft, pathRight);
        drawPath(
            resultPath,
            brush
        )
    }
}