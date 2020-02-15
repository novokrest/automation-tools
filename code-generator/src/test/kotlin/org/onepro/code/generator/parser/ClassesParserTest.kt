package org.onepro.code.generator.parser

import org.amshove.kluent.should
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.onepro.code.generator.dsl.FieldDescription
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
                name shouldEqual "Node"
                fields shouldContainAll listOf("id:Long:Identifier", "title:String")
                fieldDescriptions shouldContainAll listOf(
                    FieldDescription(
                        name = "id",
                        type = "Long",
                        comment = "Identifier"
                    ),
                    FieldDescription(
                        name = "title",
                        type = "String",
                        comment = null
                    )
                )
                true
            }
            true
        }
    }

}