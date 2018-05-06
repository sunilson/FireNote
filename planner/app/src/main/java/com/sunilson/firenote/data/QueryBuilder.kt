package com.sunilson.firenote.data

import com.google.firebase.auth.FirebaseAuth
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
    fun lockElement(id: String, locked: Boolean, parent: String? = null)

    fun loadNote(): Flowable<Note>
    fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>>
    fun loadChecklistElements() : Flowable<List<Pair<ChangeType, ChecklistElement>>>
}

@Singleton
class QueryBuilder @Inject constructor() : FirebaseRepository {

    override fun loadElement(id: String, parent: String?): Flowable<Element?> {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if(parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)

        return createFlowableFromQuery(ref, { snapshot, changeType ->
            val element = snapshot?.getValue(Element::class.java) ?: return@createFlowableFromQuery null
            when(changeType) {
                ChangeType.ADDED, ChangeType.CHANGED -> element
                ChangeType.REMOVED -> null
            }
        })
    }

    override fun lockElement(id: String, locked: Boolean, parent: String?) {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if(parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)
        ref.child("locked").setValue(locked)
    }

    override fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>> {
        return createFlowableFromQuery(FirebaseDatabase.getInstance().reference, {

        })
    }


    override fun loadChecklistElements(): Flowable<List<Pair<ChangeType, ChecklistElement>>> {
        return createFlowableFromQuery(FirebaseDatabase.getInstance().reference, {

        })
    }

    override fun loadElements(): Single<List<Element>> {
        return createSingleFromQuery(FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements").child("main"), {
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

        fun <T>createFlowableFromQuery(ref: DatabaseReference, converter: (DataSnapshot?, ChangeType) -> T): Flowable<T> {
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