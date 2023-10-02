import com.github.ajalt.clikt.core.subcommands
import clikt.CategorizeFiles
import clikt.SKUContextClikt
import clikt.logSkuInstance

fun main(args: Array<String>) = SKUContextClikt().subcommands(logSkuInstance, CategorizeFiles()).main(args)