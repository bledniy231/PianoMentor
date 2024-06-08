package teachingsolutions.domain_layer.common

import android.content.Context

class PixelLengthManager {
    companion object {
        fun getPixelsFromDp(context: Context, dpValue: Int): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun getPixelsFromSp(context: Context, spValue: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return (spValue * scaledDensity + 0.5f)
        }
    }
}