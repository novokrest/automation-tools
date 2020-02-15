package org.onepro.code.generator.dsl

data class ClassesDescription(
    val classes: List<ClassDescription>
)

data class ClassDescription(
    val name: String,
    val fields: List<String>,
    val required: List<String>?,
    val json: Boolean?,
    val equal: Boolean?
) {
    val fieldDescriptions = fields.map { parseFieldDescription(it) }

    private fun parseFieldDescription(field: String): FieldDescription {
        val parts = field.split(":").map { it.trim() }
        return FieldDescription(
            name = parts[0],
            type = parts[1],
            comment = if (parts.size == 2) {
                null
            } else {
                parts[2]
            }
        )
    }
}

data class FieldDescription(
    val name: String,
    val type: String,
    val comment: String?
)
