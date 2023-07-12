package com.example.shoebox.model

class UserModel {
    constructor(name: String?, email: String?, password: String?, mobile: String?) {
        this.name = name
        this.password = password
        this.email = email
        this.mobile = mobile
    }

    @JvmField
    var name: String? = null
    @JvmField
    var email: String? = null
    @JvmField
    var mobile: String? = null
    @JvmField
    var password: String? = null
}