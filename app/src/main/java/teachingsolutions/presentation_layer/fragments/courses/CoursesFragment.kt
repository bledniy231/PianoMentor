package teachingsolutions.presentation_layer.fragments.courses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentCoursesBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.adapters.CoursesRecyclerViewAdapter
import teachingsolutions.domain_layer.mapping_models.courses.CourseModel
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener

@AndroidEntryPoint
class CoursesFragment : Fragment(),
    ISelectRecyclerViewItemListener<CourseModel> {

    companion object {
        fun newInstance() = CoursesFragment()
    }

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CoursesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialCoursesRecyclerView()
        binding.coursesToolbar.title = arguments?.getString("Курс") ?: "Курсы"
        binding.coursesToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_back_arrow_courses_to_statistics)
        }
    }

    private fun initialCoursesRecyclerView() {
        val courseName = arguments?.getString("Курс")
        val list: List<CourseModel>? = when (courseName) {
            null -> viewModel.getCoursesList()
            "Введение" -> viewModel.getIntroductionCourseItemsList()
            "Продолжение" -> viewModel.getContinuationCourseItemsList()
            "Профи" -> viewModel.getProfiCourseItemsList()
            else -> {
                Toast.makeText(requireContext(), "Неизвестный курс", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_back_arrow_courses_to_statistics)
                null
            }
        }

        val adapter = CoursesRecyclerViewAdapter(this, when (courseName) {
            null -> CourseImplementation.BASE_COURSES
            else -> CourseImplementation.EXACT_COURSE_ITEMS
        })

        adapter.setModelsList(list ?: emptyList())
        binding.coursesRecyclerView.adapter = adapter
    }

    override fun onItemSelected(itemModel: CourseModel) {
        when (itemModel.title) {
            "Курс \"Введение\"" -> {
                val args = bundleOf("Курс" to "Введение")
                findNavController().navigate(R.id.action_open_course, args)
            }

            "Курс \"Продолжение\"" -> {
                val args = bundleOf("Курс" to "Продолжение")
                findNavController().navigate(R.id.action_open_course, args)
            }

            "Курс \"Профи\"" -> {
                val args = bundleOf("Курс" to "Профи")
                findNavController().navigate(R.id.action_open_course, args)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}