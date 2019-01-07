package ru.onepro.code.generator.dsl

data class ClassesDescription(
        val author: String,
        val classes: List<ClassDescription>
)

data class ClassDescription(
        val name: String,
        val fields: Map<String, FieldDescription>,
        val required: List<String>
)

data class FieldDescription(
        val type: String,
        val description: String
)
