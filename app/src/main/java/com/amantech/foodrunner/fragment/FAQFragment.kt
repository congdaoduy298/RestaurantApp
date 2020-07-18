package com.amantech.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.FAQRecyclerAdapter
import com.amantech.foodrunner.adapter.FavoritesRecyclerAdapter
import com.amantech.foodrunner.database.ResEntity
import com.amantech.foodrunner.model.FAQ

class FAQFragment : Fragment() {

    lateinit var recyclerFAQ: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FAQRecyclerAdapter

    var faqList = listOf<FAQ>(
        FAQ(
            "Q.1 How will the training be delivered?",
            "A.1 You will be taught using pre-recorded videos and text tutorials.The training has quizzes, assignments and test to help you learn better. At the end of the training you will attempt a project to get hands on practice of what you have learnt during your training."
        ),
        FAQ(
            "Q.2 What will be the timings of the training?",
            "A.2 This is a purely online training program, you will choose to learn at any time of the day. We will recommend a pace to be followed throughout the program, but the actual timings and the learning hours can be decided by students according to their convenience."
        ),
        FAQ(
            "Q.3 What is the duration of the training?",
            "A.3 The training duration is of 6 weeks."
        ),
        FAQ(
            "Q.4 How much time should I spend everyday?",
            "A.4 We recommend spending 10-12 hours week. However, the actual learning hours can be decided by you as per your convenience."
        ),
        FAQ(
            "Q.5 When can I start my training?",
            "A.5 You can choose your preferred batch date while signing up for the training program."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)

        recyclerFAQ = view.findViewById(R.id.recyclerFAQ)

        layoutManager = LinearLayoutManager(activity)


        if (activity != null) {

            recyclerAdapter = FAQRecyclerAdapter(activity as Context, faqList)
            recyclerFAQ.adapter = recyclerAdapter
            recyclerFAQ.layoutManager = layoutManager

            recyclerFAQ.addItemDecoration(
                DividerItemDecoration(
                    recyclerFAQ.context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )

        }


        return view
    }


}