package com.codefuelindia.dnote.view

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import com.codefuelindia.dnote.Common.*
import com.codefuelindia.dnote.Model.History
import com.codefuelindia.dnote.Model.ResCommon
import com.codefuelindia.dnote.Model.User
import com.codefuelindia.dnote.R
import com.codefuelindia.dnote.constants.MyConstants
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.content_report_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_report)


        credit = RetrofitClient.getClient(MyConstants.BASE_URL).create(AddCredit::class.java)

        progressDialog = ProgressDialog(this@ReportActivity)
        progressDialog.setCancelable(false)

        creditHistory = RetrofitClient.getClient(MyConstants.BASE_URL).create(CreditHistory::class.java)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        report_toolbar?.title = "Credit form"
        report_toolbar?.setNavigationOnClickListener { finish() }

        getUserList = RetrofitClient.getClient(MyConstants.BASE_URL).create(UserListFetch::class.java)

        report_rvDebitList.layoutManager = LinearLayoutManager(
            this@ReportActivity,
            LinearLayoutManager.VERTICAL, false
        )

        report_rvCreditList.layoutManager = LinearLayoutManager(
            this@ReportActivity,
            LinearLayoutManager.VERTICAL, false
        )


        report_edtAdress.setOnTouchListener { v, event ->

            if (report_edtAdress.hasFocus()) {

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
                report_progressBar.visibility = View.GONE

            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                report_progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    userList = response.body() as ArrayList<User>
                    if (userList.size > 0) {

                        report_autoCustomerName.setAdapter(
                            AutoCompleteAdapter(
                                this@ReportActivity,
                                R.layout.row_autocomplete_user, userList
                            )
                        )

                        report_autoCustomerMobile.setAdapter(
                            AutoCompleteAdapter(
                                this@ReportActivity,
                                R.layout.row_autocomplete_user, userList
                            )
                        )

                    }


                } else {


                }

            }


        })

        // --------------------- auto complete -----------------------

        report_autoCustomerName.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    selectedUser = parent.getItemAtPosition(position) as User?
                    if (selectedUser != null) {

                        report_autoCustomerName.setText(selectedUser?.name)
                        report_autoCustomerMobile.setText(selectedUser?.mobile)
                        report_edtAdress.setText(selectedUser?.address)
                        report_edtCity.setText(selectedUser?.city)

                        report_progressBar.visibility = View.VISIBLE

                        creditHistory.getCreditHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                report_progressBar.visibility = View.GONE
                                creditList.clear()
                                showCreditDebitTotal()

                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                report_progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    creditList = response.body() as ArrayList<History>
                                    if (creditList.size > 0) {

                                        report_rvCreditList.adapter = CreditListAdapter(creditList)
                                        showCreditDebitTotal()


                                    } else {
                                        creditList.clear()
                                        report_rvCreditList.adapter = CreditListAdapter(ArrayList())
                                        showCreditDebitTotal()
                                    }


                                } else {
                                    creditList.clear()
                                    showCreditDebitTotal()

                                }


                            }


                        })
                        report_progressBar.visibility = View.VISIBLE

                        creditHistory.getDebittHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                report_progressBar.visibility = View.GONE
                                debitList.clear()
                                showCreditDebitTotal()


                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                report_progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    debitList = response.body() as ArrayList<History>
                                    if (debitList.size > 0) {
                                        report_rvDebitList.adapter = DebitListAdapter(debitList)
                                        showCreditDebitTotal()


                                    } else {
                                        report_rvDebitList.adapter = DebitListAdapter(ArrayList())
                                        debitList.clear()
                                        showCreditDebitTotal()

                                    }


                                } else {


                                }


                            }


                        })

                    }


                }

        report_autoCustomerMobile.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    selectedUser = parent.getItemAtPosition(position) as User?


                    if (selectedUser != null) {

                        report_autoCustomerName.setText(selectedUser?.name)
                        report_autoCustomerMobile.setText(selectedUser?.mobile)
                        report_edtAdress.setText(selectedUser?.address)
                        report_edtCity.setText(selectedUser?.city)
                        report_progressBar.visibility = View.VISIBLE

                        creditHistory.getCreditHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                report_progressBar.visibility = View.GONE
                                creditList.clear()

                                showCreditDebitTotal()

                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                report_progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    creditList = response.body() as ArrayList<History>
                                    if (creditList.size > 0) {

                                        report_rvCreditList.adapter = CreditListAdapter(creditList)
                                        showCreditDebitTotal()


                                    } else {
                                        report_rvCreditList.adapter = CreditListAdapter(ArrayList())
                                        creditList.clear()
                                        showCreditDebitTotal()

                                    }


                                } else {
                                    creditList.clear()
                                    showCreditDebitTotal()


                                }


                            }


                        })
                        report_progressBar.visibility = View.VISIBLE


                        creditHistory.getDebittHistory(selectedUser!!.id).enqueue(object : Callback<List<History>> {
                            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                report_progressBar.visibility = View.GONE
                                debitList.clear()

                            }

                            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                report_progressBar.visibility = View.GONE

                                if (response.isSuccessful) {

                                    debitList = response.body() as ArrayList<History>
                                    if (debitList.size > 0) {
                                        report_rvDebitList.adapter = DebitListAdapter(debitList)
                                        showCreditDebitTotal()


                                    } else {
                                        report_rvDebitList.adapter = DebitListAdapter(ArrayList())
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

        report_tvTotalDebit.text = "Total: ".plus(sumDebit.toString())
        report_tvTotalCredit.text = "Total: ".plus(sumCredit.toString())
        report_tvTotalAmountCredit.text = "Remaining Amount: ".plus(selectedUser?.remainingPayment)


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
                            MyConstants.showToast(this@ReportActivity, "Internal server error")

                        }

                        override fun onResponse(call: Call<ResCommon>, response: Response<ResCommon>) {
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                            }

                            if (response.isSuccessful) {

                                if (response.body()!!.msg.equals("true", true)) {
                                    MyConstants.showToast(this@ReportActivity, "Successfully added")
                                    finish()


                                } else {

                                    MyConstants.showToast(this@ReportActivity, "Try again error!")


                                }


                            } else {
                                MyConstants.showToast(this@ReportActivity, "Internal server error")

                            }


                        }


                    })


            } else {
                MyConstants.showToast(this@ReportActivity, "Enter Amount")
            }

        }


    }



}
