package ru.onepro.code.generator.writer

import ru.onepro.code.generator.model.AnnotationModel
import ru.onepro.code.generator.model.Modifier

object ClassUtils {

    fun concatAnnotations(annotations: List<AnnotationModel>, separator: String = " "): String {
        return concatTokens(
                annotations.map {
                    "@" + it.type.simpleName!! +
                            (if (it.arguments.isEmpty()) "" else "(" + it.arguments.map { "\"$it\"" }.reduce { acc, arg -> "$acc, $arg" } + ")")
                },
                separator
        )
    }

    fun concatModifiers(modifiers: List<Modifier>): String {
        return concatTokens(modifiers.map { it.code })
    }

    fun concatTokens(vararg tokens: String): String {
        return concatTokens(tokens.toList())
    }

    fun concatTokens(tokens: List<String>, separator: String = " "): String {
        return if (tokens.isEmpty()) "" else tokens.reduce { acc, token -> "$acc$separator$token" }
    }

}