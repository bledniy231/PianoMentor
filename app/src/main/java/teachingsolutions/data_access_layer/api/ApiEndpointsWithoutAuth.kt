package teachingsolutions.data_access_layer.api

enum class ApiEndpointsWithoutAuth(val value: String) {
    AUTH("api/ApplicationUser/Login"),
    REGISTER("api/ApplicationUser/Register")
}