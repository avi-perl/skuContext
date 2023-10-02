package skuContext

import com.github.doyaaaaaken.kotlincsv.client.ICsvFileWriter
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class RawEvent(val value: String, val timestamp: LocalDateTime, val userProvidedId: String?, val uuid: UUID) {
    constructor(value: String, id: String?) : this(value, LocalDateTime.now(), id, UUID.randomUUID())

    companion object {
        fun getWriter(file: File, write: ICsvFileWriter.() -> Unit) {
            return csvWriter().open(file, true, write)
        }

        fun loadFile(file: File): List<RawEvent> {
            return mutableListOf<RawEvent>().let { list ->
                csvReader().open(file.absolutePath) {
                    readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                        list.add(
                            RawEvent(
                                row["value"] ?: "",
                                LocalDateTime.parse(
                                    row["timestamp"],
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                                ),
                                row["id"],
                                UUID.fromString(row["uuid"])
                            )
                        )
                    }
                }
                list
            }
        }
    }
}