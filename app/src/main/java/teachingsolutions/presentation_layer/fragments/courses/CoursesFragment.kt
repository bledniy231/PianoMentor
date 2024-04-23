package teachingsolutions.presentation_layer.fragments.courses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentCoursesBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.adapters.CoursesRecyclerViewAdapter
import teachingsolutions.domain_layer.mapping_models.CourseRecyclerViewItemModel
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener
import javax.inject.Inject

@AndroidEntryPoint
class CoursesFragment : Fragment(),
    ISelectRecyclerViewItemListener<CourseRecyclerViewItemModel> {

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
        //viewModel = ViewModelProvider(this, ViewModelsFactory())[CoursesViewModel::class.java]
        initialCoursesRecyclerView()
        binding.coursesToolbar.title = arguments?.getString("Курс") ?: "Курсы"
        binding.coursesToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_back_arrow_courses_to_statistics)
        }
    }

    private fun initialCoursesRecyclerView() {
        val list = viewModel.getCoursesList()

        val adapter = CoursesRecyclerViewAdapter(this)
        adapter.setModelsList(list)
        binding.coursesRecyclerView.adapter = adapter
    }

    override fun onItemSelected(itemModel: CourseRecyclerViewItemModel) {
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