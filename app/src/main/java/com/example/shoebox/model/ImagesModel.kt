package com.example.shoebox.model

import java.io.Serializable

data class ImagesModel(
    val images: List<String>? = listOf<String>(),
) : Serializable