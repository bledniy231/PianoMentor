package teachingsolutions.presentation_layer.fragments.piano

import android.animation.LayoutTransition
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.pianomentor.databinding.FragmentEnterPianoLessonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterPianoLessonFragment : Fragment() {

    private var _binding: FragmentEnterPianoLessonBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EnterPianoLessonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPianoLessonBinding.inflate(inflater, container, false)
        _binding!!.taskContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardView = binding.taskDescriptionCardView

        cardView.setOnClickListener {
            val visibility = if (cardView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.taskContainer, AutoTransition())
            cardView.visibility = visibility
        }
    }
}