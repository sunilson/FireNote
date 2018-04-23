package com.sunilson.firenote.adapters

import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter
import com.sunilson.firenote.LocalSettingsManager
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.homepage.MainActivity
import java.util.*
import javax.inject.Inject

class ElementRecyclerAdapter(val context: AppCompatActivity,
                             val onClickListener: View.OnClickListener,
                             val onLongClickListener: View.OnLongClickListener,
                             val recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    private val list = ArrayList<Element>()
    private val allItems = ArrayList<Element>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        val element = list[position]
        return if (element.locked) 0
        else 1
    }

    fun getElement(id: String) : Element? {
        list.forEach {if(it.elementID == id) return it}
        return null
    }

    fun add(element: Element) : Int{

        if(this.context is MainActivity) {
            var position = 0
            if (LocalSettingsManager.getInstance().getCategoryVisibility(element.category.categoryID) != -1 && LocalSettingsManager.getInstance().getColorVisibility(element.color) != -1) {

            }

            } else {
            list.add(element)
            sort(context.getString(R.string.sort_ascending_name))
            val position = list.indexOf(element)
            notifyItemInserted(position)
            return position
        }

        if (activity is MainActivity) {
            var position = 0
            if (localSettingsManager.getCategoryVisibility(element.category.categoryID) != -1 && localSettingsManager.getColorVisibility(element.color) != -1) {
                list.add(element)
                val sort = LocalSettingsManager.getInstance().sortingMethod
                if (sort != null) {
                    sort(LocalSettingsManager.getInstance().sortingMethod)
                } else {
                    sort(context.getString(R.string.sort_ascending_name))
                }
                position = list.indexOf(element)
                notifyItemInserted(position)
            }
            allItems.add(element)
            return position
        }
    }

    fun sort() {

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemDismiss(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: Any) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }
    }
}