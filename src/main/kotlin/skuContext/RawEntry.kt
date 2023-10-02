package skuContext

import java.io.File
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class RawEntry(val value: String, val timestamp: LocalDateTime, val userProvidedId: String?, val uuid: UUID) {
    constructor(value: String, id: String?) : this(value, LocalDateTime.now(), id, UUID.randomUUID())

    companion object {
        fun loadFile(file: File): List<RawEntry> {
            return mutableListOf<RawEntry>().let { list ->
                var lineNumber = 0
                file.forEachLine { rawLine ->
                    println(rawLine)
                    if (lineNumber > 0) {
                        rawLine.split(",").map { it.trim() }.let { row ->
                            list.add(
                                RawEntry(
                                    row[3],
                                    LocalDateTime.parse(
                                        row[1],
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                                    ),
                                    row[2],
                                    UUID.fromString(row[0])
                                )
                            )
                        }
                    }
                    lineNumber++
                }
                list
            }
        }
    }
}

fun OutputStream.writeRawEntry(entry: RawEntry) {
    bufferedWriter().let {
        it.appendLine("${entry.uuid}, ${entry.timestamp}, ${entry.userProvidedId ?: ""}, ${entry.value}")
        it.flush()
    }
}