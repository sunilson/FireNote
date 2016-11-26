package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.Element;

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
    List<Integer> hidden = new ArrayList<>();
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


    @Override
    public int getCount() {
        return list.size();
    }

    public void sort(String sortMethod) {
        if (sortMethod.equals("dateDescending")) {
            sortByDateDescending();
        } else if (sortMethod.equals("dateAscending")) {
            sortByDateAscending();
        } else if (sortMethod.equals("nameDescending")) {
            sortByNameDescending();
        } else if (sortMethod.equals("nameAscending")) {
            sortByNameAscending();
        }
    }

    private void sortByDateDescending() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {

                if (o1.getCreationDate().after(o2.getCreationDate())) {
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

                if (o1.getCreationDate().after(o2.getCreationDate())) {
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

    private void sortByNameDescending() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {

                if (o1.getTitle().compareTo(o2.getTitle()) < 0) {
                    return 1;
                } else if (o1.getTitle().compareTo(o2.getTitle()) > 0) {
                    return -1;
                }
                return 0;
            }
        };

        Collections.sort(list, comp);
        notifyDataSetChanged();
    }

    private void sortByNameAscending() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {

                if (o1.getTitle().compareTo(o2.getTitle()) < 0) {
                    return -1;
                } else if (o1.getTitle().compareTo(o2.getTitle()) > 0) {
                    return 1;
                }
                return 0;
            }
        };

        Collections.sort(list, comp);
        notifyDataSetChanged();
    }


    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
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

        Element element = (Element) getItem(position);
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (LocalSettingsManager.getInstance().getCategoryVisibility(element.getCategory().getCategoryID()) == -1 || LocalSettingsManager.getInstance().getColorVisibility(element.getColor()) == -1) {
            return layoutInflater.inflate(R.layout.null_item, parent, false);
        }

        View row = convertView;
        ElementHolder elementHolder;

        if (row instanceof LinearLayout) {
            elementHolder = (ElementHolder) row.getTag();
        } else {
            row = layoutInflater.inflate(resource, parent, false);
            elementHolder = new ElementHolder();
            elementHolder.elementDate = (TextView) row.findViewById(R.id.elementList_date);
            elementHolder.elementCategory = (TextView) row.findViewById(R.id.elementList_category);
            elementHolder.elementIcon = (ImageView) row.findViewById(R.id.elementList_icon);
            elementHolder.elementTitle = (TextView) row.findViewById(R.id.elementList_title);
            row.setTag(elementHolder);
        }

        elementHolder.elementTitle.setText(element.getTitle());
        elementHolder.elementIcon.setImageResource(element.getIcon());
        DateFormat df = new SimpleDateFormat("dd. MMM.", Locale.getDefault());
        elementHolder.elementDate.setText(df.format(element.getCreationDate()));
        elementHolder.elementCategory.setText(element.getCategory().getCategoryName());

        int elementColor = element.getColor();
        row.findViewById(R.id.elementList_icon_holder).setBackgroundColor(elementColor);
        row.findViewById(R.id.elementList_content).setBackgroundColor(ColorUtils.setAlphaComponent(elementColor, 80));

        return row;
    }


    static class ElementHolder {
        TextView elementTitle, elementDate, elementCategory;
        ImageView elementIcon;
    }
}


