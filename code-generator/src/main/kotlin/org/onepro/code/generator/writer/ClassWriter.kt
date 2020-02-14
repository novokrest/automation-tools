package org.onepro.code.generator.writer

import org.onepro.code.generator.model.*
import org.onepro.code.generator.model.CheckConstructorParamsMode.CheckRequired
import org.onepro.code.generator.model.CheckConstructorParamsMode.RequireNonNull
import org.onepro.code.generator.utils.StringUtils
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KClass

object ClassWriter {

    private val WELL_KNOWN_TYPES = mapOf(
            "ZonedDateTime" to ZonedDateTime::class
    )

    fun write(classModel: ClassModel, output: OutputStream) {
        CodeWriter(BufferedOutputStream(output)).use { writer ->
            writer.writeWithSemicolon(ClassUtils.concatTokens("package", "generated")).writeEmptyLine()
            writeImports(writer, classModel)
            writer.writeEmptyLine()
            writeClass(writer, classModel)
        }
    }

    private fun writeImports(writer: CodeWriter, classModel: ClassModel) {
        val importedClasses: Set<KClass<*>> = collectImports(classModel)
        importedClasses.stream().map { it.java.canonicalName }.sorted().forEach {
            writer.writeWithSemicolon(ClassUtils.concatTokens("import", it))
        }
    }

    private fun collectImports(classModel: ClassModel): Set<KClass<*>> {
        val importClasses = mutableSetOf<KClass<*>>()
        collectImports(importClasses, classModel)
        return importClasses
    }

    private fun collectImports(importClasses: MutableSet<KClass<*>>, classModel: ClassModel) {
        classModel.fields.forEach { addFieldTypeToImport(importClasses, it) }
        classModel.constructor.parameters.flatMap { it.annotations }.forEach { addAnnotationToImport(importClasses, it) }
        classModel.getters.flatMap { it.annotations }.forEach { addAnnotationToImport(importClasses, it) }
        if (classModel.getters.any { it.asOptional }) {
            importClasses.add(Optional::class)
        }
        classModel.methods.flatMap { it.method.annotations }.forEach { addAnnotationToImport(importClasses, it) }
        classModel.methods.flatMap { it.method.usedClasses }.forEach { importClasses.add(it) }
        classModel.nestedClasses.forEach { collectImports(importClasses, it) }
    }

    private fun addFieldTypeToImport(importClasses: MutableSet<KClass<*>>, it: FieldModel) {
        if (WELL_KNOWN_TYPES.containsKey(it.type)) {
            importClasses.add(WELL_KNOWN_TYPES[it.type]!!)
        }
    }

    private fun addAnnotationToImport(importClasses: MutableSet<KClass<*>>, it: AnnotationModel) {
        if (!it.type.java.canonicalName.startsWith("java.lang")) {
            importClasses.add(it.type)
        }
    }

    private fun writeClass(writer: CodeWriter, classModel: ClassModel, separateFieldsWithEmptyLine: Boolean = true) {
        writer.write(
                ClassUtils.concatTokens(ClassUtils.concatModifiers(classModel.modifiers), "class", classModel.name, "{")
        ).writeEmptyLine()

        if (separateFieldsWithEmptyLine) {
            classModel.fields.forEach {
                writer.writeWithSemicolon(
                        ClassUtils.concatTokens(ClassUtils.concatModifiers(it.modifiers), it.type, it.name)
                ).writeEmptyLine()
            }
        } else {
            classModel.fields.forEach {
                writer.writeWithSemicolon(
                        ClassUtils.concatTokens(ClassUtils.concatModifiers(it.modifiers), it.type, it.name)
                )
            }
            writer.writeEmptyLine()
        }

        writeConstructor(writer, classModel)
        writer.writeEmptyLine()

        classModel.getters.forEach {
            writeGetter(writer, it)
            writer.writeEmptyLine()
        }

        classModel.methods.forEach {
            writeMethod(writer, it)
            writer.writeEmptyLine()
        }

        classModel.nestedClasses.forEach {
            writeClass(writer, it, separateFieldsWithEmptyLine = false)
            writer.writeEmptyLine()
        }

        writer.write("}")
    }

    private fun writeMethod(writer: CodeWriter, writableMethod: WritableMethod) {
        val method = writableMethod.method
        if (method.annotations.isNotEmpty()) {
            writer.write(
                    ClassUtils.concatAnnotations(method.annotations, System.lineSeparator())
            )
        }
        writer.write(
                ClassUtils.concatTokens(
                        ClassUtils.concatModifiers(method.modifiers),
                        method.returnType,
                        method.name + "(" +
                                ClassUtils.concatTokens(
                                        method.parameters.map {
                                            ClassUtils.concatTokens(ClassUtils.concatAnnotations(it.annotations), it.type, it.name)
                                        },
                                        ", "
                                ) +
                                ") {")
        )
        writableMethod.writeBody(writer)
        writer.write("}")
    }

    private fun writeConstructor(writer: CodeWriter, classModel: ClassModel) {
        val constructor = classModel.constructor
        if (constructor.parameters.isEmpty()) {
            writer.write(
                    ClassUtils.concatTokens(ClassUtils.concatModifiers(constructor.modifiers), classModel.name + "() {")
            )
            writer.write("}")
        } else {
            writer.write(
                    ClassUtils.concatTokens(ClassUtils.concatModifiers(constructor.modifiers), classModel.name + "(")
            )

            writer.write(ClassUtils.concatTokens(
                    constructor.parameters.map {
                        ClassUtils.concatTokens(ClassUtils.concatAnnotations(it.annotations), it.type, it.name)
                    },
                    ",${System.lineSeparator()}"
            ))

            writer.write(") {")

            constructor.parameters.forEach {
                writer.writeWithSemicolon(
                        ClassUtils.concatTokens("this.${it.name}", "=", wrapNameWithRequireNonnull(classModel.checkConstructorParamsMode, it))
                )
            }

            writer.write("}")
        }

    }

    private fun wrapNameWithRequireNonnull(mode: CheckConstructorParamsMode, parameter: ParameterModel): String = if (parameter.required) {
        when (mode) {
            CheckRequired -> "checkRequired(\"${parameter.name}\", ${parameter.name})"
            RequireNonNull -> "requireNonNull(${parameter.name}, \"${parameter.name}\")"
        }
    } else {
        parameter.name
    }

    private fun writeGetter(writer: CodeWriter, getter: GetterModel) {
        val returnType = if (getter.asOptional) "Optional<${getter.fieldType}>" else getter.fieldType
        val returnedValue = if (getter.asOptional) "Optional.ofNullable(${getter.fieldName})" else getter.fieldName
        writer.write(
                ClassUtils.concatAnnotations(getter.annotations, System.lineSeparator())
        )
        writer.write(
                ClassUtils.concatTokens(ClassUtils.concatModifiers(getter.modifiers), returnType, "get${StringUtils.fromUpperCase(getter.fieldName)}()", "{")
        )
        writer.writeWithSemicolon("return $returnedValue")
        writer.write("}")
    }

}