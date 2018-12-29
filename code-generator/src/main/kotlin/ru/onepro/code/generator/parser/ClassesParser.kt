package ru.onepro.code.generator.parser

import com.fasterxml.jackson.databind.ObjectMapper
import ru.onepro.code.generator.dsl.ClassesDescription
import java.io.File
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path

object ClassesParser {

    private val objectMapper = ObjectMapper(YAMLFactory())
            .registerKotlinModule()

    fun parse(filePath: Path): ClassesDescription =
            try {
                objectMapper.readValue(File(filePath.toString()))
            } catch (e: Exception) {
                throw RuntimeException("Bad file format: path=$filePath")
            }

}