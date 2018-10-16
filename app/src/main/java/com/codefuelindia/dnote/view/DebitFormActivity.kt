package com.codefuelindia.dnote.view

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import com.codefuelindia.dnote.Common.AutoCompleteAdapter
import com.codefuelindia.dnote.Common.ProductListFetch
import com.codefuelindia.dnote.Common.RetrofitClient
import com.codefuelindia.dnote.Common.UserListFetch
import com.codefuelindia.dnote.Model.Product
import com.codefuelindia.dnote.Model.User
import com.codefuelindia.dnote.R
import com.codefuelindia.dnote.constants.MyConstants

import kotlinx.android.synthetic.main.activity_debit_form.*
import kotlinx.android.synthetic.main.content_debit_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter


class DebitFormActivity : AppCompatActivity() {

    private lateinit var getUserList: UserListFetch
    private lateinit var getProductList: ProductListFetch
    private var userList: ArrayList<User> = ArrayList()
    private var productList: ArrayList<Product> = ArrayList()
    private var selectedUser: User? = null
    private var selectedProduct:Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_form)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserList = RetrofitClient.getClient(MyConstants.BASE_URL).create(UserListFetch::class.java)
        getProductList = RetrofitClient.getClient(MyConstants.BASE_URL).create(ProductListFetch::class.java)

        toolbar?.title = "Debit form"
        toolbar?.setNavigationOnClickListener { finish() }

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

        // -------------------------- API CALL -----------------------------------//

        getUserList.getUserList().enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {


            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {

                if (response.isSuccessful) {

                    userList = response.body() as ArrayList<User>
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

                        spProduct.adapter = MyCustomAdapter66(this@DebitFormActivity,
                            R.layout.row_autocomplete_user,productList)

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

                    }


                }

        autoCustomerMobile.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->

                    autoCustomerName.setText(selectedUser?.name)
                    autoCustomerMobile.setText(selectedUser?.mobile)
                    edtAdress.setText(selectedUser?.address)
                    edtCity.setText(selectedUser?.city)


                }


        //  product list spinner
        spProduct.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {



            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {




            }

        }




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

        internal fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
            // TODO Auto-generated method stub
            //return super.getView(position, convertView, parent);

            val inflater = layoutInflater
            val row = inflater.inflate(R.layout.row_autocomplete_user, parent, false)
            val label = row.findViewById(R.id.tvName) as TextView
            label.text = categorylists[position].name


            return row
        }
    }

}
