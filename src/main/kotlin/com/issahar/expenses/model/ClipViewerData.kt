package com.issahar.expenses.model

data class ClipViewerData(val instanceUid: String,
                          val images: List<String>?,
                          val networkResults: NetworkResults?,
                          val cineRate: Int,
                          val imagesUploadedSuccessfully: Boolean)
