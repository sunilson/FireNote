package com.sunilson.firenote.data

import android.sax.Element
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.*
import com.sunilson.firenote.data.firebaseSnapshotModels.FirebaseSnapshot
import com.sunilson.firenote.presentation.shared.ChangeType
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import tailoredapps.com.tameetingraumapp.data.models.Location
import tailoredapps.com.tameetingraumapp.data.models.MeetingRoom

interface EventRepository {
    fun loadElements() : Flowable<List<Pair<ChangeType, Element>>>
}

class QueryBuilder : EventRepository {

    override fun loadElements() : Flowable<List<Pair<ChangeType, Element>>> {

    }

    companion object {
        fun <T> createFlowableFromQuery(ref: DatabaseReference, converter: (DataSnapshot?) -> FirebaseSnapshot<T>) : Flowable<FirebaseSnapshot<T>> {
            return Flowable.create({ emitter ->
                val listener = object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) = emitter.onNext(converter(p0))
                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) = emitter.onNext(converter(p0))
                    override fun onChildRemoved(p0: DataSnapshot?) = emitter.onNext(converter(p0))
                }
                emitter.setCancellable {
                    if(!emitter.isCancelled)  ref.removeEventListener(listener)
                }
                ref.addChildEventListener(listener)
            }, BackpressureStrategy.BUFFER)
        }
    }
}