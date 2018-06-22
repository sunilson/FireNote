package com.sunilson.firenote.presentation.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.authentication.fragments.StartFragment
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.googleSignInRequestCode
import com.sunilson.firenote.presentation.shared.interfaces.CanNavigateFragments
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class AuthenticationActivity : BaseActivity(), CanNavigateFragments, AuthenticationPresenterContract.View, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var pres: AuthenticationPresenterContract.Presenter

    override val presenter: AuthenticationPresenterContract.Presenter
        get() = pres

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        supportFragmentManager.beginTransaction().replace(R.id.activity_authentication_framelayout, StartFragment.newInstance(), "start").commit()
    }

    override fun navigateTo(fragment: Fragment, replace: Boolean, addToBackStack: Boolean) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        transaction = if(replace) transaction.replace(R.id.activity_authentication_framelayout, fragment)
        else transaction.add(R.id.activity_authentication_framelayout, fragment)
        if(addToBackStack) transaction = transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun pop() {
        supportFragmentManager.popBackStack()
    }

    override fun loggedIn() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    override fun onBackPressed() {
        if(!supportFragmentManager.findFragmentByTag("start").isVisible) super.onBackPressed()
    }

    override fun showSuccess(message: String?) {
        supportFragmentManager.fragments.forEach {
            if(it.isVisible) (it as IBaseView).showSuccess(message)
        }
    }

    override fun showError(message: String?) {
        supportFragmentManager.fragments.forEach {
            if(it.isVisible) (it as IBaseView).showError(message)
        }
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
        supportFragmentManager.fragments.forEach {
            if(it.isVisible) (it as IBaseView).toggleLoading(loading, message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent )
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == googleSignInRequestCode) presenter.handleSocialSignIn(data)
    }
}