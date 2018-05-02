package com.sunilson.firenote.data.models

data class BundleBin(var notes: HashMap<String, Note> = hashMapOf(), var checklists: HashMap<String, Checklist> = hashMapOf())