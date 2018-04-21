package com.sunilson.firenote.presentation.views;

import android.app.Activity;
import android.content.Context;

import com.sunilson.firenote.Interfaces.BundleInterface;
import com.sunilson.firenote.Interfaces.ElementInterface;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.adapters.SpinnerAdapter;

/**
 * @author Linus Weiss
 */

/**
 * View for content of Edit Element Dialog
 */
public class EditElementView extends ElementDialogView{

    String type, id;

    public EditElementView(Context context, final SpinnerAdapter categoryAdapter, String type, String id) {
        super(context, categoryAdapter);

        this.type = type;
        this.id = id;

        Activity activity = (Activity) getContext();

        //Check if element is inside of bundle and if we are in an element of that bundle
        if (activity instanceof BundleInterface && !type.equals("bundle")) {
            int position = 0;
            BundleInterface bundleInterface = (BundleInterface) getContext();
            Element element = bundleInterface.getElementAdapter().getElement(id);

            if (element != null) {
                if (element.getTitle() != null) {
                    title.setText(element.getTitle());
                }
                if (element.getCategoryID() != null) {
                    position = mainActivityInterface.getSpinnerCategoryAdapter().getPositionWithID(element.getCategoryID());
                }
            }

            categorySpinner.setSelection(position);
            selectColor(position);

        } else if (activity instanceof ElementInterface) {
            //If we just are in an element
            ElementInterface elementInterface = (ElementInterface) getContext();
            int position = 0;
            if (elementInterface.getElementTitle() != null) {
                title.setText(elementInterface.getElementTitle());
            }
            if (elementInterface.getElementCategoryID() != null) {
                position = mainActivityInterface.getSpinnerCategoryAdapter().getPositionWithID(elementInterface.getElementCategoryID());
            }
            categorySpinner.setSelection(position);
            position = colorAdapter.getPositionWithColor(elementInterface.getElementColor());
            selectColor(position);
        } else if (activity instanceof MainActivityInterface) {
            //If we are on the start page
            if (mainActivityInterface.getElementAdapter().getElement(id) != null) {
                title.setText(mainActivityInterface.getElementAdapter().getElement(id).getTitle());
                Element element = mainActivityInterface.getElementAdapter().getElement(id);
                int position = mainActivityInterface.getSpinnerCategoryAdapter().getPositionWithID(element.getCategoryID());
                categorySpinner.setSelection(position);
                position = colorAdapter.getPositionWithColor(element.getColor());
                selectColor(position);
            }
        }

        title.setSelection(title.getText().length());
    }
}
