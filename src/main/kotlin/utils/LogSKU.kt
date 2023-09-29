package utils

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess

data class Entry(val value: String, val timestamp: LocalDateTime, val userId: String?, val uuid: UUID) {
    companion object {
        fun create(value: String, id: String?) = Entry(value, LocalDateTime.now(), id, UUID.randomUUID())

        fun loadFile(file: File): List<Entry> {
            return listOf<Entry>().apply {
                var lineNumber = 0
                file.forEachLine {
                    println(it)
                    if (lineNumber > 0) {
                        it.split(",").map { it.trim() }.let { row ->
                            add(Entry(
                                row[3],
                                LocalDateTime.parse(row[1],
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")),
                                row[2],
                                UUID.fromString(row[0])
                            ))
                        }
                    }
                    lineNumber++
                }
            }
        }
    }
}

fun OutputStream.writeEntry(entry: Entry) {
    bufferedWriter().let {
        it.appendLine("${entry.uuid}, ${entry.timestamp}, ${entry.userId ?: ""}, ${entry.value}")
        it.flush()
    }
}

class LogSKU : CliktCommand(help = "Log entries to a file with timestamps") {
    private val config by requireObject<Map<String, String>>()
    private val scannerId by option(help = "Identification for independent tracking")

    override fun run() {
        config["logFile"]?.let { logFile ->
            File(logFile).let {
                if (!it.exists()) {
                    it.createNewFile()
                    it.appendText("uuid, timestamp, id, value\n")
                }
            }

            FileOutputStream(logFile, true).let { file ->
                while (true) {
                    readlnOrNull()?.let { input ->
                        if (input.equals("exit", ignoreCase = true)) {
                            exitProcess(0)
                        }

                        file.writeEntry(Entry.create(input, scannerId))
                    }
                }
            }
        }
    }
}