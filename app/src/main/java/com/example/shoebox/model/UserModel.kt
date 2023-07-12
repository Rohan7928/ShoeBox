package com.example.shoebox.model

data class UserModel (
    @JvmField
    var name: String? = null,
    @JvmField
    var email: String? = null,
    @JvmField
    var mobile: String? = null,
    @JvmField
    var password: String? = null,
)