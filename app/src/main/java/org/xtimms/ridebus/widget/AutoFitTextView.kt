package org.xtimms.ridebus.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView

class AutoFitTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyle) {

    private val availableSpaceRect = RectF()
    private val sizeTester: SizeTester
    private var maxTextSize: Float = 0.toFloat()
    private var spacingMult = 1.0f
    private var spacingAdd = 0.0f
    private var minTextSize: Float = 0.toFloat()
    private var widthLimit: Int = 0
    private var maxLines: Int = 0
    private var initialized = false
    private var textPaint: TextPaint? = null

    private interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying `suggestedSize` to
         * text, it takes less space than `availableSpace`, > 0
         * otherwise
         */
        fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int
    }

    init {
        minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
        maxTextSize = textSize
        textPaint = TextPaint(paint)
        if (maxLines == 0) {
            maxLines = NO_LINE_LIMIT
        }
        sizeTester = object : SizeTester {
            val textRect = RectF()

            override fun onTestSize(suggestedSize: Int, availableSpace: RectF): Int {
                textPaint!!.textSize = suggestedSize.toFloat()
                val transformationMethod = transformationMethod
                val text: String = transformationMethod?.getTransformation(text, this@AutoFitTextView)?.toString()
                    ?: text.toString()
                val singleLine = maxLines == 1
                if (singleLine) {
                    textRect.bottom = textPaint!!.fontSpacing
                    textRect.right = textPaint!!.measureText(text)
                } else {
                    val layout: StaticLayout =
                        StaticLayout.Builder.obtain(text, 0, text.length, textPaint!!, widthLimit)
                            .setLineSpacing(spacingAdd, spacingMult)
                            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                            .setIncludePad(true)
                            .build()
                    if (maxLines != NO_LINE_LIMIT && layout.lineCount > maxLines) {
                        return 1
                    }
                    textRect.bottom = layout.height.toFloat()
                    var maxWidth = -1
                    val lineCount = layout.lineCount
                    for (i in 0 until lineCount) {
                        val end = layout.getLineEnd(i)
                        if (i < lineCount - 1 && end > 0 && !isValidWordWrap(text[end - 1])) {
                            return 1
                        }
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i)) {
                            maxWidth = layout.getLineRight(i).toInt() - layout.getLineLeft(i).toInt()
                        }
                    }
                    textRect.right = maxWidth.toFloat()
                }
                textRect.offsetTo(0f, 0f)
                return if (availableSpace.contains(textRect)) -1 else 1
            }
        }
        initialized = true
    }

    fun isValidWordWrap(c: Char): Boolean {
        return c == ' ' || c == '-'
    }

    override fun setAllCaps(allCaps: Boolean) {
        super.setAllCaps(allCaps)
        adjustTextSize()
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        adjustTextSize()
    }

    override fun setTextSize(size: Float) {
        maxTextSize = size
        adjustTextSize()
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        this.maxLines = maxLines
        adjustTextSize()
    }

    override fun getMaxLines(): Int {
        return maxLines
    }

    override fun setSingleLine() {
        super.setSingleLine()
        maxLines = 1
        adjustTextSize()
    }

    override fun setSingleLine(singleLine: Boolean) {
        super.setSingleLine(singleLine)
        maxLines = when {
            singleLine -> 1
            else -> NO_LINE_LIMIT
        }
        adjustTextSize()
    }

    override fun setLines(lines: Int) {
        super.setLines(lines)
        maxLines = lines
        adjustTextSize()
    }

    override fun setTextSize(unit: Int, size: Float) {
        val c = context
        val r: Resources = if (c == null) {
            Resources.getSystem()
        } else {
            c.resources
        }
        maxTextSize = TypedValue.applyDimension(unit, size, r.displayMetrics)
        adjustTextSize()
    }

    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        spacingMult = mult
        spacingAdd = add
    }

    private fun adjustTextSize() {
        if (!initialized) {
            return
        }
        val startSize = minTextSize.toInt()
        val heightLimit = measuredHeight - compoundPaddingBottom - compoundPaddingTop
        widthLimit = measuredWidth - compoundPaddingLeft - compoundPaddingRight
        if (widthLimit <= 0) {
            return
        }
        textPaint = TextPaint(paint)
        availableSpaceRect.apply {
            right = widthLimit.toFloat()
            bottom = heightLimit.toFloat()
        }
        superSetTextSize(startSize)
    }

    private fun superSetTextSize(startSize: Int) {
        val textSize = binarySearch(startSize, maxTextSize.toInt(), sizeTester, availableSpaceRect)
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    private fun binarySearch(start: Int, end: Int, sizeTester: SizeTester, availableSpace: RectF): Int {
        var lastBest = start
        var lo = start
        var hi = end - 1
        var mid: Int
        while (lo <= hi) {
            mid = (lo + hi).ushr(1)
            val midValCmp = sizeTester.onTestSize(mid, availableSpace)
            when {
                midValCmp < 0 -> {
                    lastBest = lo
                    lo = mid + 1
                }
                midValCmp > 0 -> {
                    hi = mid - 1
                    lastBest = hi
                }
                else -> return mid
            }
        }
        return lastBest
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        super.onTextChanged(text, start, before, after)
        adjustTextSize()
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)
        if (width != oldwidth || height != oldheight) {
            adjustTextSize()
        }
    }

    companion object {
        private const val NO_LINE_LIMIT = -1
    }
}
