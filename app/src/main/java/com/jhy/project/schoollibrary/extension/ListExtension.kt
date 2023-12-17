package com.jhy.project.schoollibrary.extension

inline fun <reified T> List<*>.asListOfType(): List<T>? =
    if (all { it is T })
        @Suppress("UNCHECKED_CAST")
        this as List<T> else
        null

inline fun <reified T> Any.asList(): List<T> {
    return (this as List<*>).filterIsInstance<T>()
}