package ru.onepro.code.generator.writer

import ru.onepro.code.generator.model.ClassModel
import ru.onepro.code.generator.model.GetterModel
import ru.onepro.code.generator.model.WritableMethod
import ru.onepro.code.generator.utils.StringUtils
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.reflect.KClass

object ClassWriter {

    fun write(classModel: ClassModel, output: OutputStream) {
        CodeWriter(BufferedOutputStream(output)).use { writer ->
            writer.writeWithSemicolon("package ").writeEmptyLine()
            writeImports(writer, classModel)
            writer.writeEmptyLine()
            writeClass(writer, classModel)
        }
    }

    private fun writeImports(writer: CodeWriter, classModel: ClassModel) {
        val importedClasses: Set<KClass<*>> = collectImports(classModel)
        importedClasses.forEach {
            writer.writeWithSemicolon("import ${it.java.canonicalName}")
        }
    }

    private fun collectImports(classModel: ClassModel): Set<KClass<*>> {
        val importClasses = mutableSetOf<KClass<*>>()
        collectImports(importClasses, classModel)
        return importClasses
    }

    private fun collectImports(importClasses: MutableSet<KClass<*>>, classModel: ClassModel) {
        classModel.constructor.parameters.flatMap { it.annotations }.forEach { importClasses.add(it.type) }
        classModel.getters.flatMap { it.annotations }.forEach { importClasses.add(it.type) }
        if (classModel.getters.any { it.asOptional }) {
            importClasses.add(Optional::class)
        }
        classModel.methods.flatMap { it.method.annotations }.forEach { importClasses.add(it.type) }
        classModel.nestedClasses.forEach { collectImports(importClasses, it) }
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
        writer.write(
                ClassUtils.concatAnnotations(method.annotations, System.lineSeparator())
        )
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
                        ClassUtils.concatTokens("this.${it.name}", "=", it.name)
                )
            }

            writer.write("}")
        }

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