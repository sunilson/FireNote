package com.sunilson.firenote.data.models

import kotlin.Comparator

object ElementComparators {
    fun sortByCategory() : Comparator<Element> = Comparator { o1, o2 ->
        when {
            o1.category.name < o2.category.name -> -1
            o1.category.name > o2.category.name -> 1
            else -> 0
        }
    }

    fun sortByName(descending: Boolean): Comparator<Element> = Comparator { o1: Element, o2: Element ->
        var result = when {
            o1.creationDate.after(o2.creationDate) -> -1
            o1.creationDate.before(o2.creationDate) -> 1
            else -> 0
        }
        if(descending) result *= -1
        result
    }

    fun sortByDate(descending: Boolean): Comparator<Element> = Comparator { o1: Element, o2: Element ->
        var result = when {
            o1.title.compareTo(o2.title, true) < 0 -> -1
            o1.title.compareTo(o2.title, true) > 0 -> 1
            else -> 0
        }
        if(descending) result *= -1
        result
    }
}