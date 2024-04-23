package teachingsolutions.presentation_layer.fragments.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import teachingsolutions.data_access_layer.login_registration.UserRepository
import teachingsolutions.presentation_layer.fragments.courses.CoursesViewModel
import teachingsolutions.presentation_layer.fragments.login.LoginViewModel
import teachingsolutions.presentation_layer.fragments.registration.RegistrationViewModel
import teachingsolutions.presentation_layer.fragments.statistics.StatisticsViewModel

//class ViewModelsFactory : ViewModelProvider.Factory {
//
//    private val userRepository = UserRepository()
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
//                LoginViewModel(loginRegisterRepository = userRepository) as T
//            }
//            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
//                RegistrationViewModel(loginRegisterRepository = userRepository) as T
//            }
//            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
//                StatisticsViewModel() as T
//            }
//            modelClass.isAssignableFrom(CoursesViewModel::class.java) -> {
//                CoursesViewModel() as T
//            }
//            else -> throw IllegalArgumentException("Unknown ViewModel class")
//        }
//    }
//}