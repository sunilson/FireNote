package com.sunilson.firenote.presentation.elementDialog.di

import com.sunilson.firenote.presentation.adapters.CategorySpinnerAdapter
import com.sunilson.firenote.presentation.adapters.ColorAddArrayAdapter
import com.sunilson.firenote.presentation.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import dagger.Module
import dagger.Provides

@DialogFragmentScope
@Module
class ElementDialogModule {

    @Provides
    @DialogFragmentScope
    fun provideCategorySpinnerAdapter(elementDialog: ElementDialog): CategorySpinnerAdapter {
        return CategorySpinnerAdapter(elementDialog.context!!)
    }

    @Provides
    @DialogFragmentScope
    fun provideColorArrayAdapter(elementDialog: ElementDialog): ColorAddArrayAdapter {
        return ColorAddArrayAdapter(elementDialog.context!!)
    }

    /*
            @DialogFragmentScope
            @Provides
            @JvmStatic
            fun provideContext(elementDialog: ElementDialog) : Context {
                return elementDialog.context!!
            }
            */
}