package com.codefuelindia.dnote.view

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.codefuelindia.dnote.R

import kotlinx.android.synthetic.main.activity_credit_form.*
import kotlinx.android.synthetic.main.content_credit_form.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import com.codefuelindia.dnote.Common.*
import com.codefuelindia.dnote.Model.History
import com.codefuelindia.dnote.Model.ResCommon
import com.codefuelindia.dnote.Model.User
import com.codefuelindia.dnote.constants.MyConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreditFormActivity : AppCompatActivity() {
    private lateinit var getUserList: UserListFetch
    private var userList: ArrayList<User> = ArrayList()
    private var selectedUser: User? = null
    private lateinit var creditHistory: CreditHistory
    private var creditList: ArrayList<History> = ArrayList()
    private var debitList: ArrayList<History> = ArrayList()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var credit: AddCredit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_form)
        setSupportActionBar(toolbar)

        credit = RetrofitClient.getClient(MyConstants.BASE_URL).create(AddCredit::class.java)

        progressDialog = ProgressDialog(this@CreditFormActivity)
        progressDialog.setCancelable(false)

        creditHistory = RetrofitClient.getClient(MyConstants.BASE_URL).create(CreditHistory::class.java)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.title = "Credit form"
        toolbar?.setNavigationOnClickListener { finish() }

        getUserList = RetrofitClient.getClient(MyConstants.BASE_URL).create(UserListFetch::class.java)

        rvDebitList.layoutManager = LinearLayoutManager(
            this@CreditFormActivity,
            LinearLayoutManager.VERTICAL, false
        )

        rvCreditList.layoutManager = LinearLayoutManager(
            this@CreditFormActivity,
            LinearLayoutManager.VERTICAL, false
        )


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


        // -----------------------api call----------------------//

        getUserList.getUserList().enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                progressBar.visibility = View.GONE

            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    userList = response.body() as ArrayList<User>
                    if (userList.size > 0) {

                        autoCustomerName.setAdapter(
                            AutoCompleteAdapter(
                                this@CreditFormActivity,
                                R.layout.row_autocomplete_user, userList
                            )
                        )

                        autoCustomerMobile.setAdapter(
                            AutoCompleteAdapter(
                                this@CreditFormActivity,
                                R.layout.row_autocomplete_user, userList
                            )
                        )

                    }


                } else {


                }

            }


        })

        // --------------------- auto complete -----------------------

        autoCustomerName.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    selectedUser = parent.getItemAtPosition(position) as User?
                    if (selectedUser != null) {

                        autoCustomerName.setText(selectedUser?.name)
                        autoCustomerMobile.setText(selectedUser?.mobile)
                        edtAdress.setText(selectedUser?.address)
                        edtCity.setText(selectedUser?.city)

                        progressBar.visibility = View.VISIBLE

                        creditHistory.getCreditHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                progressBar.visibility = View.GONE
                                creditList.clear()
                                showCreditDebitTotal()

                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    creditList = response.body() as ArrayList<History>
                                    if (creditList.size > 0) {

                                        rvCreditList.adapter = CreditListAdapter(creditList)
                                        showCreditDebitTotal()


                                    } else {
                                        creditList.clear()
                                        rvCreditList.adapter = CreditListAdapter(ArrayList())
                                        showCreditDebitTotal()
                                    }


                                } else {
                                    creditList.clear()
                                    showCreditDebitTotal()

                                }


                            }


                        })
                        progressBar.visibility = View.VISIBLE

                        creditHistory.getDebittHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                progressBar.visibility = View.GONE
                                debitList.clear()
                                showCreditDebitTotal()


                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    debitList = response.body() as ArrayList<History>
                                    if (debitList.size > 0) {
                                        rvDebitList.adapter = DebitListAdapter(debitList)
                                        showCreditDebitTotal()


                                    } else {
                                        rvDebitList.adapter = CreditListAdapter(ArrayList())
                                        debitList.clear()
                                        showCreditDebitTotal()

                                    }


                                } else {


                                }


                            }


                        })

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
                        progressBar.visibility = View.VISIBLE

                        creditHistory.getCreditHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                progressBar.visibility = View.GONE
                                creditList.clear()

                                showCreditDebitTotal()

                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    creditList = response.body() as ArrayList<History>
                                    if (creditList.size > 0) {

                                        rvCreditList.adapter = CreditListAdapter(creditList)
                                        showCreditDebitTotal()


                                    } else {
                                        rvCreditList.adapter = CreditListAdapter(ArrayList())
                                        creditList.clear()
                                        showCreditDebitTotal()

                                    }


                                } else {
                                    creditList.clear()
                                    showCreditDebitTotal()


                                }


                            }


                        })
                        progressBar.visibility = View.VISIBLE


                        creditHistory.getDebittHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                progressBar.visibility = View.GONE
                                debitList.clear()

                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    debitList = response.body() as ArrayList<History>
                                    if (debitList.size > 0) {
                                        rvDebitList.adapter = DebitListAdapter(debitList)
                                        showCreditDebitTotal()


                                    } else {
                                        rvDebitList.adapter = DebitListAdapter(ArrayList())
                                        debitList.clear()
                                        showCreditDebitTotal()

                                    }


                                } else {
                                    debitList.clear()
                                    showCreditDebitTotal()


                                }


                            }


                        })

                    }


                }

    }


    fun showCreditDebitTotal() {

        var sumDebit = 0.0

        var sumCredit = 0.0

        for (item in debitList) {

            sumDebit += item.rs.toDouble()


        }


        for (item in creditList) {

            sumCredit += item.rs.toDouble()

        }

        tvTotalDebit.text = "Total: ".plus(sumDebit.toString())
        tvTotalCredit.text = "Total: ".plus(sumCredit.toString())
        tvTotalAmountCredit.text = "Remaining Amount: ".plus(selectedUser?.remainingPayment)


    }


    // ----------------------  credit adapter--------------------//

    class CreditListAdapter(private val dataSet: ArrayList<History>) :
        RecyclerView.Adapter<CreditListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvDate: TextView
            val tvRate: TextView

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
                tvDate = v.findViewById(R.id.tvDate)
                tvRate = v.findViewById(R.id.tvRate)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view.
            val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_credit_history_header, viewGroup, false)

            return ViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            Log.d(TAG, "Element $position set.")

            // Get element from your dataset at this position and replace the contents of the view
            // with that element

            viewHolder.tvDate.text = dataSet[position].createAt
            viewHolder.tvRate.text = dataSet[position].rs
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

        companion object {
            private val TAG = "CustomAdapter"
        }
    }


    // ------------------------------------ Debit list adapter-----------------------------//

    class DebitListAdapter(private val dataSet: ArrayList<History>) :
        RecyclerView.Adapter<DebitListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvDate: TextView
            val tvRate: TextView

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { }
                tvDate = v.findViewById(R.id.tvDate)
                tvRate = v.findViewById(R.id.tvRate)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view.
            val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_credit_history_header, viewGroup, false)

            return ViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the contents of the view
            // with that element

            viewHolder.tvDate.text = dataSet[position].createAt
            viewHolder.tvRate.text = dataSet[position].rs
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

        companion object {
            private val TAG = "CustomAdapter"
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_pay, menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.action_menu_pay) {

            if (selectedUser != null) {

                showPayDialog()


            }


            return true
        }


        return false
    }

    fun showPayDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.row_custom_pay_dialog, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Pay Amount")
        val mAlertDialog = mBuilder.show()

        val edtAmount = mDialogView.findViewById<TextInputEditText>(R.id.edtPay)
        val tvRemaining = mDialogView.findViewById<TextView>(R.id.tvTotalAmountCredit)
        val btnPay = mDialogView.findViewById<Button>(R.id.btnPayCredit)

        btnPay.setOnClickListener {

            if (edtAmount.text.toString().isNotEmpty() && edtAmount.text.toString().toDouble() > 0.0) {

                mAlertDialog.dismiss()
                progressDialog.show()


                credit.addCreditData(selectedUser!!.id, edtAmount.text.toString().trim())
                    .enqueue(object : Callback<ResCommon> {
                        override fun onFailure(call: Call<ResCommon>, t: Throwable) {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                            }
                            MyConstants.showToast(this@CreditFormActivity, "Internal server error")

                        }

                        override fun onResponse(call: Call<ResCommon>, response: Response<ResCommon>) {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                            }

                            if (response.isSuccessful) {

                                if (response.body()!!.msg.equals("true", true)) {
                                    MyConstants.showToast(this@CreditFormActivity, "Successfully added")
                                    finish()


                                } else {

                                    MyConstants.showToast(this@CreditFormActivity, "Try again error!")


                                }


                            } else {
                                MyConstants.showToast(this@CreditFormActivity, "Internal server error")

                            }


                        }


                    })


            } else {
                MyConstants.showToast(this@CreditFormActivity, "Enter Amount")
            }

        }


    }


}
