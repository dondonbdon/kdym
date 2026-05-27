package dev.bti.kdym.data.local.serializers

import com.google.firebase.Timestamp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TimestampSerializer : KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString("${value.seconds},${value.nanoseconds}")
    }

    override fun deserialize(decoder: Decoder): Timestamp {
        val parts = decoder.decodeString().split(",")
        return Timestamp(parts[0].toLong(), parts[1].toInt())
    }
}
