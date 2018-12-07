package com.codefuelindia.dnote.view

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.AdapterView
import com.codefuelindia.dnote.R
import com.codefuelindia.dnote.constants.MyConstants

import kotlinx.android.synthetic.main.activity_debit_form.*
import kotlinx.android.synthetic.main.content_debit_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.codefuelindia.dnote.Common.*
import com.codefuelindia.dnote.Model.*
import com.google.gson.Gson


class DebitFormActivity : AppCompatActivity() {

    private lateinit var getUserList: UserListFetch
    private lateinit var getProductList: ProductListFetch
    private var userList: ArrayList<User> = ArrayList()
    private var productList: ArrayList<Product> = ArrayList()
    private var selectedUser: User? = null
    private var selectedProduct: Product? = null
    private var debitList: ArrayList<DebitModel> = ArrayList()
    private lateinit var customAdapter: CustomAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var addDebit: AddDebit
    private var isResponceOk = false
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_form)
        setSupportActionBar(toolbar)

        sessionManager = SessionManager(this@DebitFormActivity)
        addDebit = RetrofitClient.getClient(MyConstants.BASE_URL).create(AddDebit::class.java)


        progressDialog = ProgressDialog(this@DebitFormActivity)
        progressDialog.setCancelable(false)


        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserList = RetrofitClient.getClient(MyConstants.BASE_URL).create(UserListFetch::class.java)
        getProductList = RetrofitClient.getClient(MyConstants.BASE_URL).create(ProductListFetch::class.java)

        toolbar?.title = "Debit form"
        toolbar?.setNavigationOnClickListener { finish() }

        rvList?.layoutManager = LinearLayoutManager(
            this@DebitFormActivity,
            LinearLayoutManager.VERTICAL, false
        )

        customAdapter = CustomAdapter(debitList)
        rvList.adapter = customAdapter


        edtAdress.setOnTouchListener { v, event ->

            if (edtAdress.hasFocus()) {

                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener true
                    }
                }

            }


            return@setOnTouchListener false
        }


        btnSaveDebitData?.setOnClickListener {

            saveDebitData()


        }


        // -------------------------- API CALL -----------------------------------//

        getUserList.getUserList().enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {

                isResponceOk = false
                MyConstants.showToast(this@DebitFormActivity, "Server error!Try again")
                finish()

            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {

                if (response.isSuccessful) {

                    userList = response.body() as ArrayList<User>
                    isResponceOk = true
                    if (userList.size > 0) {

                        autoCustomerName.setAdapter(
                            AutoCompleteAdapter(
                                this@DebitFormActivity,
                                R.layout.row_autocomplete_user, userList
                            )
                        )

                        autoCustomerMobile.setAdapter(
                            AutoCompleteAdapter(
                                this@DebitFormActivity,
                                R.layout.row_autocomplete_user, userList
                            )
                        )

                    }


                } else {

                    isResponceOk = false
                    MyConstants.showToast(this@DebitFormActivity, "Server error!Try again")
                    finish()

                }

            }


        })


        getProductList.getProductList().enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {


            }

            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {

                    productList = response.body() as ArrayList<Product>

                    if (productList.size > 0) {

                        spProduct.adapter = MyCustomAdapter66(
                            this@DebitFormActivity,
                            R.layout.row_autocomplete_user, productList
                        )

                    }


                } else {

                }


            }


        })


        // --------------------- auto complete ---------------------//


        autoCustomerName.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    selectedUser = parent.getItemAtPosition(position) as User?
                    if (selectedUser != null) {

                        autoCustomerName.setText(selectedUser?.name)
                        autoCustomerMobile.setText(selectedUser?.mobile)
                        edtAdress.setText(selectedUser?.address)
                        edtCity.setText(selectedUser?.city)

                        tvUserPurchaseAmount.text = "Remaining: ".plus(selectedUser?.remainingPayment)


                    }


                }

        autoCustomerMobile.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    selectedUser = parent.getItemAtPosition(position) as User?
                    if (selectedUser != null) {

                        autoCustomerName.setText(selectedUser?.name)
                        autoCustomerMobile.setText(selectedUser?.mobile)
                        edtAdress.setText(selectedUser?.address)
                        edtCity.setText(selectedUser?.city)

                        tvUserPurchaseAmount.text = "Remaining: ".plus(selectedUser?.remainingPayment)


                    }

                }


        //  product list spinner
        spProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {


            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                selectedProduct = parent?.getItemAtPosition(position) as Product

                edtRate.setText(selectedProduct?.rate)


            }

        }

        btnAddDebit?.setOnClickListener {


            MyConstants.hideSoftKeyBoard(this@DebitFormActivity.currentFocus, this@DebitFormActivity)



            if (selectedProduct != null && edtRate.text.toString().trim().isNotEmpty()
                && edtQty.text.toString().trim().isNotEmpty()
                && edtQty.text.toString().toInt() != 0
                && edtRate.text.toString().toDouble() != 0.0
            ) {

                // add in recyclerview

                val debitModel = DebitModel()
                debitModel.name = selectedProduct?.name
                debitModel.qty = edtQty.text.toString().trim()
                debitModel.rate = edtRate.text.toString().trim()
                debitModel.total = (edtQty.text.toString().toDouble() * edtRate.text.toString().toDouble()).toString()

                debitList.add(debitModel)

                customAdapter.notifyDataSetChanged()

                var sum = 0.0

                for (item in debitList) {

                    sum += item.total.toDouble()

                }

                if (sum > 0) {

                    tvGrandTotal.text = "Grand Total: ".plus(sum.toString())

                }


            }


        }


    }

    private fun saveDebitData() {


        if (debitList.isNotEmpty() && isResponceOk) {

            val insertDebit = InserDebitData()
            insertDebit.u_id = sessionManager.keyUId

            if (selectedUser == null || selectedUser!!.id == null) {
                insertDebit.id = ""
            } else {
                insertDebit.id = selectedUser?.id
            }


            insertDebit.mobile = autoCustomerMobile.text.toString().trim()
            insertDebit.addr = edtAdress.text.toString().trim()
            insertDebit.city = edtCity.text.toString().trim()
            insertDebit.name = autoCustomerName.text.toString().trim()
            insertDebit.remark = edtDescription.text.toString().trim()

            var sum = 0.0

            for (item in debitList) {

                sum += item.total.toDouble()

            }

            insertDebit.total = sum.toString()

            insertDebit.debitModelArrayList = debitList

            Log.e("u_id",sessionManager.keyUId)

            Log.e("debit json", Gson().toJson(insertDebit))

            progressDialog.show()

            // ---------------------Add debit api call ----------------


            addDebit.addDebitData(insertDebit).enqueue(object : Callback<ResCommon> {
                override fun onFailure(call: Call<ResCommon>, t: Throwable) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    MyConstants.showToast(this@DebitFormActivity, "Internal server error")


                }

                override fun onResponse(call: Call<ResCommon>, response: Response<ResCommon>) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }

                    if (response.isSuccessful) {

                        if (response.body()!!.msg.equals("true", true)) {

                            MyConstants.showToast(this@DebitFormActivity, "Successfully added")
                            finish()

                        } else if (response.body()!!.msg.equals("10x", true)) {
                            MyConstants.showToast(this@DebitFormActivity, "Subscription expired..")
                            finishAffinity()
                            startActivity(Intent(this@DebitFormActivity, LoginActivity::class.java))

                        } else {

                            MyConstants.showToast(this@DebitFormActivity, "Try again..")

                        }

                    } else {

                        MyConstants.showToast(this@DebitFormActivity, "Internal server error")


                    }


                }


            })


        }

    }

    // ----------------------menu ------------------------------//

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_refresh_debitlist, menu)

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.action_menu_refresh) {

            debitList.clear()
            customAdapter.notifyDataSetChanged()
            tvGrandTotal.text = ""

            return true

        } else if (item!!.itemId == R.id.action_menu_save) {
            saveDebitData()

        }



        return false
    }


    inner class MyCustomAdapter66 internal constructor(
        context: Context, textViewResourceId: Int,
        private val categorylists: ArrayList<Product>
    )// TODO Auto-generated constructor stub
        : ArrayAdapter<Product>(context, textViewResourceId, categorylists) {

        override fun getDropDownView(
            position: Int, convertView: View?,
            parent: ViewGroup
        ): View {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent)
        }

        private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            val inflater = layoutInflater
            val row = inflater.inflate(R.layout.row_autocomplete_user, parent, false)
            val label = row.findViewById(R.id.tvName) as TextView
            val label22 = row.findViewById<TextView>(R.id.tvMobile)
            label22.visibility = View.GONE
            label.text = categorylists[position].name


            return row
        }
    }

    // ------------------------------ recycler view---------------------------------------//


    inner class CustomAdapter(private val dataSet: ArrayList<DebitModel>) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        var mdataSet: ArrayList<DebitModel> = dataSet

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvName: TextView
            val tvRate: TextView
            val tvQty: TextView
            val tvTotal: TextView
            val ivRemove: ImageView

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { }
                tvName = v.findViewById<TextView>(R.id.tvProductName)
                tvRate = v.findViewById(R.id.tvRate)
                tvQty = v.findViewById(R.id.tvQty)
                tvTotal = v.findViewById(R.id.tvTotal)
                ivRemove = v.findViewById(R.id.ivRemove)

                ivRemove.visibility = View.VISIBLE

                ivRemove.setOnClickListener {
                    dataSet.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    notifyDataSetChanged()


                    var sum = 0.0

                    for (items in dataSet) {

                        sum += items.total.toDouble()

                    }

                    tvGrandTotal.text = "Grand Total: ".plus(sum.toString())


                }
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view.
            val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_debit_header, viewGroup, false)

            return ViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            viewHolder.tvName.text = dataSet[position].name
            viewHolder.tvRate.text = dataSet[position].rate
            viewHolder.tvQty.text = dataSet[position].qty
            viewHolder.tvTotal.text = dataSet[position].total


        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size


    }


    // debit save -- addDebit
    // credit save -- addCredit id,amount

}
