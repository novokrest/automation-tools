package org.onepro.code.generator.parser

import org.onepro.code.generator.dsl.ClassesDescription
import java.nio.file.Path

object ClassesParser {

    fun parse(filePath: Path): ClassesDescription = Parser.parseClasses(filePath)

}