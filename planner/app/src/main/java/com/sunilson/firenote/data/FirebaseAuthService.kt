package com.sunilson.firenote.data

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.support.v4.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.googleSignInRequestCode
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IAuthentication {
    fun emailSignIn(email: String, password: String, reAuth: Boolean = false): Completable
    fun startGoogleSignIn(activity: Activity? = null, fragment: Fragment? = null)
    fun handleGoogleSignIn(intent: Intent, reAuth: Boolean = false): Completable
    fun changePassword(password: String, repeatedPassword: String) : Completable
    fun passwordReset(email: String): Completable
    fun register(email: String, password: String, repeatedPassword: String): Completable
}

@Singleton
class FirebaseAuthService @Inject constructor(val context: Application) : IAuthentication {

    private val mGoogleApiClient: GoogleApiClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("376414129715-ehhkuv1f9acftujtvuk0r9biir5c98v2.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun emailSignIn(email: String, password: String, reAuth: Boolean): Completable {
        return when {
            email.isEmpty() -> Completable.error(IllegalArgumentException(context.getString(R.string.email_empty)))
            password.isEmpty() -> Completable.error(IllegalArgumentException(context.getString(R.string.password_empty)))
            else -> {
                if (!reAuth) createCompletableFromTask(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password))
                else createCompletableFromTask(FirebaseAuth.getInstance().currentUser!!.reauthenticate(EmailAuthProvider.getCredential(email, password)))
            }
        }
    }

    override fun changePassword(password: String, repeatedPassword: String): Completable {
        if(password.isEmpty() || password != repeatedPassword) return Completable.error(IllegalArgumentException(context.getString(R.string.password_not_equal_empty)))
        return createCompletableFromTask(FirebaseAuth.getInstance().currentUser!!.updatePassword(password))
    }

    override fun passwordReset(email: String): Completable {
        if (email.isEmpty()) return Completable.error(IllegalArgumentException(context.getString(R.string.email_empty)))
        return createCompletableFromTask(FirebaseAuth.getInstance().sendPasswordResetEmail(email))
    }

    override fun startGoogleSignIn(activity: Activity?, fragment: Fragment?) {
        if(activity != null) activity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), googleSignInRequestCode)
        else if(fragment != null) fragment.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), googleSignInRequestCode)
    }

    override fun handleGoogleSignIn(intent: Intent, reAuth: Boolean): Completable {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            return if (reAuth) createCompletableFromTask(FirebaseAuth.getInstance().currentUser!!.reauthenticate(credential))
            else createCompletableFromTask(FirebaseAuth.getInstance().signInWithCredential(credential))
        } catch (e: ApiException) {
            Completable.error(IllegalArgumentException())
        }
    }

    override fun register(email: String, password: String, repeatedPassword: String): Completable {
        return when {
            email.isEmpty() -> Completable.error(IllegalArgumentException(context.getString(R.string.email_empty)))
            password.isEmpty() -> Completable.error(IllegalArgumentException(context.getString(R.string.password_empty)))
            password != repeatedPassword -> Completable.error(IllegalArgumentException(context.getString(R.string.error_password_match)))
            else -> createCompletableFromTask(FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password))
        }
    }

    companion object {
        fun createCompletableFromTask(task: Task<*>): Completable {
            return Completable.create { emitter ->
                task
                        .addOnFailureListener { if (!emitter.isDisposed) emitter.onError(it) }
                        .addOnSuccessListener { if (!emitter.isDisposed) emitter.onComplete() }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}