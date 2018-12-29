package ru.onepro.code.generator.generator

import ru.onepro.code.generator.dsl.ClassDescription
import ru.onepro.code.generator.model.*
import ru.onepro.code.generator.utils.StringUtils
import ru.onepro.code.generator.writer.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths

object ClassGenerator {

    fun generate(outputDir: Path, classDescription: ClassDescription): Path {
        val classModel = buildClassModel(classDescription)
        val generatedClassFilePath: Path = Paths.get(outputDir.toString(), "${classModel.name}.java")
        FileOutputStream(File(generatedClassFilePath.toString())).use {
            ClassWriter.write(classModel, it)
        }
        return generatedClassFilePath
    }

    private fun buildClassModel(classDescription: ClassDescription): ClassModel {
        val className = classDescription.name
        val fieldModels = classDescription.fields.map { buildFieldModel(name = it.key, type = it.value) }
        val isFieldRequired: (FieldModel) -> Boolean = { fieldModel -> classDescription.required.contains(fieldModel.name) }
        val builderClassModel = buildBuilderClassModel(className, fieldModels, isFieldRequired)
        return ClassModel(
                name = className,
                modifiers = listOf(Modifier.PUBLIC),
                fields = fieldModels,
                constructor = buildConstructorModel(fieldModels, isFieldRequired),
                getters = fieldModels.map { buildGetterModel(it, isFieldRequired) },
                methods = buildBuilderFactoryMethods(className = className, builderClassModel = builderClassModel),
                nestedClasses = listOf(builderClassModel)
        )
    }

    private fun buildFieldModel(name: String, type: String): FieldModel {
        return FieldModel(
                name = name,
                type = type,
                modifiers = listOf(Modifier.PRIVATE, Modifier.FINAL)
        )
    }

    private fun buildConstructorModel(fieldModels: List<FieldModel>, isFieldRequired: (FieldModel) -> Boolean): ConstructorModel {
        return ConstructorModel(
                parameters = fieldModels.map { buildConstructorParameterModel(it, isFieldRequired)},
                modifiers = listOf(Modifier.PRIVATE)
        )
    }

    private fun buildConstructorParameterModel(fieldModel: FieldModel, isFieldRequired: (FieldModel) -> Boolean): ParameterModel {
        return ParameterModel(
                name = fieldModel.name,
                type = fieldModel.type,
                annotations = getAnnotationsFromFieldRequirement(fieldModel, isFieldRequired)
        )
    }

    private fun buildGetterModel(fieldModel: FieldModel, isFieldRequired: (FieldModel) -> Boolean): GetterModel {
        return GetterModel(
                fieldName = fieldModel.name,
                fieldType = fieldModel.type,
                modifiers = listOf(Modifier.PUBLIC),
                annotations = listOf(AnnotationModel.NONNULL),
                asOptional = !isFieldRequired(fieldModel)
        )
    }

    private fun buildBuilderFactoryMethods(className: String, builderClassModel: ClassModel): List<WritableMethod> {
        return listOf(
                BuilderFactoryMethod(
                        MethodModel(
                                name = "builder",
                                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                                annotations = listOf(AnnotationModel.NONNULL),
                                parameters = emptyList(),
                                returnType = builderClassModel.name
                        )
                ),
                BuilderFactoryCopyMethod(
                        MethodModel(
                                name = "builder",
                                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                                annotations = listOf(AnnotationModel.NONNULL),
                                parameters = listOf(ParameterModel(
                                        name = "copy",
                                        type = className,
                                        annotations = listOf(AnnotationModel.NONNULL)
                                )),
                                returnType = builderClassModel.name
                        ),
                        builderClassModel
                )
        )
    }

    private fun buildBuilderClassModel(ownerClassName: String,
                                       fieldModels: List<FieldModel>,
                                       isFieldRequired: (FieldModel) -> Boolean): ClassModel {
        val builderClassName = "Builder"
        val builderFields = fieldModels.map { it.withoutModifier(Modifier.FINAL) }
        return ClassModel(
                name = builderClassName,
                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                fields = builderFields,
                constructor = ConstructorModel(emptyList(), listOf(Modifier.PRIVATE)),
                getters = emptyList(),
                methods = buildBuilderMethodModels(ownerClassName, builderClassName, builderFields, isFieldRequired),
                nestedClasses = emptyList()
        )
    }

    private fun buildBuilderMethodModels(ownerClassName: String,
                                         builderClassName: String,
                                         builderFields: List<FieldModel>,
                                         isFieldRequired: (FieldModel) -> Boolean): List<WritableMethod> {
        val methods = mutableListOf<WritableMethod>()
        methods.addAll(builderFields.map { WithMethod(buildBuilderMethodModel(builderClassName, it, isFieldRequired(it))) })
        methods.add(
                BuilderBuildMethod(
                        MethodModel(
                                name = "build",
                                modifiers = listOf(Modifier.PUBLIC),
                                annotations = listOf(AnnotationModel.NONNULL),
                                parameters = emptyList(),
                                returnType = ownerClassName
                        ),
                        builderFields
                )
        )
        return methods
    }

    private fun buildBuilderMethodModel(builderClassName: String, fieldModel: FieldModel, fieldRequired: Boolean): MethodModel {
        return MethodModel(
                name = "with" + StringUtils.fromUpperCase(fieldModel.name),
                modifiers = listOf(Modifier.PUBLIC),
                annotations = getAnnotationsFromFieldRequirement(fieldRequired),
                parameters = listOf(
                        ParameterModel(
                                name = fieldModel.name,
                                type = fieldModel.type,
                                annotations = getAnnotationsFromFieldRequirement(fieldRequired)
                        )
                ),
                returnType = builderClassName
        )
    }

    private fun getAnnotationsFromFieldRequirement(fieldModel: FieldModel, isFieldRequired: (FieldModel) -> Boolean): List<AnnotationModel> {
        return getAnnotationsFromFieldRequirement(isFieldRequired(fieldModel))
    }

    private fun getAnnotationsFromFieldRequirement(fieldRequired: Boolean): List<AnnotationModel> {
        return if (fieldRequired) listOf(AnnotationModel.NONNULL) else listOf(AnnotationModel.NULLABLE)
    }

}