package teachingsolutions.data_access_layer.DAL_models.user

data class ChangePasswordRequestApi(
    val userId: Long,
    val oldPassword: String,
    val newPassword: String,
    val repeatNewPassword: String
)