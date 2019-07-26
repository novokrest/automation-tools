package ru.onepro.code.generator.generator

import ru.onepro.code.generator.config.Config
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

    fun generate(outputDir: Path, classDescription: ClassDescription, config: Config): Path {
        val classModel = buildClassModel(classDescription, config)
        val generatedClassFilePath: Path = Paths.get(outputDir.toString(), "${classModel.name}.java")
        FileOutputStream(File(generatedClassFilePath.toString())).use {
            ClassWriter.write(classModel, it)
        }
        return generatedClassFilePath
    }

    private fun buildClassModel(classDescription: ClassDescription, config: Config): ClassModel {
        val className = classDescription.name
        val fieldModels = classDescription.fields.map{
            buildFieldModel(name = it.key, type = it.value, isFieldRequired = classDescription.required?.contains(it.key) ?: true, isFieldPublic = config.isFieldPublic)
        }
        val builderClassModel = buildBuilderClassModel(className, fieldModels, config)
        return ClassModel(
                name = className,
                modifiers = listOf(Modifier.PUBLIC),
                fields = fieldModels,
                constructor = buildConstructorModel(fieldModels, config.withNonnull),
                getters = if (config.isFieldPublic) emptyList() else fieldModels.map { buildGetterModel(it, config.withNonnull) },
                methods = buildEqualsHashCodeToStringMethod(className, fieldModels, config.withNonnull) + buildBuilderFactoryMethods(className = className, builderClassModel = builderClassModel, withNonnull = config.withNonnull),
                nestedClasses = listOf(builderClassModel)
        )
    }

    private fun buildFieldModel(name: String, type: String, isFieldRequired: Boolean, isFieldPublic: Boolean): FieldModel {
        return FieldModel(
                name = name,
                type = type,
                modifiers = listOf(if (isFieldPublic) Modifier.PUBLIC else Modifier.PRIVATE, Modifier.FINAL),
                required = isFieldRequired
        )
    }

    private fun buildConstructorModel(fieldModels: List<FieldModel>, withNonnull: Boolean): ConstructorModel {
        return ConstructorModel(
                parameters = fieldModels.map { buildConstructorParameterModel(it, withNonnull)},
                modifiers = listOf(Modifier.PRIVATE)
        )
    }

    private fun buildConstructorParameterModel(fieldModel: FieldModel, withNonnull: Boolean): ParameterModel {
        return ParameterModel(
                name = fieldModel.name,
                type = fieldModel.type,
                annotations = getAnnotationsFromFieldRequirement(fieldModel.required, withNonnull),
                required = fieldModel.required
        )
    }

    private fun buildGetterModel(fieldModel: FieldModel, withNonnull: Boolean): GetterModel {
        return GetterModel(
                fieldName = fieldModel.name,
                fieldType = fieldModel.type,
                modifiers = listOf(Modifier.PUBLIC),
                annotations = if (withNonnull) listOf(AnnotationModel.NONNULL) else emptyList(),
                asOptional = !fieldModel.required
        )
    }

    private fun buildEqualsHashCodeToStringMethod(className: String, fields: List<FieldModel>, withNonnull: Boolean): List<WritableMethod> {
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
                                annotations = if (withNonnull) listOf(AnnotationModel.NONNULL, AnnotationModel.OVERRIDE) else listOf(AnnotationModel.OVERRIDE),
                                parameters = emptyList(),
                                returnType = "String"
                        ),
                        className = className,
                        fields = fields
                )
        )
    }


    private fun buildBuilderFactoryMethods(className: String, builderClassModel: ClassModel, withNonnull: Boolean): List<WritableMethod> {
        return listOf(
                BuilderFactoryMethod(
                        MethodModel(
                                name = "builder",
                                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                                annotations = if (withNonnull) listOf(AnnotationModel.NONNULL) else emptyList(),
                                parameters = emptyList(),
                                returnType = builderClassModel.name
                        )
                ),
                BuilderFactoryCopyMethod(
                        MethodModel(
                                name = "builder",
                                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                                annotations = if (withNonnull) listOf(AnnotationModel.NONNULL) else emptyList(),
                                parameters = listOf(ParameterModel(
                                        name = "copy",
                                        type = className,
                                        annotations = if (withNonnull) listOf(AnnotationModel.NONNULL) else emptyList(),
                                        required = true
                                )),
                                returnType = builderClassModel.name
                        ),
                        builderClassModel
                )
        )
    }

    private fun buildBuilderClassModel(ownerClassName: String,
                                       fieldModels: List<FieldModel>,
                                       config: Config): ClassModel {
        val builderClassName = "Builder"
        val builderFields = fieldModels.map { it.withoutModifier(Modifier.FINAL) }
        return ClassModel(
                name = builderClassName,
                modifiers = listOf(Modifier.PUBLIC, Modifier.STATIC),
                fields = builderFields,
                constructor = ConstructorModel(emptyList(), listOf(Modifier.PRIVATE)),
                getters = emptyList(),
                methods = buildBuilderMethodModels(ownerClassName, builderClassName, builderFields, config.withNonnull, config.builderMethodPrefix),
                nestedClasses = emptyList()
        )
    }

    private fun buildBuilderMethodModels(ownerClassName: String,
                                         builderClassName: String,
                                         builderFields: List<FieldModel>,
                                         withNonnull: Boolean,
                                         builderMethodPrefix: String): List<WritableMethod> {
        val methods = mutableListOf<WritableMethod>()
        methods.addAll(builderFields.map { WithMethod(buildBuilderMethodModel(builderClassName, it, withNonnull, builderMethodPrefix)) })
        methods.add(
                BuilderBuildMethod(
                        MethodModel(
                                name = "build",
                                modifiers = listOf(Modifier.PUBLIC),
                                annotations = if (withNonnull) listOf(AnnotationModel.NONNULL) else emptyList(),
                                parameters = emptyList(),
                                returnType = ownerClassName
                        ),
                        builderFields
                )
        )
        return methods
    }

    private fun buildBuilderMethodModel(builderClassName: String, fieldModel: FieldModel, withNonnull: Boolean, builderMethodPrefix: String): MethodModel {
        return MethodModel(
                name = if (builderMethodPrefix.isEmpty()) fieldModel.name else builderMethodPrefix + StringUtils.fromUpperCase(fieldModel.name),
                modifiers = listOf(Modifier.PUBLIC),
                annotations = if (withNonnull) listOf(AnnotationModel.NONNULL) else emptyList(),
                parameters = listOf(
                        ParameterModel(
                                name = fieldModel.name,
                                type = fieldModel.type,
                                annotations = getAnnotationsFromFieldRequirement(fieldModel.required, withNonnull),
                                required =  fieldModel.required
                        )
                ),
                returnType = builderClassName
        )
    }

    private fun getAnnotationsFromFieldRequirement(fieldRequired: Boolean, withNonnull: Boolean): List<AnnotationModel> {
        return if (withNonnull) {
            if (fieldRequired) listOf(AnnotationModel.NONNULL) else listOf(AnnotationModel.NULLABLE)
        } else {
            emptyList()
        }
    }

}