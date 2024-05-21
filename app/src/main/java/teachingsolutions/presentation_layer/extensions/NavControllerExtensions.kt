package teachingsolutions.presentation_layer.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavController
import android.os.Bundle

public fun NavController.safelyNavigate(@IdRes resId: Int, args: Bundle? = null) =
    try { navigate(resId, args) }
    catch (_: Exception) {  }