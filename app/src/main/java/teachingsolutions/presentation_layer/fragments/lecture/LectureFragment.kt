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
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentLectureBinding
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
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
    private lateinit var args: Bundle

    private val viewModel: LectureViewModel by viewModels()
    private var popupMenu: PopupMenu? = null

    private var fileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var file: File? = null
    private var gestureDetector: GestureDetector? = null

    private var lectureAnimation: LectureAnimation = LectureAnimation.NONE
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    private var currentPageIndex = 0
    private var isEndedReading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLectureBinding.inflate(inflater, container, false)
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

        val courseItemId = args.getInt("CourseItemId")
        val courseItemTitle = args.getString("CourseItemTitle")
        if (courseItemId > 0 && !courseItemTitle.isNullOrEmpty()) {
            //viewModel.deleteLecturePdf(courseItemId, courseName)
            viewModel.getLecturePdf(courseItemId, courseItemTitle)
        } else {
            Toast.makeText(requireContext(), "FAIL: Incorrect arguments", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        isEndedReading = args.getString("CourseItemProgressType") == CourseItemProgressType.COMPLETED.value
        binding.lectureToolbar.title = args.getString("CourseItemTitle")
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
            if (currentPageIndex + 1 < (pdfRenderer?.pageCount ?: 0)) {
                currentPageIndex++
                file?.let { displayPdf(SwipeDirection.RIGHT) }
            }
        }

        binding.buttonPrevious.setOnClickListener {
            if (currentPageIndex > 0) {
                currentPageIndex--
                file?.let { displayPdf(SwipeDirection.LEFT) }
            }
        }

        viewModel.lecturePdfResult.observe(viewLifecycleOwner) { lecturePdfResultUI ->
            lecturePdfResultUI.success?.let { file ->
                this.file = file
                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                if (fileDescriptor == null) {
                    Toast.makeText(requireContext(), "FAIL: File descriptor is null", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                pdfRenderer = PdfRenderer(fileDescriptor!!)
                displayPdf(SwipeDirection.NONE)
            }
            lecturePdfResultUI.error?.let { error ->
                Toast.makeText(requireContext(), "FAIL: $error", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }

        binding.lectureToolbar.setNavigationOnClickListener {
            onExit(courseItemId)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit(courseItemId)
            }
        })

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

        if (args.getString("CourseItemProgressType") == CourseItemProgressType.NOT_STARTED.value) {
            viewModel.setLectureProgress(courseItemId, CourseItemProgressType.IN_PROGRESS)
        }
    }

    private fun displayPdf(direction: SwipeDirection) {
        val pageCount = pdfRenderer?.pageCount
        val currentPage = pdfRenderer?.openPage(currentPageIndex)
        if (pageCount != null && currentPageIndex == pageCount - 1) {
            isEndedReading = true
        }

        val bitmap = Bitmap.createBitmap(currentPage?.width ?: 0, currentPage?.height ?: 0, Bitmap.Config.ARGB_8888)
        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        if (lectureAnimation == LectureAnimation.NONE) {
            binding.pdfView.setImageBitmap(bitmap)
        }
        else {
            imageViewAnimatedChange(requireContext(), binding.pdfView, bitmap.copy(bitmap.config, true), direction)
        }

        binding.pageCounter.text = getString(R.string.page_counter, currentPageIndex + 1, pageCount)
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
                        if (currentPageIndex + 1 < pdfRenderer!!.pageCount) {
                            currentPageIndex++
                            file?.let { displayPdf(SwipeDirection.LEFT) }
                        }
                        return true
                    }
                    // Свайп вправо
                    else if (e2.x - e1.x > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (currentPageIndex > 0) {
                            currentPageIndex--
                            file?.let { displayPdf(SwipeDirection.RIGHT) }
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun onExit(courseItemId: Int) {
        val shouldWaitResult = checkIfLectureCompleted(courseItemId)
        if (shouldWaitResult) {
            viewModel.setLectureProgressResult.observe(viewLifecycleOwner) { defResponse ->
                    defResponse.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                    findNavController().popBackStack()
                }
        }
        else {
            findNavController().popBackStack()
        }
    }

    private fun checkIfLectureCompleted(courseItemId: Int): Boolean {
        return if (isEndedReading && args.getString("CourseItemProgressType") != CourseItemProgressType.COMPLETED.value) {
            viewModel.setLectureProgress(courseItemId, CourseItemProgressType.COMPLETED)
            true
        } else {
            false
        }
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