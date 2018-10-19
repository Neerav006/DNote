package com.codefuelindia.dnote.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.TextView
import com.codefuelindia.dnote.Common.SessionManager
import com.codefuelindia.dnote.Common.UserListFetch
import com.codefuelindia.dnote.Model.User
import com.codefuelindia.dnote.R
import com.codefuelindia.dnote.constants.MyConstants
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.content_summary_report.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class SummaryReportActivity : AppCompatActivity() {
    private lateinit var getUserList: UserListFetch
    private var userList: ArrayList<User> = ArrayList()

    private val PDF_DIRECTORY = "/Dnote"
    private val FILE_NAME = "summary"

    private lateinit var sessionManager:SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_report)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        sessionManager = SessionManager(this@SummaryReportActivity)

        rvList.layoutManager = LinearLayoutManager(
            this@SummaryReportActivity,
            LinearLayoutManager.VERTICAL, false
        )



        getUserList.getUserList().enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                progressBar.visibility = View.GONE
                rvList.adapter = UserListAdapter(ArrayList())
                MyConstants.showToast(this@SummaryReportActivity, "Internal server error")
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {

                    userList = response.body() as ArrayList<User>
                    if (userList.size > 0) {

                        rvList.adapter = UserListAdapter(userList)

                    } else {
                        rvList.adapter = UserListAdapter(ArrayList())
                        MyConstants.showToast(this@SummaryReportActivity, "No record found")
                    }


                } else {
                    rvList.adapter = UserListAdapter(ArrayList())
                    MyConstants.showToast(this@SummaryReportActivity, "Internal server error")

                }

            }


        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_pdf_report_view, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == R.id.action_menu_pdf_view) {

            requestWriteExternalPermission()


            return userList.size >0


        }



        return false
    }


    // ------------------------------------ adapter-----------------------------------------------

    inner class UserListAdapter(private val dataSet: ArrayList<User>) :
        RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvName: TextView
            val tvCity: TextView
            val tvMobile: TextView
            val tvCredit: TextView
            val tvDebit: TextView
            val tvTotalCrdr: TextView

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { }
                tvName = v.findViewById(R.id.tvName)
                tvCity = v.findViewById(R.id.tvCity)
                tvMobile = v.findViewById(R.id.tvMobile)
                tvCredit = v.findViewById(R.id.tvTotalCredit)
                tvDebit = v.findViewById(R.id.tvTotalDebit)
                tvTotalCrdr = v.findViewById(R.id.tvTotalCrDr)
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

            viewHolder.tvName.text = dataSet[position].name
            viewHolder.tvCity.text = dataSet[position].city
            viewHolder.tvMobile.text = dataSet[position].mobile
            viewHolder.tvCredit.text = "Credit: ".plus(dataSet[position].collectedPayment)
            viewHolder.tvDebit.text = "Debit; ".plus(dataSet[position].totalPayment)
            viewHolder.tvTotalCrdr.text = "Rem: ".plus(dataSet[position].remainingPayment)


        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size


    }


    @AfterPermissionGranted(MyConstants.WRITE_EXTERNAL_STORAGE)
    fun requestWriteExternalPermission() {

        val perms = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (EasyPermissions.hasPermissions(this, *perms)) {

            generateReportPdf("TCS", "9558521007", "Gandhinagar")

        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this@SummaryReportActivity, MyConstants.WRITE_EXTERNAL_STORAGE, *perms)
                    .setPositiveButtonText("Ok")
                    .setNegativeButtonText("cancel")
                    .setRationale("Storage permission required..")
                    .setTheme(R.style.AppTheme)
                    .build()
            )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
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
                customerDetail.setWidths(intArrayOf(1, 1, 1, 1))
                customerDetail.horizontalAlignment = Element.ALIGN_CENTER
                customerDetail.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                customerDetail.defaultCell.paddingTop = 5f
                customerDetail.defaultCell.paddingBottom = 5f



                val debitHeader = PdfPTable(2)
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




                document.add(tableCompanyHeader)
                document.add(customerDetail)
                document.add(reportHeader)
                document.add(debitHeader)
                document.add(historyHeader)










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


}
