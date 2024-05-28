package teachingsolutions.presentation_layer.fragments.piano.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.example.pianomentor.R
import com.example.pianomentor.databinding.CircleIconNoteBinding


class NoteIconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var _binding: CircleIconNoteBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var title: String? by viewProperty(null)

    @get:ColorInt
    private var noteBackgroundColor: Int by viewProperty(Color.RED)

    @get:ColorInt
    private var titleColor: Int by viewProperty(Color.BLACK)

    @get:Px
    private var titleSize: Int by viewProperty(12)

    init {
        initAttributes(attrs, defStyleAttr)
        _binding = CircleIconNoteBinding.inflate(LayoutInflater.from(context), this, true)
        binding.noteTitleTv.textSize = titleSize.toFloat()
        binding.noteTitleTv.setTextColor(titleColor)
        binding.noteBackgroundFl.backgroundTintList = ColorStateList.valueOf(noteBackgroundColor)
        binding.noteTitleTv.text = title
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs ?: return
        context.obtainStyledAttributes(attrs, R.styleable.NoteIconView, defStyleAttr, 0)
            .use { typedArray ->
                title = typedArray.getString(
                    R.styleable.NoteIconView_title
                )
                noteBackgroundColor = typedArray.getColor(
                    R.styleable.NoteIconView_noteBackgroundColor, noteBackgroundColor
                )
                titleColor = typedArray.getColor(
                    R.styleable.NoteIconView_titleColor, titleColor
                )
                titleSize = typedArray.getDimensionPixelSize(
                    R.styleable.NoteIconView_titleSize, titleSize
                )
            }

    }
}