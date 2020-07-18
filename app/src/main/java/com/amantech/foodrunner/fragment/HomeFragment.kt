package com.amantech.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amantech.foodrunner.R
import com.amantech.foodrunner.adapter.HomeRecyclerAdapter
import com.amantech.foodrunner.model.Restaurant
import com.amantech.foodrunner.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    var resInfoList = arrayListOf<Restaurant>()

    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var customDialog: CustomDialog

    var selectedOption: Int = -1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)



        recyclerHome = view.findViewById(R.id.recyclerHome)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        customDialog = CustomDialog(activity as Context)


        //networking part
        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(
                    Request.Method.GET, url, null, Response.Listener {
                        //Handle Response

//                        println("Response is: $it")//OK

                        val mainData = it.getJSONObject("data")

                        try {
                            progressLayout.visibility = View.GONE
                            val success = mainData.getBoolean("success")


                            if (success) {
                                val gson = GsonBuilder().create()
                                val allRes =
                                    gson.fromJson(mainData.toString(), Restaurants::class.java)
                                resInfoList = allRes.data
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, resInfoList)

                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "Some unexpected error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    },
                    Response.ErrorListener {
                        //Handle Errors
                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
                                "Volley error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "SECRET_TOKEN_HERE"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")

            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }


    var ratingComparator = Comparator<Restaurant> { res1, res2 ->

        if (res1.rating.compareTo(res2.rating, true) == 0) {
            //sort according to name if rating is same
            res2.name.compareTo(res1.name, true)
        } else {
            res1.rating.compareTo(res2.rating, true)
        }

    }

    var costComparatorLowToHigh = Comparator<Restaurant> { res1, res2 ->
        if (res1.cost_for_one.compareTo(res2.cost_for_one, true) == 0) {
            //sort according to name if price is same
            res1.name.compareTo(res2.name, true)
        } else {
            res1.cost_for_one.compareTo(res2.cost_for_one, true)
        }
    }

    var costComparatorHighToLow = Comparator<Restaurant> { res1, res2 ->
        if (res1.cost_for_one.compareTo(res2.cost_for_one, true) == 0) {
            //sort according to name if price is same
            res2.name.compareTo(res1.name, true)
        } else {
            res1.cost_for_one.compareTo(res2.cost_for_one, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if (id == R.id.actionSort) {
            //open dialog box
            customDialog.show()
            customDialog.setOnDismissListener {
                when (customDialog.selectedOption) {
                    R.id.sortLowToHigh -> {
                        Collections.sort(resInfoList, costComparatorLowToHigh)
                    }
                    R.id.sortHighToLow -> {
                        Collections.sort(resInfoList, costComparatorHighToLow)
                        resInfoList.reverse()
                    }
                    R.id.sortRating -> {
                        Collections.sort(resInfoList, ratingComparator)
                        resInfoList.reverse()
                    }
                    else -> {
                        //Do nothing
                    }
                }
                recyclerAdapter.notifyDataSetChanged()
            }


        }


        return super.onOptionsItemSelected(item)
    }


    class CustomDialog(
        context: Context
    ) : Dialog(context), android.view.View.OnClickListener {

        var dialog: Dialog? = null
        var positiveButton: Button? = null
        var negativeButton: Button? = null
        var radioGroup: RadioGroup? = null

        var selectedOption: Int = -1

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.radiobutton_options)
            positiveButton = findViewById(R.id.btnOK)
            negativeButton = findViewById(R.id.btnCancel)
            radioGroup = findViewById(R.id.group_Menu)
            positiveButton!!.setOnClickListener(this)
            negativeButton!!.setOnClickListener(this)

            //keep previously selectedOption checked
            if (selectedOption != -1) {
                radioGroup!!.check(selectedOption)
            }
        }


        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btnCancel -> {
                    dismiss()
                }
                R.id.btnOK -> {
                    when (radioGroup!!.checkedRadioButtonId) {
                        R.id.sortLowToHigh -> {
                            selectedOption = R.id.sortLowToHigh
                        }
                        R.id.sortHighToLow -> {
                            selectedOption = R.id.sortHighToLow
                        }
                        R.id.sortRating -> {
                            selectedOption = R.id.sortRating
                        }
                    }
                    dismiss()
                }

            }
        }

    }


}

class Restaurants(val data: ArrayList<Restaurant>)
