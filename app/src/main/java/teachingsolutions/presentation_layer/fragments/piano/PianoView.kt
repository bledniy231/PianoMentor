package teachingsolutions.presentation_layer.fragments.piano

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.widget.NestedScrollView
import com.example.pianomentor.databinding.PianoViewBinding


class PianoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private var _binding: PianoViewBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    init {
        _binding = PianoViewBinding.inflate(LayoutInflater.from(context), this, true)

    }

}