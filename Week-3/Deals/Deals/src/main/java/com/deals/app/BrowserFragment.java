/*
 * Project:		Deals
 *
 * Package:		Deals-Deals
 *
 * Author:		aaronburke
 *
 * Date:		 	1 21, 2014
 */

package com.deals.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;


public class BrowserFragment extends Fragment {

    private BrowserListener listener;

    public interface BrowserListener {
        public void onFilterSelection(int position, String selection);
        public void onProductSelection(int index, String filterString, int filterIndex);
        public void onQueryAll();

    }

    // Spinner variables
    public static String[] mListItems;
    public static Spinner selectionSpinner;

    // Query All Button
    public static Button queryAllBtn;


    // List View
    public static ListView resultsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.api_browser, container, false);

        mListItems = getResources().getStringArray(R.array.selection_array);

        // Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mListItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectionSpinner = (Spinner) view.findViewById(R.id.filterSpinner);
        selectionSpinner.setAdapter(spinnerAdapter);
        selectionSpinner.setEnabled(false);
        selectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onFilterSelection(position,parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Button
        queryAllBtn = (Button) view.findViewById(R.id.showAllBtn);
        queryAllBtn.setEnabled(false);
        queryAllBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                listener.onQueryAll();
                selectionSpinner.setSelection(0);
            }
        });

        // ListView
        resultsList = (ListView) view.findViewById(R.id.resultsList);
        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(mContext, "Click ListItem Number " + position, Toast.LENGTH_LONG).show();

                listener.onProductSelection(position, selectionSpinner.getSelectedItem().toString(), selectionSpinner.getSelectedItemPosition() );

            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (BrowserListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " need to implement BrowserListener");
        }

    }


}
