package com.codefuelindia.dnote.view

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TextInputEditText
import android.support.v4.content.FileProvider
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
import com.codefuelindia.dnote.constants.MyConstants.Companion.WRITE_EXTERNAL_STORAGE
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.intellij.lang.annotations.JdkConstants
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreditFormActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {


    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {


    }

    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE)
    fun requestWriteExternalPermission() {

        val perms = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(this, *perms)) {

            generateReportPdf(sessionManager.userName, sessionManager.mobile, sessionManager.addr)

        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this@CreditFormActivity, WRITE_EXTERNAL_STORAGE, *perms)
                    .setPositiveButtonText("Ok")
                    .setNegativeButtonText("cancel")
                    .setRationale("Storage permission required..")
                    .setTheme(R.style.AppTheme)
                    .build()
            )
        }

    }

    private lateinit var getUserList: UserListFetch
    private var userList: ArrayList<User> = ArrayList()
    private var selectedUser: User? = null
    private lateinit var creditHistory: CreditHistory
    private var creditList: ArrayList<History> = ArrayList()
    private var debitList: ArrayList<History> = ArrayList()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var credit: AddCredit
    private val PDF_DIRECTORY = "/Dnote"
    private val FILE_NAME = "report"
    private var from: String? = null
    private var userIdfrom: String? = null
    private var fromUser: User? = null

    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_form)
        setSupportActionBar(toolbar)

        sessionManager = SessionManager(this@CreditFormActivity)


        if (intent != null) {
            from = intent.getStringExtra("from")
            userIdfrom = intent.getStringExtra("id")
            fromUser = intent.getParcelableExtra("user")


        }


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


        if (userIdfrom != null && fromUser != null) {

            autoCustomerName.setText(fromUser?.name)
            autoCustomerMobile.setText(fromUser?.mobile)
            edtAdress.setText(fromUser?.address)
            edtCity.setText(fromUser?.city)



            progressBar.visibility = View.VISIBLE

            creditHistory.getCreditHistory(userIdfrom!!).enqueue(object : Callback<List<History>> {
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

            creditHistory.getDebittHistory(userIdfrom!!).enqueue(object : Callback<List<History>> {
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

        if (fromUser != null) {
            tvTotalAmountCredit.text = "Remaining Amount: ".plus(fromUser?.remainingPayment)
        } else {
            tvTotalAmountCredit.text = "Remaining Amount: ".plus(selectedUser?.remainingPayment)
        }


    }


    // ----------------------  credit adapter--------------------//

    inner class CreditListAdapter(private val dataSet: ArrayList<History>) :
        RecyclerView.Adapter<CreditListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
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

            viewHolder.tvDate.text = getYMDDate(dataSet[position].createAt).toString()
            viewHolder.tvRate.text = dataSet[position].rs
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size


    }


    // ------------------------------------ Debit list adapter-----------------------------//

    inner class DebitListAdapter(private val dataSet: ArrayList<History>) :
        RecyclerView.Adapter<DebitListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
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

            viewHolder.tvDate.text = getYMDDate(dataSet[position].createAt).toString()
            viewHolder.tvRate.text = dataSet[position].rs
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_pay, menu)

        val menuItem = menu?.findItem(R.id.action_menu_pay)

        menuItem?.isVisible = !(from != null && from == "detail")


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.action_menu_pay) {

            if (selectedUser != null) {

                showPayDialog()


            }


            return true
        } else if (item.itemId == R.id.action_menu_share) {

            if (selectedUser != null || fromUser != null) {

                requestWriteExternalPermission()


            }


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


    fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children!!.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete()
    }


    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //                Environment.DIRECTORY_PICTURES), PDF_DIRECTORY);

        deleteDir(File(Environment.getExternalStorageDirectory(), PDF_DIRECTORY))

        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory(), PDF_DIRECTORY
        )
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(PDF_DIRECTORY, "failed to create directory")
                return null
            }
        }

        // Create a media file name

        var mediaFile: File? = null


        try {
            mediaFile = File.createTempFile(FILE_NAME, ".pdf", mediaStorageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mediaFile
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    fun generateReportPdf(companyName: String, contactNo: String, addr: String) {


        val file = getOutputMediaFile()

        if (file != null) {

            val document = Document(PageSize.A4)

            try {
                PdfWriter.getInstance(document, FileOutputStream(file.absolutePath))

                document.open()

                val paragraph = Paragraph()
                val fntSize: Float
                val lineSpacing: Float
                fntSize = 10f
                lineSpacing = 30f

                val tableCompanyHeader = PdfPTable(3)
                tableCompanyHeader.widthPercentage = 100f
                tableCompanyHeader.spacingBefore = 30f
                tableCompanyHeader.setWidths(intArrayOf(2, 1, 2))
                tableCompanyHeader.horizontalAlignment = Element.ALIGN_CENTER
                tableCompanyHeader.defaultCell.horizontalAlignment = Element.ALIGN_CENTER





                tableCompanyHeader.addCell(
                    Phrase(
                        lineSpacing, companyName,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                tableCompanyHeader.addCell(
                    Phrase(
                        lineSpacing, contactNo,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                tableCompanyHeader.addCell(
                    Phrase(
                        lineSpacing, addr,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                val reportHeader = PdfPTable(1)
                reportHeader.widthPercentage = 100f
                reportHeader.spacingBefore = 30f
                reportHeader.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                reportHeader.defaultCell.paddingTop = 5f
                reportHeader.defaultCell.paddingBottom = 5f
                reportHeader.addCell(
                    Phrase(
                        lineSpacing, "Credit Debit Report",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                val customerDetail = PdfPTable(4)
                customerDetail.widthPercentage = 100f
                customerDetail.setWidths(intArrayOf(1, 1, 1, 1))
                customerDetail.horizontalAlignment = Element.ALIGN_CENTER
                customerDetail.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                customerDetail.defaultCell.paddingTop = 5f
                customerDetail.defaultCell.paddingBottom = 5f

                customerDetail.addCell(
                    Phrase(
                        lineSpacing, selectedUser?.name,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                customerDetail.addCell(
                    Phrase(
                        lineSpacing, selectedUser?.mobile,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                customerDetail.addCell(
                    Phrase(
                        lineSpacing, selectedUser?.address,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )
                customerDetail.addCell(
                    Phrase(
                        lineSpacing, selectedUser?.city,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                val debitHeader = PdfPTable(2)
                debitHeader.widthPercentage = 100f
                debitHeader.setWidths(intArrayOf(1, 1))
                debitHeader.horizontalAlignment = Element.ALIGN_CENTER
                debitHeader.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                debitHeader.defaultCell.paddingTop = 5f
                debitHeader.defaultCell.paddingBottom = 5f


                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Credit",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Debit",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                val historyHeader = PdfPTable(4)
                historyHeader.widthPercentage = 100f
                historyHeader.setWidths(intArrayOf(1, 1, 1, 1))
                historyHeader.horizontalAlignment = Element.ALIGN_CENTER
                historyHeader.defaultCell.horizontalAlignment = Element.ALIGN_CENTER

                historyHeader.addCell(
                    Phrase(
                        lineSpacing, "Date",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                historyHeader.addCell(
                    Phrase(
                        lineSpacing, "Rs",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                historyHeader.addCell(
                    Phrase(
                        lineSpacing, "Date",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                historyHeader.addCell(
                    Phrase(
                        lineSpacing, "Rs",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                val count = if (debitList.size > creditList.size) {
                    debitList.size
                } else {
                    creditList.size
                }

                document.add(tableCompanyHeader)
                document.add(customerDetail)
                document.add(reportHeader)
                document.add(debitHeader)
                document.add(historyHeader)





                for (i in 0 until count) {

                    val rsCreditTable = PdfPTable(4)
                    rsCreditTable.widthPercentage = 100f
                    rsCreditTable.setWidths(intArrayOf(1, 1, 1, 1))

                    if (i < creditList.size) {


                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, creditList[i].createAt,
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )

                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, creditList[i].rs,
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )


                    } else {

                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, "",
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )

                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, "",
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )


                    }


                    if (i < debitList.size) {


                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, debitList[i].createAt,
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )

                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, debitList[i].rs,
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )


                    } else {

                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, "",
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )

                        rsCreditTable.addCell(
                            Phrase(
                                lineSpacing, "",
                                FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                            )
                        )


                    }

                    document.add(rsCreditTable)


                }

                var sumDebit = 0.0

                var sumCredit = 0.0

                for (item in debitList) {

                    sumDebit += item.rs.toDouble()


                }


                for (item in creditList) {

                    sumCredit += item.rs.toDouble()

                }

                val totalCreditDebit = PdfPTable(4)
                totalCreditDebit.widthPercentage = 100f
                totalCreditDebit.setWidths(intArrayOf(1, 1, 1, 1))

                totalCreditDebit.addCell(
                    Phrase(
                        lineSpacing, "Total: ",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                totalCreditDebit.addCell(
                    Phrase(
                        lineSpacing, sumCredit.toString(),
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                totalCreditDebit.addCell(
                    Phrase(
                        lineSpacing, "Total: ",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                totalCreditDebit.addCell(
                    Phrase(
                        lineSpacing, sumDebit.toString(),
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                document.add(totalCreditDebit)


                val remainingTable = PdfPTable(1)
                remainingTable.defaultCell.paddingTop = 5f
                remainingTable.defaultCell.paddingBottom = 5f
                remainingTable.widthPercentage = 100f
                remainingTable.setWidths(intArrayOf(1))
                remainingTable.spacingBefore = 20f

                remainingTable.addCell(
                    Phrase(
                        lineSpacing, "Total Remaining:  ".plus((sumDebit - sumCredit).toString()),
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                val footer1 = PdfPTable(1)
                footer1.widthPercentage = 100f
                footer1.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                footer1.defaultCell.paddingTop = 5f
                footer1.defaultCell.paddingBottom = 5f

                footer1.addCell(
                    Phrase(
                        lineSpacing, "Design & Developed By Codefuel Technology Pvt. Ltd. ",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                val footer2 = PdfPTable(1)
                footer2.widthPercentage = 100f
                footer2.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                footer2.defaultCell.paddingTop = 5f
                footer2.defaultCell.paddingBottom = 5f

                footer2.addCell(
                    Phrase(
                        lineSpacing, "Mobile :- 9427745635 E-Mail :- info@codefuelindia.com\n" +
                                "F-1, Ashwamegh City Center, Opp. Medical College, Polytechnic-Gadhoda Road, Motipura, Himmatnagar, Gujarat\n" +
                                "383001",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                document.add(remainingTable)
                document.add(footer1)
                document.add(footer2)

                document.close()



                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    var uri: Uri? = null
                    // So you have to use Provider
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", file)

                        // Add in case of if We get Uri from fileProvider.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } else {
                        uri = Uri.fromFile(file)
                    }

                    intent.setDataAndType(uri, "application/pdf")
                    startActivity(intent)
                } catch (e: RuntimeException) {

                }


            } catch (e: DocumentException) {

                Log.e("exception", e.message)

            }


        }


    }


    fun getYMDDate(dateString: String): String {


//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
//        val date = format.parse(dateString)

        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateString)
        return SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(dateTime)

        //  Log.e("formated date", newstring)

        //  return SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(newstring)


    }

}
