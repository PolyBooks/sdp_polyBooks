package com.github.polybooks.core

/**
 * An Interest is something that can make a Book interesting for a user.
 * Interests can be Courses, Fields or Semesters.
 * */
sealed class Interest

/**
 * A course that a student can follow (e.g. COM-101)
 * @property courseName The identifier name of that course
 * */
data class Course(val courseName : String) : Interest()

/**
 * A field of study.
 * @property fieldName The identifier name of that field
 * */
data class Field(val fieldName : String) : Interest()

/**
 * Represents the pair section-semester a student can be in (e.g. IC BA6)
 * @property section The identifier string of that section.
 * @property semester The identifier string of that semester.
 * */
data class Semester(val section : String, val semester : String) : Interest()