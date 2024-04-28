package teachingsolutions.domain_layer.courses

import teachingsolutions.data_access_layer.common.ActionResult
import teachingsolutions.data_access_layer.courses.CoursesDataSource
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemModel
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemProgressType
import teachingsolutions.domain_layer.mapping_models.courses.CourseItemType
import teachingsolutions.domain_layer.mapping_models.courses.CourseModel
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CourseItemsResult
import teachingsolutions.presentation_layer.fragments.courses.model.CourseModelUI
import teachingsolutions.presentation_layer.fragments.courses.model.CoursesResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepository @Inject constructor(
    private val coursesDataSource: CoursesDataSource
) {
    private var coursesCached = emptyList<CourseModel>()
    private var coursesItemsCached = emptyList<CourseItemModel>()

    suspend fun getCourses(userId: Long): ActionResult<CoursesResult> {
        if (coursesCached.isNotEmpty()) return ActionResult.Success(CoursesResult(coursesCached.map {
            CourseModelUI(
                it.title,
                it.subtitle,
                it.description,
                it.progressInPercent
            )
        }, null))

        when (val result = coursesDataSource.getCourses(userId)) {
            is ActionResult.Success -> {
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

                return ActionResult.Success(CoursesResult(coursesCached.map {
                    CourseModelUI(
                        it.title,
                        it.subtitle,
                        it.description,
                        it.progressInPercent
                    )
                }, null))
            }
            is ActionResult.NormalError -> {
                return ActionResult.NormalError(CoursesResult(null, result.data.errors?.joinToString { it } ?: "Error while getting courses"))
            }
            is ActionResult.ExceptionError -> {
                return ActionResult.ExceptionError(result.exception)
            }
            else -> return ActionResult.ExceptionError(Exception("OTHER_EX: No courses found or no internet connection"))
        }
    }

    suspend fun getCourseItems(userId: Long, courseId: Int): ActionResult<CourseItemsResult> {
        if (coursesItemsCached.isNotEmpty()) return ActionResult.Success(CourseItemsResult(coursesItemsCached.map {
            CourseItemModelUI(
                it.title,
                it.courseItemType,
                it.courseItemProgressType
            )
        }, null))

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

                return ActionResult.Success(CourseItemsResult(coursesItemsCached.map {
                    CourseItemModelUI(
                        it.title,
                        it.courseItemType,
                        it.courseItemProgressType
                    )
                }, null))
            }
            is ActionResult.NormalError -> {
                return ActionResult.NormalError(CourseItemsResult(null, result.data.errors?.joinToString { it } ?: "Error while getting course items"))
            }
            is ActionResult.ExceptionError -> {
                return ActionResult.ExceptionError(result.exception)
            }
            else -> return ActionResult.ExceptionError(Exception("OTHER_EX: No course items found or no internet connection"))
        }
    }

    private fun getCourseItemType(courseItemTypeInString: String): CourseItemType {
        return try {
            CourseItemType.valueOf(courseItemTypeInString)
        } catch (e: Exception) {
            CourseItemType.LECTURE
        }
    }

    private fun getCourseItemProgressType(courseItemProgressTypeInString: String): CourseItemProgressType {
        return try {
            CourseItemProgressType.valueOf(courseItemProgressTypeInString)
        } catch (e: Exception) {
            CourseItemProgressType.NOT_STARTED
        }
    }
}