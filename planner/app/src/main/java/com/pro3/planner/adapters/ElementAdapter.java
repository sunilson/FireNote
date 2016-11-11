package com.pro3.planner.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.activities.MainActivity;
import com.pro3.planner.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by linus_000 on 05.11.2016.
 */

public class ElementAdapter extends ArrayAdapter {

    List<Element> list = new ArrayList<>();
    int resource;

    public ElementAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public void clear() {
        super.clear();
        list.clear();
    }

    public void add(Element element) {
        list.add(element);
        notifyDataSetChanged();
    }

    public void update(Element element, String noteID) {
        ListIterator<Element> it = list.listIterator();

        while (it.hasNext()) {
            Element nextElement = it.next();
            if (nextElement.getNoteID().equals(noteID)) {
                it.set(element);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void sort(String sortMethod) {
        if (sortMethod.equals("dateDescending")) {
            sortByDateDescending();
        } else if (sortMethod.equals("dateAscending")) {
            sortByDateAscending();
        }
    }

    private void sortByDateDescending() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {

                if(o1.getCreationDate().after(o2.getCreationDate())) {
                    return -1;
                } else if (o1.getCreationDate().before(o2.getCreationDate())) {
                    return 1;
                }

                return 0;
            }
        };

        Collections.sort(list, comp);
        notifyDataSetChanged();
    }
    private void sortByDateAscending() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {

                if(o1.getCreationDate().after(o2.getCreationDate())) {
                    return 1;
                } else if (o1.getCreationDate().before(o2.getCreationDate())) {
                    return -1;
                }
                return 0;
            }
        };

        Collections.sort(list, comp);
        notifyDataSetChanged();
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

    public void remove(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void remove(String noteID) {
        Iterator<Element> it = list.iterator();

        while (it.hasNext()) {
            Element element = it.next();
            if (element.getNoteID().equals(noteID)) {
                it.remove();
            }
        }

        notifyDataSetChanged();
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
            elementHolder.elementDate = (TextView) row.findViewById(R.id.elementList_date);
            elementHolder.elementIcon = (ImageView) row.findViewById(R.id.elementList_icon);
            elementHolder.elementTitle = (TextView) row.findViewById(R.id.elementList_title);
            row.setTag(elementHolder);
        } else {
            elementHolder = (ElementHolder) row.getTag();
        }

        Element element = (Element) getItem(position);
        elementHolder.elementTitle.setText(element.getTitle());
        elementHolder.elementIcon.setImageResource(element.getIcon());
        DateFormat df = new SimpleDateFormat("dd. MMM.", Locale.getDefault());
        elementHolder.elementDate.setText(df.format(element.getCreationDate()));


        String elementColor = element.getColor();
        if(elementColor != null) {
            row.setBackgroundColor(Color.parseColor(element.getColor()));
        } else {
            SharedPreferences prefs = getContext().getSharedPreferences(((MainActivity) getContext()).getUser().getUid(), Context.MODE_PRIVATE);
            row.setBackgroundColor(Color.parseColor(prefs.getString("defaultColor", "#FFFFA5")));
        }
        return row;
    }

    static class ElementHolder {
        TextView elementTitle, elementDate;
        ImageView elementIcon;
    }
}


