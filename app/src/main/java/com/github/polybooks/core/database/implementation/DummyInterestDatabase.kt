package com.github.polybooks.core.database.implementation

import com.github.polybooks.core.*
import com.github.polybooks.core.database.InterestDatabase
import com.github.polybooks.utils.mockCourses
import com.github.polybooks.utils.mockFields
import com.github.polybooks.utils.mockSemesters
import java.util.concurrent.CompletableFuture

class DummyInterestDatabase : InterestDatabase {

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
        TODO("Not yet implemented")
    }

    override fun setUserInterests(user: User, interests: List<Interest>): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }
}