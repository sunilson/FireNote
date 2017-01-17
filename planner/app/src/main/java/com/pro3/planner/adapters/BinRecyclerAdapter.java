package com.pro3.planner.adapters;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pro3.planner.Interfaces.BinInterface;
import com.pro3.planner.Interfaces.ItemTouchHelperAdapter;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by linus_000 on 26.11.2016.
 */

public class BinRecyclerAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter{

    private List<Element> list = new ArrayList();
    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private Context context;
    private LayoutInflater inflater;

    public BinRecyclerAdapter(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.context = context;
        this.mOnLongClickListener = onLongClickListener;
        this.mOnClickListener = onClickListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bin_list_layout, parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public Element getItem(int position) {
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        Element element = list.get(position);
        viewHolder.elementTitle.setText(element.getTitle());
        viewHolder.elementCategory.setText(element.getCategoryName());

        if (element.getNoteType().equals("checklist")) {
            viewHolder.elementIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
        } else if (element.getNoteType().equals("note")) {
            viewHolder.elementIcon.setImageResource(R.drawable.ic_note_white_24dp);
        } else {
            viewHolder.elementIcon.setImageResource(R.drawable.ic_list_white_24dp);
        }

        int elementColor = element.getColor();
        viewHolder.iconHolder.setBackgroundColor(elementColor);
        viewHolder.content.setBackgroundColor(ColorUtils.setAlphaComponent(elementColor, 80));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        BinInterface binInterface = (BinInterface) context;
        binInterface.getBinReference().child(list.get(position).getElementID()).removeValue();
    }

    public void add(Element element) {
        list.add(element);
        int index = list.indexOf(element);
        notifyItemInserted(index);
    }

    public void remove(String noteID) {
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


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView elementTitle, elementCategory;
        public ImageView elementIcon;
        public LinearLayout iconHolder, content;

        public ViewHolder (View itemView) {
            super(itemView);
            elementTitle = (TextView) itemView.findViewById(R.id.binList_title);
            elementCategory = (TextView) itemView.findViewById(R.id.binList_category);
            elementIcon = (ImageView) itemView.findViewById(R.id.binList_icon);
            iconHolder = (LinearLayout) itemView.findViewById(R.id.binList_icon_holder);
            content = (LinearLayout) itemView.findViewById(R.id.binList_content);
        }
    }
}
