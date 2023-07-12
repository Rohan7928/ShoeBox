package com.example.shoebox.model

import java.io.Serializable

class DataModel : Serializable {
    @JvmField
    var name: String? = null
    @JvmField
    var price: String? = null
    var description: String? = null
    var id: Int? = null
    @JvmField
    var image: String? = null
    @JvmField
    var count: Int?  = null

    constructor() {}
    constructor(
        name: String?,
        price: String?,
        description: String?,
        image: String?,
        id: Int?,
        count: Int?
    ) {
        this.name = name
        this.price = price
        this.description = description
        this.image = image
        this.id = id
        this.count = count
    }
}