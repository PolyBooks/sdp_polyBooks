package com.github.polybooks.database

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.activities.MainActivity
import com.github.polybooks.core.Field
import com.github.polybooks.core.LoggedUser
import org.junit.*
import org.junit.Assert.*

class FBInterestDatabaseTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val firestore = FirebaseProvider.getFirestore()
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
        Intents.release()
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

    }

    @Test
    fun addCoursesAndRetrieveThem() {

    }



}