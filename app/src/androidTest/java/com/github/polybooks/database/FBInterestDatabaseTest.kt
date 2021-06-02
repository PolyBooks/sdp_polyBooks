package com.github.polybooks.database

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.*
import org.junit.*
import org.junit.Assert.*
import org.junit.Test


class FBInterestDatabaseTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val interestDB = FBInterestDatabase()

    private val testUser = LoggedUser("301966", "Le givre")


    @Test
    fun addTopicsAndRetrieveThem() {
        val testTopic1 = Topic("Biology")
        val testTopic2 = Topic("Computer Science")
        val testTopic3 = Topic("Architecture")

        val addedTopic1 = interestDB.addTopic(testTopic1)
        val addedTopic2 = interestDB.addTopic(testTopic2)
        val addedTopic3 = interestDB.addTopic(testTopic3)

        assertNotNull(addedTopic1.get())
        assertNotNull(addedTopic2.get())
        assertNotNull(addedTopic3.get())
        assertEquals(testTopic1, addedTopic1.get())
        assertEquals(testTopic2, addedTopic2.get())
        assertEquals(testTopic3, addedTopic3.get())

        val retrievedTopics = interestDB.listAllTopics().get()
        val expectedTopics: List<Topic> = listOf(testTopic1, testTopic2, testTopic3)
        assertNotNull(retrievedTopics)
        assertTrue(retrievedTopics.containsAll(expectedTopics))
        assertTrue(expectedTopics.containsAll(retrievedTopics))

    }

    @Test
    fun addSemestersAndRetrieveThem() {
        val testSemester1 = Semester("IN", "BA1")
        val testSemester2 = Semester("ENV", "BA5")
        val testSemester3 = Semester("SC", "BA6")

        val addedSemester1 = interestDB.addSemester(testSemester1)
        val addedSemester2 = interestDB.addSemester(testSemester2)
        val addedSemester3 = interestDB.addSemester(testSemester3)

        assertNotNull(addedSemester1.get())
        assertNotNull(addedSemester2.get())
        assertNotNull(addedSemester3.get())
        assertEquals(testSemester1, addedSemester1.get())
        assertEquals(testSemester2, addedSemester2.get())
        assertEquals(testSemester3, addedSemester3.get())

        val retrievedSemesters = interestDB.listAllSemesters().get()
        val expectedSemesters: List<Semester> = listOf(testSemester1, testSemester2, testSemester3)
        assertNotNull(retrievedSemesters)
        assertTrue(retrievedSemesters.containsAll(expectedSemesters))
        assertTrue(expectedSemesters.containsAll(retrievedSemesters))
    }

    @Test
    fun addCoursesAndRetrieveThem() {
        val testCourse1 = Course("COM-101")
        val testCourse2 = Course("CS-306")
        val testCourse3 = Course("CS-323")

        val addedCourse1 = interestDB.addCourse(testCourse1)
        val addedCourse2 = interestDB.addCourse(testCourse2)
        val addedCourse3 = interestDB.addCourse(testCourse3)

        assertNotNull(addedCourse1.get())
        assertNotNull(addedCourse2.get())
        assertNotNull(addedCourse3.get())
        assertEquals(testCourse1, addedCourse1.get())
        assertEquals(testCourse2, addedCourse2.get())
        assertEquals(testCourse3, addedCourse3.get())

        val retrievedCourses = interestDB.listAllCourses().get()
        val expectedCourses: List<Course> = listOf(testCourse1, testCourse2, testCourse3)
        assertNotNull(retrievedCourses)
        assertTrue(retrievedCourses.containsAll(expectedCourses))
        assertTrue(expectedCourses.containsAll(retrievedCourses))
    }


    private val userInterestsList: List<Interest> = listOf(
        Semester("IN", "BA1"),
        Course("COM-101"),
        Course("CS-306"),
        Course("CS-323"),
        Semester("SC", "BA6"),
        Topic("Computer Science")
    )



    @Test
    fun addAndRetrieveUserInterestsForLoggedInUser() {
        val newUserInterest = interestDB.setLoggedUserInterests(testUser, userInterestsList)

        assertNotNull(newUserInterest.get())
        assertTrue(newUserInterest.get().containsAll(userInterestsList))
        assertTrue(userInterestsList.containsAll(newUserInterest.get()))

        val retrievedUserInterests = interestDB.getLoggedUserInterests(testUser)
        assertNotNull(retrievedUserInterests.get())
        assertTrue(newUserInterest.get().containsAll(retrievedUserInterests.get()))
        assertTrue(retrievedUserInterests.get().containsAll(newUserInterest.get()))
    }

    @Test
    fun multipleSetGetForUserInterestsForLoggedInUser() {
        val newUserInterest = interestDB.setLoggedUserInterests(testUser, userInterestsList)

        assertNotNull(newUserInterest.get())
        assertTrue(newUserInterest.get().containsAll(userInterestsList))
        assertTrue(userInterestsList.containsAll(newUserInterest.get()))

        val retrievedUserInterests = interestDB.getLoggedUserInterests(testUser)
        assertNotNull(retrievedUserInterests.get())
        assertTrue(newUserInterest.get().containsAll(retrievedUserInterests.get()))
        assertTrue(retrievedUserInterests.get().containsAll(newUserInterest.get()))

        // After that, it should contain only the new interest, not combined with the previous list.
        val newSingleInterest = listOf(Semester("ENV", "BA5"))
        val newUserInterest2 = interestDB.setLoggedUserInterests(testUser, newSingleInterest)

        assertNotNull(newUserInterest2.get())
        assertTrue(newUserInterest2.get().containsAll(newSingleInterest))
        assertTrue(newSingleInterest.containsAll(newUserInterest2.get()))

        val retrievedUserInterests2 = interestDB.getLoggedUserInterests(testUser)
        assertNotNull(retrievedUserInterests2.get())
        assertTrue(newSingleInterest.containsAll(retrievedUserInterests2.get()))
        assertTrue(retrievedUserInterests2.get().containsAll(newSingleInterest))
    }


}