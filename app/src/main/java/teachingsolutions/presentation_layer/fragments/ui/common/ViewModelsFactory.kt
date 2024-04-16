package teachingsolutions.presentation_layer.fragments.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import teachingsolutions.presentation_layer.fragments.data.login_registration.LoginRegistrationDataSource
import teachingsolutions.presentation_layer.fragments.data.login_registration.LoginRegistrationRepository
import teachingsolutions.presentation_layer.fragments.ui.courses.CoursesViewModel
import teachingsolutions.presentation_layer.fragments.ui.login.LoginViewModel
import teachingsolutions.presentation_layer.fragments.ui.registration.RegistrationViewModel
import teachingsolutions.presentation_layer.fragments.ui.statistics.StatisticsViewModel

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class ViewModelsFactory : ViewModelProvider.Factory {

    private val loginRegistrationRepository = LoginRegistrationRepository(
        dataSource = LoginRegistrationDataSource()
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(loginRegistrationRepository = loginRegistrationRepository) as T
            }
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                RegistrationViewModel(loginRegisterRepository = loginRegistrationRepository) as T
            }
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel() as T
            }
            modelClass.isAssignableFrom(CoursesViewModel::class.java) -> {
                CoursesViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}