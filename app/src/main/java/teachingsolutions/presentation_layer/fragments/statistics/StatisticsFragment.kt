package teachingsolutions.presentation_layer.fragments.statistics

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentStatisticsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.mapping_models.statistics.BaseStatisticsModel
import teachingsolutions.presentation_layer.adapters.MainMenuRecyclerViewAdapter
import teachingsolutions.presentation_layer.adapters.StatisticsViewPagerAdapter
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel
import teachingsolutions.presentation_layer.fragments.statistics.model.MainMenuItemModelUI
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsResultUI
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsViewPagerItemModelUI
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener


@AndroidEntryPoint
class StatisticsFragment : Fragment(),
    ISelectRecyclerViewItemListener<MainMenuItemModelUI> {
    companion object {
        fun newInstance() = StatisticsFragment()
    }

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.isShouldRemoveExpandedCorners = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshUserIfNeeds()

        val userStatisticsObserver = Observer<StatisticsResultUI?> { statResultUI ->
            statResultUI ?: return@Observer

            statResultUI.success?.let {
                initialStatisticsViewPager(it)
            }

            statResultUI.error?.let {
                val error = if (it == "Unauthorized") {
                    getString(R.string.login_for_statistics)
                } else {
                    it
                }
                updateUiWithStatisticsFailed(error)
                initialStatisticsViewPager(UserStatisticsModel(
                    listOf(
                        StatisticsViewPagerItemModelUI(0, 0, getString(R.string.tests_done), getString(R.string.zero_tests_done)),
                        StatisticsViewPagerItemModelUI(0, 0, getString(R.string.courses_done), getString(R.string.zero_courses_done))
                    ),
                    BaseStatisticsModel(0, 0, getString(R.string.exercise)),
                    BaseStatisticsModel(0, 0, getString(R.string.lectures)),
                    BaseStatisticsModel(0, 0, getString(R.string.introduction_course))
                ))
            }
        }

        val refreshingCheckedObserver = Observer<Boolean?> { isRefreshingChecked ->
            isRefreshingChecked ?: return@Observer

//            if (!isUserAvailable) {
//                Toast.makeText(context, "Войдите в аккаунт", Toast.LENGTH_LONG).show()
//            }

            initialMainMenuRecyclerView()
            viewModel.getUserStatistics()
            viewModel.userStatstics.observe(viewLifecycleOwner, userStatisticsObserver)
            binding.toolBarUserIconGoLogin.setOnClickListener {
                if (viewModel.isUserLoggedIn()) {
                    findNavController().navigate(R.id.action_choose_profile)
                } else {
                    findNavController().navigate(R.id.action_choose_register_or_login)
                }
            }

            viewModel.clearLiveData()
        }

        viewModel.isRefreshingChecked.observe(viewLifecycleOwner, refreshingCheckedObserver)
    }

    private fun initialStatisticsViewPager(statResult: UserStatisticsModel) {
        val statisticsViewPagerAdapter = context?.let { StatisticsViewPagerAdapter(it) }
        statisticsViewPagerAdapter?.setModelsList(statResult.statListViewPagerItems)
        binding.statViewPager.adapter = statisticsViewPagerAdapter
        binding.statViewPager.offscreenPageLimit = 2
        binding.statViewPager.isUserInputEnabled = true
        val middle = Integer.MAX_VALUE / 2
        val start = middle - (middle % (statisticsViewPagerAdapter?.itemCount ?: 1))
        binding.statViewPager.currentItem = start

        binding.previousViewpagerButton.setOnClickListener {
            val currentIndex = binding.statViewPager.currentItem

            if (currentIndex > 0)
            {
                binding.statViewPager.currentItem = currentIndex - 1
            }
            else
            {
                binding.statViewPager.currentItem = binding.statViewPager.adapter?.itemCount?.minus(1) ?: 0
            }
        }

        binding.nextViewpagerButton.setOnClickListener {
            val currentIndex = binding.statViewPager.currentItem
            val viewPagerItemCount = binding.statViewPager.adapter?.itemCount

            if (currentIndex < (viewPagerItemCount?.minus(1) ?: 0))
            {
                binding.statViewPager.currentItem++
            }
            else
            {
                binding.statViewPager.currentItem = 0
            }
        }

//        binding.exercisesCircleProgressBar.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_green, resources.newTheme())
//        binding.lecturesCircleProgressBar.progressDrawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_progress_bar_brown, resources.newTheme())

        val exerciseProgressAnim = viewModel.getObjectAnimator(binding.exercisesCircleProgressBar, statResult.exercisesProgressModel.progressValueInPercent)
        val exerciseTextAnim = viewModel.getValueAnimator(binding.exercisesCounterText, statResult.exercisesProgressModel.progressValueAbsolute)
        val lectureProgressAnim = viewModel.getObjectAnimator(binding.lecturesCircleProgressBar, statResult.lecturesProgressModel.progressValueInPercent)
        val lectureTextAnim = viewModel.getValueAnimator(binding.lecturesCounterText, statResult.lecturesProgressModel.progressValueAbsolute)
        val courseProgressAnim = viewModel.getObjectAnimator(binding.coursesLinearProgressBar, statResult.coursesProgressModel.progressValueInPercent)
        val courseTextAnim = viewModel.getValueAnimator(binding.coursesPercentText, statResult.coursesProgressModel.progressValueInPercent, R.string.percent)

        exerciseProgressAnim.start()
        exerciseTextAnim.start()
        lectureProgressAnim.start()
        lectureTextAnim.start()
        courseProgressAnim.start()
        courseTextAnim.start()

        binding.exercisesText.text = statResult.exercisesProgressModel.title
        binding.lecturesText.text = statResult.lecturesProgressModel.title
        binding.coursesNameText.text = statResult.coursesProgressModel.title
    }

    private fun initialMainMenuRecyclerView() {
        val mainMenuItemModels = viewModel.getMainMenuItems()

        val mainMenuRecyclerViewAdapter = MainMenuRecyclerViewAdapter(this)
        mainMenuRecyclerViewAdapter.setModelsList(mainMenuItemModels)
        binding.mainMenuRecyclerView.adapter = mainMenuRecyclerViewAdapter
    }

    override fun onItemSelected(itemModel: MainMenuItemModelUI) {
        when (itemModel.titleText) {
            "Курсы" -> {
                findNavController().navigate(R.id.action_choose_courses)
            }
            "Практические занятия" -> {
                //findNavController().navigate(R.id.action_statisticsFragment_to_practiceFragment)
            }
            "Лекции по теории" -> {
                //findNavController().navigate(R.id.action_statisticsFragment_to_theoryLecturesFragment)
            }
            "Тесты по теории" -> {
                //findNavController().navigate(R.id.action_statisticsFragment_to_theoryTestsFragment)
            }
            "Тренировка слуха" -> {
                //findNavController().navigate(R.id.action_statisticsFragment_to_hearingTrainingFragment)
            }
            "Анализатор звука" -> {
                //findNavController().navigate(R.id.action_statisticsFragment_to_soundAnalyzerFragment)
            }
        }
    }

    private fun updateUiWithStatisticsFailed(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}