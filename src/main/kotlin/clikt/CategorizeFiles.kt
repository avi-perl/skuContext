package clikt

import skuContext.RawEvent
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.boolean
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

class CategorizeFiles : CliktCommand(help = "Categorize files based on the entries in the log file") {
    private val config by requireObject<Map<String, String>>()
    private val filesDir by argument(help = "Path to a dir of files to categorize").file(
        mustExist = true,
        canBeFile = false,
        mustBeReadable = true,
    )
    private val recursive by option(help = "Explore the directory recursively").boolean().default(false)

    private fun listFiles(dir: File, allowRecursive: Boolean = true, handleFile: (File) -> Unit) {
        dir.listFiles()?.forEach { file ->
            if (file.isFile) {
                handleFile(file)
            } else if (allowRecursive && file.isDirectory) {
                listFiles(file, true, handleFile)
            }
        }
    }

    override fun run() {
        println(RawEvent.loadFile(File(config["logFile"] ?: "")))
        listFiles(filesDir, recursive) {
            Files.readAttributes(FileSystems.getDefault().getPath(it.path), BasicFileAttributes::class.java).let { metadata ->
                println(metadata.creationTime())
            }
        }
    }
}