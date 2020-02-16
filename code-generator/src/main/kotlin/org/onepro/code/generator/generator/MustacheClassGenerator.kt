package org.onepro.code.generator.generator

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.onepro.code.generator.config.Config
import org.onepro.code.generator.dsl.ClassDescription
import org.onepro.code.generator.dsl.FieldDescription
import org.onepro.code.generator.model.CheckConstructorParamsMode.RequireNonNull
import java.io.FileWriter
import java.nio.file.Path

class MustacheClassGenerator {

    private val mustacheFactory: MustacheFactory = DefaultMustacheFactory()

    fun generate(outputDir: Path, clazz: ClassDescription, config: Config): Path {
        val template = chooseTemplate(clazz, config)
        val mustache = mustacheFactory.compile(template.resourceName)
        val classFilePath = outputDir.resolve("${clazz.name}.java")
        classWriter(classFilePath).use {
            mustache.execute(it, template.params).flush()
        }
        return classFilePath
    }

    private fun chooseTemplate(clazz: ClassDescription, config: Config): Template =
        if (clazz.tiny == true) {
            Template("class_tiny.mustache", tinyClassTemplateParams(clazz))
        } else {
            Template("class.mustache", classTemplateParams(clazz, config))
        }

    private fun tinyClassTemplateParams(clazz: ClassDescription): Map<String, Any> =
        mapOf(
            "className" to clazz.name,
            "classNameInCamelCase" to clazz.name.decapitalize()
        )

    private fun classTemplateParams(clazz: ClassDescription, config: Config): Map<String, Any> =
        mapOf(
            "className" to clazz.name,
            "classNameInCamelCase" to clazz.name.decapitalize(),
            "withPackage" to config.withPackage,
            "isNonNullAnnotationUsed" to config.withNonnull,
            "isRequireNonNullUsed" to (config.checkConstructorParamsMode == RequireNonNull),
            "doesUseClassNameAsFactoryMethod" to config.doesUseClassNameAsFactoryMethod,
            "isBuilderFactoryInsideModelClass" to config.isBuilderFactoryInsideModelClass,
            "withJson" to (clazz.json ?: false),
            "withEquals" to (clazz.equal ?: false),
            "fields" to clazz.fieldDescriptions!!.withIndex().map {
                val index = it.index
                val field = it.value
                mapOf(
                    "type" to field.type,
                    "name" to field.name,
                    "isRequired" to isFieldRequired(clazz, field),
                    "hasComment" to (field.comment != null),
                    "comment" to (field.comment ?: ""),
                    "isLast" to (index == clazz.fields!!.size - 1),
                    "isFirst" to (index == 0),
                    "nameInPascalCase" to field.name.capitalize(),
                    "isGetPropertyWithPrefix" to !config.isGetPropertyWithoutPrefix
                )
            }
        )

    private fun isFieldRequired(clazz: ClassDescription, field: FieldDescription): Boolean =
        clazz.required?.contains(field.name) ?: true

    private fun classWriter(classFilePath: Path) = FileWriter(classFilePath.toFile())

    private data class Template(
        val resourceName: String,
        val params: Map<String, Any?>
    )
}