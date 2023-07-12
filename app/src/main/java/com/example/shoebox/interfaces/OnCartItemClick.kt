package com.example.shoebox.interfaces

import com.example.shoebox.model.DataModel

interface OnCartItemClick {
    fun addItemValue(dataModel: DataModel?)
    fun deleteItemValue(dataModel: DataModel?)
}