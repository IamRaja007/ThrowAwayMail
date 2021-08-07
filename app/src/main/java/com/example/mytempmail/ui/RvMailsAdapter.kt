package com.example.mytempmail.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mytempmail.R
import com.example.mytempmail.models.MailBoxModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_showmail.*
import kotlinx.android.synthetic.main.item_row_mail.view.*
import kotlin.collections.ArrayList

class RvMailsAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MailBoxModel>() {

        override fun areItemsTheSame(oldItem: MailBoxModel, newItem: MailBoxModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MailBoxModel, newItem: MailBoxModel): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row_mail,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {

                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<MailBoxModel>) {
        differ.submitList(list)
    }

    class ViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MailBoxModel) = with(itemView) {
            itemView.setOnClickListener {
                item.id?.let { it1 ->
                    interaction?.onItemSelected(
                        adapterPosition, item,
                        it1.toInt()
                    )
                }
            }
            val name=SplitEmail(item.from!!)?.get(0) ?: "noName"
            itemView.tvName.text = name
            itemView.tvSubject.text = item.subject
            itemView.tvDate.text = item.date
            itemView.tvEmail.text = item.id
            Picasso.get().load(
                "https://ui-avatars.com/api/?background=234&color=fff&size=256&rounded=true&name=$name"
            ).placeholder(R.drawable.default_image).into(itemView.circleImageViewMail)

        }
    }

    interface Interaction {   //detecting clicks on each item of recycler view items
        fun onItemSelected(position: Int, item: MailBoxModel, itemID: Int)
    }

}

fun SplitEmail(string: String): List<String>? {
    val index = string.indexOf('@')
    val prefix = string.substring(0, index)
    val domain = string.substring(index + 1)
    val list: ArrayList<String> = ArrayList()
    list.add(0, prefix)
    list.add(1, domain)
    return list
}