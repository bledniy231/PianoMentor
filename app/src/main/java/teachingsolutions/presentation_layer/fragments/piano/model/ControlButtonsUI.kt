package teachingsolutions.presentation_layer.fragments.piano.model

import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton

data class ControlButtonsUI(
    var frameLayout: FrameLayout,
    var repeatButton: MaterialButton,
    var exerciseCounter: TextView,
    var nextButton: MaterialButton
)