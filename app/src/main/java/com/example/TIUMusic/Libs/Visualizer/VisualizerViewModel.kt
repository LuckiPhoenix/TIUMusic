package com.example.TIUMusic.Libs.Visualizer

import android.media.audiofx.Visualizer
import androidx.compose.ui.util.lerp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.hypot
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

object VisualizerSettings {
    var VisualizerEnabled : Boolean = false;
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

    init {

        fftBytes = ByteArray(0);
        fftM = MutableStateFlow(DoubleArray(0));
        println(visualizer);
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
        }
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
        GetFFT();
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
        for (i in 0 until frequencyMap.value.size) {
            val fre = frequencyMap.value[i];
            if (fre.first > Hz) {
                beginHz = frequencyMap.value[i - 1];
                endHz = frequencyMap.value[i];
                break;
            }
        }
        if (endHz.first - beginHz.first == 0)
            return 0f;
        return lerp(
            beginHz.second.toFloat(),
            endHz.second.toFloat(),
            Easing((Hz - beginHz.first).toFloat() / (endHz.first - beginHz.first).toFloat(),  EasingType.OutCubic)
        )
    }

}
