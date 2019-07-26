package ru.onepro.code.generator.generator

import ru.onepro.code.generator.config.Config
import ru.onepro.code.generator.dsl.ClassDescription
import ru.onepro.code.generator.parser.ClassesParser
import java.nio.file.Path

object ClassesGenerator {

    fun run(inputClassesFilePath: Path, outputDirPath: Path, config: Config): List<Path> {
        val classDescriptions: List<ClassDescription> = ClassesParser.parse(inputClassesFilePath).classes
        return classDescriptions.map { ClassGenerator.generate(outputDirPath, it, config) }
    }

}