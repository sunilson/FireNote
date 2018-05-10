package com.sunilson.firenote.presentation.shared.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

abstract class BaseActivity : AppCompatActivity(), IBaseView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showTutorial()
    }

    override fun showError(message: String?) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    override fun showSuccess(message: String?) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}