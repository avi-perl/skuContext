import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import kotlin.system.exitProcess

data class Entry(val value: String, val timestamp: LocalDateTime) {
    companion object {
        fun create(value: String) = Entry(value, LocalDateTime.now())
    }
}

fun OutputStream.writeEntry(entry: Entry) {
    bufferedWriter().let {
        it.appendLine("${entry.timestamp.toString()}, ${entry.value}")
        it.flush()
    }
}


fun main(args: Array<String>) {
    println("Begin entering SKUs:")

    FileOutputStream("output.csv", true).let { file ->
        while (true) {
            readlnOrNull()?.let { input ->
                if (input.equals("exit", ignoreCase = true)) {
                    exitProcess(0)
                }

                file.writeEntry(Entry.create(input))
                println("Text logged to '$input'.")
            }
        }
    }
}