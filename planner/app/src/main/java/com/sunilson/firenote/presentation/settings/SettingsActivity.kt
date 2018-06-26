package com.sunilson.firenote.presentation.settings

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.dialogs.ChangeLoginPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.ChangeMasterPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.authenticationDialog.AuthenticationDialog
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : BaseActivity(), View.OnClickListener, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var authDialog: AuthenticationDialog? = null

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Init click listeners
        about.setOnClickListener(this)
        change_password.setOnClickListener(this)
        master_password.setOnClickListener(this)
        username.setOnClickListener(this)
        delete_account.setOnClickListener(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            username.setText(getString(R.string.logged_in_as) + " \"" + user.email + "\"")
            //Init presenter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.master_password -> {
                ChangeMasterPasswordDialog.newInstance().show(supportFragmentManager, "dialog")
            }
            R.id.change_password -> {
                authDialog = AuthenticationDialog.newInstance()
                authDialog?.listener = object : DialogListener<Boolean> {
                    override fun onResult(result: Boolean?) {
                        if(result != null && result) ChangeLoginPasswordDialog.newInstance().show(supportFragmentManager, "dialog")
                    }
                }
                authDialog!!.show(supportFragmentManager, "dialog")
            }
        }
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
}