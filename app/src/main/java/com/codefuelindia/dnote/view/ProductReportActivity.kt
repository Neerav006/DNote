package com.codefuelindia.dnote.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.codefuelindia.dnote.Common.RetrofitClient
import com.codefuelindia.dnote.Common.SessionManager
import com.codefuelindia.dnote.Model.ResProductReport
import com.codefuelindia.dnote.R
import com.codefuelindia.dnote.constants.MyConstants
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import java.util.ArrayList

class ProductReportActivity : AppCompatActivity() {
    lateinit var productReportAPI: ProductReportAPI

    lateinit var tv_name: TextView
    lateinit var tv_number: TextView
    lateinit var tv_address: TextView
    lateinit var tv_city: TextView
    lateinit var tv_remainAmt: TextView
    lateinit var tv_recViewTitle: TextView
    lateinit var tv_noTrans: TextView
    lateinit var recyclerView_transactions: RecyclerView
    lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    lateinit var recAdapter: RecAdapter
    lateinit var responseArrayList: ArrayList<ResProductReport>
    private val PDF_DIRECTORY = "/Dnote"
    private val FILE_NAME = "productdetail"
    private var u_id: String? = null
    private var userName: String? = null
    private var userAddr: String? = null
    private var mobile: String? = null
    private var remainingAmount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_report)

        productReportAPI = getProductReportAPIService(BASE_URL)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        tv_name = findViewById(R.id.productReport_tv_name)
        tv_number = findViewById(R.id.productReport_tv_number)
        tv_address = findViewById(R.id.productReport_tv_address)
        tv_city = findViewById(R.id.productReport_tv_city)
        tv_remainAmt = findViewById(R.id.productReport_tv_remainAmt)
        tv_recViewTitle = findViewById(R.id.productReport_tv_recViewTitle)
        tv_noTrans = findViewById(R.id.productReport_tv_noTrans)
        recyclerView_transactions = findViewById(R.id.productReport_recView_transactions)
        progressBar = findViewById(R.id.productReport_progressBar)

        u_id = intent.getStringExtra("id")
        tv_name.text = intent.getStringExtra("name")
        tv_number.text = intent.getStringExtra("number")
        tv_address.text = intent.getStringExtra("address")
        tv_city.text = intent.getStringExtra("city")
        tv_remainAmt.text = intent.getStringExtra("remainingPayment") + " /-"
        sessionManager = SessionManager(this@ProductReportActivity)

        userName = intent.getStringExtra("name")
        userAddr = intent.getStringExtra("address")
        mobile = intent.getStringExtra("number")
        remainingAmount = intent.getStringExtra("remainingPayment")

        getAllTransactions()

    }

    private fun getAllTransactions() {
        progressBar.visibility = View.VISIBLE

        productReportAPI.login_user(u_id).enqueue(object : Callback<List<ResProductReport>> {
            override fun onResponse(call: Call<List<ResProductReport>>, response: Response<List<ResProductReport>>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    if (response.body() != null) {

                        responseArrayList = ArrayList()
                        responseArrayList = response.body() as ArrayList<ResProductReport>

                        recAdapter = RecAdapter(responseArrayList)

                        recyclerView_transactions.layoutManager =
                                LinearLayoutManager(this@ProductReportActivity, LinearLayoutManager.VERTICAL, false)
                        recyclerView_transactions.addItemDecoration(
                            DividerItemDecoration(
                                this@ProductReportActivity,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        recyclerView_transactions.adapter = recAdapter

                        recAdapter.notifyDataSetChanged()

                        if (responseArrayList.size < 0) {
                            tv_recViewTitle.visibility = View.INVISIBLE
                            tv_noTrans.visibility = View.VISIBLE
                        }

                    } else {
                        // response body is null
                    }

                } else {
                    // response not successful
                }

            }

            override fun onFailure(call: Call<List<ResProductReport>>, t: Throwable) {
                progressBar.visibility = View.GONE
            }
        })

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_pdf_report_view, menu)
        val menuSearch = menu?.findItem(R.id.action_search)
        menuSearch.isVisible = false
        val menuRefresh = menu.findItem(R.id.action_menu_pdf_refresh)
        menuRefresh.isVisible = false


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

            R.id.action_menu_pdf_view -> {

                if (responseArrayList.isNotEmpty()) {


                    requestWriteExternalPermission()


                }


            }
        }

        return super.onOptionsItemSelected(item)
    }


    @AfterPermissionGranted(MyConstants.WRITE_EXTERNAL_STORAGE)
    fun requestWriteExternalPermission() {

        val perms = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(this, *perms)) {

            generateReportPdf(sessionManager.userName, sessionManager.mobile, sessionManager.addr)

        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this@ProductReportActivity, MyConstants.WRITE_EXTERNAL_STORAGE, *perms)
                    .setPositiveButtonText("Ok")
                    .setNegativeButtonText("cancel")
                    .setRationale("Storage permission required..")
                    .setTheme(R.style.AppTheme)
                    .build()
            )
        }

    }


    //----------------------------------------------- APIs ----------------------------------------------------------//

    fun getProductReportAPIService(baseUrl: String): ProductReportAPI {
        return RetrofitClient.getClient(baseUrl).create(ProductReportAPI::class.java)
    }

    interface ProductReportAPI {
        @POST("productDetailReport")
        @FormUrlEncoded
        fun login_user(@Field("id") id: String?): Call<List<ResProductReport>>
    }

    //------------------------------------------------- Adapter Class ----------------------------------------------//

    inner class RecAdapter internal constructor(private val mDataSet: ArrayList<ResProductReport>) :
        RecyclerView.Adapter<RecAdapter.ViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_list_product_report, viewGroup, false)

            return ViewHolder(v)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            viewHolder.textView_date.text = mDataSet[position].create_at
            viewHolder.textView_name.text = mDataSet[position].product_name
            viewHolder.textView_qty.text = mDataSet[position].quntity
            viewHolder.textView_amount.text = mDataSet[position].total

        }

        override fun getItemCount(): Int {
            return mDataSet.size
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val textView_date: TextView
            val textView_name: TextView
            val textView_qty: TextView
            val textView_amount: TextView

            init {

                textView_date = v.findViewById<View>(R.id.row_productReport_tv_date) as TextView
                textView_name = v.findViewById<View>(R.id.row_productReport_tv_name) as TextView
                textView_qty = v.findViewById<View>(R.id.row_productReport_tv_qty) as TextView
                textView_amount = v.findViewById<View>(R.id.row_productReport_tv_amount) as TextView

            }
        }

    }

    companion object {

        private val BASE_URL = "http://www.dnote.xyz/api/"
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


                val customerDetail = PdfPTable(4)
                customerDetail.widthPercentage = 100f
                customerDetail.setWidths(intArrayOf(1, 1, 1, 1))
                customerDetail.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                customerDetail.defaultCell.verticalAlignment = Element.ALIGN_CENTER
                customerDetail.defaultCell.paddingTop = 10f
                customerDetail.defaultCell.paddingBottom = 10f

                customerDetail.addCell(
                    Phrase(
                        lineSpacing, userName,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                customerDetail.addCell(
                    Phrase(
                        lineSpacing, mobile,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                customerDetail.addCell(
                    Phrase(
                        lineSpacing, addr,
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )
                customerDetail.addCell(
                    Phrase(
                        lineSpacing, "Remaining Amt: ".plus(remainingAmount),
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
                        lineSpacing, "Product Detail Report",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )


                val debitHeader = PdfPTable(6)
                debitHeader.widthPercentage = 100f
                debitHeader.setWidths(intArrayOf(1, 1, 1, 1, 1, 1))
                debitHeader.horizontalAlignment = Element.ALIGN_CENTER
                debitHeader.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                debitHeader.defaultCell.paddingTop = 5f
                debitHeader.defaultCell.paddingBottom = 5f


                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Product Name",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Rate",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Qty",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Amount",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Remark",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )

                debitHeader.addCell(
                    Phrase(
                        lineSpacing, "Date",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                    )
                )






                document.add(tableCompanyHeader)
                document.add(customerDetail)
                document.add(reportHeader)
                document.add(debitHeader)

                for ((index, value) in (responseArrayList).withIndex()) {

                    val dataOfCrdr = PdfPTable(6)
                    dataOfCrdr.widthPercentage = 100f
                    dataOfCrdr.setWidths(intArrayOf(1, 1, 1, 1, 1, 1))
                    dataOfCrdr.horizontalAlignment = Element.ALIGN_CENTER
                    dataOfCrdr.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                    dataOfCrdr.defaultCell.paddingTop = 5f
                    dataOfCrdr.defaultCell.paddingBottom = 5f


                    dataOfCrdr.addCell(
                        Phrase(
                            lineSpacing, value.product_name,
                            FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                        )
                    )

                    dataOfCrdr.addCell(
                        Phrase(
                            lineSpacing, value.rate,
                            FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                        )
                    )

                    dataOfCrdr.addCell(
                        Phrase(
                            lineSpacing, value.quntity,
                            FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                        )
                    )
                    dataOfCrdr.addCell(
                        Phrase(
                            lineSpacing, value.total,
                            FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                        )
                    )

                    dataOfCrdr.addCell(
                        Phrase(
                            lineSpacing, value.remark,
                            FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                        )
                    )

                    dataOfCrdr.addCell(
                        Phrase(
                            lineSpacing, value.create_at,
                            FontFactory.getFont(FontFactory.TIMES_BOLD, 10f)
                        )
                    )


                    document.add(dataOfCrdr)


                }

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


}
