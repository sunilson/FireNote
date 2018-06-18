//package com.sunilson.firenote.presentation.adapters;
//
//import android.content.Context;
//import android.graphics.Paint;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sunilson.firenote.Interfaces.ChecklistInterface;
//import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter;
//import com.sunilson.firenote.R;
//import com.sunilson.firenote.data.models.ChecklistElement;
//import com.sunilson.firenote.presentation.application.BaseApplication;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @author Linus Weiss
// */
//
//public class ChecklistRecyclerAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
//
//    private List<ChecklistElement> list = new ArrayList<>();
//    private Context context;
//    private final View.OnClickListener mOnClickListener;
//    private final View.OnLongClickListener mOnLongClickListener;
//    private LayoutInflater inflater;
//    private RecyclerView recyclerView;
//
//    public ChecklistRecyclerAdapter(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, RecyclerView recyclerView) {
//        this.context = context;
//        this.mOnLongClickListener = onLongClickListener;
//        this.mOnClickListener = onClickListener;
//        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.recyclerView = recyclerView;
//    }
//
//    private class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView elementText;
//        CheckBox checkBox;
//        LinearLayout container;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            elementText = (TextView) itemView.findViewById(R.id.checkList_element_text);
//            checkBox = (CheckBox) itemView.findViewById(R.id.checkList_element_checkBox);
//            container = (LinearLayout) itemView.findViewById(R.id.container);
//
//            itemView.setTag(this);
//        }
//    }
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = inflater.inflate(R.layout.checklist_list_layout, parent, false);
//        v.setOnClickListener(mOnClickListener);
//        v.setOnLongClickListener(mOnLongClickListener);
//        return new ViewHolder(v);
//    }
//


//    /**
//     * Remove first element with given ID
//     * @param elementID ID of element to be deleted
//     */
//    public void remove(String elementID) {
//        //Iterate over list and remove item
//        Iterator<ChecklistElement> it = list.iterator();
//        int index = 0;
//
//        while (it.hasNext()) {
//            ChecklistElement element = it.next();
//            if (element.getElementID().equals(elementID)) {
//                it.remove();
//                break;
//            }
//            index++;
//        }
//
//        //Fixes for deleting first or last item (bugs with adapter animations)
//        if (index == 0) {
//            recyclerView.getLayoutManager().scrollToPosition(index);
//        }
//
//
//        if (index == list.size()) {
//            notifyDataSetChanged();
//        } else {
//            notifyItemRemoved(index);
//        }
//    }

//
//    public ChecklistElement getItemWithID(String id) {
//        for (ChecklistElement checklistElement : list) {
//            if (checklistElement.getElementID().equals(id)) {
//                return checklistElement;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Replace element in adapter (compared by ID)
//     *
//     * @param element New element
//     */
//    public void update(ChecklistElement element) {
//        //Iterate to element and replace it, else return null
//        Iterator<ChecklistElement> it = list.iterator();
//        int index = 0;
//
//        while (it.hasNext()) {
//            ChecklistElement checklistElement = it.next();
//            if (checklistElement.getElementID().equals(element.getElementID())) {
//                break;
//            }
//            index++;
//        }
//
//        list.set(index, element);
//        notifyDataSetChanged();
//    }

//
//    @Override
//    public boolean onItemMove(int fromPosition, int toPosition) {
//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(list, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(list, i, i - 1);
//            }
//        }
//        notifyItemMoved(fromPosition, toPosition);
//        return true;
//    }
//
//    /**
//     * Called when element is swiped
//     *
//     * @param position Position of element
//     */
//    @Override
//    public void onItemDismiss(int position) {
//        //Remove element
//        ChecklistInterface checklistInterface = (ChecklistInterface) context;
//        checklistInterface.getElementsReference().child(getItem(position).getElementID()).removeValue();
//        if(!((BaseApplication) context.getApplicationContext()).getInternetConnected()) {
//            Toast.makeText(context, R.string.edit_no_connection, Toast.LENGTH_LONG).show();
//        }
//    }
//}