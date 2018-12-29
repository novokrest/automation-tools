package ru.onepro.code.generator.utils

object StringUtils {

    fun fromUpperCase(str: String): String {
        return if (str.isEmpty()) {
            str
        } else {
            str.substring(0, 1).toUpperCase() + str.substring(1)
        }
    }

}