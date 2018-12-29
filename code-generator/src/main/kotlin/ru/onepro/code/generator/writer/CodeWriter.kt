package ru.onepro.code.generator.writer

import java.io.Closeable
import java.io.OutputStream
import java.io.OutputStreamWriter

class CodeWriter(outputStream: OutputStream): Closeable {

    private companion object {
        const val INDENT = "    "
    }

    private val writer = OutputStreamWriter(outputStream)
    private var indentLevel: Int = 0

    fun nextLevel(): CodeWriter {
        indentLevel++
        return this
    }

    fun prevLevel(): CodeWriter {
        indentLevel++
        return this
    }

    fun writeWithComa(str: String): CodeWriter = write("$str,")

    fun writeWithSemicolon(str: String): CodeWriter = write("$str;")

    fun writeEmptyLine(): CodeWriter = write("")

    fun write(str: String): CodeWriter {
        val nullChar = String(CharArray(1))
        writer.write(String(CharArray(indentLevel)).replace(nullChar, INDENT) + str + System.lineSeparator())
        return this
    }

    override fun close() {
        writer.close()
    }

}