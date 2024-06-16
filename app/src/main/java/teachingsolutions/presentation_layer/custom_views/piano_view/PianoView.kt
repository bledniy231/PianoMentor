package teachingsolutions.presentation_layer.custom_views.piano_view

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.example.pianomentor.R
import com.example.pianomentor.databinding.PianoViewBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class PianoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: PianoViewBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val canPress = true

    @get:JvmName("getOctave")
    private var octave: Int by viewProperty(1)

    private val noteButtonsToSounds: MutableMap<MaterialButton, MediaPlayer?> = ConcurrentHashMap()

    init {
        _binding = PianoViewBinding.inflate(LayoutInflater.from(context), this, true)

        initAttributes(attrs, defStyleAttr)
        coroutineScope.launch {
            initNoteSounds()
        }

        setOnTouchListener { _, event ->
            val action = event.actionMasked
            if (!canPress) {
                return@setOnTouchListener false
            }

            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> handleDown(event.actionIndex, event)
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

    private fun handleDown(which: Int, event: MotionEvent) {
        val x = event.getX(which).toInt()
        val y = event.getY(which).toInt()
        for ((button, _) in noteButtonsToSounds) {
            if (button.tag.toString().endsWith("sharp", true)) {
                if (!button.isPressed &&
                    button.left <= x && x <= button.right &&
                    button.top <= y && y <= button.bottom) {
                    handleKeyDown(which, event, button)
                }
            } else {
                var isInsideBlackKey = false
                for ((blackKey, _) in noteButtonsToSounds) {
                    if (blackKey.tag.toString().endsWith("sharp", true) &&
                        blackKey.left <= x && x <= blackKey.right &&
                        blackKey.top <= y && y <= blackKey.bottom) {
                        isInsideBlackKey = true
                        break
                    }
                }
                if (!isInsideBlackKey && !button.isPressed &&
                    button.left <= x && x <= button.right &&
                    button.top <= y && y <= button.bottom) {
                    handleKeyDown(which, event, button)
                }
            }
        }
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

    private fun handleKeyDown(which: Int, event: MotionEvent, button: MaterialButton) {
        button.isPressed = true

        coroutineScope.launch {
            if (noteButtonsToSounds[button] == null) {
                loadNoteSound(button)
            }
            noteButtonsToSounds[button]?.start()
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

        val player = noteButtonsToSounds[button]
        player?.let {
            coroutineScope.launch {
                var volume = 1.0f
                while (volume > 0) {
                    volume -= 0.05f
                    if (volume < 0) {
                        volume = 0f
                    }
                    it.setVolume(volume, volume)
                    delay(10)
                }
                it.pause()
                it.seekTo(0)
                it.setVolume(1f, 1f)
            }
        }
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs ?: return
        context.obtainStyledAttributes(attrs, R.styleable.PianoView, defStyleAttr, 0)
            .use { typedArray ->
                octave = typedArray.getInt(R.styleable.PianoView_octave, 1)
            }
    }

    private fun initNoteSounds() {
        for (i in 0 until binding.pianoContainer.childCount) {
            val button = binding.pianoContainer.getChildAt(i) as? MaterialButton
            if (button != null) {
                loadNoteSound(button)
            }
        }
    }

    private fun loadNoteSound(btn: MaterialButton) {
        val name = "${btn.tag}_$octave"
        val soundRes = resources.getIdentifier(name, "raw", context.packageName)
        val soundPlayer = MediaPlayer.create(context, soundRes)
        noteButtonsToSounds.putIfAbsent(btn, soundPlayer)
    }
}