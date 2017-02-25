package com.sunilson.firenote.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunilson.firenote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Linus Weiss
 */

public class DialogMenuAdapter extends ArrayAdapter {

    private List<MenuItem> list = new ArrayList<>();
    private int resource;

    public DialogMenuAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    public void add(String text, int drawableIcon) {
        list.add(new MenuItem(text, drawableIcon));
        notifyDataSetChanged();
    }

    public String getName(int position) {
        MenuItem menuItem = list.get(position);
        return menuItem.getText();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ElementHolder elementHolder;
        if(row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(resource, parent, false);
            elementHolder = new ElementHolder();
            elementHolder.text = (TextView) row.findViewById(R.id.dialog_menu_text);
            elementHolder.icon = (ImageView) row.findViewById(R.id.dialog_menu_icon);
            row.setTag(elementHolder);
        } else {
            elementHolder = (ElementHolder) row.getTag();
        }

        MenuItem item = (MenuItem) getItem(position);
        if (item != null) {
            elementHolder.text.setText(item.getText());
            elementHolder.icon.setImageResource(item.getIconDrawable());
        }
        return row;
    }

    private static class ElementHolder {
        TextView text;
        ImageView icon;
    }

    //Viewholder class
    class MenuItem {
        private String text;
        private int iconDrawable;

        public MenuItem(String text, int iconDrawable) {
            this.text = text;
            this.iconDrawable = iconDrawable;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        int getIconDrawable() {
            return iconDrawable;
        }

        public void setIconDrawable(int iconDrawable) {
            this.iconDrawable = iconDrawable;
        }
    }
}
