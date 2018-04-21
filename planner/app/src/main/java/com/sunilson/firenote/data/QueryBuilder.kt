package com.sunilson.firenote.data

import com.google.firebase.firestore.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import tailoredapps.com.tameetingraumapp.data.models.Location
import tailoredapps.com.tameetingraumapp.data.models.MeetingRoom

interface EventRepository {

}

class QueryBuilder : EventRepository {
    fun loadFullRoom() : Flowable<MeetingRoom> {
        return createFlowableFromQuery(FirebaseFirestore.getInstance().collection("bla"), {
            MeetingRoom("bla")
        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
    }

    fun loadLocationPreview() : Flowable<Location> {
        return createFlowableFromQuery(FirebaseFirestore.getInstance().collection("bla"), {
            Location(1, "bla")
        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        fun <T> createFlowableFromQuery(query: Query, converter: (QuerySnapshot?) -> T) : Flowable<T> {
            return Flowable.create({ emitter ->
                var listenerRegistration : ListenerRegistration? = null
                val listener = EventListener<QuerySnapshot> { p0, p1 -> emitter.onNext(converter(p0)) }

                emitter.setCancellable {
                    if(!emitter.isCancelled && listenerRegistration != null)  listenerRegistration!!.remove()
                }

                listenerRegistration = query.addSnapshotListener(listener)
            }, BackpressureStrategy.BUFFER)
        }
    }
}