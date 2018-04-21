package com.sunilson.firenote.adapters;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunilson.firenote.Interfaces.BinInterface;
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter;
import com.sunilson.firenote.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Linus Weiss
 */

public class BinRecyclerAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter{

    private List<Element> list = new ArrayList<>();
    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;
    private Context context;

    public BinRecyclerAdapter(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.context = context;
        this.mOnLongClickListener = onLongClickListener;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bin_list_layout, parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        return new ViewHolder(v);
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

    /**
     * Interface method onItemMove. Not used
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    /**
     * Interface Method onItemDismiss is called when element is swiped
     *
     * @param position Position of swiped element
     */
    @Override
    public void onItemDismiss(int position) {
        BinInterface binInterface = (BinInterface) context;
        //Remove swiped element
        binInterface.getBinReference().child(list.get(position).getElementID()).removeValue();
    }

    public void add(Element element) {
        list.add(element);
        int index = list.indexOf(element);
        notifyItemInserted(index);
    }

    /**
     * Remove item with given ID
     *
     * @param noteID Id of element to remove
     */
    public void remove(String noteID) {
        //Iterate over list and find element with ID, remove first occurence
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


    private class ViewHolder extends RecyclerView.ViewHolder{
        TextView elementTitle, elementCategory;
        ImageView elementIcon;
        LinearLayout iconHolder, content;

        ViewHolder(View itemView) {
            super(itemView);
            elementTitle = (TextView) itemView.findViewById(R.id.binList_title);
            elementCategory = (TextView) itemView.findViewById(R.id.binList_category);
            elementIcon = (ImageView) itemView.findViewById(R.id.binList_icon);
            iconHolder = (LinearLayout) itemView.findViewById(R.id.binList_icon_holder);
            content = (LinearLayout) itemView.findViewById(R.id.binList_content);
        }
    }
}
