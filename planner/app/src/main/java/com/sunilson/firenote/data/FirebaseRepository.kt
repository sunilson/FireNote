package com.sunilson.firenote.data

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.data.models.FirebaseElement
import com.sunilson.firenote.presentation.shared.parseFirebaseElement
import com.sunilson.firenote.presentation.shared.storeElement
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IRepository {
    fun loadElements(userId: String, elementID: String? = null): Flowable<Pair<ChangeType, Element>?>
    fun loadBinElements(userId: String, elementID: String? = null): Flowable<Pair<ChangeType, Element>?>
    fun loadAllElements(userId: String): Single<List<Element>>
    fun loadElement(userId: String, id: String, parent: String? = null): Flowable<Element?>
    fun lockElement(userId: String, id: String, locked: Boolean, parent: String? = null): Completable
    fun loadNoteContent(userId: String, id: String): Flowable<String>

    fun loadChecklistElements(userId: String, id: String): Flowable<Pair<ChangeType, ChecklistElement>>
    fun addChecklistElement(userId: String, elementID: String, checklistElement: ChecklistElement): Completable
    fun updateChecklistElement(userId: String, elementID: String, checklistElement: ChecklistElement): Completable
    fun removeChecklistElement(userId: String, elementID: String, checklistElement: ChecklistElement): Completable

    fun storeNoteText(userId: String, id: String, text: String): Completable
    fun storeElement(userId: String, element: Element): Completable
    fun deleteElement(userId: String, id: String, parent: String? = null): Completable
    fun restoreElement(userId: String, id: String, parent: String? = null): Completable
    fun clearBin(userId: String, parent: String? = null): Completable
    fun deleteBinElement(userId: String, elementID: String, parent: String? = null): Completable
    fun updateElement(userId: String, element: Element): Completable
    fun elementWasDeleted(userId: String, id: String, parent: String? = null): Completable

    fun checkMasterPasswordSet(userId: String): Single<Boolean>
}

@Singleton
class FirebaseRepository @Inject constructor() : IRepository {

    override fun storeNoteText(userId: String, id: String, text: String): Completable {
        return createCompletableFromTask(FirebaseDatabase.getInstance().reference.child("users").child(userId)
                .child("contents")
                .child(id)
                .child("text")
                .setValue(text))
    }

    override fun loadElement(userId: String, id: String, parent: String?): Flowable<Element?> {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)

        return createFlowableValueFromQuery(ref) { snapshot ->
            val firebaseElement = snapshot?.getValue(FirebaseElement::class.java)
            firebaseElement?.elementID = snapshot!!.key
            firebaseElement?.parseElement()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadAllElements(userId: String): Single<List<Element>> {
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements").child("main")
        return createSingleFromQuery(ref) {
            val result = mutableListOf<Element>()
            it?.children?.forEach {
                result.add(it.parseFirebaseElement().parseElement())
            }
            Log.d("Linus", result.toString())
            result
        }
    }

    override fun loadBinElements(userId: String, elementID: String?): Flowable<Pair<ChangeType, Element>?> {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("bin")
        ref = if (elementID != null) ref.child("bundles").child(elementID)
        else ref.child("main")

        return createFlowableFromQuery(ref) { snapshot, changeType ->
            return@createFlowableFromQuery if (snapshot != null) {
                val tempEvent = snapshot.getValue(FirebaseElement::class.java)!!.parseElement()
                tempEvent.elementID = snapshot.key
                tempEvent.deleted = true
                Pair(changeType, tempEvent)
            } else null
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadNoteContent(userId: String, id: String): Flowable<String> {
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("contents").child(id).child("text")
        return createFlowableValueFromQuery(ref) { dataSnapshot ->
            dataSnapshot?.getValue(String::class.java) ?: ""
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun lockElement(userId: String, id: String, locked: Boolean, parent: String?): Completable {

        return Completable.create { emitter ->
            FirebaseDatabase.getInstance()
                    .reference
                    .child("users")
                    .child(userId)
                    .child("settings")
                    .child("masterPassword")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {}
                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0 == null || p0.value == null) emitter.onError(Error())
                            else {
                                var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements")
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

    override fun storeElement(userId: String, element: Element): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements")
        ref = if (element.parent != null) ref.child("bundles").child(element.parent)
        else ref.child("main")
        return createCompletableFromTask(ref.push().storeElement(element))
    }

    override fun deleteElement(userId: String, id: String, parent: String?): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)
        return createCompletableFromTask(ref.removeValue())
    }

    override fun elementWasDeleted(userId: String, id: String, parent: String?): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("bin")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)

        return Completable.create { emitter ->
            val listener = ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    if (p0 != null) emitter.onError(p0.toException())
                    else emitter.onError(Exception())
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0 != null && p0.value != null && p0.child("noteType").value != null) emitter.onComplete()
                }
            })

            emitter.setCancellable { ref.removeEventListener(listener) }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun restoreElement(userId: String, id: String, parent: String?): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("bin")
        ref = if (parent != null) ref.child("bundles").child(parent).child(id)
        else ref.child("main").child(id)

        return Completable.create { emitter ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    if (p0 != null) emitter.onError(p0.toException())
                    else emitter.onError(Exception())
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0 != null && p0.child("noteType").value != null) {
                        val tempEvent = p0.parseFirebaseElement()
                        ref.removeValue().addOnFailureListener { emitter.onError(it) }.addOnSuccessListener {
                            var newRef = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements")
                            newRef = if (parent != null) newRef.child("bundles").child(parent).child(id)
                            else newRef.child("main").child(id)
                            newRef.setValue(tempEvent).addOnFailureListener { emitter.onError(it) }.addOnSuccessListener {
                                emitter.onComplete()
                            }
                        }
                    } else emitter.onError(Exception())
                }
            })
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun clearBin(userId: String, parent: String?): Completable {
        return Completable.create { emitter ->
            var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("bin")
            ref = if (parent != null) ref.child("bundles").child(parent)
            else ref.child("main")

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    emitter.onError(Exception())
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    val ids = mutableListOf<String>()
                    p0?.children?.forEach { ids.add(it.key) }
                    ref.removeValue().addOnSuccessListener {
                        ids.forEach { id ->
                            val contentRef = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("contents").child(id)
                            contentRef.removeValue()
                        }
                        emitter.onComplete()
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
                }
            })

        }
    }

    override fun deleteBinElement(userId: String, elementID: String, parent: String?): Completable {
        return Completable.create { emitter ->
            var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("bin")
            ref = if (parent != null) ref.child("bundles").child(parent).child(elementID)
            else ref.child("main").child(elementID)

            ref.removeValue().addOnSuccessListener {
                val contentRef = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("contents").child(elementID)
                contentRef.removeValue().addOnSuccessListener {
                    emitter.onComplete()
                }.addOnFailureListener {
                    emitter.onError(it)
                }
            }.addOnFailureListener {
                emitter.onError(it)
            }
        }
    }

    override fun updateElement(userId: String, element: Element): Completable {
        var ref = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("elements")
        ref = if (element.parent != null) ref.child("bundles").child(element.parent).child(element.elementID)
        else ref.child("main").child(element.elementID)
        return createCompletableFromTask(ref.storeElement(element))
    }

    override fun loadChecklistElements(userId: String, id: String): Flowable<Pair<ChangeType, ChecklistElement>> {
        var ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(userId)
                .child("contents")
                .child(id)
                .child("elements")

        return createFlowableFromQuery(ref) { dataSnapshot, changeType ->
            val result = dataSnapshot?.getValue(ChecklistElement::class.java)
            result?.id = dataSnapshot?.key
            Pair(changeType, result!!)
        }
    }

    override fun addChecklistElement(userId: String, elementID: String, checklistElement: ChecklistElement): Completable {
        val ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(userId)
                .child("contents")
                .child(elementID)
                .child("elements")
                .push()
        return createCompletableFromTask(ref.setValue(checklistElement))
    }

    override fun updateChecklistElement(userId: String, elementID: String, checklistElement: ChecklistElement): Completable {
        val ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(userId)
                .child("contents")
                .child(elementID)
                .child("elements")
                .child(checklistElement.id)
        return createCompletableFromTask(ref.setValue(checklistElement))
    }

    override fun removeChecklistElement(userId: String, id: String, checklistElement: ChecklistElement): Completable {
        val ref = FirebaseDatabase.getInstance().reference.child("users")
                .child(userId)
                .child("contents")
                .child(id)
                .child("elements")
                .child(checklistElement.id)
        return createCompletableFromTask(ref.removeValue())
    }

    override fun loadElements(userId: String, elementID: String?): Flowable<Pair<ChangeType, Element>?> {
        var ref = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(userId)
                .child("elements")
        ref = if (elementID == null) ref.child("main")
        else ref.child("bundles").child(elementID)

        return createFlowableFromQuery(ref) { snapshot, changeType ->
            return@createFlowableFromQuery if (snapshot != null) {
                val tempEvent = snapshot.getValue(FirebaseElement::class.java)!!.parseElement()
                tempEvent.elementID = snapshot.key
                if (elementID != null) tempEvent.parent = elementID
                Pair(changeType, tempEvent)
            } else null
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun checkMasterPasswordSet(userId: String): Single<Boolean> {
        return Single.create<Boolean> {
            FirebaseDatabase
                    .getInstance()
                    .reference
                    .child("users")
                    .child(userId)
                    .child("settings")
                    .child("masterPassword")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            it.onError(Exception())
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0 == null || p0.value == null) it.onSuccess(false)
                            else it.onSuccess(true)
                        }
                    })
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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