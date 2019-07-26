package ru.onepro.code.generator.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ru.onepro.code.generator.config.Config
import ru.onepro.code.generator.dsl.ClassesDescription
import java.io.File
import java.nio.file.Path

object Parser {

    private val objectMapper = ObjectMapper(YAMLFactory())
            .registerKotlinModule()

    fun parseClasses(filePath: Path): ClassesDescription = parse(filePath)

    fun parseConfig(filePath: Path): Config = parse(filePath)

    private inline fun <reified T> parse(filePath: Path): T {
        return try {
            objectMapper.readValue(File(filePath.toString()))
        } catch (e: Exception) {
            throw RuntimeException("Bad file format: path=$filePath")
        }
    }

}