package teachingsolutions.domain_layer.courses

import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.courses.CoursesDataSource
import teachingsolutions.domain_layer.common.FileStorageManager
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemModel
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemType
import teachingsolutions.domain_layer.mapping_models.courses.CourseModel
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemsResultUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CoursesResultUI
import teachingsolutions.presentation_layer.fragments.lecture.model.LecturePdfResultUI
import java.io.File
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepository @Inject constructor(
    private val coursesDataSource: CoursesDataSource,
    private val fileStorageManager: FileStorageManager
) {
    private var coursesCached = emptyList<CourseModel>()
    private var coursesItemsCached = emptyList<CourseItemModel>()

    suspend fun getCourses(userId: Long): ActionResult<CoursesResultUI> {
        if (coursesCached.isNotEmpty()) return ActionResult.Success(CoursesResultUI(coursesCached.map {
            CourseModelUI(
                it.courseId,
                it.title,
                it.subtitle,
                it.description,
                it.progressInPercent
            )
        }, null))

        when (val result = coursesDataSource.getCourses(userId)) {
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

                return ActionResult.Success(CoursesResultUI(coursesCached.map {
                    CourseModelUI(
                        it.courseId,
                        it.title,
                        it.subtitle,
                        it.description,
                        it.progressInPercent
                    )
                }, null))
            }
            is ActionResult.NormalError -> {
                return ActionResult.NormalError(CoursesResultUI(null, result.data.errors?.joinToString { it } ?: "Error while getting courses"))
            }
            is ActionResult.ExceptionError -> {
                return ActionResult.ExceptionError(result.exception)
            }
            else -> return ActionResult.ExceptionError(Exception("OTHER_EX: No courses found or no internet connection"))
        }
    }

    suspend fun getCourseItems(userId: Long, courseId: Int): ActionResult<CourseItemsResultUI> {
        val neededCourseItemsFromCache = coursesItemsCached.filter { it.courseId == courseId }.sortedBy { it.position }
        if (neededCourseItemsFromCache.isNotEmpty()) {
            return ActionResult.Success(CourseItemsResultUI(neededCourseItemsFromCache.map {
                CourseItemModelUI(
                    it.courseId,
                    it.title,
                    it.courseItemType,
                    it.courseItemProgressType,
                    it.courseItemId
                )
            }, null))
        }

        when (val result = coursesDataSource.getCourseItems(userId, courseId)) {
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

                val sortedCourseItems = coursesItemsCached.filter { it.courseId == courseId }.sortedBy { it.position }
                return ActionResult.Success(CourseItemsResultUI(sortedCourseItems.map {
                    CourseItemModelUI(
                        it.courseId,
                        it.title,
                        it.courseItemType,
                        it.courseItemProgressType,
                        it.courseItemId
                    )
                }, null))
            }
            is ActionResult.NormalError -> {
                return ActionResult.NormalError(CourseItemsResultUI(null, result.data.errors?.joinToString { it } ?: "Error while getting course items"))
            }
            is ActionResult.ExceptionError -> {
                return ActionResult.ExceptionError(result.exception)
            }
            else -> return ActionResult.ExceptionError(Exception("OTHER_EX: No course items found or no internet connection"))
        }
    }

    suspend fun getLecturePdfFile(courseItemId: Int, courseName: String): ActionResult<LecturePdfResultUI> {
        val localFile = fileStorageManager.getLecturePdf(courseItemId, courseName)
        if (localFile != null) {
            val lecturePdfResultUI = LecturePdfResultUI(localFile, null)
            return ActionResult.Success(lecturePdfResultUI)
        }

        return when (val result = coursesDataSource.getLecturePdf(courseItemId)) {
            is ActionResult.Success -> {
                val file = fileStorageManager.saveLecturePdf(courseItemId, courseName, result.data)
                ActionResult.Success(LecturePdfResultUI(file, null))
            }
            is ActionResult.NormalError -> {
                ActionResult.NormalError(LecturePdfResultUI(null, result.data.string()))
            }
            is ActionResult.ExceptionError -> {
                ActionResult.ExceptionError(result.exception)
            }
        }
    }

    private fun getCourseItemType(courseItemTypeInString: String): CourseItemType {
        return try {
            CourseItemType.valueOf(courseItemTypeInString.uppercase(Locale.ROOT))
        } catch (e: Exception) {
            CourseItemType.LECTURE
        }
    }

    private fun getCourseItemProgressType(courseItemProgressTypeInString: String): CourseItemProgressType {
        return try {
            CourseItemProgressType.valueOf(courseItemProgressTypeInString.uppercase(Locale.ROOT))
        } catch (e: Exception) {
            CourseItemProgressType.NOT_STARTED
        }
    }

    fun deleteLecturePdfFile(courseItemId: Int, courseName: String) {
        fileStorageManager.deleteLecturePdf(courseItemId, courseName)
    }
}