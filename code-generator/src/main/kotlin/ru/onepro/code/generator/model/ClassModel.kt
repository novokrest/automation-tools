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
        val modifiers: List<Modifier>
)

fun FieldModel.withoutModifier(vararg modifiersToRemove: Modifier): FieldModel =
        FieldModel(
                name = this.name,
                type = this.type,
                modifiers = this.modifiers.filter { !modifiersToRemove.contains(it) }
        )

fun FieldModel.withoutModifier(modifiersToRemove: List<Modifier>): FieldModel =
        FieldModel(
                name = this.name,
                type = this.type,
                modifiers = this.modifiers.filter { !modifiersToRemove.contains(it) }
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
        val returnType: String
)

data class ParameterModel(
        val name: String,
        val type: String,
        val annotations: List<AnnotationModel>
)

data class AnnotationModel(
        val type: KClass<*>,
        val arguments: List<String>
) {
    companion object {
        val NONNULL = AnnotationModel(javax.annotation.Nonnull::class, emptyList())
        val NULLABLE = AnnotationModel(javax.annotation.Nullable::class, emptyList())
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