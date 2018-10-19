package com.codefuelindia.dnote.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.codefuelindia.dnote.Adapter.AdapterAdsVP;
import com.codefuelindia.dnote.Common.OnImagePressedListener;
import com.codefuelindia.dnote.R;
import com.codefuelindia.dnote.constants.MyConstants;
import com.codefuelindia.dnote.view.*;

import java.util.ArrayList;


public class DashboardFragment extends Fragment {

    private Context context;

    View view_main;
    CardView cardView_debit, cardView_credit, cardView_customers, cardView_products, cardView_reports;
    ViewPager viewPager_ads;


    private ArrayList<Integer> tipsArray = new ArrayList<Integer>();
    private Handler handler;
    private int eventAction = -1;
    private int currentPage = 0;


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
        cardView_reports = view_main.findViewById(R.id.dash_card_reports);
        viewPager_ads = view_main.findViewById(R.id.dash_viewPager_ads);


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

        cardView_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodReports();
            }
        });


        tipsArray.add(R.drawable.codefuel);
        tipsArray.add(R.drawable.codefuel);
        tipsArray.add(R.drawable.codefuel);

        init(view_main);


        return view_main;
    }

    private void methodReports() {
        Intent i = new Intent(context, ChooseReportTypeActivity.class);
        startActivity(i);
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


    private void init(View view) {

        // Auto start of viewpager
        handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == tipsArray.size()) {
                    currentPage = 0;
                }
                viewPager_ads.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 2000);
            }
        };

        handler.post(Update);


        viewPager_ads.setAdapter(new AdapterAdsVP(getActivity(), tipsArray, new OnImagePressedListener() {
            @Override
            public void onImagePressed(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    eventAction = event.getAction();
                    handler.removeCallbacksAndMessages(null);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    eventAction = event.getAction();
                    handler.post(Update);

                }

            }
        }));

        viewPager_ads.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (handler != null) {
            handler.removeCallbacks(null);
        }


    }

}
