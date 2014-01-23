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
import android.app.ActionBar;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements BrowserFragment.BrowserListener, ProductDetailFragment.ProductDetailListener {
    static Context mContext;

    // File writing variables
    FileManager mFile;
    static String mJsonFile = "json_data.txt";

    // saved instance status
    Bundle savedInstanceState;

    ProductDetailFragment viewer;
    public String productUriString;

    // Can be used to tell if the file has already been created on the device
    public static Boolean writeStatus;

    // Query All Button
    public static Button queryAllBtn;

    public static Spinner selectionSpinner;
    public static int spinnerIndex;

    // List View
    public static ListView resultsList;

    // ArrayList of hashmaps for listview
    public ArrayList<HashMap<String, String>> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browserfrag);

        // Initialize context
        mContext = this;

        writeStatus = false;

        // Saved instance string for ProductDetail frag
        productUriString = null;

        selectionSpinner = (Spinner)findViewById(R.id.filterSpinner);
        queryAllBtn = (Button)findViewById(R.id.showAllBtn);
        resultsList = (ListView)findViewById(R.id.resultsList);

        Boolean status = ConnectionStatus.getNetworkStatus(mContext);

        // Get ProductDetail Fragment
        viewer = (ProductDetailFragment) getFragmentManager().findFragmentById(R.id.productdetail_fragment);

        // Check for spinner position so it does not fire when screen rotation occurs
        if (savedInstanceState != null) {
            spinnerIndex = savedInstanceState.getInt("spinnerIndex");
            productUriString = savedInstanceState.getString("productUriString");
        } else {
            spinnerIndex = 0;
        }

        // Use saved instance data if available
        if (savedInstanceState != null) {
            Log.i("API BROWSER", "Using Saved Instance");

            writeStatus = (savedInstanceState.getBoolean("status"));
            productList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("savedList");

            if (productList != null) {
                Log.i("SAVED INSTANCE", "Product list recreation");
                ProductListAdapter adapter = new ProductListAdapter(mContext, productList, R.layout.list_row,
                        new String[]{"productName", "vendor", "productPrice"}, new int[]{R.id.productName, R.id.vendor, R.id.productPrice});
                // Add the adapter to the ListView
                resultsList.setAdapter(adapter);
                selectionSpinner.setEnabled(true);
                queryAllBtn.setEnabled(true);
                if (viewer.isInLayout() && productUriString != null) {
                    Uri savedDetailUri;
                    if (savedInstanceState.getString("productUriString") != null) {
                        savedDetailUri = Uri.parse(savedInstanceState.getString("productUriString"));
                        viewer.updateProductDetails(savedDetailUri);
                    }
                }

            }
        }

        Handler jsonServiceHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                //TODO: Handle different types of messages
                String results = null;
                Uri initialUri = null;

                if (msg.arg1 == RESULT_OK && msg.obj != null) {
                    Log.i("JSON HANDLER", "Service Completed Successfully");
                    Log.i("JSON HANDLER", (String) msg.obj);
                    // Obtain json string from service
                    results = (String) msg.obj;

                    // Instantiate FileManager singleton
                    mFile = FileManager.getMinstance();
                    writeStatus = mFile.writeFile(mContext, mJsonFile, results);

                    if (writeStatus) {
                        initialUri = CollectionProvider.JsonData.CONTENT_URI;
                        onListUpdate(initialUri);

                    }

                }

            }
        };

        // Check for file and network status
        if (!writeStatus && status) {

            Messenger jsonServiceMessenger = new Messenger(jsonServiceHandler);
            Intent startJsonDataIntent = new Intent(this, JsonDataService.class);
            startJsonDataIntent.putExtra(JsonDataService.MESSENGER_KEY, jsonServiceMessenger);
            startService(startJsonDataIntent);
        } else if (writeStatus && savedInstanceState == null) {
            onListUpdate(CollectionProvider.JsonData.CONTENT_URI);
        } else if (!status) {
            Toast.makeText(this, "No network connection found and no local data to display.", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle result = data.getExtras();
            if (result != null) {
                Log.i("RETURN ACTIVITY", result.getString("FINISHED"));
            }
        }
    }

    // Update list with api data
    public void onListUpdate(Uri uri) {

        productList = new ArrayList<HashMap<String, String>>();

        // Get cursor and URI
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        // Check if the cursor returned values
        if (cursor == null) {
            Toast.makeText(this, "Cursor value is null", Toast.LENGTH_LONG).show();
            Log.i("onListUpdate", "NULL CURSOR AT: " + uri.toString());
        }
        // Clear the product list of values if already created
        if (productList != null) {
            productList.clear();
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                for (int i = 0, j = cursor.getCount(); i < j; i++) {
                    HashMap<String, String> listMap = new HashMap<String, String>();

                    listMap.put("productName", cursor.getString(1));
                    listMap.put("vendor", cursor.getString(2));
                    listMap.put("productPrice", cursor.getString(3));

                    cursor.moveToNext();

                    productList.add(listMap);

                    //
                    AppWidgetManager awm = AppWidgetManager.getInstance(mContext);
                    awm.notifyAppWidgetViewDataChanged(awm.getAppWidgetIds(new ComponentName(mContext,
                            WidgetProvider.class)), R.id.stack_view);

                }
            }
        }
        // Create the adapter from the ArrayList of HashMaps and map to the list_row xml layout
        ProductListAdapter adapter = new ProductListAdapter(this, productList, R.layout.list_row,
                new String[]{"productName", "vendor", "productPrice"}, new int[]{R.id.productName, R.id.vendor, R.id.productPrice});
        // Add the adapter to the ListView
        resultsList.setAdapter(adapter);
        selectionSpinner.setEnabled(true);
        queryAllBtn.setEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.api_browser);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("status", writeStatus);
        savedInstanceState.putInt("spinnerIndex", selectionSpinner.getSelectedItemPosition());

        if (productUriString != null) {
            savedInstanceState.putString("productUriString", productUriString);
        }


        if(productList != null && !productList.isEmpty()) {
            savedInstanceState.putSerializable("savedList", (Serializable) productList);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onFilterSelection(int position,String selection) {
        if (position != 0 && spinnerIndex != selectionSpinner.getSelectedItemPosition()) {

            Log.i("SPINNER SELECTION", selectionSpinner.getItemAtPosition(position).toString());

            Uri uriFilter = null;
            // Create uri to pass to onListUpdate based on the selection
            uriFilter = Uri.parse("content://" + CollectionProvider.AUTHORITY + "/items/type/" + selection);
            onListUpdate(uriFilter);

            if (savedInstanceState != null) {
                spinnerIndex = selectionSpinner.getSelectedItemPosition();
            }
        }
    }

    @Override
    public void onProductSelection(int index, String filterString, int filterIndex) {
        Log.i("onProductSelection", "Fired onProductSelection");
        if (viewer == null || !viewer.isInLayout()) {
            // Create intent for new activity
            Intent productDetailActivity = new Intent(mContext, ProductListDetail.class);
            // Attach index data of selected product
            productDetailActivity.putExtra("index", index);
            productDetailActivity.putExtra("filterString", filterString);
            productDetailActivity.putExtra("filterIndex", filterIndex);

            startActivityForResult(productDetailActivity, 0);
        } else {
            Uri productUri;
            // Create URI for product call to capture data
            if (filterIndex == 0) {
                productUri = Uri.parse("content://" + CollectionProvider.AUTHORITY + "/items/" + index);
                productUriString = "content://" + CollectionProvider.AUTHORITY + "/items/" + index;
            } else {
                productUri = Uri.parse("content://" + CollectionProvider.AUTHORITY + "/items/type/" + filterString + "/" + index);
                productUriString = "content://" + CollectionProvider.AUTHORITY + "/items/type/" + filterString + "/" + index;
            }
            Log.i("onProductSelection", "Fired view.updateProductFragment");
            viewer.updateProductDetails(productUri);

        }
    }

    @Override
    public void onQueryAll() {
        onListUpdate(CollectionProvider.JsonData.CONTENT_URI);
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
