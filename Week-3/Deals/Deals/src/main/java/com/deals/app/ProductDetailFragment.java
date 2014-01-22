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
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class ProductDetailFragment extends Fragment {

    private ProductDetailListener listener;
    private ProductListDetail viewer = null;

    public interface ProductDetailListener {
        public void onWebLaunchClick(HashMap<String, String> productInfo);
    }

    Bundle savedInstanceState;

    // Holds Cursor data from content provider
    HashMap<String, String> productInfo;

    // Layout variables and array containers for each column
    TextView productName;

    TextView row2;
    TextView row3;
    TextView row4;
    TextView row5;
    TextView row6;
    TextView row7;

    TextView vendor;
    TextView price;
    TextView color;
    TextView mpn;
    TextView upc;
    TextView mfr;

    Button webLaunchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.product_detail, container, false);

        Resources res = getResources();
        String[] detailStrings = res.getStringArray(R.array.detail_categories_array);

        // URI for ContentProvider
        Uri productUri = null;

        // Initialize layout variables and array containers for each column
        productName = (TextView) view.findViewById(R.id.productDetailName);

        row2 = (TextView) view.findViewById(R.id.rowVendor);
        row3 = (TextView) view.findViewById(R.id.rowPrice);
        row4 = (TextView) view.findViewById(R.id.rowColor);
        row5 = (TextView) view.findViewById(R.id.rowMpn);
        row6 = (TextView) view.findViewById(R.id.rowUpc);
        row7 = (TextView) view.findViewById(R.id.rowMfr);

        vendor = (TextView) view.findViewById(R.id.productDetailVendor);
        price = (TextView) view.findViewById(R.id.productDetailPrice);
        color = (TextView) view.findViewById(R.id.productDetailColor);
        mpn = (TextView) view.findViewById(R.id.productDetailMpn);
        upc = (TextView) view.findViewById(R.id.productDetailUpc);
        mfr = (TextView) view.findViewById(R.id.productDetailMfr);

        row2.setText(detailStrings[0]);
        row3.setText(detailStrings[1]);
        row4.setText(detailStrings[2]);
        row5.setText(detailStrings[3]);
        row6.setText(detailStrings[4]);
        row7.setText(detailStrings[5]);

        webLaunchBtn = (Button) view.findViewById(R.id.webLaunchBtn);

        // Set button text
        webLaunchBtn.setText(R.string.webBtn);

        webLaunchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //Start Activity to view the selected file
                if (productInfo != null) {
                    listener.onWebLaunchClick(productInfo);
                }

            }
        });

        return view;
    }



    public void updateProductDetails(Uri productUri) {
        // Check for valid cursor or saved data
        if (productUri != null && savedInstanceState == null) {
            // Call the ContentProvider with the relevant URI
            Cursor cursor = getActivity().getContentResolver().query(productUri, null, null, null, null);

            // Check if the cursor returned values
            if (cursor == null) {
                Log.i("ProductListDetail", "NULL CURSOR AT: " + productUri.toString());
            }
            // Clear the product list of values if already created
            if (productInfo != null) {
                productInfo.clear();
            }

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    for (int i = 0, j = cursor.getCount(); i < j; i++) {
                        productInfo = new HashMap<String, String>();

                        productInfo.put("productName", cursor.getString(1));
                        productInfo.put("vendor", cursor.getString(2));
                        productInfo.put("productPrice", cursor.getString(3));
                        productInfo.put("productColor", cursor.getString(4));
                        productInfo.put("productMpn", cursor.getString(5));
                        productInfo.put("productUpc", cursor.getString(6));
                        productInfo.put("productManufacturer", cursor.getString(7));
                        productInfo.put("productUrl", cursor.getString(8));

                    }
                }

            }
            if (productInfo != null) {
                Log.i("PRODUCT DETAIL", "VALUES: " + productInfo.values().toString());

                productName.setText(productInfo.get("productName"));
                vendor.setText(productInfo.get("vendor"));
                price.setText(productInfo.get("productPrice"));
                color.setText(productInfo.get("productColor"));
                mpn.setText(productInfo.get("productMpn"));
                upc.setText(productInfo.get("productUpc"));
                mfr.setText(productInfo.get("productManufacturer"));

            }

        } else {
            Log.i("PRODUCT_LIST_DETAIL", "Using Saved Data");
            productInfo = (HashMap<String, String>) savedInstanceState.getSerializable("savedList");

            productName.setText(productInfo.get("productName"));
            vendor.setText(productInfo.get("vendor"));
            price.setText(productInfo.get("productPrice"));
            color.setText(productInfo.get("productColor"));
            mpn.setText(productInfo.get("productMpn"));
            upc.setText(productInfo.get("productUpc"));
            mfr.setText(productInfo.get("productManufacturer"));
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (ProductDetailListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " need to implement ProductDetailListener");
        }

    }

}
