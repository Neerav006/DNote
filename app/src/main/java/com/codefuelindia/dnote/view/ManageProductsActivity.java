package com.codefuelindia.dnote.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.codefuelindia.dnote.Common.RetrofitClient;
import com.codefuelindia.dnote.Model.Product;
import com.codefuelindia.dnote.Model.ResCommon;
import com.codefuelindia.dnote.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://www.dnote.xyz/api/";
    ProductListAPI productListAPI;
    DeleteProductAPI deleteProductAPI;

    RecyclerView recyclerView_productList;
    TextView textView_noproducts;
    ProgressBar progressBar;

    RecAdapter recAdapter;
    private ArrayList<Product> resProductArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        productListAPI = getCustomerListAPIService(BASE_URL);
        deleteProductAPI = getDeleteProductAPIService(BASE_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.manageProducts_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView_productList = findViewById(R.id.manageProducts_recView);
        textView_noproducts = findViewById(R.id.manageProducts_tv_noProducts);
        progressBar = findViewById(R.id.manageProducts_progressBar);


        getAllProducts();


    }


    private void getAllProducts() {
        progressBar.setVisibility(View.VISIBLE);

        productListAPI.getProductList().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    if (response.body() != null) {

                        resProductArrayList = new ArrayList<>();
                        resProductArrayList = (ArrayList<Product>) response.body();

                        recAdapter = new RecAdapter(resProductArrayList);

                        recyclerView_productList.setLayoutManager(new LinearLayoutManager(ManageProductsActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView_productList.addItemDecoration(new DividerItemDecoration(ManageProductsActivity.this, DividerItemDecoration.VERTICAL));
                        recyclerView_productList.setAdapter(recAdapter);

                        recAdapter.notifyDataSetChanged();


                    } else {
                        // response body null
                    }

                } else {
                    // response not successful
                }

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_product_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_addProduct:
                goToAddProduct();

        }

        return super.onOptionsItemSelected(item);
    }

    private void goToAddProduct() {
        startActivity(new Intent(ManageProductsActivity.this, AddProductActivity.class));
    }


    private void showDeleteDialog(final String p_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to delete this product?")
                .setTitle("Confirm Delete");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                progressBar.setVisibility(View.VISIBLE);

                deleteProductAPI.deleteProduct(p_id).enqueue(new Callback<ResCommon>() {
                    @Override
                    public void onResponse(Call<ResCommon> call, Response<ResCommon> response) {

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {

                            if (response.body() != null) {

                                if (response.body().getMsg().equals("true")) {
                                    // Product deleted
                                    Toast.makeText(getApplicationContext(), "Product deleted", Toast.LENGTH_SHORT).show();

                                    resProductArrayList.clear();

                                    recAdapter.notifyDataSetChanged();

                                    getAllProducts();


                                }

                            } else {
                                // response body null
                            }

                        } else {
                            // response not successful
                        }

                    }

                    @Override
                    public void onFailure(Call<ResCommon> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


//---------------------------------------- APIs -----------------------------------------------------//

    ProductListAPI getCustomerListAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(ProductListAPI.class);
    }

    interface ProductListAPI {
        @POST("getProduct")
        Call<List<Product>> getProductList();
    }


    DeleteProductAPI getDeleteProductAPIService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(DeleteProductAPI.class);
    }

    interface DeleteProductAPI {
        @POST("deleteProduct")
        @FormUrlEncoded
        Call<ResCommon> deleteProduct(@Field("id") String id);
    }


//---------------------------------------- Adapter Class --------------------------------------------//

    public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

        private ArrayList<Product> mDataSet;

        RecAdapter(ArrayList<Product> mDataSet) {
            this.mDataSet = mDataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_list_products, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            viewHolder.getTextView_index().setText(String.valueOf(position + 1));
            viewHolder.getTextView_name().setText(mDataSet.get(position).getName());
            viewHolder.getTextView_rate().setText(mDataSet.get(position).getRate() + "/-");

            viewHolder.getImageView_remove().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(), "Clicked: " + position, Toast.LENGTH_SHORT).show();

                    showDeleteDialog(mDataSet.get(position).getId());

                }
            });

            viewHolder.getLinearLayout_row().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(ManageProductsActivity.this, ProductItemDetailsActivity.class);

                    i.putExtra("name", mDataSet.get(position).getName());
                    i.putExtra("rate", mDataSet.get(position).getRate());

                    startActivity(i);

                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final LinearLayout linearLayout_row;
            private final TextView textView_index, textView_name, textView_rate;
            private final ImageView imageView_remove;

            ViewHolder(View v) {
                super(v);

                linearLayout_row = v.findViewById(R.id.row_list_products);
                textView_index = (TextView) v.findViewById(R.id.row_productList_tv_index);
                textView_name = (TextView) v.findViewById(R.id.row_productList_tv_name);
                textView_rate = (TextView) v.findViewById(R.id.row_productList_tv_rate);
                imageView_remove = (ImageView) v.findViewById(R.id.row_productList_iv_remove);

            }

            public LinearLayout getLinearLayout_row() {
                return linearLayout_row;
            }

            TextView getTextView_name() {
                return textView_name;
            }

            TextView getTextView_index() {
                return textView_index;
            }

            TextView getTextView_rate() {
                return textView_rate;
            }

            public ImageView getImageView_remove() {
                return imageView_remove;
            }
        }

    }

}
