package org.onepro.code.generator

import org.onepro.code.generator.config.Config
import org.onepro.code.generator.generator.ClassesGenerator
import org.onepro.code.generator.parser.Parser
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
    val inputClassesFilePath = Paths.get("input/classes.yml")
    if (!Files.exists(inputClassesFilePath)) {
        println("File with classes descriptions was not found: path=$inputClassesFilePath")
    }
    val configPropValue = System.getProperty("config")
    val config = if (configPropValue == null) Config() else Parser.parseConfig(Path.of("config", "${configPropValue.toLowerCase()}.yml"))
    val generatedFiles = ClassesGenerator.run(inputClassesFilePath, Paths.get("output"), config)
    println("Generated files:")
    generatedFiles.forEach { println("\t$it") }
}