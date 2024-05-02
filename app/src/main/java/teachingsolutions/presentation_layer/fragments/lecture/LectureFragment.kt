package teachingsolutions.presentation_layer.fragments.lecture

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentLectureBinding
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class LectureFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener {

    companion object {
        fun newInstance() = LectureFragment()
    }

    private var _binding: FragmentLectureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LectureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLectureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments == null) {
            Toast.makeText(requireContext(), "FAIL: Empty arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.lectureToolbar.title = requireArguments().getString("CourseName")

        val pdfView = PDFView(requireContext(), null)
        binding.lectureParentView.addView(pdfView)
        binding.lecturePdfView.visibility = View.GONE
        binding.lecturesLoading.visibility = View.VISIBLE

        viewModel.lecturePdfResult.observe(viewLifecycleOwner) { lecturePdfResultUI ->
            lecturePdfResultUI?.let {
                it.success?.let { it1 -> displayPdf(binding.lecturePdfView, it1) }
            }
        }

        val courseItemId = requireArguments().getInt("CourseItemId")
        val courseName = requireArguments().getString("CourseName")
        if (courseItemId > 0 && !courseName.isNullOrEmpty()) {
            viewModel.deleteLecturePdf(courseItemId, courseName)
            viewModel.getLecturePdf(courseItemId, courseName)
        } else {
            Toast.makeText(requireContext(), "FAIL: Incorrect arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.lectureToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun displayPdf(pdfView: PDFView, file: File) {
        pdfView.fromFile(file)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(true)
            .onPageChange(this)
            .onLoad(this)
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        TODO("Not yet implemented")
    }

    override fun loadComplete(nbPages: Int) {
        binding.lecturePdfView.visibility = View.VISIBLE
        binding.lecturesLoading.visibility = View.GONE
    }
}