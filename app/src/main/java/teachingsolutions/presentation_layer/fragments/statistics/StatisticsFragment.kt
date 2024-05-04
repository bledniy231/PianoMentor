package teachingsolutions.presentation_layer.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentStatisticsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import teachingsolutions.presentation_layer.adapters.MainMenuRecyclerViewAdapter
import teachingsolutions.presentation_layer.adapters.StatisticsViewPagerAdapter
import teachingsolutions.domain_layer.mapping_models.statistics.UserStatisticsModel
import teachingsolutions.presentation_layer.fragments.statistics.model.MainMenuItemModelUI
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
        lifecycleScope.launch {
            val isUserAvailable = viewModel.isUserStillAvailable()
            if (!isUserAvailable) {
                Toast.makeText(context, "Войдите в аккаунт", Toast.LENGTH_SHORT).show()
            }
        }

        initialStatisticsViewPager()
        initialMainMenuRecyclerView()
        binding.toolBarUserIconGoLogin.setOnClickListener {
            if (viewModel.isUserLoggedIn()) {
                findNavController().navigate(R.id.action_choose_profile)
            } else {
                findNavController().navigate(R.id.action_choose_register_or_login)
            }
        }
    }

    private fun initialStatisticsViewPager() {
        val userStatistics: UserStatisticsModel = viewModel.getUserStatistics()

        val statisticsViewPagerAdapter = context?.let { StatisticsViewPagerAdapter(it) }
        statisticsViewPagerAdapter?.setModelsList(userStatistics.statListViewPagerItems)
        binding.statViewPager.adapter = statisticsViewPagerAdapter
        binding.statViewPager.offscreenPageLimit = 2
        binding.statViewPager.isUserInputEnabled = false

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

        binding.exercisesCircleProgressBar.progress = userStatistics.exercisesProgressModel.progressValueInPercent
        binding.exercisesCounterText.text = userStatistics.exercisesProgressModel.progressValueAbsolute.toString()
        binding.exercisesText.text = userStatistics.exercisesProgressModel.text

        binding.lecturesCircleProgressBar.progress = userStatistics.lecturesProgressModel.progressValueInPercent
        binding.lecturesCounterText.text = userStatistics.lecturesProgressModel.progressValueAbsolute.toString()
        binding.lecturesText.text = userStatistics.lecturesProgressModel.text

        binding.coursesLinearProgressBar.progress = userStatistics.coursesProgressModel.progressValueInPercent
        binding.coursesPercentText.text = "${userStatistics.coursesProgressModel.progressValueInPercent.toString()}%"
        binding.coursesNameText.text = userStatistics.coursesProgressModel.text
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}