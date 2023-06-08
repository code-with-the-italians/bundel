package dev.sebastiano.bundel.preferences

sealed class Lce<T> {

    class Loading<T> : Lce<T>()

    data class Data<T>(val value: T) : Lce<T>()

    data class Error<T, E : Throwable>(val throwable: E) : Lce<T>()
}
