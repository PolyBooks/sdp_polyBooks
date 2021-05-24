package com.github.polybooks.adapter

import android.content.Context
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.database.Database
import com.github.polybooks.database.DummyInterestDatabase
import com.github.polybooks.utils.StringsManip.getName
import com.github.polybooks.utils.StringsManip.mergeSectionAndSemester

/**
 * An adapter when filtering by Interest, which is a dynamic list of filtering values
 *
 * @param itemViewId   view id in the xml layout of a value item
 * @see   ParameterAdapter
 */
class InterestsParameterAdapter<T: Interest>(
    itemViewId: Int,
    private val interestType: Interest
):
    ParameterAdapter<T>(
        itemViewId,
        listOf()
    ) {
    init {
        val setV = { values: Any -> setValues(values as List<T>) }
        when (interestType) {
            Interest.COURSE -> Database.interestDatabase.listAllCourses().thenAccept(setV)
            Interest.FIELD -> Database.interestDatabase.listAllFields().thenAccept(setV)
            Interest.SEMESTER -> Database.interestDatabase.listAllSemesters().thenAccept(setV)
        }
    }

    enum class Interest {
        COURSE, FIELD, SEMESTER
    }

    override fun getItemViewType(): Int {
        return VIEW_TYPE_CHECKBOX
    }

    override fun getValueName(value: T, context: Context?): String {
        return getName(value as com.github.polybooks.core.Interest)
    }
}