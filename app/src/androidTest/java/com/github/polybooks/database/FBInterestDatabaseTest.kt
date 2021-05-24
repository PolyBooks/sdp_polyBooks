package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.LoggedUser
import com.github.polybooks.core.Semester
import org.junit.*
import org.junit.Assert.*

class FBInterestDatabaseTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    //private val firestore = FirebaseProvider.getFirestore()
    private val interestDB = Database.interestDatabase

    private val testUser = LoggedUser(301966, "Le givre")

    @Before
    fun setUp() {
        /*firestore.initializeTestApp({
            projectId: "PolyBooks",
            auth: { uid: "alice", email: "alice@example.com" }
        })*/
    }

    @After
    fun cleanUp() {
        //Intents.release()
    }


    @Test
    fun addFieldsAndRetrieveThem() {
        val testField1 = Field("Biology")
        val testField2 = Field("Computer Science")
        val testField3 = Field("Architecture")

        val addedField1 = interestDB.addField(testField1)
        val addedField2 = interestDB.addField(testField2)
        val addedField3 = interestDB.addField(testField3)

        assertNotNull(addedField1.get())
        assertNotNull(addedField2.get())
        assertNotNull(addedField3.get())
        assertEquals(testField1, addedField1.get())
        assertEquals(testField2, addedField2.get())
        assertEquals(testField3, addedField3.get())

        val retrievedFields = interestDB.listAllFields().get()
        val expectedFields: List<Field> = listOf(testField1, testField2, testField3)
        assertNotNull(retrievedFields)
        assertTrue(retrievedFields.containsAll(expectedFields))
        assertTrue(expectedFields.containsAll(retrievedFields))

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



}