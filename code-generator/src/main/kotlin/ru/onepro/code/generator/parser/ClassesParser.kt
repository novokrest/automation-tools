package ru.onepro.code.generator.parser

import ru.onepro.code.generator.dsl.ClassesDescription
import java.nio.file.Path

object ClassesParser {

    fun parse(filePath: Path): ClassesDescription = Parser.parseClasses(filePath)

}