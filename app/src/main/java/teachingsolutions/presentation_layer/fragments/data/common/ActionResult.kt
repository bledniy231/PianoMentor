package teachingsolutions.presentation_layer.fragments.data.common

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ActionResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : ActionResult<T>()
    data class Error(val exception: Exception) : ActionResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}