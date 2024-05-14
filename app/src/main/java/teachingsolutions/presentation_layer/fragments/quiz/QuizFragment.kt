package teachingsolutions.presentation_layer.fragments.quiz

import android.content.Context
import android.graphics.Rect
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentQuizBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.presentation_layer.adapters.QuizViewPagerAdapter
import teachingsolutions.presentation_layer.fragments.common.DefaultResponseUI
import teachingsolutions.presentation_layer.fragments.quiz.model.GetQuizResponseUI
import teachingsolutions.presentation_layer.fragments.quiz.model.QuestionViewPagerUI
import teachingsolutions.presentation_layer.fragments.quiz.model.StartQuizModel

@AndroidEntryPoint
class QuizFragment : Fragment() {
    companion object {
        fun newInstance() = QuizFragment()
    }

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var args: Bundle

    private val viewModel: QuizViewModel by viewModels()
    private var resultModels: List<QuestionViewPagerUI>? = null
    private val startQuizModel: StartQuizModel = StartQuizModel()

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

        try {
            args = requireArguments()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "FAIL: Empty arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        val adapter = context?.let { QuizViewPagerAdapter(it) }
        if (adapter == null) {
            Toast.makeText(requireContext(), "FAIL: Adapter is null", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        val quizStatus = args.getString("CourseItemProgressType")?.let { CourseItemProgressType.from(it) }
        if (quizStatus == null) {
            Toast.makeText(requireContext(), "FAIL: Quiz status is null", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        val courseId = args.getInt("CourseId")
        val courseItemId = args.getInt("CourseItemId")

        viewModel.getQuizQuestions(courseId, courseItemId)
        binding.quizLoading.visibility = View.VISIBLE
        binding.quizCompleteButton.visibility = View.VISIBLE
        viewModel.showAlertDialog(quizStatus!!, requireContext(), binding.quizCompleteButton)

        val quizQuestionsObserver = Observer<GetQuizResponseUI?> { quizQuestions ->
            quizQuestions ?: return@Observer

            binding.quizLoading.visibility = View.GONE
            quizQuestions.error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }

            quizQuestions.success?.let { questions ->
                adapter?.setModelsList(questions, startQuizModel.startQuiz ?: true, quizStatus)
                binding.quizViewPager.adapter = adapter
                binding.quizViewPager.isUserInputEnabled = true
                binding.quizViewPager.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        with(outRect) {
                            if (parent.getChildAdapterPosition(view) == 0) {
                                left = 20
                            }
                            right = 20
                        }
                    }
                })
            }
        }

        val quizSavingResultBACKObserver = Observer<DefaultResponseUI?> { result ->
                findNavController().popBackStack()
                result ?: return@Observer

                if (result.message != null) {
                    Toast.makeText(requireContext(), "FAIL: Error while saving quiz result", Toast.LENGTH_LONG).show()
                }
            }

        val quizSavingResultCOMPLETEObserver = Observer<DefaultResponseUI?> { result ->
            result ?: return@Observer
            if (result.message != null) {
                Toast.makeText(requireContext(), "FAIL: Error while saving quiz result", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }

            val (correctAnswers, correctUserAnswers, correctAnswersPercentage) = viewModel.calculateQuizResults(resultModels ?: emptyList())
            val bundle = Bundle().apply {
                putInt("CourseId", courseId)
                putInt("CourseItemId", courseItemId)
                putString("CourseTitle", args.getString("courseTitle"))
                putInt("CorrectUserAnswers", correctUserAnswers)
                putInt("CorrectAnswers", correctAnswers)
                putDouble("CorrectAnswersPercentage", correctAnswersPercentage)
            }
            findNavController().navigate(R.id.action_open_quiz_result, bundle)
        }

        val alertDialogObserver = Observer<Boolean?> { startQuiz ->
            startQuiz ?: return@Observer

            startQuizModel.startQuiz = startQuiz
            viewModel.quizQuestions.observe(viewLifecycleOwner, quizQuestionsObserver)
            binding.quizToolbar.setNavigationOnClickListener {
                if (!startQuiz) {
                    findNavController().popBackStack()
                    return@setNavigationOnClickListener
                }

                resultModels = adapter?.models
                viewModel.setQuizResult(courseId, courseItemId, false, resultModels ?: emptyList())
                viewModel.quizSavingResult.observe(viewLifecycleOwner, quizSavingResultBACKObserver)
            }

            binding.quizCompleteButton.setOnClickListener {
                if (!startQuiz) {
                    Toast.makeText(requireContext(), "FAIL: Quiz is not restarted", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    return@setOnClickListener
                }

                resultModels = adapter?.models
                viewModel.setQuizResult(courseId, courseItemId, true, resultModels ?: emptyList())
                viewModel.quizSavingResult.observe(viewLifecycleOwner, quizSavingResultCOMPLETEObserver)
            }
        }

        viewModel.alertDialogResult.observe(viewLifecycleOwner, alertDialogObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}