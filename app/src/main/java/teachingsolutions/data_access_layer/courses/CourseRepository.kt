package teachingsolutions.data_access_layer.courses

import javax.inject.Inject

class CourseRepository @Inject constructor(
    private val coursesDataSource: CoursesDataSource
) {


}