package teachingsolutions.presentation_layer.fragments.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import teachingsolutions.presentation_layer.fragments.data.model.MainMenuRecyclerViewItemModel
import teachingsolutions.presentation_layer.fragments.data.model.StatisticsViewPagerItemModel


class StatisticsFragment : Fragment() {

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

        initialStatisticsViewPager()
        initialMainMenuRecyclerView()

        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.isShouldRemoveExpandedCorners = false
        /*val h = getScreenHeightInDp()
        if (h - 330 <= 350)
        {
            binding.statisticsRootViewGroup.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            //behavior.setPeekHeight(h - 350)
        }
        else
        {
            binding.statisticsRootViewGroup.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, h - 330)
        }*/
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBarUserIconGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_choose_register_or_login)
        }
    }

    private fun initialStatisticsViewPager() {
        val statisticsList: List<StatisticsViewPagerItemModel> = listOf(
            StatisticsViewPagerItemModel(10, "Выполнено тестов", "Вы прошли 1 тест по теории, продолжайте в том же духе"),
            StatisticsViewPagerItemModel(33, "Завершено курсов", "Вы завершили 1 курс, это большой шаг!")
        )

        val statisticsViewPagerAdapter = context?.let { StatisticsViewPagerAdapter(it) }
        statisticsViewPagerAdapter?.setModelsList(statisticsList)
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

        binding.exercisesCircleProgressBar.progress = 90
        binding.exercisesCounterText.text = "21"
        binding.exercisesText.text = "Упражнение"

        binding.lecturesCircleProgressBar.progress = 33
        binding.lecturesCounterText.text = "1"
        binding.lecturesText.text = "Лекция"

        binding.coursesLinearProgressBar.progress = 25
        binding.coursesPercentText.text = "25%"
        binding.coursesNameText.text = "Курс 'Интервалы'"
    }

    private fun initialMainMenuRecyclerView() {
        val mainMenuItemModels = listOf(
            MainMenuRecyclerViewItemModel(R.drawable.icon_cources, "Курсы"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_practice, "Практические занятия"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_lectures, "Лекции по теории"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_lectures_tests, "Тесты по теории"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_hearing, "Тренировка слуха"),
            MainMenuRecyclerViewItemModel(R.drawable.icon_sound_analyze, "Анализатор звука")
        )

        val mainMenuRecyclerViewAdapter = MainMenuRecyclerViewAdapter()
        mainMenuRecyclerViewAdapter.setModelsList(mainMenuItemModels)
        binding.mainMenuRecyclerView.adapter = mainMenuRecyclerViewAdapter
    }
}