package org.onepro.code.generator.generator

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.apache.commons.io.FileUtils
import org.onepro.code.generator.config.Config
import org.testng.annotations.Test
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ClassesGeneratorTest {

    @Test
    fun `should generate class`() {
        val inputFile = Paths.get(javaClass.getResource("classes.yml").toURI())
        val outputDir = Files.createTempDirectory("code-generator-")
        val generatedFiles = ClassesGenerator.run(inputFile, outputDir, Config())

        generatedFiles.size shouldEqualTo 1
        val generatedFile = generatedFiles[0]

        val expectedFile = Paths.get(javaClass.getResource("Node.java").toURI())
        readFile(generatedFile) shouldEqual readFile(expectedFile)
    }

    private fun readFile(path: Path) = FileUtils.readFileToString(File(path.toUri()), StandardCharsets.UTF_8)

}