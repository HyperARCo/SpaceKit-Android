package com.dentreality.spacekit.sample.common

import android.content.Context
import java.io.File

class CachedAssetFile(private val context: Context, private val assetFilename: String) {

    fun getFile(): File {
        val destination = File(context.cacheDir, assetFilename)
        destination.parentFile?.mkdirs()

        val fileInputStream = context.assets.open(assetFilename)

        fileInputStream.use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return destination
    }
}