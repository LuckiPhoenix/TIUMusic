package com.example.TIUMusic.Libs.Visualizer

import androidx.compose.animation.core.Easing
import java.lang.Math.pow
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class EasingType {
    Linear,
    InSine, OutSine,
    InQuad, OutQuad,
    InCubic, OutCubic,
    InQuart, OutQuart,
    InQuint, OutQuint,
    InExpo, OutExpo,
    InCirc, OutCirc,
    InBack, OutBack
}

public fun Easing(value : Float, type : EasingType) : Float{
    return Easing(value.toDouble(), type).toFloat();
}

public fun Easing(value: Double, type : EasingType) : Double {
    val x = value.coerceIn(0.0, 1.0); // For copying from easings.net
    when (type) {
        EasingType.Linear -> return x;
        EasingType.InSine -> return 1.0 - cos((x * Math.PI) / 2.0);
        EasingType.OutSine -> return sin((x * Math.PI) / 2.0);
        EasingType.InQuad -> return x * x;
        EasingType.OutQuad -> return 1.0 - (1.0 - x) * (1.0 - x);
        EasingType.InCubic -> return x * x * x;
        EasingType.OutCubic -> return 1.0 - (1.0 - x).pow(3.0);
        EasingType.InQuart -> return x * x * x * x;
        EasingType.OutQuart -> return 1.0 - (1.0 - x).pow(4.0);
        EasingType.InQuint -> return x * x * x * x * x;
        EasingType.OutQuint -> return 1.0 - (1.0 - x).pow(5.0);
        EasingType.InExpo -> return if (x == 0.0) 0.0 else 2.0.pow(10.0 * x - 10.0);
        EasingType.OutExpo -> return if (x == 1.0) 1.0 else 1.0 - 2.0.pow(-10.0 * x);
        EasingType.InCirc -> return 1.0 - sqrt(1.0 - x.pow(2.0));
        EasingType.OutCirc -> return sqrt(1.0 - (x - 1.0).pow(2.0));
        EasingType.InBack -> {
            val c1 = 1.70158;
            val c3 = c1 + 1.0;
            return c3 * x * x * x - c1 * x * x;
        }
        EasingType.OutBack -> {
            val c1 = 1.70158;
            val c3 = c1 + 1.0;
            return 1.0 + c3 * (x - 1.0).pow(3.0) + c1 * (x - 1.0).pow(2.0);
        }
        else -> return x;
    }
}
