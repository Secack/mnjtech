package su.akari.mnjtech.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.akari.mnjtech.PR
import su.akari.mnjtech.data.dao.AppDataBase
import su.akari.mnjtech.data.model.jwgl.Course
import su.akari.mnjtech.data.model.jwgl.CourseItem
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.ui.theme.toHex
import su.akari.mnjtech.util.DataStateFlow
import su.akari.mnjtech.util.collectAsStateFlow
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class IndexViewModel(
    private val njtechRepo: NjtechRepo, dataBase: AppDataBase
) : ViewModel() {
    private val courseDao = dataBase.courseDao()
    var localCurriculum = mutableStateListOf<CourseItem>()
    var localCurriculumFetched by mutableStateOf(false)
    val curriculum = DataStateFlow<List<CourseItem>>()
    val logoutFlow = DataStateFlow<Unit>()
    val startDate = PR.startDate.flow
        .filterNotNull()
        .map { Instant.ofEpochMilli(it).atZone(ZoneOffset.ofHours(8)).toLocalDate() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            courseDao.getAllCoursesFlow().collectLatest { resp ->
                if (resp.isNotEmpty()) {
                    localCurriculum.clear()
                    localCurriculum.addAll(
                        resp.map {
                            CourseItem(
                                name = it.name,
                                venue = it.venue,
                                weeks = it.weekStart..it.weekEnd,
                                dayOfWeek = it.dayOfWeek,
                                sections = it.sectionStart..it.sectionEnd,
                                teacher = it.teacher,
                                color = Color(it.color.toLong(16))
                            )
                        }
                    )
                }
                localCurriculumFetched = true
            }
        }
    }

    fun queryCurriculum(year: Int, term: Profile.Term) {
        viewModelScope.launch {
            njtechRepo.getCurriculum(year, term.value)
                .collectAsStateFlow(viewModelScope, curriculum)
        }
    }

    fun saveCurriculum(courses: List<CourseItem>, week: Int) {
        viewModelScope.launch {
            PR.startDate.set(
                LocalDate.now().minusWeeks(week.dec().toLong()).with(DayOfWeek.MONDAY)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.ofHours(8)).toEpochMilli()
            )
            courseDao.deleteAllCourses()
            courseDao.insertCourses(
                courses.map {
                    Course(
                        name = it.name,
                        venue = it.venue,
                        weekStart = it.weeks.first,
                        weekEnd = it.weeks.last,
                        dayOfWeek = it.dayOfWeek,
                        sectionStart = it.sections.first,
                        sectionEnd = it.sections.last,
                        teacher = it.teacher,
                        color = it.color.toHex()
                    )
                }
            )
        }
    }

    fun logout() =
        njtechRepo.logout().collectAsStateFlow(CoroutineScope(Dispatchers.IO), logoutFlow)
}