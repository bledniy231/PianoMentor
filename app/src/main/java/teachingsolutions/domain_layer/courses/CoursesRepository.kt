package teachingsolutions.domain_layer.courses

import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.courses.CoursesDataSource
import teachingsolutions.domain_layer.common.FileStorageManager
import teachingsolutions.domain_layer.domain_models.courses.CourseItemModel
import teachingsolutions.domain_layer.domain_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.domain_models.courses.CourseItemType
import teachingsolutions.domain_layer.domain_models.courses.CourseModel
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemsResultUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CoursesResultUI
import teachingsolutions.presentation_layer.fragments.common.FileResultUI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepository @Inject constructor(
    private val coursesDataSource: CoursesDataSource,
    private val fileStorageManager: FileStorageManager
) {
    private var coursesCached = emptyList<CourseModel>()
    private var coursesItemsCached = emptyList<CourseItemModel>()

    suspend fun getCourses(userId: Long): CoursesResultUI {
        if (coursesCached.isNotEmpty()) return CoursesResultUI(coursesCached.map {
            CourseModelUI(
                it.courseId,
                it.title,
                it.subtitle,
                it.description,
                it.progressInPercent
            )
        }, null)

        return when (val result = coursesDataSource.getCourses(userId)) {
            is ActionResult.Success -> {
                result.data.courses.sortBy { it.position }
                coursesCached = result.data.courses.map {
                    CourseModel(
                        it.courseId,
                        it.position,
                        it.title,
                        it.subtitle,
                        it.description,
                        it.progressInPercent
                    )
                }

                CoursesResultUI(coursesCached.map {
                    CourseModelUI(
                        it.courseId,
                        it.title,
                        it.subtitle,
                        it.description,
                        it.progressInPercent
                    )
                }, null)
            }
            is ActionResult.NormalError -> {
                CoursesResultUI(null, result.data.errors?.joinToString { it } ?: "Error while getting courses")
            }
            is ActionResult.ExceptionError -> {
                CoursesResultUI(null, result.exception.message ?: "Exception while getting courses")
            }
        }
    }

    suspend fun getCourseItems(userId: Long, courseId: Int, filter: CourseItemType? = null): CourseItemsResultUI {
        if (filter != null) {
            val neededCourseItemsFromCache = coursesItemsCached.filter { it.courseItemType == filter }.sortedBy { it.position }
            if (neededCourseItemsFromCache.isNotEmpty()) {
                return CourseItemsResultUI(neededCourseItemsFromCache.map {
                    CourseItemModelUI(
                        it.courseId,
                        it.title,
                        it.courseItemType,
                        it.courseItemProgressType,
                        it.courseItemId
                    )
                }, null)
            }
        } else {
            val neededCourseItemsFromCache = coursesItemsCached.filter { it.courseId == courseId }.sortedBy { it.position }
            if (neededCourseItemsFromCache.size == 10) {
                return CourseItemsResultUI(neededCourseItemsFromCache.map {
                    CourseItemModelUI(
                        it.courseId,
                        it.title,
                        it.courseItemType,
                        it.courseItemProgressType,
                        it.courseItemId
                    )
                }, null)
            }
        }

        val result = if (filter == null) {
            coursesDataSource.getCourseItems(userId, courseId)
        } else {
            coursesDataSource.getCourseItemsWithFilter(userId, filter)
        }

        return when (result) {
            is ActionResult.Success -> {
                coursesItemsCached = result.data.courseItems.map {
                    CourseItemModel(
                        it.courseItemId,
                        it.position,
                        it.title,
                        getCourseItemType(it.courseItemType),
                        getCourseItemProgressType(it.courseItemProgressType),
                        it.courseId
                    )
                }

                val sortedCourseItems = if (filter == null) {
                    coursesItemsCached.filter { it.courseId == courseId }.sortedBy { it.position }
                } else {
                    coursesItemsCached.filter { it.courseItemType == filter }.sortedBy { it.position }
                }
                CourseItemsResultUI(sortedCourseItems.map {
                    CourseItemModelUI(
                        it.courseId,
                        it.title,
                        it.courseItemType,
                        it.courseItemProgressType,
                        it.courseItemId
                    )
                }, null)
            }
            is ActionResult.NormalError -> {
                CourseItemsResultUI(null, result.data.errors?.joinToString { it } ?: "Error while getting courses")
            }
            is ActionResult.ExceptionError -> {
                CourseItemsResultUI(null, result.exception.message ?: "Exception while getting courses")
            }
        }
    }

    suspend fun getLecturePdfFile(courseItemId: Int, courseItemTitle: String): FileResultUI {
        val localFile = fileStorageManager.getLecturePdf(courseItemId, courseItemTitle)
        if (localFile != null) {
            return FileResultUI(localFile, null)
        }

        return when (val result = coursesDataSource.getCourseItemFile(courseItemId)) {
            is ActionResult.Success -> {
                val file = fileStorageManager.saveLecturePdf(courseItemId, courseItemTitle, result.data)
                FileResultUI(file, null)
            }
            is ActionResult.NormalError -> {
                FileResultUI(null, result.data.string())
            }
            is ActionResult.ExceptionError -> {
                FileResultUI(null, result.exception.message)
            }
        }
    }

    private fun getCourseItemType(courseItemTypeInString: String): CourseItemType {
        return try {
            CourseItemType.from(courseItemTypeInString)
        } catch (e: Exception) {
            CourseItemType.LECTURE
        }
    }

    private fun getCourseItemProgressType(courseItemProgressTypeInString: String): CourseItemProgressType {
        return try {
            CourseItemProgressType.from(courseItemProgressTypeInString)
        } catch (e: Exception) {
            CourseItemProgressType.NOT_STARTED
        }
    }

    fun deleteLecturePdfFile(courseItemId: Int, courseName: String) {
        fileStorageManager.deleteLecturePdf(courseItemId, courseName)
    }

    fun getCourseIdByCourseItemId(courseItemId: Int): Int {
        return coursesItemsCached.find { it.courseItemId == courseItemId }?.courseId ?: 0
    }

    fun setCourseItemProgress(courseId:Int, courseItemId: Int, courseItemProgressType: CourseItemProgressType) {
        coursesItemsCached.find { it.courseItemId == courseItemId }?.courseItemProgressType = courseItemProgressType
        if (courseItemProgressType == CourseItemProgressType.COMPLETED) {
            val neededCourse = coursesCached.find { it.courseId == courseId }
            val resultPercent = neededCourse?.progressInPercent?.plus((100 / coursesItemsCached.filter { it.courseId == courseId }.size))
            if (resultPercent != null) {
                neededCourse.progressInPercent = resultPercent
            }
        }
    }

    fun clearCache() {
        coursesCached = emptyList()
        coursesItemsCached = emptyList()
    }
}