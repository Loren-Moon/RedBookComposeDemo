package com.loren.redbook.data.util

import java.math.RoundingMode
import java.text.NumberFormat
import kotlin.math.abs

object NumberUtil {

    fun round2StringWithChinese(
        value: Double?,
        fraction: Int = 2,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        if (value == null) return "--"
        val v = value
        val absV = abs(v)
        return if (absV >= 1000000000000) {
            formatNumber(v / 1000000000000, fraction = fraction, suffix = "万亿", roundingMode = roundingMode)
        } else if (absV >= 100000000) {
            formatNumber(v / 100000000, fraction = fraction, suffix = "亿", roundingMode = roundingMode)
        } else if (absV >= 10000) {
            formatNumber(v / 10000, fraction = fraction, suffix = "万", roundingMode = roundingMode)
        } else {
            formatNumber(v, fraction = 0, roundingMode = roundingMode)
        }
    }

    fun formatNumber(
        value: Number?, fraction: Int = 2, suffix: String = "", roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        return value?.let {
            NumberFormat.getInstance().run {
                isGroupingUsed = false
                minimumFractionDigits = fraction
                maximumFractionDigits = fraction
                this.maximumIntegerDigits
                this.roundingMode = roundingMode
                format(it) + suffix
            }
        } ?: run {
            "--"
        }
    }

}