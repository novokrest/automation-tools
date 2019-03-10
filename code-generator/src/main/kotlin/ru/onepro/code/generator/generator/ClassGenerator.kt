package ru.onepro.code.generator.generator

import ru.onepro.code.generator.dsl.ClassDescription
import ru.onepro.code.generator.model.*
import ru.onepro.code.generator.utils.StringUtils
import ru.onepro.code.generator.writer.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

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
        val fieldModels = classDescription.fields.map{
            buildFieldModel(name = it.key, type = it.value, isFieldRequired = classDescription.required?.contains(it.key) ?: true)
        }
        val builderClassModel = buildBuilderClassModel(className, fieldModels)
        return ClassModel(
                name = className,
                modifiers = listOf(Modifier.PUBLIC),
                fields = fieldModels,
                constructor = buildConstructorModel(fieldModels),
                getters = fieldModels.map { buildGetterModel(it) },
                methods = buildEqualsHashCodeToStringMethod(className, fieldModels) + buildBuilderFactoryMethods(className = className, builderClassModel = builderClassModel),
                nestedClasses = listOf(builderClassModel)
        )
    }

    private fun buildFieldModel(name: String, type: String, isFieldRequired: Boolean): FieldModel {
        return FieldModel(
                name = name,
                type = type,
                modifiers = listOf(Modifier.PRIVATE, Modifier.FINAL),
                required = isFieldRequired
        )
    }

    private fun buildConstructorModel(fieldModels: List<FieldModel>): ConstructorModel {
        return ConstructorModel(
                parameters = fieldModels.map { buildConstructorParameterModel(it)},
                modifiers = listOf(Modifier.PRIVATE)
        )
    }

    private fun buildConstructorParameterModel(fieldModel: FieldModel): ParameterModel {
        return ParameterModel(
                name = fieldModel.name,
                type = fieldModel.type,
                annotations = getAnnotationsFromFieldRequirement(fieldModel.required),
                required = fieldModel.required
        )
    }

    private fun buildGetterModel(fieldModel: FieldModel): GetterModel {
        return GetterModel(
                fieldName = fieldModel.name,
                fieldType = fieldModel.type,
                modifiers = listOf(Modifier.PUBLIC),
                annotations = listOf(AnnotationModel.NONNULL),
                asOptional = !fieldModel.required
        )
    }

    private fun buildEqualsHashCodeToStringMethod(className: String, fields: List<FieldModel>): List<WritableMethod> {
        return listOf(
                EqualsMethod(
                        method = MethodModel(
                                name = "equals",
                                modifiers = listOf(Modifier.PUBLIC),
                                annotations = listOf(AnnotationModel.OVERRIDE),
                                parameters = listOf(
                                        ParameterModel(
                                                name = "obj",
                                                type = "Object",
                                                annotations = emptyList(),
                                                required = true
                                        )
                                ),
                                returnType = "boolean",
                                usedClasses = listOf(Objects::class)
                        ),
                        className = className,
                        fields = fields
                ),
                HashCodeMethod(
                        method = MethodModel(
                                name = "hashCode",
                                modifiers = listOf(Modifier.PUBLIC),
                                annotations = listOf(AnnotationModel.OVERRIDE),
                                parameters = emptyList(),
                                returnType = "int"
                        ),
                        fields = fields
                ),
                ToStringMethod(
                        method = MethodModel(
                                name = "toString",
                                modifiers = listOf(Modifier.PUBLIC),
                                annotations = listOf(AnnotationModel.NONNULL, AnnotationModel.OVERRIDE),
                                parameters = emptyList(),
                                returnType = "String"
                        ),
                        className = className,
                        fields = fields
                )
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
                                        annotations = listOf(AnnotationModel.NONNULL),
                                        required = true
                                )),
                                returnType = builderClassModel.name
                        ),
                        builderClassModel
                )
        )
    }

    private fun buildBuilderClassModel(ownerClassName: String,
                                       fieldModels: List<FieldModel>): ClassModel {
        val builderClassName = "Builder"
        val builderFields = fieldModels.map { it.withoutModifier(Modifier.FINAL) }
        return ClassModel(
                name = builderClassName,
                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                fields = builderFields,
                constructor = ConstructorModel(emptyList(), listOf(Modifier.PRIVATE)),
                getters = emptyList(),
                methods = buildBuilderMethodModels(ownerClassName, builderClassName, builderFields),
                nestedClasses = emptyList()
        )
    }

    private fun buildBuilderMethodModels(ownerClassName: String,
                                         builderClassName: String,
                                         builderFields: List<FieldModel>): List<WritableMethod> {
        val methods = mutableListOf<WritableMethod>()
        methods.addAll(builderFields.map { WithMethod(buildBuilderMethodModel(builderClassName, it)) })
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

    private fun buildBuilderMethodModel(builderClassName: String, fieldModel: FieldModel): MethodModel {
        return MethodModel(
                name = "with" + StringUtils.fromUpperCase(fieldModel.name),
                modifiers = listOf(Modifier.PUBLIC),
                annotations = listOf(AnnotationModel.NONNULL),
                parameters = listOf(
                        ParameterModel(
                                name = fieldModel.name,
                                type = fieldModel.type,
                                annotations = getAnnotationsFromFieldRequirement(fieldModel.required),
                                required =  fieldModel.required
                        )
                ),
                returnType = builderClassName
        )
    }

    private fun getAnnotationsFromFieldRequirement(fieldRequired: Boolean): List<AnnotationModel> {
        return if (fieldRequired) listOf(AnnotationModel.NONNULL) else listOf(AnnotationModel.NULLABLE)
    }

}