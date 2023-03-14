package com.dentreality.spacekit.sample

import android.content.Context
import com.dentreality.spacekit.android.ext.AssetIcon
import com.dentreality.spacekit.ext.Destination
import com.dentreality.spacekit.ext.DestinationPriority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

class ProductDatabase @Inject constructor(@ApplicationContext private val context: Context) {

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
    @SerialName("item_code") override val identifier: String,
    @SerialName("name") override val itemName: String,
    @SerialName("icon") val iconIdentifier: String,
    override val priority: DestinationPriority = DestinationPriority.unspecified,
) : Destination {
    override val icon: AssetIcon
        get() = AssetIcon("icons/${iconIdentifier}.png")

    override fun toString(): String = itemName
}