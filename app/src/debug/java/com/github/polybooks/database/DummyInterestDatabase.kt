package com.github.polybooks.database

import com.github.polybooks.core.*
import java.util.concurrent.CompletableFuture

/**
 * !! DO NOT INSTANTIATE THIS CLASS. You should use Database.interestDatabase instead.
 * */
object DummyInterestDatabase: InterestDatabase {


    val mockFields: List<Field> = listOf(
        Field("Biology"),
        Field("Computer Science"),
        Field("Architecture")
    )

    val mockCourses: List<Course> = listOf(
        Course("COM-101"),
        Course("CS-306"),
        Course("CS-323"),
        Course("EE-280"),
        Course("MSE-210"),
        Course("HUM-201"),
        Course("DH-405"),
        Course("ENV-444"),
        Course("MICRO-511")
    )

    val mockSemesters: List<Semester> = listOf(

        Semester("IN", "BA1"),
        Semester("SV", "BA1"),
        Semester("GC", "MA2"),
        Semester("SC", "BA6"),
        Semester("MT", "BA2"),
        Semester("MX", "BA3"),
        Semester("AR", "MA1"),
        Semester("CD", "BA4"),
        Semester("ENV", "BA5")
    )

    /**
     * Add a new field document to the fields collection
     */
    override fun addField(field: Field): CompletableFuture<Field> {
        TODO("Not yet implemented")
    }

    /**
     * Add a new semester document to the semesters collection
     */
    override fun addSemester(semester: Semester): CompletableFuture<Semester> {
        TODO("Not yet implemented")
    }

    /**
     * Add a new course document to the courses collection
     */
    override fun addCourse(course: Course): CompletableFuture<Course> {
        TODO("Not yet implemented")
    }

    override fun listAllFields(): CompletableFuture<List<Field>> {
        return CompletableFuture.supplyAsync {
            mockFields
        }
    }

    override fun listAllSemesters(): CompletableFuture<List<Semester>> {
        return CompletableFuture.supplyAsync {
            mockSemesters
        }
    }

    override fun listAllCourses(): CompletableFuture<List<Course>> {
        return CompletableFuture.supplyAsync {
            mockCourses
        }
    }

    override fun getUserInterests(user: User): CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>> {
        return CompletableFuture.supplyAsync{
            Triple(mockFields.subList(0,1), mockSemesters.subList(1,3), mockCourses.subList(0,4))
        }
    }

    override fun setUserInterests(user: User, interests: List<Interest>): CompletableFuture<Unit> {
        return CompletableFuture.supplyAsync{Unit}
    }
}