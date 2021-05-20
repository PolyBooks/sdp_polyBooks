package com.github.polybooks.utils

/**
 * Efficient and synchronized way to produce singleton object with arguments
 * Taken from https://blog.mindorks.com/how-to-create-a-singleton-class-in-kotlin
 * who themselves took it from the Kotlin Standard Library.
 */
open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}