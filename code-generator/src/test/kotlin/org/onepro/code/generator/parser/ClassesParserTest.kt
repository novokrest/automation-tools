package org.onepro.code.generator.parser

import org.amshove.kluent.should
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.testng.annotations.Test
import java.nio.file.Paths

class ClassesParserTest  {

    @Test
    fun shouldParseClasses() {
        val classesFile = Paths.get(javaClass.getResource("classes.yml").path)
        val classes = ClassesParser.parse(classesFile)

        classes should {
            this.classes.shouldNotBeEmpty()
            this.classes[0] should {
                name shouldEqual "Stub"
                fields.shouldNotBeEmpty()
                fields shouldContain Pair("id", "Long")
                fields shouldContain Pair("title", "String")
                true
            }
            true
        }
    }

}