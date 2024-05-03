package teachingsolutions.presentation_layer.fragments.lecture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentLectureBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.math.abs

@AndroidEntryPoint
class LectureFragment : Fragment() {

    companion object {
        fun newInstance() = LectureFragment()
    }

    private var _binding: FragmentLectureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LectureViewModel by viewModels()

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var file: File? = null
    private var gestureDetector: GestureDetector? = null

    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    private var currentPageIndex = 0

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
        binding.lecturesLoading.visibility = View.VISIBLE
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null) {
                    // Свайп влево
                    if (e1.x - e2.x > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD && pdfRenderer != null) {
                        if (currentPageIndex + 1 < pdfRenderer!!.pageCount) {
                            currentPageIndex++
                            file?.let { displayPdf() }
                        }
                        return true
                    }
                    // Свайп вправо
                    else if (e2.x - e1.x > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (currentPageIndex > 0) {
                            currentPageIndex--
                            file?.let { displayPdf() }
                        }
                        return true
                    }
                }
                return false
            }
        })
        binding.pdfView.setOnTouchListener { v, event ->
            gestureDetector?.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        binding.buttonNext.setOnClickListener {
            if (currentPageIndex + 1 < (pdfRenderer?.pageCount ?: 0)) {
                currentPageIndex++
                file?.let { displayPdf() }
            }
        }

        binding.buttonPrevious.setOnClickListener {
            if (currentPageIndex > 0) {
                currentPageIndex--
                file?.let { displayPdf() }
            }
        }

        viewModel.lecturePdfResult.observe(viewLifecycleOwner) { lecturePdfResultUI ->
            lecturePdfResultUI?.let { resultUI ->
                resultUI.success?.let { file ->
                    this.file = file
                    fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    if (fileDescriptor == null) {
                        Toast.makeText(requireContext(), "FAIL: File descriptor is null", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    pdfRenderer = PdfRenderer(fileDescriptor!!)
                    displayPdf()
                }
            }
        }

        val courseItemId = requireArguments().getInt("CourseItemId")
        val courseName = requireArguments().getString("CourseName")
        if (courseItemId > 0 && !courseName.isNullOrEmpty()) {
            //viewModel.deleteLecturePdf(courseItemId, courseName)
            viewModel.getLecturePdf(courseItemId, courseName)
        } else {
            Toast.makeText(requireContext(), "FAIL: Incorrect arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.lectureToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun displayPdf() {
        val pageCount = pdfRenderer?.pageCount
        val currentPage = pdfRenderer?.openPage(currentPageIndex)

        val bitmap = Bitmap.createBitmap(currentPage?.width ?: 0, currentPage?.height ?: 0, Bitmap.Config.ARGB_8888)
        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        imageViewAnimatedChange(requireContext(), binding.pdfView, bitmap.copy(bitmap.config, true))

        binding.pageCounter.text = getString(R.string.page_counter, currentPageIndex + 1, pageCount)
        currentPage?.close()

        binding.lecturesLoading.visibility = View.GONE
    }

    private fun imageViewAnimatedChange(context: Context, imageView: ImageView, newImage: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        val animIn  = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                imageView.setImageBitmap(newImage)
                animIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {}
                })
                imageView.startAnimation(animIn)
            }
        })
        imageView.startAnimation(animOut)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gestureDetector = null
        pdfRenderer?.close()
        fileDescriptor?.close()
        pdfRenderer = null
        fileDescriptor = null
    }
}