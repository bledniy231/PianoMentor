package teachingsolutions.presentation_layer.fragments.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pianomentor.R
import com.example.pianomentor.databinding.FragmentStatisticsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import teachingsolutions.presentation_layer.adapters.MainMenuRecyclerViewAdapter
import teachingsolutions.presentation_layer.adapters.StatisticsViewPagerAdapter
import teachingsolutions.presentation_layer.fragments.data.main_menu.model.MainMenuRecyclerViewItemModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.StatisticsViewPagerItemModel
import teachingsolutions.presentation_layer.fragments.data.statistics.model.UserStatisticsModel
import teachingsolutions.presentation_layer.fragments.ui.common.ViewModelsFactory
import teachingsolutions.presentation_layer.interfaces.ISelectRecyclerViewItemListener


class StatisticsFragment : Fragment(),
    ISelectRecyclerViewItemListener<MainMenuRecyclerViewItemModel> {
    companion object {
        fun newInstance() = StatisticsFragment()
    }

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StatisticsViewModel

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
        viewModel = ViewModelProvider(this, ViewModelsFactory())[StatisticsViewModel::class.java]
        initialStatisticsViewPager()
        initialMainMenuRecyclerView()
        binding.toolBarUserIconGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_choose_register_or_login)
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

    override fun onItemSelected(itemModel: MainMenuRecyclerViewItemModel) {
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
}