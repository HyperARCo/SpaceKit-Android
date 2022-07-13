package com.dentreality.spacekit.sample

import android.content.Context
import com.dentreality.spacekit.common.AssetIcon
import com.dentreality.spacekit.ext.Destination
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ProductDatabase(private val context: Context) {

    val productList: List<Product> by lazy {
        val sampleFileName = "sampleProducts.json"
        val sampleProductsInputStream = context.assets.open(sampleFileName)
        val productJson = getStreamAsString(sampleProductsInputStream, "UTF-8")
        Json.decodeFromString(productJson)
    }

    companion object {
        @Throws(IOException::class)
        fun getStreamAsString(fis: InputStream, encoding: String): String {
            BufferedReader(InputStreamReader(fis, encoding)).use { reader ->
                val builder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                    builder.append('\n')
                }
                return builder.toString()
            }
        }
    }
}

@Serializable
data class Product(
    @SerialName("upc") override val identifier: String,
    @SerialName("name") override val itemName: String,
    @SerialName("icon") val iconIdentifier: String
) : Destination {
    override val icon: AssetIcon
        get() = AssetIcon("icons/${iconIdentifier}.png")

    override fun toString(): String = itemName
}