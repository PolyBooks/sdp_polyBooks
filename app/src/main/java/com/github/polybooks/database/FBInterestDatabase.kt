package com.github.polybooks.database

import com.github.polybooks.core.*
import com.github.polybooks.utils.StringsManip.mergeSectionAndSemester
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestoreSettings
import java.util.concurrent.CompletableFuture

private const val TAG: String = "FBInterestDatabase"

// Names of the collections in Firestore
private const val fieldCollection: String = "fieldInterest"
private const val semesterCollection: String = "semesterInterest"
private const val courseCollection: String = "courseInterest"

/**
 * !! DO NOT INSTANTIATE THIS CLASS. Instead use Database.interestDatabase to access it !!
 * The chosen structure for firebase is one collection for field, one for semester and one for courses.
 * Each of them will hold documents whose attribute is the name of the interest.
 * It might seem unnecessary to have 3 root level collections for interests,
 * but it is by far the best option if we potentially want each interest to hold the list of books and user associated with it
 * As each document will be able to have a book collection and a user collection.
 * Using snapshotListener here does not feel necessary as interests are rarely changing.
 */
object FBInterestDatabase: InterestDatabase {

    /**
     * get the singleton instance of FBInterestDatabase
     * but also enable the cache
     */
    fun getInstance(): InterestDatabase {
        val settings: FirebaseFirestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
        FirebaseProvider.getFirestore().firestoreSettings = settings
        return this
    }



    /**
     * Add a new field document to the fields collection
     */
    override fun addField(field: Field) : CompletableFuture<Field> {
        val future = CompletableFuture<Field>()

        FirebaseProvider.getFirestore()
            .collection(fieldCollection)
            .document(field.name)
            .set(field, SetOptions.merge())
            .addOnSuccessListener { future.complete(field) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Failed to insert ${field.name} into Database because of: $it")) }


        return future
    }

    /**
     * Add a new semester document to the semesters collection
     */
    override fun addSemester(semester: Semester) : CompletableFuture<Semester>{
        val future = CompletableFuture<Semester>()

        FirebaseProvider.getFirestore()
            .collection(semesterCollection)
            .document(mergeSectionAndSemester(semester))
            .set(semester, SetOptions.merge())
            .addOnSuccessListener { future.complete(semester) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Failed to insert ${mergeSectionAndSemester(semester)} into Database because of: $it")) }

        return future
    }

    /**
     * Add a new course document to the courses collection
     */
    override fun addCourse(course: Course) : CompletableFuture<Course> {
        val future = CompletableFuture<Course>()

        FirebaseProvider.getFirestore()
            .collection(courseCollection)
            .document(course.name)
            .set(course, SetOptions.merge())
            .addOnSuccessListener { future.complete(course) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Failed to insert ${course.name} into Database because of: $it")) }

        return future
    }

    /**
     * Remove a field
     * Warning! If it has sub-collections they are currently not deleted
     * Deleting sub-collections is not implemented yet because we are not using sub-collections here.
     * A valid argument could also be that interests are very rarely removed
     * So it could be fine to removed them from the console (automatically deleting all the sub-collections)
     * Instead of using a function for it.
     */
    fun removeField(field: Field) : CompletableFuture<Boolean>  {
        val future = CompletableFuture<Boolean>()

        FirebaseProvider.getFirestore()
            .collection(fieldCollection)
            .document(field.name)
            .delete()
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not delete ${field.name} because of: $it")) }

        return future
    }

    /**
     * Remove a semester and all its potential sub-collections (users and books)
     */
    fun removeSemester(semester: Semester) : CompletableFuture<Boolean>  {
        return TODO("maybe in the future, if needed")
    }

    /**
     * Remove a course and all its potential sub-collections (users and books)
     */
    fun removeCourse(course: Course) : CompletableFuture<Boolean>  {
        return TODO("maybe in the future, if needed")
    }


    // TODO maybe add listAllBooksOfCourse(course), etc. and listAllUsersInterestedIn(interest) if relevant
    // TODO Also addUserInterest() and removeUserInterest() instead of setUserInterest()

    /**
     * List all the Fields in the database.
     * */
    override fun listAllFields(): CompletableFuture<List<Field>> {
        val future = CompletableFuture<List<Field>>()

        FirebaseProvider.getFirestore()
            .collection(fieldCollection)
            .get()
            .addOnSuccessListener { documentSnapshots ->
                val fieldsList = documentSnapshots.map { snapshot ->
                    Field(name = snapshot.get("name") as String)
                }
                future.complete(fieldsList)
            }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not retrieve the list of all fields because of: $it")) }

        return future
    }


    /**
     * List all the Semesters in the database.
     * */
    override fun listAllSemesters(): CompletableFuture<List<Semester>> {
        val future = CompletableFuture<List<Semester>>()

        FirebaseProvider.getFirestore()
            .collection(semesterCollection)
            .get()
            .addOnSuccessListener { documentSnapshots ->
                val semestersList = documentSnapshots.map { snapshot ->
                    Semester(
                        section = snapshot.get("section") as String,
                        semester = snapshot.get("semester") as String
                    )
                }
                future.complete(semestersList)
            }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not retrieve the list of all semesters because of: $it")) }

        return future
    }

    /**
     * List all the Courses in the database.
     * */
    override fun listAllCourses(): CompletableFuture<List<Course>> {
        val future = CompletableFuture<List<Course>>()

        FirebaseProvider.getFirestore()
            .collection(courseCollection)
            .get()
            .addOnSuccessListener { documentSnapshots ->
                val coursesList = documentSnapshots.map { snapshot ->
                    Course(name = snapshot.get("name") as String)
                }
                future.complete(coursesList)
            }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not retrieve the list of all courses because of: $it")) }

        return future
    }

    /**
     * Get the interests of the specified user
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * */
    override fun getUserInterests(user: User): CompletableFuture<Triple<List<Field>, List<Semester>, List<Course>>> {
        TODO("Not yet implemented")
    }

    /**
     * Sets the interests of the specified user.
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    override fun setUserInterests(user: User, interests: List<Interest>): CompletableFuture<Unit> {
        TODO("Not yet implemented")
    }

}