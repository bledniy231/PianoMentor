package teachingsolutions.presentation_layer.fragments.courses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentCoursesBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.adapters.CoursesRecyclerViewAdapter
import teachingsolutions.presentation_layer.fragments.courses.model.CourseImplementation
import teachingsolutions.presentation_layer.fragments.courses.model.CourseModelUI
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener

@AndroidEntryPoint
class CoursesFragment : Fragment(),
    ISelectRecyclerViewItemListener<CourseModelUI> {

    companion object {
        fun newInstance() = CoursesFragment()
    }

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CoursesViewModel by viewModels()
    private var userId: Long? = null
    private var courseImpl: CourseImplementation? = null

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

        binding.coursesToolbar.setNavigationOnClickListener {
            when (val courseId = arguments?.getString("CourseId")?.toInt() ?: 0) {
                0 -> findNavController().navigate(R.id.action_back_arrow_courses_to_statistics)
                else -> {
                    //val args = bundleOf("CourseId" to courseId)
                    findNavController().navigate(R.id.action_back_arrow_courses_to_courses/*, args*/)
                }
            }
        }
        binding.coursesLoading.visibility = View.VISIBLE
        binding.coursesRecyclerView.visibility = View.GONE
        userId = viewModel.getUserId() ?: 0

        initialReceivingElements()

        viewModel.coursesResult.observe(viewLifecycleOwner,
            Observer { coursesResultUI ->
                coursesResultUI ?: return@Observer

                binding.coursesLoading.visibility = View.GONE
                binding.coursesRecyclerView.visibility = View.VISIBLE
                coursesResultUI.error?.let {
                    updateUiWithCoursesFailed(it)
                }

                coursesResultUI.success?.let {
                    val adapter = CoursesRecyclerViewAdapter(this, courseImpl ?: CourseImplementation.BASE_COURSES)
                    adapter.setModelsList(coursesResultUI.success)

                    binding.coursesRecyclerView.adapter = adapter
                }
        })

        viewModel.courseItemsResult.observe(viewLifecycleOwner,
            Observer { courseItemsResultUI ->
                courseItemsResultUI ?: return@Observer

                binding.coursesLoading.visibility = View.GONE
                binding.coursesRecyclerView.visibility = View.VISIBLE
                courseItemsResultUI.error?.let {
                    updateUiWithCoursesFailed(it)
                }

                courseItemsResultUI.success?.let {
                    val adapter = CoursesRecyclerViewAdapter(this, courseImpl ?: CourseImplementation.EXACT_COURSE_ITEMS)
                    adapter.setModelsList(courseItemsResultUI.success)

                    binding.coursesRecyclerView.adapter = adapter
                }
        })

        binding.coursesToolbar.title = arguments?.getString("Title") ?: "Курсы"
    }

    private fun initialReceivingElements() {
        when (val courseId = arguments?.getString("CourseId")?.toInt() ?: 0) {
            0 -> {
                courseImpl = CourseImplementation.BASE_COURSES
                viewModel.getCoursesList(userId ?: 0)
            }
            else -> {
                courseImpl = CourseImplementation.EXACT_COURSE_ITEMS
                viewModel.getExactCourseItemsList(userId ?: 0, courseId)
            }
        }
    }

    override fun onItemSelected(itemModel: CourseModelUI) {
        if (itemModel.isExactItem) {
            return
        }
        else {
            val args = bundleOf("CourseId" to itemModel.courseId, "Title" to itemModel.title)
            findNavController().navigate(R.id.action_open_course, args)
        }
    }

    private fun updateUiWithCoursesFailed(errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}