package org.onepro.code.generator.generator

import org.amshove.kluent.shouldEqualTo
import org.apache.commons.io.FileUtils
import org.onepro.code.generator.config.Config
import org.testng.AssertJUnit
import org.testng.annotations.Test
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class ClassesGeneratorTest {

    @Test
    fun `should generate classes`() {
        val inputFile = Paths.get(javaClass.getResource("classes.yml").toURI())
        val outputDir = Files.createTempDirectory("code-generator-")
        val generatedFiles = ClassesGenerator.run(inputFile, outputDir, Config())

        generatedFiles.size shouldEqualTo 2

        checkGeneratedFile(generatedFiles[0], "JsonNode.java")
        checkGeneratedFile(generatedFiles[1], "Node.java")
    }

    private fun checkGeneratedFile(generatedFilePath: Path, expectedFileResource: String) {
        val actualFile = readFile(generatedFilePath)
        val expectedFile = readFile(Paths.get(javaClass.getResource(expectedFileResource).toURI()))
        assertJsonEquals(expectedFile, actualFile)
    }

    private fun assertJsonEquals(expected: String, actual: String) {
        AssertJUnit.assertEquals(expected, actual)
    }

    private fun readFile(path: Path) = FileUtils.readFileToString(File(path.toUri()), StandardCharsets.UTF_8)

}