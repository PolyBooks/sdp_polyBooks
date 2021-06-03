package com.github.polybooks.database

import com.github.polybooks.core.*
import com.github.polybooks.utils.StringsManip.mergeSectionAndSemester
import com.github.polybooks.utils.firebaseUserToUser
import com.google.firebase.firestore.SetOptions
import java.util.concurrent.CompletableFuture

private const val TAG: String = "FBInterestDatabase"

// Names of the collections in Firestore
private const val topicCollection: String = "topicInterest"
private const val semesterCollection: String = "semesterInterest"
private const val courseCollection: String = "courseInterest"
private const val userCollection: String = "user"
private const val interestsTopicName: String = "interests"

/**
 * !! DO NOT INSTANTIATE THIS CLASS. Instead use Database.interestDatabase to access it !!
 * The chosen structure for firebase is one collection for Topic, one for semester and one for courses.
 * Each of them will hold documents whose attribute is the name of the interest.
 * It might seem unnecessary to have 3 root level collections for interests,
 * but it is by far the best option if we potentially want each interest to hold the list of books and user associated with it
 * As each document will be able to have a book collection and a user collection.
 *
 * Using snapshotListener here does not feel necessary as interests are rarely changing.
 *
 * There's one more user collection used by this class only to store the user interest when logged in.
 * The current implementation is to store it as an array. The subset of selected interests per user should not
 * be changing too often, nor be too large in most cases.
 */
class FBInterestDatabase: InterestDatabase {


    /**
     * Add a new Topic document to the Topics collection
     */
    override fun addTopic(topic: Topic) : CompletableFuture<Topic> {
        val future = CompletableFuture<Topic>()

        FirebaseProvider.getFirestore()
            .collection(topicCollection)
            .document(topic.name)
            .set(topic, SetOptions.merge())
            .addOnSuccessListener { future.complete(topic) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Failed to insert ${topic.name} into Database because of: $it")) }


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
            .document(course.courseName)
            .set(course, SetOptions.merge())
            .addOnSuccessListener { future.complete(course) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Failed to insert ${course.courseName} into Database because of: $it")) }

        return future
    }

    /**
     * Remove a Topic
     * Warning! If it has sub-collections they are currently not deleted
     * Deleting sub-collections is not implemented yet because we are not using sub-collections here.
     * A valid argument could also be that interests are very rarely removed
     * So it could be fine to removed them from the console (automatically deleting all the sub-collections)
     * Instead of using a function for it.
     */
    fun removeTopic(topic: Topic) : CompletableFuture<Boolean>  {
        val future = CompletableFuture<Boolean>()

        FirebaseProvider.getFirestore()
            .collection(topicCollection)
            .document(topic.name)
            .delete()
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not delete ${topic.name} because of: $it")) }

        return future
    }

    /**
     * Remove a semester
     * Warning! If it has sub-collections they are currently not deleted
     * Deleting sub-collections is not implemented yet because we are not using sub-collections here.
     * A valid argument could also be that interests are very rarely removed
     * So it could be fine to removed them from the console (automatically deleting all the sub-collections)
     * Instead of using a function for it.
     */
    fun removeSemester(semester: Semester) : CompletableFuture<Boolean>  {
        val future = CompletableFuture<Boolean>()

        FirebaseProvider.getFirestore()
            .collection(semesterCollection)
            .document(mergeSectionAndSemester(semester))
            .delete()
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not delete ${mergeSectionAndSemester(semester)} because of: $it")) }

        return future
    }

    /**
     * Remove a course
     * Warning! If it has sub-collections they are currently not deleted
     * Deleting sub-collections is not implemented yet because we are not using sub-collections here.
     * A valid argument could also be that interests are very rarely removed
     * So it could be fine to removed them from the console (automatically deleting all the sub-collections)
     * Instead of using a function for it.
     */
    fun removeCourse(course: Course) : CompletableFuture<Boolean>  {
        val future = CompletableFuture<Boolean>()

        FirebaseProvider.getFirestore()
            .collection(courseCollection)
            .document(course.courseName)
            .delete()
            .addOnSuccessListener { future.complete(true) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not delete ${course.courseName} because of: $it")) }

        return future
    }


    // TODO maybe add listAllBooksOfCourse(course), etc. and listAllUsersInterestedIn(interest) if relevant

    /**
     * List all the Topics in the database.
     * */
    override fun listAllTopics(): CompletableFuture<List<Topic>> {
        val future = CompletableFuture<List<Topic>>()

        FirebaseProvider.getFirestore()
            .collection(topicCollection)
            .get()
            .addOnSuccessListener { documentSnapshots ->
                val topicsList = documentSnapshots.map { snapshot ->
                    Topic(name = snapshot.get("name") as String)
                }
                future.complete(topicsList)
            }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not retrieve the list of all Topics because of: $it")) }

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
                    Course(courseName = snapshot.get("courseName") as String)
                }
                future.complete(coursesList)
            }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not retrieve the list of all courses because of: $it")) }

        return future
    }

    /**
     * Get the interests of the current user
     * */
    override fun getCurrentUserInterests(): CompletableFuture<List<Interest>> {
        val user = firebaseUserToUser(FirebaseProvider.getAuth().currentUser)
        return if(user is LoggedUser) {
            getLoggedUserInterests(user)
        } else {
            getLocalUserInterests()
        }
    }

    /**
     * Sets the interests of the current user.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    override fun setCurrentUserInterests(interests: List<Interest>): CompletableFuture<List<Interest>> {
        val user = firebaseUserToUser(FirebaseProvider.getAuth().currentUser)
        return if(user is LoggedUser) {
            setLoggedUserInterests(user, interests)
        } else {
            setLocalUserInterests(interests)
        }
    }

    /**
     * Get the interests of the specified logged user
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * */
    fun getLoggedUserInterests(user: LoggedUser): CompletableFuture<List<Interest>> {
        val future = CompletableFuture<List<Interest>>()

        FirebaseProvider.getFirestore()
            .collection(userCollection)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                   val mapList: List<Map<String, String>> = document.get(interestsTopicName) as List<Map<String, String>>
                    val interestsList = mapList.map { interest ->
                        when {
                            interest.containsKey("name") -> {
                                Topic(interest["name"] as String)
                            }
                            interest.containsKey("courseName") -> {
                                Course(interest["courseName"] as String)
                            }
                            else -> {
                                Semester(
                                    section = interest["section"] as String,
                                    semester = interest["semester"] as String
                                )
                            }
                        }

                    }
                    future.complete(interestsList as List<Interest>)
                } else {
                    future.complete(emptyList())
                }

            }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Could not retrieve the list of all interests of user ${user.uid} because of: $it")) }

        return future
    }

    /**
     * Sets the interests of the specified logged user.
     * TODO: Might need to add an authentication token to restrict authenticated users to only modify their interests.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    fun setLoggedUserInterests(user: LoggedUser, interests: List<Interest>): CompletableFuture<List<Interest>> {
        val future = CompletableFuture<List<Interest>>()

        val docData = hashMapOf(
            interestsTopicName to interests
        )
        FirebaseProvider.getFirestore()
            .collection(userCollection)
            .document(user.uid)
            .set(docData, SetOptions.merge())
            .addOnSuccessListener { future.complete(interests) }
            .addOnFailureListener { future.completeExceptionally(DatabaseException("Failed to insert $docData of user ${user.uid} into Database because of: $it")) }

        return future
    }

    // TODO list
    // 2) use sharedPreferences for LoggedUser to serve as a cache.

    /**
     * Get the interests of the current unlogged local user
     * As the user is not auth, it will use exclusively the local storage.
     * */
    private fun getLocalUserInterests(): CompletableFuture<List<Interest>> {
        val future = CompletableFuture<List<Interest>>()
        future.completeExceptionally(LocalUserException("user needs to be logged in to get their interests"))
        return future
    }

    /**
     * Sets the interests of the current unlogged local user.
     * As the user is not auth, it will use exclusively the local storage.
     * @return A Future to receive confirmation of success/failure asynchronously
     * */
    private fun setLocalUserInterests(interests: List<Interest>): CompletableFuture<List<Interest>> {
        val future = CompletableFuture<List<Interest>>()
        future.completeExceptionally(LocalUserException("user needs to be logged in to set their interests to $interests"))
        return future
    }


}