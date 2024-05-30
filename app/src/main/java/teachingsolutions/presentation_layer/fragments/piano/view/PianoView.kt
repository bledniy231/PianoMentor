package teachingsolutions.presentation_layer.fragments.piano.view

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.example.pianomentor.R
import com.example.pianomentor.databinding.PianoViewBinding
import com.google.android.material.button.MaterialButton
import java.util.concurrent.CopyOnWriteArrayList

class PianoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: PianoViewBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val canPress = true
    private val pressedKeys: CopyOnWriteArrayList<MaterialButton> = CopyOnWriteArrayList<MaterialButton>()

    @get:JvmName("getOctave")
    private var octave: Int by viewProperty(1)

    private lateinit var noteButtonsToSounds: MutableMap<MaterialButton, MediaPlayer?>

    init {
        _binding = PianoViewBinding.inflate(LayoutInflater.from(context), this, true)

        noteButtonsToSounds = mutableMapOf(
            binding.noteC to null,
            binding.noteCSharp to null,
            binding.noteD to null,
            binding.noteDSharp to null,
            binding.noteE to null,
            binding.noteF to null,
            binding.noteFSharp to null,
            binding.noteG to null,
            binding.noteGSharp to null,
            binding.noteA to null,
            binding.noteASharp to null,
            binding.noteB to null
        )

        initAttributes(attrs, defStyleAttr)

        setOnTouchListener { _, event ->
            val action = event.actionMasked
            if (!canPress) {
                return@setOnTouchListener false
            }

            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> handleDown(event.actionIndex, event)

                MotionEvent.ACTION_MOVE -> {
                    run {
                        var i = 0
                        while (i < event.pointerCount) {
                            handleMove(i, event)
                            i++
                        }
                    }
                    var i = 0
                    while (i < event.pointerCount) {
                        handleDown(i, event)
                        i++
                    }
                }

                MotionEvent.ACTION_POINTER_UP -> handlePointerUp(event.getPointerId(event.actionIndex))
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handleUp()
                    return@setOnTouchListener false
                }

                else -> {}
            }
            performClick()
            return@setOnTouchListener true
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    private fun loadNoteSound(btn: MaterialButton) {
        val name = "${btn.tag}_$octave"
        val soundRes = resources.getIdentifier(name, "raw", context.packageName)
        noteButtonsToSounds[btn] =  MediaPlayer.create(context, soundRes)
    }

    private fun handleDown(which: Int, event: MotionEvent) {
        val x = event.getX(which).toInt()
        val y = event.getY(which).toInt()
        for ((button, _) in noteButtonsToSounds) {
            if (!button.isPressed &&
                button.left <= x && x <= button.right &&
                button.top <= y && y <= button.bottom) {
                handleKeyDown(which, event, button)
            }
        }
    }

    private fun handleKeyDown(which: Int, event: MotionEvent, button: MaterialButton) {
        button.isPressed = true

        if (noteButtonsToSounds[button] == null) {
            loadNoteSound(button)
        }
        noteButtonsToSounds[button]?.start()
        // здесь вы можете добавить вызов вашего слушателя, если он есть
    }

    private fun handleMove(which: Int, event: MotionEvent) {
        val x = event.getX(which).toInt()
        val y = event.getY(which).toInt()
        for ((button, _) in noteButtonsToSounds) {
            if (button.isPressed &&
                (button.left > x || x > button.right || button.top > y || y > button.bottom)) {
                handleKeyUp(button)
            } else if (!button.isPressed &&
                button.left <= x && x <= button.right &&
                button.top <= y && y <= button.bottom) {
                handleKeyDown(which, event, button)
            }
        }
    }

    private fun handlePointerUp(pointerId: Int) {
        for ((button, _) in noteButtonsToSounds) {
            if (button.isPressed) {
                handleKeyUp(button)
            }
        }
    }

    private fun handleUp() {
        for ((button, _) in noteButtonsToSounds) {
            if (button.isPressed) {
                handleKeyUp(button)
            }
        }
    }

    private fun handleKeyUp(button: MaterialButton) {
        button.isPressed = false
        noteButtonsToSounds[button]?.apply {
            pause()
            seekTo(0)
        }
        // здесь вы можете добавить вызов вашего слушателя, если он есть
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs ?: return
        context.obtainStyledAttributes(attrs, R.styleable.PianoView, defStyleAttr, 0)
            .use { typedArray ->
                octave = typedArray.getInt(R.styleable.PianoView_octave, 1)
            }
    }
}