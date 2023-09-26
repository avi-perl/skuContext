import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import kotlin.system.exitProcess

data class Entry(val value: String, val timestamp: LocalDateTime, val id: String?) {
    companion object {
        fun create(value: String, id: String?) = Entry(value, LocalDateTime.now(), id)
    }
}

fun OutputStream.writeEntry(entry: Entry) {
    bufferedWriter().let {
        it.appendLine("${entry.timestamp.toString()}, ${entry.value}, ${entry.id ?: ""}")
        it.flush()
    }
}

class SKUContext() : CliktCommand() {
    override fun run() = Unit
}

class LogSKU : CliktCommand(help = "Log entries to a file with timestamps") {
    private val file by argument(help = "The CSV file that your entries should be saved in").file(
        canBeDir = false,
        mustBeWritable = true
    )
    private val scannerId by option(help = "Identification for independent tracking")

    override fun run() {
        File(file.name).let {
            if (!it.exists()) {
                it.createNewFile()
                it.appendText("timestamp, id, value\n")
            }
        }

        FileOutputStream(file.name, true).let { file ->
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


fun main(args: Array<String>) = SKUContext().subcommands(LogSKU()).main(args)