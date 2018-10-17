package com.codefuelindia.dnote.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefuelindia.dnote.R;
import com.codefuelindia.dnote.constants.MyConstants;
import com.codefuelindia.dnote.view.CreditFormActivity;
import com.codefuelindia.dnote.view.DebitFormActivity;
import com.codefuelindia.dnote.view.ManageCustomersActivity;
import com.codefuelindia.dnote.view.ManageProductsActivity;


public class DashboardFragment extends Fragment {

    View view_main;
    CardView cardView_debit, cardView_credit, cardView_customers, cardView_products;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.context = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view_main = inflater.inflate(R.layout.fragment_dashboard, container, false);

        cardView_debit = view_main.findViewById(R.id.dash_card_debit);
        cardView_credit = view_main.findViewById(R.id.dash_card_credit);
        cardView_customers = view_main.findViewById(R.id.dash_card_manageCustomers);
        cardView_products = view_main.findViewById(R.id.dash_card_manageProducts);


        cardView_debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodDebit();
            }
        });

        cardView_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodCredit();
            }
        });

        cardView_customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodCustomers();
            }
        });

        cardView_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodProducts();
            }
        });

        return view_main;
    }


    private void methodDebit() {

        if (MyConstants.Companion.checkInternetConnection(getActivity())) {
            Intent i = new Intent(context, DebitFormActivity.class);
            startActivity(i);
        } else {
            MyConstants.Companion.showToast(getActivity(), "Turn on Internet connection");
        }


    }

    private void methodCredit() {
        if (MyConstants.Companion.checkInternetConnection(getActivity())) {
            Intent i = new Intent(context, CreditFormActivity.class);
            startActivity(i);
        } else {
            MyConstants.Companion.showToast(getActivity(), "Turn on Internet connection");
        }
    }

    private void methodCustomers() {
        Intent i = new Intent(context, ManageCustomersActivity.class);
        startActivity(i);
    }

    private void methodProducts() {
        Intent i = new Intent(context, ManageProductsActivity.class);
        startActivity(i);
    }


}
