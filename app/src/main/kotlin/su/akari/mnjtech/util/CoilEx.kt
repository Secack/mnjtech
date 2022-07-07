package su.akari.mnjtech.util

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Base64
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options

class Base64ImageFetcher(
    private val data: Uri,
    private val options: Options
) : Fetcher {
    override suspend fun fetch(): FetchResult = DrawableResult(
        drawable = BitmapDrawable(
            options.context.resources,
            Base64.decode(data.toString().substringAfter(','), Base64.DEFAULT)
                .let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        ),
        isSampled = false,
        dataSource = DataSource.MEMORY
    )

    object Factory : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher =
            Base64ImageFetcher(data, options)
    }
}