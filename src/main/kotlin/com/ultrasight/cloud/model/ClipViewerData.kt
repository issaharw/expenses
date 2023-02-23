package com.ultrasight.cloud.model

data class ClipViewerData(val instanceUid: String,
                          val images: List<String>?,
                          val networkResults: NetworkResults?,
                          val cineRate: Int,
                          val imagesUploadedSuccessfully: Boolean)
