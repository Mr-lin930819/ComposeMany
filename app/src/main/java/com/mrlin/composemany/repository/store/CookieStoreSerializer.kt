package com.mrlin.composemany.repository.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.mrlin.composemany.CookieStore
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object CookieStoreSerializer : Serializer<CookieStore> {
    override suspend fun readFrom(input: InputStream): CookieStore {
        try {
            return CookieStore.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CookieStore, output: OutputStream) {
        t.writeTo(output)
    }

    override val defaultValue: CookieStore
        get() = CookieStore.getDefaultInstance()
}