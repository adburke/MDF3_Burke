/*
 * Project:		Deals
 *
 * Package:		Deals-Deals
 *
 * Author:		aaronburke
 *
 * Purpose:     Displays a detailed view of a product selected from the MainActivity
 *
 * Date:		 	1 21, 2014
 */

package com.deals.app;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;


public class ProductListDetail extends Activity implements ProductDetailFragment.ProductDetailListener {

    // Passed in values from Product List selection
    int productIndex;
    int filterIndex;
    String filterString;

    Uri productUri;
    String productUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetailfrag);

        ActionBar actionBar = getActionBar();
        // Creates back action on icon
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ProductDetailFragment fragment = (ProductDetailFragment) getFragmentManager().findFragmentById(R.id.productdetail_fragment);

        // Capture incoming data
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            if (incomingData.getBoolean("widget")) {
                int appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                productIndex = getIntent().getIntExtra(WidgetProvider.EXTRA_ITEM, 0);
                Log.i("ProductDetail", "Launched from widget! INDEX= " + productIndex);
                filterIndex = incomingData.getInt("filterIndex");
                Log.i("filterIndex", String.valueOf(filterIndex));
                filterString = incomingData.getString("filterString");
            } else {
                productIndex = incomingData.getInt("index");
                Log.i("productIndex", String.valueOf(productIndex));
                filterIndex = incomingData.getInt("filterIndex");
                Log.i("filterIndex", String.valueOf(filterIndex));
                filterString = incomingData.getString("filterString");
                Log.i("filterString", filterString);

            }
            // Create URI for product call to capture data
            if (filterIndex == 0) {
                productUri = Uri.parse("content://" + CollectionProvider.AUTHORITY + "/items/" + productIndex);
                productUriString = "content://" + CollectionProvider.AUTHORITY + "/items/" + productIndex;
            } else {
                productUri = Uri.parse("content://" + CollectionProvider.AUTHORITY + "/items/type/" + filterString + "/" + productIndex);
                productUriString = "content://" + CollectionProvider.AUTHORITY + "/items/type/" + filterString + "/" + productIndex;
            }

        }

        // Check for valid cursor or saved data
        if (productUri != null && savedInstanceState == null) {
            // Call the ContentProvider with the relevant URI
            Cursor cursor = getContentResolver().query(productUri, null, null, null, null);

            // Check if the cursor returned values
            if (cursor == null) {
                Log.i("ProductListDetail", "NULL CURSOR AT: " + productUri.toString());
            }

            if (fragment != null) {
                fragment.updateProductDetails(productUri);
            }
        } else if (savedInstanceState != null && !incomingData.getBoolean("widget")) {
            productUriString = savedInstanceState.getString("productUri");
            productUri = Uri.parse(productUriString);
            if (fragment != null) {
                fragment.updateProductDetails(productUri);
            }
        } else if (incomingData.getBoolean("widget")) {
            productUri = Uri.parse(productUriString);
            if (fragment != null) {
                fragment.updateProductDetails(productUri);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {

            Intent intent = new Intent(this, AppInfo.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Method to pass data back to API BROWSER page
    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("FINISHED", "PASS VALUE TEST");
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.product_detail);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        if(productUri != null) {
            savedInstanceState.putString("productUri", productUriString);
        }
        Log.i("ProductListDetail", "onSaveI fired");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onWebLaunchClick(HashMap<String, String> productInfo) {
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW);
        Uri webUri = Uri.parse(productInfo.get("productUrl"));
        intent.setData(webUri);
        startActivity(intent);
    }


}
