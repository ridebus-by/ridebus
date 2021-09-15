package org.xtimms.ridebus.util.preference

import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.*
import org.xtimms.ridebus.R
import org.xtimms.ridebus.util.system.getResourceColor
import org.xtimms.ridebus.widget.preference.AdaptiveTitlePreferenceCategory
import org.xtimms.ridebus.widget.preference.IntListPreference
import org.xtimms.ridebus.widget.preference.SwitchPreferenceCategory
import org.xtimms.ridebus.widget.preference.SwitchSettingsPreference

@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class DSL

inline fun PreferenceManager.newScreen(block: (@DSL PreferenceScreen).() -> Unit): PreferenceScreen {
    return createPreferenceScreen(context).also { it.block() }
}

inline fun PreferenceGroup.preference(block: (@DSL Preference).() -> Unit): Preference {
    return initThenAdd(Preference(context), block)
}

inline fun PreferenceGroup.infoPreference(@StringRes infoRes: Int): Preference {
    return initThenAdd(
        Preference(context),
        {
            iconRes = R.drawable.ic_info
            iconTint = context.getResourceColor(android.R.attr.textColorHint)
            summaryRes = infoRes
            isSelectable = false
        }
    )
}

inline fun PreferenceGroup.switchPreference(block: (@DSL SwitchPreferenceCompat).() -> Unit): SwitchPreferenceCompat {
    return initThenAdd(SwitchPreferenceCompat(context), block)
}

inline fun PreferenceGroup.switchPreferenceCategory(block: (@DSL SwitchPreferenceCategory).() -> Unit): SwitchPreferenceCategory {
    return initThenAdd(SwitchPreferenceCategory(context), block)
}

inline fun PreferenceGroup.switchSettingsPreference(block: (@DSL SwitchSettingsPreference).() -> Unit): SwitchSettingsPreference {
    return initThenAdd(SwitchSettingsPreference(context), block)
}

inline fun PreferenceGroup.checkBoxPreference(block: (@DSL CheckBoxPreference).() -> Unit): CheckBoxPreference {
    return initThenAdd(CheckBoxPreference(context), block)
}

inline fun PreferenceGroup.editTextPreference(block: (@DSL EditTextPreference).() -> Unit): EditTextPreference {
    return initThenAdd(EditTextPreference(context), block).also(::initDialog)
}

inline fun PreferenceGroup.listPreference(block: (@DSL ListPreference).() -> Unit): ListPreference {
    return initThenAdd(ListPreference(context), block).also(::initDialog)
}

inline fun PreferenceGroup.intListPreference(block: (@DSL IntListPreference).() -> Unit): IntListPreference {
    return initThenAdd(IntListPreference(context), block).also(::initDialog)
}

inline fun PreferenceGroup.multiSelectListPreference(block: (@DSL MultiSelectListPreference).() -> Unit): MultiSelectListPreference {
    return initThenAdd(MultiSelectListPreference(context), block).also(::initDialog)
}

inline fun PreferenceScreen.preferenceCategory(block: (@DSL PreferenceCategory).() -> Unit): PreferenceCategory {
    return addThenInit(AdaptiveTitlePreferenceCategory(context), block)
}

inline fun PreferenceScreen.preferenceScreen(block: (@DSL PreferenceScreen).() -> Unit): PreferenceScreen {
    return addThenInit(preferenceManager.createPreferenceScreen(context), block)
}

fun initDialog(dialogPreference: DialogPreference) {
    with(dialogPreference) {
        if (dialogTitle == null) {
            dialogTitle = title
        }
    }
}

inline fun <P : Preference> PreferenceGroup.add(p: P): P {
    return p.apply {
        this.isIconSpaceReserved = false
        this.isSingleLineTitle = false
        addPreference(this)
    }
}

inline fun <P : Preference> PreferenceGroup.initThenAdd(p: P, block: P.() -> Unit): P {
    return p.apply {
        block()
        this.isIconSpaceReserved = false
        this.isSingleLineTitle = false
        addPreference(this)
    }
}

inline fun <P : Preference> PreferenceGroup.addThenInit(p: P, block: P.() -> Unit): P {
    return p.apply {
        this.isIconSpaceReserved = false
        this.isSingleLineTitle = false
        addPreference(this)
        block()
    }
}

inline fun Preference.onClick(crossinline block: () -> Unit) {
    setOnPreferenceClickListener { block(); true }
}

inline fun Preference.onChange(crossinline block: (Any?) -> Boolean) {
    setOnPreferenceChangeListener { _, newValue -> block(newValue) }
}

var Preference.defaultValue: Any?
    get() = null // set only
    set(value) {
        setDefaultValue(value)
    }

var Preference.titleRes: Int
    get() = 0 // set only
    set(value) {
        setTitle(value)
    }

var Preference.iconRes: Int
    get() = 0 // set only
    set(value) {
        icon = AppCompatResources.getDrawable(context, value)
    }

var Preference.summaryRes: Int
    get() = 0 // set only
    set(value) {
        setSummary(value)
    }

var Preference.iconTint: Int
    get() = 0 // set only
    set(value) {
        DrawableCompat.setTint(icon, value)
    }

var ListPreference.entriesRes: Array<Int>
    get() = emptyArray() // set only
    set(value) {
        entries = value.map { context.getString(it) }.toTypedArray()
    }

var MultiSelectListPreference.entriesRes: Array<Int>
    get() = emptyArray() // set only
    set(value) {
        entries = value.map { context.getString(it) }.toTypedArray()
    }
