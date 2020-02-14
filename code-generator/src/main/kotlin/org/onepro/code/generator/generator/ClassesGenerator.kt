package org.onepro.code.generator.generator

import org.onepro.code.generator.config.Config
import org.onepro.code.generator.dsl.ClassDescription
import org.onepro.code.generator.parser.ClassesParser
import java.nio.file.Path

object ClassesGenerator {

    fun run(inputClassesFilePath: Path, outputDirPath: Path, config: Config): List<Path> {
        val classDescriptions: List<ClassDescription> = ClassesParser.parse(inputClassesFilePath).classes
        return classDescriptions.map {
            if (config.useMustache) {
                MustacheClassGenerator().generate(outputDirPath, it, config)
            } else {
                ClassGenerator.generate(outputDirPath, it, config)
            }
        }
    }

}