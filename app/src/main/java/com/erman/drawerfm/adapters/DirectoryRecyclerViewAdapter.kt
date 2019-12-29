package com.erman.drawerfm.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.erman.drawerfm.R
import com.erman.drawerfm.utilities.getConvertedFileSize
import kotlinx.android.synthetic.main.directory_recycler_layout.view.*
import java.io.File
import java.text.SimpleDateFormat

class DirectoryRecyclerViewAdapter :
    RecyclerView.Adapter<DirectoryRecyclerViewAdapter.ViewHolder>() {

    var onClickListener: ((File) -> Unit)? = null
    var onLongClickListener: ((File) -> Unit)? = null
    var directoryList = listOf<File>()
    private var dateFormat = SimpleDateFormat("dd MMMM | HH:mm:ss")
    //var multipleSelectionList = mutableListOf<ConstraintLayout>()
    var isMultipleSelection = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.directory_recycler_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return directoryList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDirectory(directoryList[position])
        holder.itemView.directoryLayout.setBackgroundColor(Color.TRANSPARENT)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener,
        View.OnLongClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (isMultipleSelection) {
                if (!p0?.isSelected!!) {
                    itemView.directoryLayout.setBackgroundColor(Color.parseColor("#6C7782"))
                    p0.isSelected = true
                } else {
                    p0.isSelected = false
                    itemView.directoryLayout.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            /*if(multipleSelectionList.isEmpty())
                isMultipleSelection=false*/

            onClickListener?.invoke(directoryList[adapterPosition])
        }

        override fun onLongClick(p0: View?): Boolean {
            isMultipleSelection = true
            itemView.directoryLayout.setBackgroundColor(Color.parseColor("#6C7782"))
            p0?.isSelected = true
            onLongClickListener?.invoke(directoryList[adapterPosition])
            return true
        }

        fun bindDirectory(directoryData: File) {
            itemView.nameTextView.text = directoryData.name
            itemView.nameTextView.isSingleLine = true

            if (itemView.context.getSharedPreferences(
                    "com.erman.draverfm",
                    Context.MODE_PRIVATE
                ).getBoolean("marquee choice", true)
            ) {
                itemView.nameTextView.ellipsize =
                    TextUtils.TruncateAt.MARQUEE  //for sliding names if the length is longer than 1 line
                itemView.nameTextView.isSelected = true
                itemView.nameTextView.marqueeRepeatLimit = -1   //-1 is for forever
            }

            if (directoryData.isDirectory) {
                itemView.imageView.setImageResource(R.drawable.folder_icon)
                itemView.extensionTextView.text = ""
                if (directoryData.listFiles() != null) {
                    if (directoryData.listFiles().isEmpty()) {
                        itemView.totalSizeTextView.text =
                            itemView.context.getString(R.string.empty_folder)
                    } else {
                        itemView.totalSizeTextView.text =
                            directoryData.listFiles().size.toString() + " " + itemView.context.getString(
                                R.string.files_num
                            )
                        itemView.lastModifiedTextView.text =
                            dateFormat.format(directoryData.lastModified())
                    }
                }
            } else {
                itemView.imageView.setImageResource(R.drawable.file_icon)
                itemView.totalSizeTextView.visibility = View.VISIBLE
                itemView.totalSizeTextView.text = getConvertedFileSize(directoryData.length())
                itemView.lastModifiedTextView.text = dateFormat.format(directoryData.lastModified())
                itemView.extensionTextView.text = directoryData.extension
            }
        }
    }

    fun updateData(filesList: List<File>) {
        isMultipleSelection = false
        this.directoryList = filesList
        notifyDataSetChanged()
    }
}