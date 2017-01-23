package com.pro3.planner.views;

import android.app.Activity;
import android.content.Context;

import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.adapters.SpinnerAdapter;
import com.pro3.planner.baseClasses.Element;

/**
 * Created by linus_000 on 24.12.2016.
 */

public class EditElementView extends ElementDialogView{

    String type, id;

    public EditElementView(Context context, final SpinnerAdapter categoryAdapter, String type, String id) {
        super(context, categoryAdapter);

        this.type = type;
        this.id = id;

        Activity activity = (Activity) getContext();

        if (activity instanceof BundleInterface && !type.equals("bundle")) {
            int position = 0;
            int positionColor = 0;
            BundleInterface bundleInterface = (BundleInterface) getContext();
            Element element = bundleInterface.getElementAdapter().getElement(id);

            if (element != null) {
                if (element.getTitle() != null) {
                    title.setText(element.getTitle());
                }
                if (element.getCategoryID() != null) {
                    position = mainActivityInterface.getSpinnerCategoryAdapter().getPositionWithID(element.getCategoryID());
                }
                positionColor = colorAdapter.getPositionWithColor(element.getColor());
            }

            categorySpinner.setSelection(position);
            selectColor(position);

        } else if (activity instanceof ElementInterface) {
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
