@file:Suppress("unused")

package com.neilturner.aerialviews.providers

import android.content.Context
import android.net.Uri
import com.neilturner.aerialviews.R
import com.neilturner.aerialviews.models.enums.MediaItemType
import com.neilturner.aerialviews.models.enums.MediaType
import com.neilturner.aerialviews.models.enums.ProviderType
import com.neilturner.aerialviews.models.enums.SearchType
import com.neilturner.aerialviews.models.prefs.LocalMediaPrefs
import com.neilturner.aerialviews.models.videos.AerialMedia
import com.neilturner.aerialviews.models.videos.VideoMetadata
import com.neilturner.aerialviews.utils.FileHelper
import com.neilturner.aerialviews.utils.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalMediaProvider(context: Context, private val prefs: LocalMediaPrefs) : MediaProvider(context) {

    override val type = ProviderType.LOCAL

    override val enabled: Boolean
        get() = prefs.enabled

    override suspend fun fetchMedia(): List<AerialMedia> {
        return if (prefs.searchType == SearchType.MEDIA_STORE) {
            mediaStoreFetch().first
        } else {
            folderAccessFetch().first
        }
    }

    override suspend fun fetchTest(): String {
        return if (prefs.searchType == SearchType.MEDIA_STORE) {
            mediaStoreFetch().second
        } else {
            folderAccessFetch().second
        }
    }

    override suspend fun fetchMetadata(): List<VideoMetadata> {
        return emptyList()
    }

    private suspend fun folderAccessFetch(): Pair<List<AerialMedia>, String> {
        val res = context.resources
        val selected = mutableListOf<String>()
        val media = mutableListOf<AerialMedia>()
        val excluded: Int
        val images: Int

        if (prefs.legacy_volume.isEmpty()) {
            return Pair(media, res.getString(R.string.local_videos_legacy_no_volume))
        }

        if (prefs.legacy_folder.isEmpty()) {
            return Pair(media, res.getString(R.string.local_videos_legacy_no_folder))
        }

        val files = folderAccessVideosAndImages()

        if (files.isEmpty()) {
            return Pair(media, res.getString(R.string.local_videos_legacy_no_files_found))
        }

        // Only pick videos
        if (prefs.mediaType != MediaType.IMAGES) {
            selected.addAll(
                files.filter { file ->
                    FileHelper.isSupportedVideoType(file)
                }
            )
        }
        val videos = selected.size

        // Only pick images
        if (prefs.mediaType != MediaType.VIDEOS) {
            selected.addAll(
                files.filter { file ->
                    FileHelper.isSupportedImageType(file)
                }
            )
        }
        images = selected.size - videos
        excluded = files.size - selected.size

        // Create media list, adding media type
        for (file in selected) {
            val uri = Uri.parse(file)
            val item = AerialMedia(uri)
            if (FileHelper.isSupportedVideoType(file)) {
                item.type = MediaItemType.VIDEO
            } else if (FileHelper.isSupportedImageType(file)) {
                item.type = MediaItemType.IMAGE
            }
            media.add(item)
        }

        var message = String.format(res.getString(R.string.local_media_test_summary1), files.size) + "\n"
        message += String.format(res.getString(R.string.local_media_test_summary2), excluded) + "\n"
        if (prefs.mediaType != MediaType.IMAGES) {
            message += String.format(res.getString(R.string.local_media_test_summary3), videos) + "\n"
        }
        if (prefs.mediaType != MediaType.VIDEOS) {
            message += String.format(res.getString(R.string.local_media_test_summary4), images) + "\n"
        }
        message += String.format(res.getString(R.string.local_media_test_summary6), media.size)
        return Pair(media, message)
    }

    private suspend fun folderAccessVideosAndImages(): List<String> = withContext(Dispatchers.IO) {
        val folders = mutableListOf<String>()
        val found = mutableListOf<File>()

        if (prefs.legacy_volume.contains("/all", false)) {
            val vols = StorageHelper.getStoragePaths(context)
            val values = vols.map { it.key }.toTypedArray()
            for (entry in values) {
                folders.add("$entry${prefs.legacy_folder}")
            }
        } else {
            folders.add("${prefs.legacy_volume}${prefs.legacy_folder}")
        }

        for (folder in folders) {
            val directory = File(folder)
            if (!directory.exists() || !directory.isDirectory) {
                continue
            }
            val files = directory.listFiles()
            if (!files.isNullOrEmpty()
            ) {
                found.addAll(
                    files.filter { file ->
                        val filename = file.name.split("/").last()
                        !FileHelper.isDotOrHiddenFile(filename)
                    }
                )
            }
        }
        return@withContext found.map { item -> item.absolutePath }
    }

    @Suppress("JoinDeclarationAndAssignment")
    private suspend fun mediaStoreFetch(): Pair<List<AerialMedia>, String> {
        val res = context.resources!!
        val selected = mutableListOf<String>()
        val media = mutableListOf<AerialMedia>()
        val excluded: Int
        val filtered: Int
        val images: Int
        val videos: Int

        if (prefs.filter_folder.isEmpty() &&
            prefs.filter_enabled
        ) {
            return Pair(media, res.getString(R.string.local_videos_media_store_no_folder))
        }

        val files = mediaStoreVideosAndImages()

        // Add video
        if (prefs.mediaType != MediaType.IMAGES) {
            selected.addAll(
                files.filter { file ->
                    FileHelper.isSupportedVideoType(file)
                }
            )
        }
        videos = selected.size

        // Add images
        if (prefs.mediaType != MediaType.VIDEOS) {
            selected.addAll(
                files.filter { file ->
                    FileHelper.isSupportedImageType(file)
                }
            )
        }
        images = selected.size - videos
        excluded = files.size - selected.size

        // Apply folder filter
        for (file in selected) {
            val uri = Uri.parse(file)
            if (prefs.filter_enabled && FileHelper.shouldFilter(uri, prefs.filter_folder)) {
                continue
            }
            media.add(AerialMedia(uri))
        }
        filtered = selected.size - media.size

        var message = String.format(res.getString(R.string.local_media_test_summary1), files.size) + "\n"
        message += String.format(res.getString(R.string.local_media_test_summary2), excluded) + "\n"
        if (prefs.mediaType != MediaType.IMAGES) {
            message += String.format(res.getString(R.string.local_media_test_summary3), videos) + "\n"
        }
        if (prefs.mediaType != MediaType.VIDEOS) {
            message += String.format(res.getString(R.string.local_media_test_summary4), images) + "\n"
        }
        message += String.format(res.getString(R.string.local_media_test_summary5), filtered) + "\n"
        message += String.format(res.getString(R.string.local_media_test_summary6), media.size)
        return Pair(media, message)
    }

    private suspend fun mediaStoreVideosAndImages(): List<String> = withContext(Dispatchers.IO) {
        val files = FileHelper.findLocalVideos(context) +
            FileHelper.findLocalImages(context)

        return@withContext files.filter { item ->
            !FileHelper.isDotOrHiddenFile(item)
        }
    }

    companion object {
        private const val TAG = "LocalMediaProvider"
    }
}
