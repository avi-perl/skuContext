package clikt

import skuContext.RawEvent
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import kotlin.system.exitProcess

class LogSKU : CliktCommand(help = "Log entries to a file with timestamps") {
    private val config by requireObject<Map<String, String>>()
    private val userProvidedId by option(help = "A user provided value that will be added to the logged event")

    override fun run() {
        config["logFile"]?.let { logFile ->
            RawEvent.getWriter(File(logFile)) {
                while (true) {
                    Runtime.getRuntime().addShutdownHook(Thread {
                        writeRow(RawEvent("exit", userProvidedId))
                        println("Program was terminated by the user.")
                    })

                    readlnOrNull()?.let { input ->
                        writeRow(RawEvent(input, userProvidedId))

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