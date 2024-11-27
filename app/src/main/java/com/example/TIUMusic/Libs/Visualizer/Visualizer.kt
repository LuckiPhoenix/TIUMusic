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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

fun ensureVisualizerPermissionAllowed(activity : ComponentActivity) {
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
    VisualizerSettings.VisualizerEnabled = successCounter >= 2;
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
    var ___ran by remember { mutableStateOf(0) }
    var prevTime by remember { mutableStateOf(System.currentTimeMillis()) };
    var fft by remember { mutableStateOf(doubleArrayOf()) }
    // INIT sa
    LaunchedEffect(Unit) {
        if (___ran > 0)
            return@LaunchedEffect;
        println("INIT");
        Thread(Runnable {

        }).start();
        launch {
            ___ran++;
            while (true) {
                fft = visualizerViewModel.GetTransformedFFT(0, 22050).copyOf();
                delay(16);
            }
        }
    }
    Canvas(modifier = modifier.background(Color.Transparent)) {
        // FPS COUNTER

        val deltaTime : Float = 1 / ((System.currentTimeMillis() - prevTime) / 1000f);
        //println("Delta Time : $deltaTime")
        prevTime = System.currentTimeMillis();

        val center: Size = size / 2f;
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
        val pathRight = Path();
        val pathLeft = Path();
        val beginOffset = Offset(
            cos(Math.PI / 2).toFloat() * radius + center.width,
            sin(Math.PI / 2).toFloat() * radius + center.height
        )
        pathRight.moveTo(beginOffset.x, beginOffset.y)
        pathLeft.moveTo(beginOffset.x,beginOffset.y)
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
            pathRight.lineTo(
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
            pathLeft.lineTo(
                nextPointMirrored.x,
                nextPointMirrored.y,
            )
            hertz = lerp(minHertz, maxHertz, Easing((i + 1).toFloat() / COUNT.toFloat(), EasingType.InQuad));
        }
        pathRight.lineTo(
            beginOffset.x,
            beginOffset.y
        )
        pathLeft.lineTo(
            beginOffset.x,
            beginOffset.y
        )
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
    var ___ran by remember { mutableStateOf(0) }
    var prevTime by remember { mutableStateOf(System.currentTimeMillis()) };
    var fft by remember { mutableStateOf(doubleArrayOf()) }
    // INIT sa
    LaunchedEffect(Unit) {
        println("INIT");
        launch {
            ___ran++;
            while (true) {
                fft = visualizerViewModel.GetTransformedFFT(0, 22050).copyOf();
                val deltaTime: Float = 1 / ((System.currentTimeMillis() - prevTime) / 1000f);
                //println(fft.toList());
                //println("Delta Time : $deltaTime")
                delay(16);
            }
        }
    }
    Canvas(modifier = modifier.background(Color.Transparent)) {
        // FPS COUNTER


        val deltaTime: Float = 1 / ((System.currentTimeMillis() - prevTime) / 1000f);
        //println("Delta Time : $deltaTime")
        prevTime = System.currentTimeMillis();

        val center: Size = size / 2f;

        val COUNT = fft.size - 1;
        var minVal = 0.0f;
        var maxVal = 0.0f;
        for (fftData in fft) {
            minVal = min(minVal, fftData.toFloat());
            maxVal = max(maxVal, fftData.toFloat());
        }
        val range = maxVal - minVal;
        val scaleFactor = range + 0.00001f;

        val brush = radialGradient(
            0.0f to Color(0xFF020024),
            0.5f to Color(0xFF090979),
            1.0f to Color(0xFF00d4ff),
            center = Offset(center.width, center.height),
            radius = radius + lineHeight / 5
        );

        var hertz = minHertz;
        var barHeight = 0f;
        // Setup path
        val pathRight = Path();
        val pathLeft = Path();
        val beginOffset = Offset(
            cos(Math.PI / 2).toFloat() * radius + center.width,
            sin(Math.PI / 2).toFloat() * radius + center.height
        )
        pathRight.moveTo(beginOffset.x, beginOffset.y)
        pathLeft.moveTo(beginOffset.x, beginOffset.y)
        for (i in 0..COUNT) {
            val xOffset: Float = barDistance * i - barDistance * COUNT / 2;
            val angle = i.toFloat() / COUNT.toFloat() * 1.0f * Math.PI;
            barHeight = (barHeight + lineHeight *
                    ((visualizerViewModel.GetVolumeFrequency(hertz.roundToInt()) - minVal) / scaleFactor
                            * lerp(
                        0.3f,
                        maxVal,
                        Easing(i.toFloat() / COUNT.toFloat(), EasingType.OutQuad)
                    ))
                    / 5) / 2;
            // Right
            val direction = Offset(
                cos(angle - Math.PI / 2).toFloat(),
                sin(angle - Math.PI / 2).toFloat()
            );
            val middle =
                direction * radius + Offset(center.width, center.height)// + Offset(0f, -200f);
            val nextPoint = (middle + direction * barHeight);
            pathRight.lineTo(
                nextPoint.x,
                nextPoint.y
            )
            // Left
            val directionMirrored = Offset(
                x = cos(-angle - Math.PI / 2).toFloat(),
                y = sin(-angle - Math.PI / 2).toFloat()
            );
            val middleMirrored = directionMirrored * radius + Offset(
                center.width,
                center.height
            )// + Offset(0f, -200f);
            val nextPointMirrored = (middleMirrored + directionMirrored * barHeight);
            pathLeft.lineTo(
                nextPointMirrored.x,
                nextPointMirrored.y,
            )
            hertz = lerp(
                minHertz,
                maxHertz,
                Easing((i + 1).toFloat() / COUNT.toFloat(), EasingType.InQuad)
            );
        }
        pathRight.lineTo(
            beginOffset.x,
            beginOffset.y
        )
        pathLeft.lineTo(
            beginOffset.x,
            beginOffset.y
        )
        val resultPath = Path.combine(PathOperation.Union, pathLeft, pathRight);
        drawPath(
            resultPath,
            brush
        )
    }
}