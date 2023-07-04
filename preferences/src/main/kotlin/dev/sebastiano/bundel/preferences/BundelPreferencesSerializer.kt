package dev.sebastiano.bundel.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.sebastiano.bundel.proto.BundelPreferences
import java.io.InputStream
import java.io.OutputStream

internal object BundelPreferencesSerializer : Serializer<BundelPreferences> {

    override val defaultValue: BundelPreferences = BundelPreferences.getDefaultInstance()

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFrom(input: InputStream): BundelPreferences {
        try {
            return BundelPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: BundelPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}
