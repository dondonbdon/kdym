package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class PlayItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val description: String? = null,
    val kind: String = "video", // video, audio, gallery
    val layout: String = "featuredWide", // featuredWide, shortform, audio, photo
    val thumbnailURL: String? = null,
    val mediaURL: String? = null,
    val externalURL: String? = null,
    val platform: String? = null,
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true,
    val campId: String? = null,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
