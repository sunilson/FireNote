package com.sunilson.firenote.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

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

    private fun aboutDialog() {}
    private fun changePasswordDialog() {}

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) { }
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun showTutorial() {}

}