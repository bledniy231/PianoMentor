package teachingsolutions.presentation_layer.fragments.piano.view

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import kotlin.math.ceil
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty



val Int.dp: Int
    get() = ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)).toInt()

val Float.dp: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)


/**
 * An extension for properties on View classes to initialize with [ViewPropertyDelegate].
 *
 * @param defaultValue A default value for this property.
 *
 * @return A [ViewPropertyDelegate] which is readable and writable property.
 */
@JvmSynthetic
internal fun <T : Any?> View.viewProperty(defaultValue: T): ViewPropertyDelegate<T> {
    return ViewPropertyDelegate(defaultValue) {
        invalidate()
    }
}

/**
 * A delegate class to invalidate View class if the [propertyValue] has been updated by a new value.
 *
 * @param defaultValue A default value for this property.
 * @param invalidator An executable lambda to invalidate [View].
 *
 * @return A readable and writable property.
 */
internal class ViewPropertyDelegate<T : Any?>(
    defaultValue: T,
    private val invalidator: () -> Unit
) : ReadWriteProperty<Any?, T> {

    private var propertyValue: T = defaultValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return propertyValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (propertyValue != value) {
            propertyValue = value
            invalidator()
        }
    }
}