package ru.onepro.code.generator.model

import ru.onepro.code.generator.writer.ClassUtils
import ru.onepro.code.generator.writer.CodeWriter
import kotlin.reflect.KClass

data class ClassModel(
        val name: String,
        val modifiers: List<Modifier>,
        val fields: List<FieldModel>,
        val constructor: ConstructorModel,
        val getters: List<GetterModel>,
        val methods: List<WritableMethod>,
        val nestedClasses: List<ClassModel>
)

data class FieldModel(
        val name: String,
        val type: String,
        val modifiers: List<Modifier>,
        val required: Boolean
)

fun FieldModel.withoutModifier(vararg modifiersToRemove: Modifier): FieldModel =
        FieldModel(
                name = this.name,
                type = this.type,
                modifiers = this.modifiers.filter { !modifiersToRemove.contains(it) },
                required = this.required
        )

fun FieldModel.withModifierSubstitution(oldModifier: Modifier, newModifier: Modifier): FieldModel =
        FieldModel(
                name = this.name,
                type = this.type,
                modifiers = this.modifiers.map { if (it == oldModifier) newModifier else it },
                required = this.required
        )

fun FieldModel.withoutModifier(modifiersToRemove: List<Modifier>): FieldModel =
        FieldModel(
                name = this.name,
                type = this.type,
                modifiers = this.modifiers.filter { !modifiersToRemove.contains(it) },
                required = this.required
        )

data class ConstructorModel(
        val parameters: List<ParameterModel>,
        val modifiers: List<Modifier>
)

data class GetterModel(
        val fieldName: String,
        val fieldType: String,
        val modifiers: List<Modifier>,
        val annotations: List<AnnotationModel>,
        val asOptional: Boolean
)

data class MethodModel (
        val name: String,
        val modifiers: List<Modifier>,
        val annotations: List<AnnotationModel>,
        val parameters: List<ParameterModel>,
        val returnType: String,
        val usedClasses: List<KClass<*>> = emptyList()
)

data class ParameterModel(
        val name: String,
        val type: String,
        val annotations: List<AnnotationModel>,
        val required: Boolean
)

data class AnnotationModel(
        val type: KClass<*>,
        val arguments: List<String>
) {
    companion object {
        val NONNULL = AnnotationModel(javax.annotation.Nonnull::class, emptyList())
        val NULLABLE = AnnotationModel(javax.annotation.Nullable::class, emptyList())
        val OVERRIDE = AnnotationModel(Override::class, emptyList())
    }
}

enum class Modifier(val code: String) {
    PRIVATE("private"),
    PUBLIC("public"),
    STATIC("static"),
    FINAL("final"),
}

interface WritableMethod {
    val method: MethodModel
    fun writeBody(writer: CodeWriter)
}

class BuilderFactoryMethod(override val method: MethodModel) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        writer.writeWithSemicolon("return new ${method.returnType}()")
    }

}

class BuilderFactoryCopyMethod(override val method: MethodModel,
                               private val builderClassModel: ClassModel) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        val builderClassName = builderClassModel.name
        val builderParamName = "builder"
        val copyParameter = method.parameters[0]
        writer.writeWithSemicolon(ClassUtils.concatTokens(builderClassName, builderParamName, "=", "new", "$builderClassName()"))
        builderClassModel.fields.forEach {
            writer.writeWithSemicolon(ClassUtils.concatTokens("$builderParamName.${it.name}", "=", "${copyParameter.name}.${it.name}"))
        }
        writer.writeWithSemicolon("return $builderParamName")
    }

}

class WithMethod(override val method: MethodModel) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        val parameter = method.parameters[0]
        writer.writeWithSemicolon(ClassUtils.concatTokens("this.${parameter.name}", "=", parameter.name))
        writer.writeWithSemicolon(ClassUtils.concatTokens("return", "this"))
    }

}

class BuilderBuildMethod(override val method: MethodModel,
                         private val builderFields: List<FieldModel>) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        writer.write(ClassUtils.concatTokens("return", "new", "${method.returnType}("))
        writer.write(
                ClassUtils.concatTokens(
                        builderFields.map { it.name },
                        ",${System.lineSeparator()}"
                )
        )
        writer.writeWithSemicolon(")")
    }

}

class EqualsMethod(override val method: MethodModel,
                   private val className: String,
                   private val fields: List<FieldModel>) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        val parameter = method.parameters[0]

        writer.write(ClassUtils.concatTokens("if", ClassUtils.concatTokens("(this", "==", "${parameter.name})"), "{"))
        writer.writeWithSemicolon(ClassUtils.concatTokens("return", "true"))
        writer.write("}")

        writer.write(ClassUtils.concatTokens("if", ClassUtils.concatTokens("(${parameter.name}", "==", "null", "||", "${parameter.name}.getClass()", "!=", "getClass())"), "{"))
        writer.writeWithSemicolon(ClassUtils.concatTokens("return", "false"))
        writer.write("}")

        val otherVarName = "other"
        writer.writeWithSemicolon(ClassUtils.concatTokens(className, otherVarName, "=", "($className)", parameter.name))
        writer.writeWithSemicolon(
                ClassUtils.concatTokens(
                        "return",
                        ClassUtils.concatTokens(
                                fields.map { ClassUtils.concatTokens("Objects.equals(${it.name},", "$otherVarName.${it.name})") },
                                " &&${System.lineSeparator()}"
                        )
                )
        )
    }

}

class HashCodeMethod(override val method: MethodModel,
                     private val fields: List<FieldModel>) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        val fieldsSeparatedByComma = ClassUtils.concatTokens(fields.map { it.name }, ", ")
        writer.writeWithSemicolon(ClassUtils.concatTokens("return", "Objects.hash($fieldsSeparatedByComma)"))
    }

}

class ToStringMethod(override val method: MethodModel,
                     private val className: String,
                     private val fields: List<FieldModel>) : WritableMethod {

    override fun writeBody(writer: CodeWriter) {
        writer.write(ClassUtils.concatTokens("return", "\"$className{\"", "+"))
        if (!fields.isEmpty()) {
            val firstField = fields[0]
            writeField(writer, firstField, true)
            fields.stream().skip(1).forEach { writeField(writer, it, false) }
        }
        writer.writeWithSemicolon("\'}\'")
    }

    private fun writeField(writer: CodeWriter, field: FieldModel, isFirst: Boolean) {
        val prefix = if (isFirst) "" else ", "
        val useCommaForValue = field.type == "String"
        val tokens = mutableListOf<String>()
        tokens.add("\"$prefix${field.name}=${if (useCommaForValue) "'" else ""}\"")
        tokens.add("+")
        tokens.add(field.name)
        tokens.add("+")
        if (useCommaForValue) {
            tokens.add("'\\''")
            tokens.add("+")
        }
        writer.write(ClassUtils.concatTokens(tokens))
    }

}