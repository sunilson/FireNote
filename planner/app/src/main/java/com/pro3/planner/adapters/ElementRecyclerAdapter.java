package com.pro3.planner.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ItemTouchHelperAdapter;
import com.pro3.planner.Interfaces.MainActivityInterface;
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
 * Created by linus_000 on 23.11.2016.
 */

public class ElementRecyclerAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private List<Element> list = new ArrayList();
    private List<Element> allItems = new ArrayList<>();
    private LayoutInflater inflater;
    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private Context context;

    public ElementRecyclerAdapter(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        super();
        this.context = context;
        this.mOnLongClickListener = onLongClickListener;
        this.mOnClickListener = onClickListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void clear() {
        int listEnd = list.size();
        list.clear();
        allItems.clear();
        notifyItemRangeChanged(0, listEnd);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    public List<Element> getList() {
        return allItems;
    }

    @Override
    public void onItemDismiss(int position) {
        //Löschen durch swipe
        Element element = getItem(position);
        Activity activity = (Activity) context;

        if (activity instanceof MainActivityInterface) {
            MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication)activity.getApplicationContext()).mainContext;
            mainActivityInterface.getElementsReference().child(element.getElementID()).removeValue();
        } else {
            BundleInterface bundleInterface = (BundleInterface) activity;
            bundleInterface.getElementsReference().child(element.getElementID()).removeValue();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView elementTitle, elementDate, elementCategory;
        public ImageView elementIcon, lockIcon;
        public LinearLayout iconHolder, content;

        public ViewHolder (View itemView) {
            super(itemView);
            elementTitle = (TextView) itemView.findViewById(R.id.elementList_title);
            elementDate = (TextView) itemView.findViewById(R.id.elementList_date);
            elementCategory = (TextView) itemView.findViewById(R.id.elementList_category);
            elementIcon = (ImageView) itemView.findViewById(R.id.elementList_icon);
            iconHolder = (LinearLayout) itemView.findViewById(R.id.elementList_icon_holder);
            content = (LinearLayout) itemView.findViewById(R.id.elementList_content);
            lockIcon = (ImageView) itemView.findViewById(R.id.elementLList_lock);
        }
    }

    public class SwipeableViewHolder extends RecyclerView.ViewHolder {
        public TextView elementTitle, elementDate, elementCategory;
        public ImageView elementIcon, lockIcon;
        public LinearLayout iconHolder, content;

        public SwipeableViewHolder (View itemView) {
            super(itemView);
            elementTitle = (TextView) itemView.findViewById(R.id.elementList_title);
            elementDate = (TextView) itemView.findViewById(R.id.elementList_date);
            elementCategory = (TextView) itemView.findViewById(R.id.elementList_category);
            elementIcon = (ImageView) itemView.findViewById(R.id.elementList_icon);
            iconHolder = (LinearLayout) itemView.findViewById(R.id.elementList_icon_holder);
            content = (LinearLayout) itemView.findViewById(R.id.elementList_content);
            lockIcon = (ImageView) itemView.findViewById(R.id.elementLList_lock);
        }
    }

    @Override
    public int getItemViewType(int position) {

        Element element = list.get(position);

        if (element.getLocked()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Element element = list.get(position);

        if (element.getLocked()) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.elementTitle.setText(element.getTitle());

            viewHolder.elementCategory.setText(element.getCategoryName());


            if (element.getNoteType().equals("checklist")) {
                viewHolder.elementIcon.setImageResource(R.drawable.element_checklist_icon);
            } else if (element.getNoteType().equals("note")) {
                viewHolder.elementIcon.setImageResource(R.drawable.element_note_icon);
            } else {
                viewHolder.elementIcon.setImageResource(R.drawable.element_bundle_icon);
            }

            DateFormat df = new SimpleDateFormat("dd. MMM.", Locale.getDefault());
            viewHolder.elementDate.setText(df.format(element.getCreationDate()));

            int elementColor = element.getColor();
            viewHolder.iconHolder.setBackgroundColor(elementColor);
            viewHolder.content.setBackgroundColor(ColorUtils.setAlphaComponent(elementColor, 80));

            if (element.getLocked()) {
                viewHolder.lockIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.lockIcon.setVisibility(View.INVISIBLE);
            }
        } else {
            SwipeableViewHolder viewHolder = (SwipeableViewHolder) holder;
            viewHolder.elementTitle.setText(element.getTitle());

            viewHolder.elementCategory.setText(element.getCategoryName());

            if (element.getNoteType().equals("checklist")) {
                viewHolder.elementIcon.setImageResource(R.drawable.element_checklist_icon);
            } else if (element.getNoteType().equals("note")) {
                viewHolder.elementIcon.setImageResource(R.drawable.element_note_icon);
            } else {
                viewHolder.elementIcon.setImageResource(R.drawable.element_bundle_icon);
            }

            DateFormat df = new SimpleDateFormat("dd. MMM.", Locale.getDefault());
            viewHolder.elementDate.setText(df.format(element.getCreationDate()));

            int elementColor = element.getColor();
            viewHolder.iconHolder.setBackgroundColor(elementColor);
            viewHolder.content.setBackgroundColor(ColorUtils.setAlphaComponent(elementColor, 80));

            if (element.getLocked()) {
                viewHolder.lockIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.lockIcon.setVisibility(View.INVISIBLE);
            }
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_list_layout, parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        if (viewType == 0) {
            return new ViewHolder(v);
        } else {
            return new SwipeableViewHolder(v);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Element getElement(String elementID) {
        Element result = null;
        Iterator<Element> it = list.iterator();

        while (it.hasNext()) {
            Element element = it.next();
            if (element.getElementID().equals(elementID)) {
                result = element;
                break;
            }
        }

        return result;
    }

    public Element getItem(int position) {
        return list.get(position);
    }

    public void add(Element element) {
        if (LocalSettingsManager.getInstance().getCategoryVisibility(element.getCategoryName()) != -1 || LocalSettingsManager.getInstance().getColorVisibility(element.getColor()) != -1) {
            list.add(element);
        }
        allItems.add(element);
        sort(LocalSettingsManager.getInstance().getSortingMethod());
        notifyDataSetChanged();
    }

    public void hideElements() {
        list.clear();
        for (Element e: allItems) {
            if (LocalSettingsManager.getInstance().getCategoryVisibility(e.getCategoryID()) == -1 || LocalSettingsManager.getInstance().getColorVisibility(e.getColor()) == -1) {
                list.remove(e);
            } else {
                list.add(e);
            }
        }
        sort(LocalSettingsManager.getInstance().getSortingMethod());
        notifyDataSetChanged();
    }

    public void update(Element element, String noteID) {

        ListIterator<Element> it = list.listIterator();

        while (it.hasNext()) {
            Element nextElement = it.next();
            if (nextElement.getElementID().equals(noteID)) {
                it.set(element);
                break;
            }
        }

        ListIterator<Element> it2 = allItems.listIterator();

        while (it2.hasNext()) {
            Element nextElement = it2.next();
            if (nextElement.getElementID().equals(noteID)) {
                it2.set(element);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void remove(String noteID) {
        removeFromAllItems(noteID);
        Iterator<Element> it = list.iterator();
        int index = 0;

        while (it.hasNext()) {
            Element element = it.next();
            if (element.getElementID().equals(noteID)) {
                it.remove();
                break;
            }
            index++;
        }
        notifyItemRemoved(index);
    }

    private void removeFromAllItems(String noteID) {
        Iterator<Element> it = allItems.iterator();
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getElementID().equals(noteID)) {
                it.remove();
                break;
            }
        }
    }

    public void sort(String sortMethod) {
        if (sortMethod.equals(context.getResources().getString(R.string.sort_descending_date))) {
            sortByDateDescending();
        } else if (sortMethod.equals(context.getResources().getString(R.string.sort_ascending_date))) {
            sortByDateAscending();
        } else if (sortMethod.equals(context.getResources().getString(R.string.sort_descending_name))) {
            sortByNameDescending();
        } else if (sortMethod.equals(context.getResources().getString(R.string.sort_ascending_name))) {
            sortByNameAscending();
        } else if (sortMethod.equals(context.getResources().getString(R.string.sort_category_name))) {
            sortByCategory();
        }
    }

    private void sortByCategory() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {

                if (o1.getCategoryName().compareTo(o2.getCategoryName()) < 0) {
                    return -1;
                } else if (o1.getCategoryName().compareTo(o2.getCategoryName()) > 0) {
                    return 1;
                }
                return 0;
            }
        };

        Collections.sort(list, comp);
        notifyDataSetChanged();
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
}
