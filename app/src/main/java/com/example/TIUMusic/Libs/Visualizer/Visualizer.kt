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
    visualizerViewModel: VisualizerViewModel
) {
    var successCounter : Int = 0;
    val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                successCounter++;
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
            VisualizerSettings.VisualizerEnabled = successCounter >= 2;
            if (VisualizerSettings.VisualizerEnabled)
                visualizerViewModel.init();
        }
    when {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED -> {
            successCounter++;
            // You can use the API that requires the permission.
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.RECORD_AUDIO) -> {
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
    when {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED -> {
            successCounter++;
            // You can use the API that requires the permission.
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.MODIFY_AUDIO_SETTINGS) -> {
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
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
        }
    }
}

// Drawer
@Composable
fun VisualizerCircleRGB(
    modifier : Modifier = Modifier,
    radius : Float = 300f,
    barDistance : Float = 20f,
    lineHeight: Float = 600f,
    minHertz : Float = 20f,
    maxHertz : Float = 15000f,
    visualizerViewModel: VisualizerViewModel
) {
    if (!VisualizerSettings.VisualizerEnabled)
        return;
    var prevTime by remember { mutableLongStateOf(System.currentTimeMillis()) };
    var pathLeft by remember { mutableStateOf(Path()) }
    var pathRight by remember { mutableStateOf(Path()) }
    var center by remember { mutableStateOf(Size(0f, 0f)) }
    var drawing by remember { mutableStateOf(false) }
    // INIT sa
    DisposableEffect(Unit) {
        println("INIT");
        var threadShouldStop : Boolean = false;
        Thread(Runnable {
            println("ran");
            while (!threadShouldStop) {
                if (drawing) continue;
                val newPathLeft = Path();
                val newPathRight = Path();
                val fft = visualizerViewModel.GetTransformedFFT(0, 22050);
                val COUNT = fft.size - 1;
                var minVal = 0.0f;
                var maxVal = 0.0f;
                for (fftData in fft) {
                    minVal = min(minVal, fftData.toFloat());
                    maxVal = max(maxVal, fftData.toFloat());
                }
                val range = maxVal - minVal;
                val scaleFactor = range + 0.00001f;

                var hertz = minHertz;
                var barHeight = 0f;
                // Setup path
                val beginOffset = Offset(
                    cos(Math.PI / 2).toFloat() * radius + center.width,
                    sin(Math.PI / 2).toFloat() * radius + center.height
                )
                newPathRight.moveTo(beginOffset.x, beginOffset.y)
                newPathLeft.moveTo(beginOffset.x,beginOffset.y)
                for (i in 0..COUNT) {
                    val xOffset: Float = barDistance * i - barDistance * COUNT / 2;
                    val angle = i.toFloat() / COUNT.toFloat() * 1.0f * Math.PI;
                    barHeight = (barHeight + lineHeight *
                            ((visualizerViewModel.GetVolumeFrequency(hertz.roundToInt()) - minVal) / scaleFactor
                                    * lerp(0.3f, maxVal, Easing( i.toFloat() / COUNT.toFloat(), EasingType.OutQuad)))
                            / 5) / 2;
                    // Right
                    val direction = Offset(
                        cos(angle - Math.PI / 2).toFloat(),
                        sin(angle - Math.PI / 2).toFloat()
                    );
                    val middle = direction * radius + Offset(center.width, center.height)// + Offset(0f, -200f);
                    val nextPoint = (middle + direction * barHeight);
                    newPathRight.lineTo(
                        nextPoint.x,
                        nextPoint.y
                    )
                    // Left
                    val directionMirrored = Offset(
                        x = cos(-angle - Math.PI / 2).toFloat(),
                        y = sin(-angle - Math.PI / 2).toFloat()
                    );
                    val middleMirrored = directionMirrored * radius + Offset(center.width, center.height)// + Offset(0f, -200f);
                    val nextPointMirrored = (middleMirrored + directionMirrored * barHeight);
                    newPathLeft.lineTo(
                        nextPointMirrored.x,
                        nextPointMirrored.y,
                    )
                    hertz = lerp(minHertz, maxHertz, Easing((i + 1).toFloat() / COUNT.toFloat(), EasingType.InQuad));
                }
                newPathRight.lineTo(
                    beginOffset.x,
                    beginOffset.y
                )
                newPathLeft.lineTo(
                    beginOffset.x,
                    beginOffset.y
                )
                pathLeft = newPathLeft;
                pathRight = newPathRight;
                // sleep(16);
            }
            println("stop");
        }).start();
        onDispose {
            threadShouldStop = true;
        }
    }
    Canvas(modifier = modifier.background(Color.Transparent)) {
        // FPS COUNTER
        drawing = true;
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
        drawing = false;
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
    var prevTime by remember { mutableLongStateOf(System.currentTimeMillis()) };
    var pathLeft by remember { mutableStateOf(Path()) }
    var pathRight by remember { mutableStateOf(Path()) }
    var center by remember { mutableStateOf(Size(0f, 0f)) }
    var drawing by remember { mutableStateOf(false) }
    // INIT sa
    DisposableEffect(Unit) {
        println("INIT");
        var threadShouldStop : Boolean = false;
        Thread(Runnable {
            println("ran");
            while (!threadShouldStop) {
                if (drawing) continue;
                val newPathLeft = Path();
                val newPathRight = Path();
                val fft = visualizerViewModel.GetTransformedFFT(0, 22050);
                val COUNT = fft.size - 1;
                var minVal = 0.0f;
                var maxVal = 0.0f;
                for (fftData in fft) {
                    minVal = min(minVal, fftData.toFloat());
                    maxVal = max(maxVal, fftData.toFloat());
                }
                val range = maxVal - minVal;
                val scaleFactor = range + 0.00001f;

                var hertz = minHertz;
                var barHeight = 0f;
                // Setup path
                val beginOffset = Offset(
                    cos(Math.PI / 2).toFloat() * radius + center.width,
                    sin(Math.PI / 2).toFloat() * radius + center.height
                )
                newPathRight.moveTo(beginOffset.x, beginOffset.y)
                newPathLeft.moveTo(beginOffset.x,beginOffset.y)
                for (i in 0..COUNT) {
                    val xOffset: Float = barDistance * i - barDistance * COUNT / 2;
                    val angle = i.toFloat() / COUNT.toFloat() * 1.0f * Math.PI;
                    barHeight = (barHeight + lineHeight *
                            ((visualizerViewModel.GetVolumeFrequency(hertz.roundToInt()) - minVal) / scaleFactor
                                    * lerp(0.3f, maxVal, Easing( i.toFloat() / COUNT.toFloat(), EasingType.OutQuad)))
                            / 5) / 2;
                    // Right
                    val direction = Offset(
                        cos(angle - Math.PI / 2).toFloat(),
                        sin(angle - Math.PI / 2).toFloat()
                    );
                    val middle = direction * radius + Offset(center.width, center.height)// + Offset(0f, -200f);
                    val nextPoint = (middle + direction * barHeight);
                    newPathRight.lineTo(
                        nextPoint.x,
                        nextPoint.y
                    )
                    // Left
                    val directionMirrored = Offset(
                        x = cos(-angle - Math.PI / 2).toFloat(),
                        y = sin(-angle - Math.PI / 2).toFloat()
                    );
                    val middleMirrored = directionMirrored * radius + Offset(center.width, center.height)// + Offset(0f, -200f);
                    val nextPointMirrored = (middleMirrored + directionMirrored * barHeight);
                    newPathLeft.lineTo(
                        nextPointMirrored.x,
                        nextPointMirrored.y,
                    )
                    hertz = lerp(minHertz, maxHertz, Easing((i + 1).toFloat() / COUNT.toFloat(), EasingType.InQuad));
                }
                newPathRight.lineTo(
                    beginOffset.x,
                    beginOffset.y
                )
                newPathLeft.lineTo(
                    beginOffset.x,
                    beginOffset.y
                )
                pathLeft = newPathLeft;
                pathRight = newPathRight;
                // sleep(16);
            }
            println("stop");
        }).start();
        onDispose {
            threadShouldStop = true;
        }
    }
    Canvas(modifier = modifier.background(Color.Transparent)) {
        // FPS COUNTER
        drawing = true;
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
        drawing = false;
    }
}