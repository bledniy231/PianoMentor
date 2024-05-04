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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentLectureBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.fragments.lecture.model.LectureAnimation
import teachingsolutions.presentation_layer.fragments.lecture.model.SwipeDirection
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
    private var popupMenu: PopupMenu? = null

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var file: File? = null
    private var gestureDetector: GestureDetector? = null

    private var lectureAnimation: LectureAnimation = LectureAnimation.NONE
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    private var newPageIndex = 0

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
        gestureDetector = getGestureDetector()
        binding.pdfView.setOnTouchListener { v, event ->
            gestureDetector?.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        binding.buttonNext.setOnClickListener {
            if (newPageIndex + 1 < (pdfRenderer?.pageCount ?: 0)) {
                newPageIndex++
                file?.let { displayPdf(SwipeDirection.RIGHT) }
            }
        }

        binding.buttonPrevious.setOnClickListener {
            if (newPageIndex > 0) {
                newPageIndex--
                file?.let { displayPdf(SwipeDirection.LEFT) }
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
                    displayPdf(SwipeDirection.NONE)
                }
                resultUI.error?.let { error ->
                    Toast.makeText(requireContext(), "FAIL: $error", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
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

        lectureAnimation = viewModel.getLectureAnimationSettings()

        popupMenu = PopupMenu(requireContext(), binding.settingsButton).apply {
            inflate(R.menu.lecture_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_no_anim -> {
                        menuItem.isChecked = true
                        lectureAnimation = LectureAnimation.NONE
                        viewModel.saveLectureAnimationSettings(lectureAnimation)
                        true
                    }
                    R.id.action_fade -> {
                        menuItem.isChecked = true
                        lectureAnimation = LectureAnimation.FADE
                        viewModel.saveLectureAnimationSettings(lectureAnimation)
                        true
                    }
                    R.id.action_slide -> {
                        menuItem.isChecked = true
                        lectureAnimation = LectureAnimation.SLIDE
                        viewModel.saveLectureAnimationSettings(lectureAnimation)
                        true
                    }
                    else -> false
                }
            }
            when (lectureAnimation) {
                LectureAnimation.NONE -> menu.findItem(R.id.action_no_anim).isChecked = true
                LectureAnimation.FADE -> menu.findItem(R.id.action_fade).isChecked = true
                LectureAnimation.SLIDE -> menu.findItem(R.id.action_slide).isChecked = true
            }
        }
        binding.settingsButton.setOnClickListener {
            popupMenu?.show()
        }
    }

    private fun displayPdf(direction: SwipeDirection) {
        val pageCount = pdfRenderer?.pageCount
        val currentPage = pdfRenderer?.openPage(newPageIndex)

        val bitmap = Bitmap.createBitmap(currentPage?.width ?: 0, currentPage?.height ?: 0, Bitmap.Config.ARGB_8888)
        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        if (lectureAnimation == LectureAnimation.NONE) {
            binding.pdfView.setImageBitmap(bitmap)
        }
        else {
            imageViewAnimatedChange(requireContext(), binding.pdfView, bitmap.copy(bitmap.config, true), direction)
        }

        binding.pageCounter.text = getString(R.string.page_counter, newPageIndex + 1, pageCount)
        currentPage?.close()

        binding.lecturesLoading.visibility = View.GONE
    }

    private fun imageViewAnimatedChange(context: Context, imageView: ImageView, newImage: Bitmap, direction: SwipeDirection) {
        if (lectureAnimation == LectureAnimation.FADE) {
            val (animOutId, animInId) = Pair(android.R.anim.fade_out, android.R.anim.fade_in)
            val animOut = AnimationUtils.loadAnimation(context, animOutId)
            val animIn = AnimationUtils.loadAnimation(context, animInId)

            animOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) { }
                override fun onAnimationEnd(animation: Animation) {
                    imageView.setImageBitmap(newImage)
                    imageView.startAnimation(animIn)
                }
                override fun onAnimationRepeat(animation: Animation?) { }
            })
            imageView.startAnimation(animOut)

        } else {
            val (animOutId, animInId) = if (direction == SwipeDirection.LEFT) {
                Pair(R.anim.image_view_slide_out_right, R.anim.image_view_slide_in_left)
            } else {
                Pair(R.anim.image_view_slide_out_left, R.anim.image_view_slide_in_right)
            }

            val animOut = AnimationUtils.loadAnimation(context, animOutId)
            val animIn = AnimationUtils.loadAnimation(context, animInId)

            animOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }
                override fun onAnimationEnd(animation: Animation) {
                    imageView.setImageBitmap(newImage)
                    imageView.startAnimation(animIn)
                }
                override fun onAnimationRepeat(animation: Animation?) { }
            })
            imageView.startAnimation(animOut)
        }
    }

    private fun getGestureDetector(): GestureDetector {
        return GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 != null) {
                    // Свайп влево
                    if (e1.x - e2.x > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD && pdfRenderer != null) {
                        if (newPageIndex + 1 < pdfRenderer!!.pageCount) {
                            newPageIndex++
                            file?.let { displayPdf(SwipeDirection.LEFT) }
                        }
                        return true
                    }
                    // Свайп вправо
                    else if (e2.x - e1.x > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (newPageIndex > 0) {
                            newPageIndex--
                            file?.let { displayPdf(SwipeDirection.RIGHT) }
                        }
                        return true
                    }
                }
                return false
            }
        })
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