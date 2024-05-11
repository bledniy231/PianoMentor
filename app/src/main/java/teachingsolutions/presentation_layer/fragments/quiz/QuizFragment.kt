package teachingsolutions.presentation_layer.fragments.quiz

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentQuizBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.presentation_layer.adapters.QuizViewPagerAdapter
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionViewPagerUI

@AndroidEntryPoint
class QuizFragment : Fragment() {
    companion object {
        fun newInstance() = QuizFragment()
    }

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments == null) {
            Toast.makeText(requireContext(), "FAIL: Empty arguments", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        val courseId = requireArguments().getInt("CourseId")
        val courseItemId = requireArguments().getInt("CourseItemId")

        viewModel.getQuizQuestions(courseId, courseItemId)
        binding.quizLoading.visibility = View.VISIBLE

        val adapter = context?.let { QuizViewPagerAdapter(it) }
        if (adapter == null) {
            Toast.makeText(requireContext(), "FAIL: Adapter is null", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        val quizStatus = requireArguments().getString("CourseItemProgressType")?.let { CourseItemProgressType.valueOf(it) }
        if (quizStatus == null) {
            Toast.makeText(requireContext(), "FAIL: Quiz status is null", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        binding.quizCompleteButton.visibility = View.VISIBLE
        var restartQuiz = false
        if (quizStatus == CourseItemProgressType.FAILED) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.quiz_failed))
                .setMessage(getString(R.string.are_you_sure_logout_dialog))
                .setPositiveButton(getString(R.string.restart_quiz)) { _, _ ->
                    restartQuiz = true
                    binding.quizCompleteButton.visibility = View.VISIBLE
                }
                .setNegativeButton(getString(R.string.show_results)) { _, _ ->
                    restartQuiz = false
                    binding.quizCompleteButton.visibility = View.GONE
                }
                .show()
        } else if (quizStatus == CourseItemProgressType.COMPLETED) {
            restartQuiz = false
            binding.quizCompleteButton.visibility = View.GONE
        }

        viewModel.quizQuestions.observe(viewLifecycleOwner,
            Observer { quizQuestions ->
                quizQuestions ?: return@Observer

                binding.quizLoading.visibility = View.GONE
                quizQuestions.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }

                quizQuestions.success?.let { questions ->
                    adapter?.setModelsList(questions, restartQuiz)
                    binding.quizViewPager.adapter = adapter
                    binding.quizViewPager.isUserInputEnabled = true
                }
            })

        binding.quizToolbar.setNavigationOnClickListener {
            if (!restartQuiz) {
                findNavController().popBackStack()
                return@setNavigationOnClickListener
            }

            val resultModels = adapter?.models
            viewModel.setQuizResult(courseId, courseItemId, false, resultModels ?: emptyList())
            viewModel.quizSavingResult.observe(viewLifecycleOwner,
                Observer { result ->
                    result ?: return@Observer
                    if (result.message != null) {
                        Toast.makeText(requireContext(), "FAIL: Error while saving quiz result", Toast.LENGTH_LONG).show()
                    }
                    findNavController().popBackStack()
                })
        }

        binding.quizCompleteButton.setOnClickListener {
            if (!restartQuiz) {
                Toast.makeText(requireContext(), "FAIL: Quiz is not restarted", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                return@setOnClickListener
            }

            val resultModels = adapter?.models
            viewModel.setQuizResult(courseId, courseItemId, true, resultModels ?: emptyList())
            viewModel.quizSavingResult.observe(viewLifecycleOwner,
                Observer { result ->
                    result ?: return@Observer
                    if (result.message != null) {
                        Toast.makeText(requireContext(), "FAIL: Error while saving quiz result", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }

                    val (correctAnswers, correctUserAnswers, correctAnswersPercentage) = viewModel.calculateQuizResults(resultModels ?: emptyList())
                    val bundle = Bundle().apply {
                        putInt("CourseId", courseId)
                        putInt("CourseItemId", courseItemId)
                        putString("CourseTitle", requireArguments().getString("courseTitle"))
                        putInt("CorrectUserAnswers", correctUserAnswers)
                        putInt("CorrectAnswers", correctAnswers)
                        putDouble("CorrectAnswersPercentage", correctAnswersPercentage)
                    }
                    findNavController().navigate(R.id.action_open_quiz_result, bundle)
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}