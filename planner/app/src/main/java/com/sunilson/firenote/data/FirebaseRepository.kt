package com.sunilson.firenote.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.data.models.FirebaseElement
import com.sunilson.firenote.presentation.shared.storeElement
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IFirebaseRepository {
    fun loadElements(user: FirebaseUser): Flowable<Pair<ChangeType, Element>?>
    fun loadElement(id: String, parent: String? = null): Flowable<Element?>
    fun lockElement(id: String, locked: Boolean, parent: String? = null): Completable
    fun loadNoteContent(id: String): Flowable<String>
    fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>>

    fun loadChecklistElements(id: String): Flowable<Pair<ChangeType, ChecklistElement>>
    fun addChecklistElement(elementID: String, checklistElement: ChecklistElement): Completable
    fun updateChecklistElement(elementID: String, checklistElement: ChecklistElement): Completable
    fun removeChecklistElement(elementID: String, checklistElement: ChecklistElement): Completable

    fun storeNoteText(id: String, text: String) : Completable
    fun storeElement(element: Element): Completable
    fun deleteElement(id: String, parent: String? = null): Completable
    fun restoreElement(id: String, parent: String? = null): Completable
    fun updateElement(element: Element): Completable
}

@Singleton
class FirebaseRepository @Inject constructor() : IFirebaseRepository {

    override fun storeNoteText(id: String, text: String) : Completable {
        return createCompletableFromTask( FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("contents")
                .child(id)
                .child("text")
                .setValue(text))
    }

    override fun loadElement(id: String, parent: String?): Flowable<Element?> {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)

        return createFlowableValueFromQuery(ref, { snapshot ->
            val firebaseElement = snapshot?.getValue(FirebaseElement::class.java)
            firebaseElement?.elementID = snapshot!!.key
            firebaseElement?.parseElement()
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadNoteContent(id: String): Flowable<String> {
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("contents").child(id).child("text")
        return createFlowableValueFromQuery(ref, { dataSnapshot ->
            dataSnapshot?.getValue(String::class.java) ?: ""
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun lockElement(id: String, locked: Boolean, parent: String?): Completable {

        return Completable.create { emitter ->
            FirebaseDatabase.getInstance()
                    .reference
                    .child("users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("settings")
                    .child("masterPassword")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {}
                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0 == null) emitter.onError(Error())
                            else {
                                var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
                                ref = if (parent != null) ref.child("bundles").child(parent).child(id)
                                else ref.child("main").child(id)
                                ref.child("locked").setValue(locked).addOnSuccessListener {
                                    emitter.onComplete()
                                }.addOnFailureListener {
                                    emitter.onError(it)
                                }
                            }
                        }
                    })
        }
    }

    override fun storeElement(element: Element): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if (element.parent != null) ref.child("bundles").child(element.parent)
        else ref.child("main")
        return createCompletableFromTask(ref.push().storeElement(element))
    }

    override fun deleteElement(id: String, parent: String?): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)
        return createCompletableFromTask(ref.removeValue())
    }

    override fun restoreElement(id: String, parent: String?): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateElement(element: Element): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("elements")
        ref = if (element.parent != null) ref.child("bundles").child(element.parent).child(element.elementID)
        else ref.child("main").child(element.elementID)
        return createCompletableFromTask(ref.storeElement(element))
    }

    override fun loadBundleElements(): Flowable<List<Pair<ChangeType, Element>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun loadChecklistElements(id: String): Flowable<Pair<ChangeType, ChecklistElement>> {
        var ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("contents")
                .child(id)
                .child("elements")

        return createFlowableFromQuery(ref, { dataSnapshot, changeType ->
            val result = dataSnapshot?.getValue(ChecklistElement::class.java)
            result?.id = dataSnapshot?.key
            Pair(changeType, result!!)
        })
    }

    override fun addChecklistElement(elementID: String, checklistElement: ChecklistElement): Completable {
        val ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("contents")
                .child(elementID)
                .child("elements")
                .push()
        return createCompletableFromTask(ref.setValue(checklistElement))
    }

    override fun updateChecklistElement(elementID: String, checklistElement: ChecklistElement): Completable {
        val ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("contents")
                .child(elementID)
                .child("elements")
                .child(checklistElement.id)
        return createCompletableFromTask(ref.setValue(checklistElement))
    }

    override fun removeChecklistElement(id: String, checklistElement: ChecklistElement): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadElements(user: FirebaseUser): Flowable<Pair<ChangeType, Element>?> {
        return createFlowableFromQuery(FirebaseDatabase.getInstance().reference.child("users").child(user.uid).child("elements").child("main"), { snapshot, changeType ->
            return@createFlowableFromQuery if (snapshot != null) {
                val tempEvent = snapshot.getValue(FirebaseElement::class.java)!!.parseElement()
                tempEvent.elementID = snapshot.key
                Pair(changeType, tempEvent)
            } else null
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        fun createCompletableFromTask(task: Task<*>): Completable {
            return Completable.create { emitter ->
                task
                        .addOnFailureListener { if (!emitter.isDisposed) emitter.onError(it) }
                        .addOnSuccessListener { if (!emitter.isDisposed) emitter.onComplete() }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }

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
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

        fun <T> createFlowableValueFromQuery(ref: DatabaseReference, converter: (DataSnapshot?) -> T): Flowable<T> {
            return Flowable.create({ emitter ->
                val listener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(p0: DataSnapshot?) = emitter.onNext(converter(p0))
                }

                emitter.setCancellable {
                    if (!emitter.isCancelled) ref.removeEventListener(listener)
                }

                ref.addValueEventListener(listener)
            }, BackpressureStrategy.BUFFER)
        }
    }
}