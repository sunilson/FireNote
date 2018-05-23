package com.sunilson.firenote.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.parseElement
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IFirebaseRepository {
    fun loadElements(user: FirebaseUser): Single<List<Element>>
    fun loadElement(id: String, parent: String? = null): Flowable<Element?>
    fun lockElement(id: String, locked: Boolean, parent: String? = null)

    fun loadNote(id: String): Flowable<String>
    fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>>
    fun loadChecklistElements(): Flowable<List<Pair<ChangeType, ChecklistElement>>>

    fun storeNoteText(id: String, text: String)
    fun storeElement(element: Element): Completable
}

@Singleton
class FirebaseRepository @Inject constructor() : IFirebaseRepository {

    override fun storeNoteText(id: String, text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadElement(id: String, parent: String?): Flowable<Element?> {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)

        return createFlowableFromQuery(ref, { snapshot, changeType ->
            val element = snapshot?.getValue(Element::class.java)
                    ?: return@createFlowableFromQuery null
            when (changeType) {
                ChangeType.ADDED, ChangeType.CHANGED -> element
                ChangeType.REMOVED -> null
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadNote(id: String): Flowable<String> {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("contents").child(id)
        return createFlowableFromQuery(ref, { dataSnapshot, changeType ->
            return@createFlowableFromQuery dataSnapshot?.getValue(String::class.java) ?: ""
        })
    }

    override fun lockElement(id: String, locked: Boolean, parent: String?) {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)
        ref.child("locked").setValue(locked)
    }

    override fun storeElement(element: Element): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        if (element.parent != null) ref = ref.child("bundles").child(element.parent)
        return Completable.create { emitter ->
            val elementRef = ref.push()
            elementRef.setValue(element).addOnFailureListener { emitter.onError(it) }.addOnSuccessListener { emitter.onComplete() }
        }
    }

    override fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun loadChecklistElements(): Flowable<List<Pair<ChangeType, ChecklistElement>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun loadElements(user: FirebaseUser): Single<List<Element>> {
        return createSingleFromQuery(FirebaseDatabase.getInstance().reference.child("users").child(user.uid).child("elements").child("main"), {
            val result = mutableListOf<Element>()
            it?.children?.forEach {
                val tempEvent = it.parseElement()
                tempEvent.elementID = it.key
                result.add(tempEvent)
            }
            result.toList()
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    companion object {

        fun <T> createSingleFromQuery(ref: DatabaseReference, converter: (DataSnapshot?) -> T): Single<T> {
            return Single.create<T> {
                val listener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        if (!it.isDisposed) ref.removeEventListener(this)
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (!it.isDisposed) ref.removeEventListener(this)
                        it.onSuccess(converter(p0))
                    }
                }

                ref.addListenerForSingleValueEvent(listener)
            }
        }

        fun <T> createFlowableFromQuery(ref: DatabaseReference, converter: (DataSnapshot?, ChangeType) -> T): Flowable<T> {
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