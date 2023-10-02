package skuContext

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
class Event(
    val value: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startTimestamp: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endTimestamp: LocalDateTime,
    val userProvidedId: String?,
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID
) {
    companion object {
        fun fromRawEvents(rawEvents: List<RawEvent>): List<Event> {
            return mutableListOf<Event>().apply {
                for (i in rawEvents.indices) {
                    rawEvents[i].let {
                        add(
                            Event(
                                it.value,
                                it.timestamp,
                                if (i < rawEvents.size - 1) rawEvents[i + 1].timestamp else it.timestamp,
                                it.userProvidedId,
                                it.uuid
                            )
                        )
                    }
                }
            }
        }
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(
            decoder.decodeString(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
        )
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
}