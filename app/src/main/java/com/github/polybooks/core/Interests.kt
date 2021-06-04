package com.github.polybooks.core

/**
 * An Interest is something that can make a Book interesting for a user.
 * Interests can be Courses, Topics or Semesters.
 * */
sealed class Interest

/**
 * A course that a student can follow (e.g. COM-101)
 * @property courseName The identifier name of that course
 * */
data class Course(val courseName : String) : Interest()

/**
 * A topic of interest, can be a field of study, or something more generic.
 * @property name The identifier name of that topic
 * */
data class Topic(val name : String) : Interest()


/**
 * Represents the pair section-semester a student can be in (e.g. IC BA6)
 * @property section The identifier string of that section.
 * @property semester The identifier string of that semester.
 * */
data class Semester(val section : String, val semester : String) : Interest()