package com.example.TIUMusic.Libs.Visualizer

import android.media.audiofx.Visualizer
import android.util.Log
import androidx.collection.emptyLongSet
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import com.example.TIUMusic.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Thread.sleep
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

object VisualizerSettings {
    var VisualizerEnabled : Boolean = false;
}

interface VisualizerListener {
    fun onChange(newPathLeft : Path, newPathRight : Path);
}

class VisualizerViewModel(
    private val captureSize: Int = Visualizer.getCaptureSizeRange()[1],
    private val audioSessionId: Int = 0
) : ViewModel() {
    companion object{
        public fun HZToFftIndex(Hz: Int, size : Int, samplingRate: Int): Int {
            return (Hz * size / (44100 * 2)).coerceIn(0, 255);
        }

        public fun dB(x: Double) : Double {
            if (x == 0.0)
                return 0.0;
            else
                return 10.0 * log10(x);
        }
    }

    private var visualizer : MutableStateFlow<Visualizer?> = MutableStateFlow(null);
    private var fftM : MutableStateFlow<DoubleArray>;
    private var fftBytes : ByteArray;
    private val frequencyMap : MutableStateFlow<MutableList<Pair<Int, Double>>> = MutableStateFlow(mutableListOf());
    private val visualizerCaptureSize : Int get() = visualizer.value?.captureSize ?: 0
    private val visualizerSamplingSize : Int get() = visualizer.value?.samplingRate ?: 0
    private val listeners = mutableListOf<VisualizerListener>();
    private var radius : Float = 300f;
    private var lineHeight: Float = 600f;
    private var minHertz : Float = 20f;
    private var maxHertz : Float = 15000f;
    private var center : Size = Size(0f, 0f);

    init {

        fftBytes = ByteArray(0);
        fftM = MutableStateFlow(DoubleArray(0));
    }

    public fun init() {
        if (VisualizerSettings.VisualizerEnabled)
        {
            visualizer = MutableStateFlow(Visualizer(audioSessionId));
            visualizer.update { it ->
                if (it != null) {
                    it.setCaptureSize(captureSize)
                    it.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
                    it.setEnabled(true);
                };
                it;
            }
            fftBytes = ByteArray(visualizerCaptureSize);
            fftM = MutableStateFlow(DoubleArray(visualizerCaptureSize / 2 - 1));

            val thread = Thread( {
                println("ran");
                var prevUpdateTime = System.currentTimeMillis();
                while (true) {
                    if (listeners.isEmpty() || MainActivity.isPaused) {
                        sleep(1000);
                        continue;
                    }
                    val fps : Float = 1 / ((System.currentTimeMillis() - prevUpdateTime) / 1000f);
                    //println("Update Delta Time : $fps")
                    prevUpdateTime = System.currentTimeMillis();
                    val fft = GetTransformedFFT(0, 22050);
                    val newPathLeft = Path();
                    val newPathRight = Path();
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
                        // val xOffset: Float = barDistance * i - barDistance * COUNT / 2;
                        val angle = i.toFloat() / COUNT.toFloat() * 1.0f * Math.PI;
                        barHeight = (barHeight + lineHeight *
                                ((GetVolumeFrequency(hertz.roundToInt()) - minVal) / scaleFactor
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
                    for (listener in listeners) {
                        listener.onChange(newPathLeft, newPathRight);
                    }
                    val fpsLimit = 50.0f;
                    val spfLimit = 1.0f / fpsLimit * 1000.0f;
                    val frameTime = 1f / fps * 1000f;
                    val sleepMs = Math.round(spfLimit).toLong() - Math.round(frameTime).toLong();
                    if (fps > fpsLimit && sleepMs > 0)
                        sleep(sleepMs);
                }
                println("stop");
            });
            thread.priority = Thread.NORM_PRIORITY;
            thread.start();
        }
    }

    private fun fftEqual(prevFFT : DoubleArray, fft: DoubleArray) : Boolean {
        for (i in 0 until min(prevFFT.size, fft.size)) {
            if (prevFFT[i].roundToInt() != fft[i].roundToInt())
                return false;
        }
        return true;
    }

    fun addVisualizerListener(
        listener: VisualizerListener,
        radius : Float = 300f,
        lineHeight: Float = 600f,
        minHertz : Float = 20f,
        maxHertz : Float = 15000f,
    ) {
        this.radius = radius
        this.lineHeight = lineHeight
        this.minHertz = minHertz
        this.maxHertz = maxHertz
        listeners.add(listener);
    }

    fun removeVisualizerListener(listener : VisualizerListener) {
        listeners.remove(listener);
    }

    public fun GetFFT() : ByteArray {
        if (!VisualizerSettings.VisualizerEnabled)
            return ByteArray(0);
        visualizer.value?.getFft(fftBytes);
        return fftBytes.copyOf();
    }

    public fun GetTransformedFFT(start : Int = 0, end : Int = 0) : DoubleArray{
        if (!VisualizerSettings.VisualizerEnabled)
            return DoubleArray(0);
        visualizer.value?.getFft(fftBytes);
        transformFftMagnitude();
        if (start <= end)
            return fftM.value.copyOf();
        return fftM.value.copyOfRange(
            HZToFftIndex(start, visualizerCaptureSize, visualizerSamplingSize),
            HZToFftIndex(end, visualizerCaptureSize, visualizerSamplingSize)
        );
    }

    public fun GetFrequencyMap() : List<Pair<Int, Double>> {
        if (!VisualizerSettings.VisualizerEnabled)
            return emptyList();
        return frequencyMap.value;
    }

    private fun transformFftMagnitude() {
        if (!VisualizerSettings.VisualizerEnabled)
            return;
        frequencyMap.value.clear();
        val SMOOTHING = 0.8;
        val samplingRate = visualizerSamplingSize;
        val captureSize = visualizerCaptureSize;
        for (k in 0 until fftBytes.size / 2 - 1) {
            val prevFFTM = fftM.value[k];
            val i = (k + 1) * 2;
            val real = fftBytes[i].toDouble();
            val img = fftBytes[i + 1].toDouble();
            fftM.value[k] = dB((hypot(real, img)));
            fftM.value[k] = fftM.value[k] * fftM.value[k] / 100;
            fftM.value[k] = (SMOOTHING) * prevFFTM + ((1 - SMOOTHING) * fftM.value[k]);
        }

        val averageNum = 2;
        for (i in 0 until fftBytes.size / 2 - 1) {
            var average = 0.0;
            var averageCount = 0;
            for (j in max(0, i - averageNum) until min(fftM.value.size, i + 1 + averageNum)) {
                average += fftM.value[i];
                averageCount++;
            }
            average /= max(averageCount, 1);
            fftM.value[i] = average;

            val fre = i * (samplingRate / 1000) / captureSize;
            frequencyMap.value.add(Pair<Int, Double>(fre, fftM.value[i]));
        }
    }

    public fun GetVolumeFrequency(Hz: Int) : Float {
        if (!VisualizerSettings.VisualizerEnabled)
            return 0.0f;
        var beginHz : Pair<Int, Double> = Pair<Int, Double>(0, 0.0);
        var endHz : Pair<Int, Double> = Pair<Int, Double>(0, 0.0);
        var index = binarySearchMap(Hz).coerceIn(1, frequencyMap.value.size);
        beginHz = frequencyMap.value[index - 1];
        endHz = frequencyMap.value[index];
//        for (i in 0 until frequencyMap.value.size) {
//            val fre = frequencyMap.value[i];
//            if (fre.first > Hz) {
//                beginHz = frequencyMap.value[i - 1];
//                endHz = frequencyMap.value[i];
//                break;
//            }
//        }
        if (endHz.first - beginHz.first == 0)
            return 0f;
        return lerp(
            beginHz.second.toFloat(),
            endHz.second.toFloat(),
            Easing((Hz - beginHz.first).toFloat() / (endHz.first - beginHz.first).toFloat(),  EasingType.OutCubic)
        )
    }

    private fun binarySearchMap(Hz: Int) : Int {
        var l = 0;
        var r = frequencyMap.value.size;
        while (l < r) {
            val mid = (l + r) / 2;
            if (frequencyMap.value[mid].first < Hz)
                l = mid + 1;
            else
                r = mid;
        }
        return l;
    }
}
