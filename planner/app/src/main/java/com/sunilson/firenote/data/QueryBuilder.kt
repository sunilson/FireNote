package com.sunilson.firenote.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sunilson.firenote.data.models.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface FirebaseRepository {
    fun loadElements(user: FirebaseUser): Single<List<Element>>
    fun loadElement(id: String, parent: String? = null) : Flowable<Element?>

    fun loadNote(): Flowable<Note>
    fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>>
    fun loadChecklistElements() : Flowable<List<Pair<ChangeType, ChecklistElement>>>
}

@Singleton
class QueryBuilder @Inject constructor() : FirebaseRepository {

    override fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>> {
        return createFlowableFromQuery(FirebaseDatabase.getInstance().reference, {

        })
    }


    override fun loadChecklistElements(): Flowable<List<Pair<ChangeType, ChecklistElement>>> {
        return createFlowableFromQuery(FirebaseDatabase.getInstance().reference, {

        })
    }

    override fun loadElements(user: FirebaseUser): Single<List<Element>> {
        return createSingleFromQuery(FirebaseDatabase.getInstance().reference.child("users").child(user.uid).child("elements").child("main"), {
            val result = mutableListOf<Element>()
            it?.children?.forEach {
                val tempEvent = it.getValue(Element::class.java)
                if(tempEvent != null){
                    tempEvent.elementID = it.key
                    result.add(tempEvent)
                }
            }
            result.toList()
        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
    }

    companion object {

        fun <T> createSingleFromQuery(ref: DatabaseReference, converter: (DataSnapshot?) -> T): Single<T> {
            return Single.create<T> {
                val listener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        if (!it.isDisposed) ref.removeEventListener(this)
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if(!it.isDisposed) ref.removeEventListener(this)
                        it.onSuccess(converter(p0))
                    }
                }

                ref.addListenerForSingleValueEvent(listener)
            }
        }

        fun <T> createFlowableFromQuery(ref: DatabaseReference, converter: (DataSnapshot?, ChangeType) -> Pair<ChangeType, T>): Flowable<Pair<ChangeType, T>> {
            return Flowable.create({ emitter ->
                val listener = object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) = emitter.onNext(converter(p0, ChangeType.CHANGED))
                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) = emitter.onNext(converter(p0, ChangeType.ADDED))
                    override fun onChildRemoved(p0: DataSnapshot?) = emitter.onNext(converter(p0, ChangeType.REMOVED))
                }
                emitter.setCancellable {
                    if (!emitter.isCancelled) ref.removeEventListener(listener)
                }
                ref.addChildEventListener(listener)
            }, BackpressureStrategy.BUFFER)
        }
    }
}