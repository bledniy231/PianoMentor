package teachingsolutions.presentation_layer.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentStatisticsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.presentation_layer.adapters.MainMenuRecyclerViewAdapter
import teachingsolutions.presentation_layer.adapters.StatisticsViewPagerAdapter
import teachingsolutions.domain_layer.domain_models.statistics.UserStatisticsModel
import teachingsolutions.presentation_layer.extensions.safelyNavigate
import teachingsolutions.presentation_layer.fragments.statistics.model.MainMenuItemModelUI
import teachingsolutions.presentation_layer.fragments.statistics.model.StatisticsResultUI
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

    private val userStatisticsObserver = Observer<StatisticsResultUI?> { statResultUI ->
        statResultUI ?: return@Observer

        statResultUI.success?.let {
            fillStatistics(it)
        }

        statResultUI.error?.let {
            val error = if (it == "Unauthorized") {
                getString(R.string.login_for_statistics)
            } else {
                it
            }
            updateUiWithStatisticsFailed(error)
            fillStatistics(viewModel.getDefaultStatistics(requireContext()))
        }
    }

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

        val refreshingCheckedObserver = Observer<Boolean?> { isRefreshingChecked ->
            isRefreshingChecked ?: return@Observer

            initialMainMenuRecyclerView()
            disposeStatistics()
            viewModel.getUserStatistics()
            viewModel.userStatstics.observe(viewLifecycleOwner, userStatisticsObserver)
            binding.toolBarUserIconGoLogin.setOnClickListener {
                if (viewModel.isUserLoggedIn()) {
                    findNavController().safelyNavigate(R.id.action_choose_profile)
                } else {
                    findNavController().safelyNavigate(R.id.action_choose_register_or_login)
                }
            }

            viewModel.clearLiveData()
        }

        viewModel.isRefreshingChecked.observe(viewLifecycleOwner, refreshingCheckedObserver)

        binding.pianoIv.setOnClickListener {
            findNavController().navigate(R.id.action_global_pianoFragment)
        }
        binding.profileIv.setOnClickListener {
            findNavController().navigate(R.id.action_global_profileFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        disposeStatistics()
    }

    private fun fillStatistics(statResult: UserStatisticsModel) {
        val statisticsViewPagerAdapter = context?.let { StatisticsViewPagerAdapter(it) }
        statisticsViewPagerAdapter?.setModelsList(statResult.statListViewPagerItems)
        binding.statViewPager.adapter = statisticsViewPagerAdapter
        binding.statViewPager.offscreenPageLimit = 2
        binding.statViewPager.isUserInputEnabled = false
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

    private fun disposeStatistics() {
        if (!viewModel.isUserLoggedIn()) {
            fillStatistics(viewModel.getDefaultStatistics(requireContext()))
        }
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