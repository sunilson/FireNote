package com.sunilson.firenote.data.models

data class SortingMethod(
        val name: String,
        val icon: Int,
        val comparator: Comparator<Element>
)