package com.amantech.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.model.FAQ

class FAQRecyclerAdapter(val context: Context, val itemList: List<FAQ>) :
    RecyclerView.Adapter<FAQRecyclerAdapter.FAQRecyclerViewHolder>() {

    class FAQRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_faq_single_row, parent, false)
        return FAQRecyclerAdapter.FAQRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FAQRecyclerViewHolder, position: Int) {
        val faq: FAQ = itemList[position]
        holder.txtQuestion.text = faq.question
        holder.txtAnswer.text = faq.answer

    }

}