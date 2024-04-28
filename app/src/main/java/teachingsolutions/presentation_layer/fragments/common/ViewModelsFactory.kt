package teachingsolutions.presentation_layer.fragments.common

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