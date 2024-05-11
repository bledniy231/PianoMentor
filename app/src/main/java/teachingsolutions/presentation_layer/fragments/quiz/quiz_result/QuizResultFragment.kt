package teachingsolutions.presentation_layer.fragments.quiz.quiz_result

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentQuizResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments == null) {
            Toast.makeText(requireContext(), "FAIL: Empty arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        val correctUserAnswers = requireArguments().getInt("CorrectUserAnswers")
        val correctAnswers = requireArguments().getInt("CorrectAnswers")
        val correctAnswersPercentage = requireArguments().getDouble("CorrectAnswersPercentage")
        if (correctUserAnswers == correctAnswers) {
            binding.imageView.setImageResource(R.drawable.icon_success_quiz)
            binding.testStatusTextView.text = getString(R.string.quiz_result_success)
        } else {
            binding.imageView.setImageResource(R.drawable.icon_failed_quiz)
            binding.testStatusTextView.text = getString(R.string.quiz_result_fail)
        }

        val correctTextAnim = animateTextView(correctUserAnswers, binding.correctAnswersTextView, R.string.correct_answers)
        val totalTextAnim = animateTextView(correctAnswers, binding.totalQuestionsTextView, R.string.total_questions)
        val percentTextAnim = animateTextView(correctAnswersPercentage, binding.percentCompleted, R.string.percent_completed)

        correctTextAnim.start()
        totalTextAnim.start()
        percentTextAnim.start()

        binding.btnBackToCourse.setOnClickListener {
            findNavController().popBackStack(R.id.action_quiz_result_to_courses, true)
            val bundle = bundleOf(
                "CourseId" to requireArguments().getInt("CourseId"),
                "CourseTitle" to requireArguments().getString("CourseTitle")
            )
            findNavController().navigate(R.id.action_quiz_result_to_courses, bundle)
        }

        binding.btnBackToStatistics.setOnClickListener {
            findNavController().popBackStack(R.id.action_quiz_result_to_statistics, false)
        }
    }

    private fun animateTextView(end: Int, textView: TextView, stringResource: Int): ValueAnimator {
        val valueAnimator = ValueAnimator.ofInt(0, end)
        valueAnimator.duration = 800

        valueAnimator.addUpdateListener { animation ->
            textView.text = getString(stringResource, animation.animatedValue as Int)
        }
        return valueAnimator
    }

    private fun animateTextView(end: Double, textView: TextView, stringResource: Int): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, end.toFloat())
        valueAnimator.duration = 800

        valueAnimator.addUpdateListener { animation ->
            textView.text = getString(stringResource, animation.animatedValue as Float)
        }
        return valueAnimator
    }
}