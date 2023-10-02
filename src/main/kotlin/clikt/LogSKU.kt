package clikt

import skuContext.RawEntry
import skuContext.writeRawEntry
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import java.io.FileOutputStream
import kotlin.system.exitProcess

class LogSKU : CliktCommand(help = "Log entries to a file with timestamps") {
    private val config by requireObject<Map<String, String>>()
    private val userProvidedId by option(help = "A user provided value that will be added to the logged entry")

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
                    Runtime.getRuntime().addShutdownHook(Thread {
                        file.writeRawEntry(RawEntry("exit", userProvidedId))
                        println("Program was terminated by the user.")
                    })

                    readlnOrNull()?.let { input ->
                        file.writeRawEntry(RawEntry(input, userProvidedId))

                        if (input.equals("exit", ignoreCase = true)) {
                            exitProcess(0)
                        }
                    }
                }
            }
        }
    }
}

val logSkuInstance = LogSKU()