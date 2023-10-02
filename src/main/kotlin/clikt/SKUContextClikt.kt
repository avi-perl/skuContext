package clikt

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

class SKUContextClikt : CliktCommand(name = "skuContext") {
    private val logFile by argument(help = "The CSV file that your entries should be saved in")
        .file(canBeDir = false)

    private val config by findOrSetObject { mutableMapOf<String, String>() }

    override fun run() {
        // For all commands except the logging creation, the file must exist.
        // TODO: Allow for when a subcommand --help is called
        if (currentContext.invokedSubcommand != logSkuInstance && !File(logFile.name).exists()) {
            throw CliktError("File does not exist: ${logFile.name}")
        }

        config["logFile"] = logFile.name
    }
}