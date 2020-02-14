package org.onepro.code.generator.generator

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.onepro.code.generator.config.Config
import org.onepro.code.generator.dsl.ClassDescription
import org.onepro.code.generator.model.CheckConstructorParamsMode.RequireNonNull
import java.io.FileWriter
import java.nio.file.Path

class MustacheClassGenerator {

    var mustacheFactory: MustacheFactory = DefaultMustacheFactory()


    fun generate(outputDir: Path, clazz: ClassDescription, config: Config): Path {
        val mustache = mustacheFactory.compile("class.mustache")
        val classFilePath = outputDir.resolve("${clazz.name}.java")
        classWriter(classFilePath).use {
            mustache.execute(it, templateParams(clazz, config)).flush()
        }
        return classFilePath
    }

    private fun classWriter(classFilePath: Path) = FileWriter(classFilePath.toFile())

    private fun templateParams(clazz: ClassDescription, config: Config): Map<String, Any> =
        mapOf(
            "className" to clazz.name,
            "classNameInCamelCase" to clazz.name.decapitalize(),
            "withPackage" to config.withPackage,
            "isNonNullAnnotationUsed" to config.withNonnull,
            "isRequireNonNullUsed" to (config.checkConstructorParamsMode == RequireNonNull),
            "fields" to clazz.fields.entries.map { it.key to it.value }.withIndex().map {
                val index = it.index
                val (name, type) = it.value
                mapOf(
                    "type" to type,
                    "name" to name,
                    "isLast" to (index == clazz.fields.size - 1),
                    "nameInPascalCase" to name.capitalize(),
                    "isGetPropertyWithPrefix" to !config.isGetPropertyWithoutPrefix,
                    "doesUseClassNameAsFactoryMethod" to config.doesUseClassNameAsFactoryMethod,
                    "isBuilderFactoryInsideModelClass" to config.isBuilderFactoryInsideModelClass
                )
            }
        )

}