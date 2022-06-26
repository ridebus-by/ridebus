package org.xtimms.ridebus.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import org.xtimms.ridebus.R

class RideBusChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.chipGroupStyle
) : ChipGroup(context, attrs, defStyleAttr) {

    private var isLayoutSuppressedCompat = false
    private var isLayoutCalledOnSuppressed = false

    override fun requestLayout() {
        if (isLayoutSuppressedCompat) {
            isLayoutCalledOnSuppressed = true
        } else {
            super.requestLayout()
        }
    }

    fun setChips(items: Collection<ChipModel>) {
        suppressLayoutCompat(true)
        try {
            for ((i, model) in items.withIndex()) {
                val chip = getChildAt(i) as Chip? ?: addChip()
                bindChip(chip, model)
            }
            if (childCount > items.size) {
                removeViews(items.size, childCount - items.size)
            }
        } finally {
            suppressLayoutCompat(false)
        }
    }

    private fun bindChip(chip: Chip, model: ChipModel) {
        chip.text = model.title
        if (model.icon == 0) {
            chip.isChipIconVisible = false
        } else {
            chip.isCheckedIconVisible = true
            chip.setChipIconResource(model.icon)
        }
    }

    private fun addChip(): Chip {
        val chip = Chip(context)
        val drawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_RideBus_Chip_Action)
        chip.setChipDrawable(drawable)
        addView(chip)
        return chip
    }

    private fun suppressLayoutCompat(suppress: Boolean) {
        isLayoutSuppressedCompat = suppress
        if (!suppress) {
            if (isLayoutCalledOnSuppressed) {
                requestLayout()
                isLayoutCalledOnSuppressed = false
            }
        }
    }

    class ChipModel(
        @DrawableRes val icon: Int,
        val title: CharSequence,
    ) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChipModel

            if (icon != other.icon) return false
            if (title != other.title) return false

            return true
        }

        override fun hashCode(): Int {
            var result = icon
            result = 31 * result + title.hashCode()
            return result
        }
    }
}
