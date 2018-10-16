package com.codefuelindia.dnote.Common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.codefuelindia.dnote.Model.User;
import com.codefuelindia.dnote.R;


import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<User> {

    Context context;
    LayoutInflater inflater;
    int resource;
    private ArrayList<User> list;
    Filter nameFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                ArrayList<User> suggestions = new ArrayList<>();

                for (User employee : list) {
                    if (employee.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || employee.getMobile().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(employee);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<User>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    public AutoCompleteAdapter(Context context, int resource, ArrayList<User> list) {
        super(context, resource);
        this.context = context;
        this.resource = resource;

        this.list = list;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = inflater.inflate(resource, null);
        }

        User model = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.tvName);
        TextView tvId = view.findViewById(R.id.tvMobile);

        textView.setText(model.getName());
        tvId.setText(model.getMobile());

        view.setTag(model);
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}