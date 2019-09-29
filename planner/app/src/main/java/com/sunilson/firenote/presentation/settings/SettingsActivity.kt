package com.sunilson.firenote.presentation.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IAuthentication
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.dialogs.ChangeLoginPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.ChangeMasterPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.ConfirmDialog
import com.sunilson.firenote.presentation.shared.dialogs.authenticationDialog.AuthenticationDialog
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.showToast
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : BaseActivity(), View.OnClickListener, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var authService: IAuthentication

    @Inject
    lateinit var repository: IRepository

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
            username.text = getString(R.string.logged_in_as) + " \"" + user.email + "\""
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
                repository.checkMasterPasswordSet(FirebaseAuth.getInstance().currentUser!!.uid).subscribe({
                    ChangeMasterPasswordDialog.newInstance(it).show(supportFragmentManager, "dialog")
                }, {
                    showToast(getString(R.string.error_change_master_password))
                })
            }
            R.id.change_password -> {
                authDialog = AuthenticationDialog.newInstance()
                authDialog?.listener = object : DialogListener<Boolean> {
                    override fun onResult(result: Boolean?) {
                        if (result != null && result) ChangeLoginPasswordDialog.newInstance().show(supportFragmentManager, "dialog")
                    }
                }
                authDialog!!.show(supportFragmentManager, "dialog")
            }
            R.id.delete_account -> {
                authDialog = AuthenticationDialog.newInstance()
                authDialog?.listener = object : DialogListener<Boolean> {
                    override fun onResult(result: Boolean?) {
                        if (result != null && result) {
                            val confirmDialog = ConfirmDialog.newInstance(getString(R.string.delete_account), getString(R.string.confirm_delete_account))
                            confirmDialog.listener = object : DialogListener<Boolean> {
                                override fun onResult(result: Boolean?) {
                                    if (result != null && result) {
                                        showSuccess(getString(R.string.account_deleted))
                                        authService.signOut()
                                    }
                                }
                            }
                            confirmDialog.show(supportFragmentManager, "dialog")
                        }
                    }
                }
                authDialog!!.show(supportFragmentManager, "dialog")
            }
            R.id.about -> {
                ConfirmDialog.newInstance(getString(R.string.about_title), getString(R.string.about), false).show(supportFragmentManager, "dialog")
            }
        }
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
}