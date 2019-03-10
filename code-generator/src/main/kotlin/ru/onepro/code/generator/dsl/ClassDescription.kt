package ru.onepro.code.generator.dsl

data class ClassesDescription(
        val classes: List<ClassDescription>
)

data class ClassDescription(
        val name: String,
        val fields: Map<String, String>,
        val required: List<String>?
)
