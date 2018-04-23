package com.sunilson.firenote.adapters;

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
import android.widget.Toast;

import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.Interfaces.BundleInterface;
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.LocalSettingsManager;
import com.sunilson.firenote.R;
import com.sunilson.firenote.data.models.Element;
import com.sunilson.firenote.presentation.homepage.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * @author Linus Weiss
 */

public class ElementRecyclerAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private List<Element> list = new ArrayList<>();
    private List<Element> allItems = new ArrayList<>();
    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private Context context;
    private Activity activity;
    private RecyclerView recyclerView;

    public ElementRecyclerAdapter(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, RecyclerView recyclerView) {
        super();
        this.context = context;
        this.mOnLongClickListener = onLongClickListener;
        this.mOnClickListener = onClickListener;
        this.activity = (Activity) context;
        this.recyclerView = recyclerView;
    }

    public void clear() {
        list.clear();
        allItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    public List<Element> getList() {
        return allItems;
    }

    @Override
    public String toString() {
        String result = "";

        for (Element element : list) {
            String noteType = "";

            switch (element.getNoteType()) {
                case "checklist":
                    noteType = context.getString(R.string.element_checklist);
                    break;
                case "note":
                    noteType = context.getString(R.string.element_note);
                    break;
                default:
                    break;
            }

            result += "- " + noteType + ": " + element.getTitle() + "\n";
        }

        return result;
    }

    /**
     * Swipe Dismiss of Element
     *
     * @param position Position of swiped element
     */
    @Override
    public void onItemDismiss(int position) {
        Element element = getItem(position);
        Activity activity = (Activity) context;

        //Check if in Bundle or on Main Page and delete the correct Reference
        if (activity instanceof MainActivityInterface) {
            MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication)activity.getApplicationContext()).mainContext;
            mainActivityInterface.setDeletedElement(true);
            mainActivityInterface.getElementsReference().child(element.getElementID()).removeValue();
        } else {
            BundleInterface bundleInterface = (BundleInterface) activity;
            bundleInterface.setDeletedElement(true);
            bundleInterface.getElementsReference().child(element.getElementID()).removeValue();
        }

        if(!((BaseApplication) context.getApplicationContext()).getInternetConnected()) {
            Toast.makeText(context, R.string.edit_no_connection, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Custom ViewHolder for the RecyclerView
     */
    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView elementTitle, elementDate, elementCategory;
        ImageView elementIcon, lockIcon;
        LinearLayout iconHolder, content, container;

        ViewHolder (View itemView) {
            super(itemView);
            elementTitle = (TextView) itemView.findViewById(R.id.elementList_title);
            elementDate = (TextView) itemView.findViewById(R.id.elementList_date);
            elementCategory = (TextView) itemView.findViewById(R.id.elementList_category);
            elementIcon = (ImageView) itemView.findViewById(R.id.elementList_icon);
            iconHolder = (LinearLayout) itemView.findViewById(R.id.elementList_icon_holder);
            content = (LinearLayout) itemView.findViewById(R.id.elementList_content);
            lockIcon = (ImageView) itemView.findViewById(R.id.elementLList_lock);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }

    /**
     * Custom ViewHolder for Items that can be deleted via Swipe
     */
    public class SwipeableViewHolder extends RecyclerView.ViewHolder {
        TextView elementTitle, elementDate, elementCategory;
        ImageView elementIcon, lockIcon;
        LinearLayout iconHolder, content, container;

        SwipeableViewHolder (View itemView) {
            super(itemView);
            elementTitle = (TextView) itemView.findViewById(R.id.elementList_title);
            elementDate = (TextView) itemView.findViewById(R.id.elementList_date);
            elementCategory = (TextView) itemView.findViewById(R.id.elementList_category);
            elementIcon = (ImageView) itemView.findViewById(R.id.elementList_icon);
            iconHolder = (LinearLayout) itemView.findViewById(R.id.elementList_icon_holder);
            content = (LinearLayout) itemView.findViewById(R.id.elementList_content);
            lockIcon = (ImageView) itemView.findViewById(R.id.elementLList_lock);
            container = (LinearLayout) itemView.findViewById(R.id.container);
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

        //Get the correct Viewholder, depending on if the item is locked or not
        if (element.getLocked()) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.elementTitle.setText(element.getTitle());

            viewHolder.elementCategory.setText(element.getCategory().getCategoryName());


            switch (element.getNoteType()) {
                case "checklist":
                    viewHolder.elementIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
                    break;
                case "note":
                    viewHolder.elementIcon.setImageResource(R.drawable.ic_note_white_24dp);
                    break;
                default:
                    viewHolder.elementIcon.setImageResource(R.drawable.ic_list_white_24dp);
                    break;
            }

            Date currentDate = new Date();
            DateFormat df = null;
            if (currentDate.getYear() == element.getCreationDate().getYear()) {
                df = new SimpleDateFormat("dd.MMM", Locale.getDefault());
            } else {
                df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            }
            viewHolder.elementDate.setText(df.format(element.getCreationDate()));

            int elementColor = element.getColor();
            viewHolder.iconHolder.setBackgroundColor(elementColor);
            //Set Background of element to 80% Opacity
            viewHolder.content.setBackgroundColor(ColorUtils.setAlphaComponent(elementColor, 80));

            if (element.getLocked()) {
                viewHolder.lockIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.lockIcon.setVisibility(View.GONE);
            }

        } else {
            SwipeableViewHolder viewHolder = (SwipeableViewHolder) holder;
            viewHolder.elementTitle.setText(element.getTitle());

            viewHolder.elementCategory.setText(element.getCategory().getCategoryName());

            switch (element.getNoteType()) {
                case "checklist":
                    viewHolder.elementIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
                    break;
                case "note":
                    viewHolder.elementIcon.setImageResource(R.drawable.ic_note_white_24dp);
                    break;
                default:
                    viewHolder.elementIcon.setImageResource(R.drawable.ic_list_white_24dp);
                    break;
            }

            Date currentDate = new Date();
            DateFormat df;
            if (currentDate.getYear() == element.getCreationDate().getYear()) {
                df = new SimpleDateFormat("dd.MMM", Locale.getDefault());
            } else {
                df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            }

            viewHolder.elementDate.setText(df.format(element.getCreationDate()));

            int elementColor = element.getColor();
            viewHolder.iconHolder.setBackgroundColor(elementColor);
            viewHolder.content.setBackgroundColor(ColorUtils.setAlphaComponent(elementColor, 80));

            if (element.getLocked()) {
                viewHolder.lockIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.lockIcon.setVisibility(View.GONE);
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

    /**
     * Get Element from Adapter by ID
     * @param elementID ID of the element
     * @return First Element with given ID
     */
    public Element getElement(String elementID) {
        Element result = null;

        for (Element element : list) {
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

    /**
     * Add new Element to Adapater and notify data change
     *
     * @param element Element to add
     * @return Position of new Element
     */
    public int add(Element element) {
        if (activity instanceof MainActivity) {
            int position = 0;
            if (LocalSettingsManager.getInstance().getCategoryVisibility(element.getCategory().getCategoryID()) != -1 && LocalSettingsManager.getInstance().getColorVisibility(element.getColor()) != -1) {
                list.add(element);
                String sort = LocalSettingsManager.getInstance().getSortingMethod();
                if (sort != null) {
                    sort(LocalSettingsManager.getInstance().getSortingMethod());
                } else {
                    sort(context.getString(R.string.sort_ascending_name));
                }
                position = list.indexOf(element);
                notifyItemInserted(position);
            }
            allItems.add(element);
            return position;
        } else {
            list.add(element);
            sort(context.getString(R.string.sort_ascending_name));
            int position = list.indexOf(element);
            notifyItemInserted(position);
            return position;
        }
    }

    /**
     * Hide all elements that are set to invisible in local settings
     */
    public void hideElements() {
        list.clear();
        boolean changed = false;

        for (Element e: allItems) {
            //If Invisible, remove them from the visible list and set changed to true
            if (LocalSettingsManager.getInstance().getCategoryVisibility(e.getCategory().getCategoryID()) == -1 || LocalSettingsManager.getInstance().getColorVisibility(e.getColor()) == -1) {
                list.remove(e);
                changed = true;
            } else {
                list.add(e);
                changed = true;
            }
        }

        //If something has changed, sort the list and notify that the data has changed
        if (changed) {
            String sort = LocalSettingsManager.getInstance().getSortingMethod();
            if (sort != null) {
                sort(LocalSettingsManager.getInstance().getSortingMethod());
            } else {
                sort(context.getString(R.string.sort_ascending_name));
            }
            notifyDataSetChanged();
        }
    }

    /**
     * Update an Element in the Adapter
     *
     * @param element New Element
     * @param noteID Id of Element that should be updated
     */
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

    /**
     * Remove Element from Adapter by ID
     *
     * @param noteID ID of element that should be removed
     * @return Position of removed Item
     */
    public int remove(String noteID) {
        if (activity instanceof MainActivity) {
            removeFromAllItems(noteID);
        }

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

        if (index == 0) {
            recyclerView.getLayoutManager().scrollToPosition(index);
        }

        if (index == list.size()) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(index);
        }
        return index;
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

    /**
     * Sort Adapter by given Sorting method
     *
     * @param sortMethod Short Name of Sorting Method
     */
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
        Comparator<Element> comp = (o1, o2) -> {
            if (o1.getCategory().getCategoryName().compareToIgnoreCase(o2.getCategory().getCategoryName()) < 0) {
                return -1;
            } else if (o1.getCategory().getCategoryName().compareToIgnoreCase(o2.getCategory().getCategoryName()) > 0) {
                return 1;
            }
            return 0;
        };

        Collections.sort(list, comp);
    }

    private void sortByDateDescending() {
        Comparator<Element> comp = (o1, o2) -> {
            if (o1.getCreationDate().after(o2.getCreationDate())) {
                return -1;
            } else if (o1.getCreationDate().before(o2.getCreationDate())) {
                return 1;
            }
            return 0;
        };

        Collections.sort(list, comp);
    }

    private void sortByDateAscending() {
        Comparator<Element> comp = (o1, o2) -> {
            if (o1.getCreationDate().after(o2.getCreationDate())) {
                return 1;
            } else if (o1.getCreationDate().before(o2.getCreationDate())) {
                return -1;
            }
            return 0;
        };

        Collections.sort(list, comp);
    }

    private void sortByNameDescending() {
        Comparator<Element> comp = (o1, o2) -> {

            if (o1.getTitle().compareToIgnoreCase(o2.getTitle()) < 0) {
                return 1;
            } else if (o1.getTitle().compareToIgnoreCase(o2.getTitle()) > 0) {
                return -1;
            }
            return 0;
        };

        Collections.sort(list, comp);
    }

    private void sortByNameAscending() {
        Comparator<Element> comp = new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {
                if (o1.getTitle().compareToIgnoreCase(o2.getTitle()) < 0) {
                    return -1;
                } else if (o1.getTitle().compareToIgnoreCase(o2.getTitle()) > 0) {
                    return 1;
                }
                return 0;
            }
        };

        Collections.sort(list, comp);
    }
}
