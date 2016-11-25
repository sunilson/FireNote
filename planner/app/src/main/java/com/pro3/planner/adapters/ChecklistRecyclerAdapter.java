package com.pro3.planner.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pro3.planner.Interfaces.CanDeleteChecklistElement;
import com.pro3.planner.Interfaces.ItemTouchHelperAdapter;
import com.pro3.planner.Interfaces.OnStartDragListener;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.ChecklistElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by linus_000 on 24.11.2016.
 */

public class ChecklistRecyclerAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private List<ChecklistElement> list = new ArrayList();
    Context context;
    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private LayoutInflater inflater;
    private final OnStartDragListener onStartDragListener;

    public ChecklistRecyclerAdapter(OnStartDragListener onStartDragListener, Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.context = context;
        this.onStartDragListener = onStartDragListener;
        this.mOnLongClickListener = onLongClickListener;
        this.mOnClickListener = onClickListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView elementText;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            elementText = (TextView) itemView.findViewById(R.id.checkList_element_text);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkList_element_checkBox);

            itemView.setTag(this);
        }
    }

    public void clear() {
        list.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.checklist_list_layout, parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public void checkItem(int position) {

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        ChecklistElement checklistElement = list.get(position);
        CheckBox checkBox = viewHolder.checkBox;
        TextView textView = viewHolder.elementText;

        /*
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }

                return false;
            }
        });
        */

        textView.setText(checklistElement.getText());

        if (checklistElement.isFinished()) {
            checkBox.setChecked(true);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(context.getResources().getColor(R.color.text_crossed_out));
        } else {
            checkBox.setChecked(false);
            textView.setPaintFlags(textView.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(context.getResources().getColor(R.color.primary_text_color));
        }
    }

    public ChecklistElement getItem(int position) {
        return list.get(position);
    }

    public void add(ChecklistElement element) {
        list.add(element);
        int position = list.indexOf(element);
        notifyItemInserted(position);
    }

    public void remove(String elementID) {
        Iterator<ChecklistElement> it = list.iterator();
        int index = 0;

        while (it.hasNext()) {
            ChecklistElement element = it.next();
            if (element.getElementID().equals(elementID)) {
                it.remove();
                break;
            }
            index++;
        }

        notifyItemRemoved(index);
    }

    public void update(ChecklistElement element) {
        Iterator<ChecklistElement> it = list.iterator();
        int index = 0;

        while (it.hasNext()) {
            ChecklistElement checklistElement = it.next();
            if (checklistElement.getElementID().equals(element.getElementID())) {
                break;
            }
            index++;
        }

        list.set(index, element);
        notifyItemChanged(index);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public void onItemDismiss(int position) {
        CanDeleteChecklistElement canDeleteChecklistElement = (CanDeleteChecklistElement) context;
        canDeleteChecklistElement.getElementsReference().child(getItem(position).getElementID()).removeValue();
    }
}
