package ru.onepro.code.generator

import ru.onepro.code.generator.generator.ClassesGenerator
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val inputClassesFilePath = Paths.get("input/classes.yml")
    if (!Files.exists(inputClassesFilePath)) {
        println("File with classes descriptions was not found: path=$inputClassesFilePath")
    }
    val withoutNonnullPropValue = System.getProperty("withoutNonnull")
    val withNonnull: Boolean = withoutNonnullPropValue == null
    println("'$withoutNonnullPropValue': $withNonnull")
    val generatedFiles = ClassesGenerator.run(inputClassesFilePath, Paths.get("output"), withNonnull)
    println("Generated files:")
    generatedFiles.forEach { println("\t$it") }
}