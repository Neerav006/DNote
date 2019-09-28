package com.codefuelindia.dnote.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.*
import com.codefuelindia.dnote.Common.RetrofitClient
import com.codefuelindia.dnote.Model.User
import com.codefuelindia.dnote.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST

import java.util.ArrayList

class ManageCustomersActivity : AppCompatActivity() {
   lateinit var customerListAPI: CustomerListAPI

   lateinit   var recyclerView_customerList: RecyclerView
    lateinit var textView_noCustomers: TextView
   lateinit var progressBar: ProgressBar
    private var searchView: SearchView? = null

    private lateinit var recAdapter: RecAdapter
    private var resCustomerArrayList: ArrayList<User>? = null

    internal var flag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_customers)

        customerListAPI = getCustomerListAPIService(BASE_URL)

        if (intent != null) {
            flag = intent.getStringExtra("flag")
        }

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)



        recyclerView_customerList = findViewById(R.id.manageCustomers_recView)
        textView_noCustomers = findViewById(R.id.manageCustomers_tv_noCustomers)
        progressBar = findViewById(R.id.manageCustomers_progressBar)


        getAllCustomers()

    }

    private fun getAllCustomers() {
        progressBar.visibility = View.VISIBLE

        customerListAPI.customerList.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    if (response.body() != null) {

                        resCustomerArrayList = ArrayList()
                        resCustomerArrayList = response.body() as ArrayList<User>?

                        recAdapter = RecAdapter(resCustomerArrayList!!)

                        recyclerView_customerList.layoutManager =
                                LinearLayoutManager(this@ManageCustomersActivity, LinearLayoutManager.VERTICAL, false)
                        recyclerView_customerList.addItemDecoration(
                            DividerItemDecoration(
                                this@ManageCustomersActivity,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        recyclerView_customerList.adapter = recAdapter

                        recAdapter.notifyDataSetChanged()


                    } else {
                        // response body null
                    }

                } else {
                    // response not successful
                }

            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {

                if (!searchView!!.isIconified) {
                    searchView!!.isIconified = true

                }
                else{
                    finish()
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_pdf_report_view, menu)

        val pdfMenu = menu?.findItem(R.id.action_menu_pdf_view)
        pdfMenu.isVisible = false


        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = (menu?.findItem(R.id.action_search)!!.actionView as SearchView)

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {


                return false

            }

            override fun onQueryTextChange(p0: String?): Boolean {

                if (resCustomerArrayList!!.isNotEmpty()) {

                    recAdapter.filter.filter(p0!!.trim().toLowerCase())

                }


                return false
            }

        }
        )

        return super.onCreateOptionsMenu(menu)


    }

    //---------------------------------------- APIs ------------------------------------------------//

    internal fun getCustomerListAPIService(baseUrl: String): CustomerListAPI {
        return RetrofitClient.getClient(baseUrl).create(CustomerListAPI::class.java)
    }

    interface CustomerListAPI {
        @get:POST("getUsers")
        val customerList: Call<List<User>>
    }


    //----------------------------------- Adapter Class ---------------------------------------------//

    private inner class RecAdapter internal constructor(private val mDataSet: ArrayList<User>) :
        RecyclerView.Adapter<RecAdapter.ViewHolder>() , Filterable {

      private  var filteredUserList:ArrayList<User>

        init {

            filteredUserList = mDataSet

        }


        override fun getFilter(): Filter {

            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {

                    val charString = constraint.toString()

                    if (charString.trim().isEmpty()) {

                        filteredUserList = mDataSet

                    } else {
                        val filterUserLocal: ArrayList<User> = ArrayList()


                        for (items in mDataSet) {

                            if (items.name.contains(charString) || items.mobile.contains(charString)) {

                                filterUserLocal.add(items)
                            }


                        }

                        filteredUserList = filterUserLocal

                    }

                    val filterResults = FilterResults()
                    filterResults.values = filteredUserList
                    return filterResults


                }


                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                    filteredUserList = results!!.values as ArrayList<User>
                    notifyDataSetChanged()


                }


            }


        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_list_customers, viewGroup, false)

            return ViewHolder(v)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            viewHolder.textView_index.text = (position + 1).toString()
            viewHolder.textView_name.text = filteredUserList[position].name
            viewHolder.textView_number.text = filteredUserList[position].mobile
            viewHolder.textView_city.text = filteredUserList[position].city
            viewHolder.textView_remainAmt.text = filteredUserList[position].remainingPayment

            viewHolder.linearLayout_row.setOnClickListener {
                /*Intent i = new Intent(ManageCustomersActivity.this, CustomerDetailsActivity.class);

                    i.putExtra("name", mDataSet.get(position).getName());
                    i.putExtra("number", mDataSet.get(position).getMobile());
                    i.putExtra("city", mDataSet.get(position).getCity());
                    i.putExtra("address", mDataSet.get(position).getAddress());

                    i.putExtra("totalPayment", mDataSet.get(position).getTotalPayment());
                    i.putExtra("collectedPayment", mDataSet.get(position).getCollectedPayment());
                    i.putExtra("remainingPayment", mDataSet.get(position).getRemainingPayment());

                    startActivity(i);*/

                if (flag != null) {

                    val i = Intent(this@ManageCustomersActivity, ProductReportActivity::class.java)

                    i.putExtra("id", filteredUserList[position].id)
                    i.putExtra("name", filteredUserList[position].name)
                    i.putExtra("number", filteredUserList[position].mobile)
                    i.putExtra("city", filteredUserList[position].city)
                    i.putExtra("address", filteredUserList[position].address)
                    i.putExtra("remainingPayment", filteredUserList[position].remainingPayment)

                    startActivity(i)

                } else {

                    val i = Intent(this@ManageCustomersActivity, CreditFormActivity::class.java)

                    i.putExtra("from", "detail")
                    i.putExtra("id", mDataSet[position].id)
                    i.putExtra("user", mDataSet[position])

                    startActivity(i)

                }
            }

        }

        override fun getItemCount(): Int {
            return filteredUserList.size
        }

        internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val linearLayout_row: LinearLayout
            val textView_index: TextView
            val textView_name: TextView
            val textView_number: TextView
            val textView_city: TextView
            val textView_remainAmt: TextView

            init {

                linearLayout_row = v.findViewById<View>(R.id.row_list_customers) as LinearLayout
                textView_index = v.findViewById<View>(R.id.row_customerList_tv_index) as TextView
                textView_name = v.findViewById<View>(R.id.row_customerList_tv_name) as TextView
                textView_number = v.findViewById<View>(R.id.row_customerList_tv_number) as TextView
                textView_city = v.findViewById<View>(R.id.row_customerList_tv_city) as TextView
                textView_remainAmt = v.findViewById<View>(R.id.row_customerList_tv_amount) as TextView
            }
        }

    }

    companion object {

        private val BASE_URL = "http://www.dnote.xyz/api/"
    }

    override fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified) {
            searchView!!.isIconified = true
            return
        }
        super.onBackPressed()
    }

}
