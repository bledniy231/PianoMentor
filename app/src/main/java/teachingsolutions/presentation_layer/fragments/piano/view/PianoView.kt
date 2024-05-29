package teachingsolutions.presentation_layer.fragments.piano.view

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.example.pianomentor.R
import com.example.pianomentor.databinding.PianoViewBinding
import com.google.android.material.button.MaterialButton


class PianoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: PianoViewBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var octave: Int = 1
        set(value) {
            field = when {
                value < 1 -> 1
                value > 7 -> 7
                else -> value
            }
        }

    private val notes = emptyList<MediaPlayer>()

    private lateinit var noteButtons: Map<Int, Int>

    init {
        _binding = PianoViewBinding.inflate(LayoutInflater.from(context), this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.PianoView, 0, 0)
            .apply {
                try {
                    octave = getInt(R.styleable.PianoView_octave, 1)
                } finally {
                    recycle()
                }
            }

        val notesArrayId = resources.getIdentifier("octave_$octave", "array", context.packageName)
        if (notesArrayId != 0) { // Проверяем, что идентификатор найден
            val notesArray = resources.obtainTypedArray(notesArrayId)
            noteButtons = mapOf(
                R.id.note_C to notesArray.getResourceId(0, 0),
                R.id.note_C_sharp to notesArray.getResourceId(1, -1),
                R.id.note_D to notesArray.getResourceId(2, 0),
                R.id.note_D_sharp to notesArray.getResourceId(3, 0),
                R.id.note_E to notesArray.getResourceId(4, 0),
                R.id.note_F to notesArray.getResourceId(5, 0),
                R.id.note_F_sharp to notesArray.getResourceId(6, 0),
                R.id.note_G to notesArray.getResourceId(7, 0),
                R.id.note_G_sharp to notesArray.getResourceId(8, 0),
                R.id.note_A to notesArray.getResourceId(9, 0),
                R.id.note_A_sharp to notesArray.getResourceId(10, 0),
                R.id.note_B to notesArray.getResourceId(11, 0)
            )
            notesArray.recycle()
        } else {
            Log.e("ResourceError", "Array resource not found for octave_$octave")
        }


        setOnTouchListener { _, event ->
            val key = getKeyFromTouch(event.x, event.y)
            if (key != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        notes[key].start()
                    }
                    MotionEvent.ACTION_UP -> {
                        notes[key].pause()
                        notes[key].seekTo(0)
                    }
                }
            }
            performClick()
            true
        }
    }

    private fun getKeyFromTouch(x: Float, y: Float): Int? {
        for (noteBtn in noteButtons) {
            val button = findViewById<MaterialButton>(noteBtn)
            if (button.left <= x && x <= button.right && button.top <= y && y <= button.bottom) {
                return noteBtn.
            }
        }
        return null
    }
}