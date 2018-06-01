package com.sunilson.firenote.presentation.authentication

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.googleSignInRequestCode
import com.sunilson.firenote.presentation.shared.storeElement
import io.reactivex.Single
import javax.inject.Inject


@ActivityScope
class AuthenticationPresenter @Inject constructor(val view: AuthenticationPresenterContract.View)
    : AuthenticationPresenterContract.Presenter, BasePresenter(view), GoogleApiClient.OnConnectionFailedListener {

    private val mGoogleApiClient: GoogleApiClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("376414129715-ehhkuv1f9acftujtvuk0r9biir5c98v2.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(view.mContext!!)
                .enableAutoManage(view.mContext as FragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null) {
            FirebaseDatabase.getInstance().reference.child("users")
                    .child(FirebaseAuth.getInstance().currentUser?.uid)
                    .child("settings")
                    .child("registered")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            view.loggedIn()
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            val registered = p0?.getValue<Boolean>(Boolean::class.java)
                            if (registered == null || !registered) addDefaultValues().subscribe { _, _ -> view.loggedIn() }
                            else view.loggedIn()
                        }
                    })
        }
    }

    override fun onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        super.onStart()
    }

    override fun onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        super.onStop()
    }

    override fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showError(view.mContext?.getString(R.string.login_field_error))
            return
        }
        view.toggleLoading(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { view.showSuccess(view.mContext!!.getString(R.string.logged_in_as) + it.user.displayName) }
                .addOnFailureListener { view.showError("Sign in Failed!") }
    }

    override fun startSocialSignIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        (view.mContext as Activity).startActivityForResult(signInIntent, googleSignInRequestCode)
    }

    override fun handleSOcialSignIn(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
            view.toggleLoading(true)
        } catch (e: ApiException) { }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener { view.showSuccess(view.mContext!!.getString(R.string.logged_in_as) + it.user.displayName) }
                .addOnFailureListener { view.showError(it.message) }
    }

    private fun addDefaultValues(): Single<Any> {
        return Single.create { emitter ->
            val mReference = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser?.uid)

            //Add Elements
            var dRef = mReference.child("elements").child("main").push()
            val firstChecklist = Element(elementID = dRef.key,
                    noteType = "checklist",
                    _title = view.mContext!!.getString(R.string.example_checklist),
                    _category = Category("general", view.mContext!!.getString(R.string.category_general)),
                    _color = ContextCompat.getColor(view.mContext!!, R.color.note_color_1))
            dRef.storeElement(firstChecklist)
            dRef = mReference.child("elements").child("main").push()
            val firstNote = Element(elementID = dRef.key,
                    noteType = "note",
                    _title = view.mContext!!.getString(R.string.example_note),
                    _category = Category("general", view.mContext!!.getString(R.string.category_general)),
                    _color = ContextCompat.getColor(view.mContext!!, R.color.note_color_4))
            dRef.storeElement(firstNote)
            dRef = mReference.child("elements").child("main").push()
            val firstBundle = Element(elementID = dRef.key,
                    noteType = "bundle",
                    _title = view.mContext!!.getString(R.string.example_bundle),
                    _category = Category("general", view.mContext!!.getString(R.string.category_general)),
                    _color = ContextCompat.getColor(view.mContext!!, R.color.note_color_7))
            dRef.storeElement(firstBundle)

            //Add Checklist Elements
            val checklistElement = ChecklistElement(text = view.mContext!!.getString(R.string.example_checklist_element))
            dRef = mReference.child("contents").child(firstChecklist.elementID).child("elements").push()

            checklistElement.elementID = dRef.key
            dRef.setValue(checklistElement)

            dRef = mReference.child("contents").child(firstChecklist.elementID).child("elements").push()
            checklistElement.elementID = dRef.key
            dRef.setValue(checklistElement)

            dRef = mReference.child("contents").child(firstChecklist.elementID).child("elements").push()
            checklistElement.elementID = dRef.key
            dRef.setValue(checklistElement)

            //Add Bundle Items
            val bundleChildRef = mReference.child("elements").child("bundles").child(firstBundle.elementID).push()
            val bundleChildRef2 = mReference.child("elements").child("bundles").child(firstBundle.elementID).push()

            val bundleChecklist = Element(
                    elementID = bundleChildRef.key,
                    _color = ContextCompat.getColor(view.mContext!!, R.color.note_color_2),
                    _category = Category("general", view.mContext!!.getString(R.string.category_general)),
                    noteType = "checklist",
                    _title = view.mContext!!.getString(R.string.example_checklist))
            val bundleNote = Element(
                    elementID = bundleChildRef2.key,
                    _color = ContextCompat.getColor(view.mContext!!, R.color.note_color_6),
                    _category = Category("general", view.mContext!!.getString(R.string.category_general)),
                    noteType = "note",
                    _title = view.mContext!!.getString(R.string.example_note))

            bundleChildRef.storeElement(bundleChecklist)
            bundleChildRef2.storeElement(bundleNote)

            //Set Registered
            mReference.child("settings").child("registered").setValue(true)

            //Add Note Content
            mReference.child("contents").child(firstNote.elementID).child("text").setValue(view.mContext!!.getString(R.string.example_note_text)).addOnSuccessListener {
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                view.showSuccess(view.mContext!!.getString(R.string.register_success))
                emitter.onSuccess(true)
            }
        }
    }

    override fun register(email: String, password: String, repeatedPassword: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && password == repeatedPassword) {
            view.toggleLoading(true)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        view.toggleLoading(false)
                    }
                    .addOnFailureListener {
                        when (it) {
                            is FirebaseAuthWeakPasswordException -> view.showError(view.mContext!!.getString(R.string.error_register_password_weak))
                            is FirebaseAuthInvalidCredentialsException -> view.showError(view.mContext!!.getString(R.string.error_register_invalid_email))
                            is FirebaseAuthUserCollisionException -> view.showError(view.mContext!!.getString(R.string.error_register_user_already_exists))
                            else -> view.showError(it.message)
                        }
                    }
        } else {
            view.showError(view.mContext!!.getString(R.string.register_error))
        }
    }

    override fun resetPassword(email: String) {
        view.toggleLoading(true)
        if (email.isNotEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener { view.showSuccess(view.mContext!!.getString(R.string.reset_success)) }
                    .addOnFailureListener { view.showError(view.mContext!!.getString(R.string.reset_failed)) }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) = view.showError(view.mContext!!.getString(R.string.login_error))
}