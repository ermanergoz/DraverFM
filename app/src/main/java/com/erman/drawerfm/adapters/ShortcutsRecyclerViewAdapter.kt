package com.erman.drawerfm.adapters

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.erman.drawerfm.R
import com.erman.drawerfm.activities.FragmentActivity
import kotlinx.android.synthetic.main.shortcut_recycler_layout.view.*

class ShortcutRecyclerViewAdapter(var isMarqueeEnabled: Boolean) :
    RecyclerView.Adapter<ShortcutRecyclerViewAdapter.ShortcutHolder>() {

    var shortcuts: Map<String, String> = mutableMapOf()

    override fun getItemCount(): Int {
        return shortcuts.count()
    }

    override fun onBindViewHolder(
        holder: ShortcutHolder, position: Int
    ) {
        holder.bindButtons(shortcuts.keys.elementAt(position), shortcuts.values.elementAt(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutHolder {
        return ShortcutHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.shortcut_recycler_layout,
                parent,
                false
            )
        )
    }

    inner class ShortcutHolder(var view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {
        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        fun bindButtons(shortcutName: String, path: String) {
            itemView.shortcut.text = shortcutName
            itemView.shortcut.tag = path
            itemView.shortcut.setBackgroundResource(R.drawable.storage_button_style)
            itemView.shortcut.isSingleLine = true

            if (isMarqueeEnabled) {
                itemView.shortcut.ellipsize =
                    TextUtils.TruncateAt.MARQUEE  //for sliding names if the length is longer than 1 line
                itemView.shortcut.isSelected = true
                itemView.shortcut.marqueeRepeatLimit = -1   //-1 is for forever
            }
        }

        override fun onClick(p0: View?) {
            val intent = Intent(view.context, FragmentActivity::class.java)
            intent.putExtra("path", itemView.shortcut.tag.toString())
            startActivity(view.context, intent, null)
        }

        override fun onLongClick(view: View): Boolean {
            //TODO: Implement shortcut recycler view adapter onLonClick listener to remove the shortcut
            return true
        }
    }

    fun updateData(shortcuts: Map<String, String>) {
        this.shortcuts = shortcuts
        notifyDataSetChanged()
    }
}
