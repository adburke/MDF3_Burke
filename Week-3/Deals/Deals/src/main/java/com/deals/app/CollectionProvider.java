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


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CollectionProvider extends ContentProvider {

    public static final String AUTHORITY = "com.deals.app.CollectionProvider";

    public static class JsonData implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.deals.app.item";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.deals.app.item";

        public static final String NAME_COLUMN = "productName";
        public static final String VENDOR_COLUMN = "vendor";
        public static final String PRICE_COLUMN = "productPrice";
        public static final String COLOR_COLUMN = "productColor";
        public static final String MPN_COLUMN = "productMpn";
        public static final String UPC_COLUMN = "productUpc";
        public static final String MANUFACTURER_COLUMN = "productManufacturer";
        public static final String URL_COLUMN = "productUrl";

        public static final String[] LIST_PROJECTION = {"_Id", NAME_COLUMN, VENDOR_COLUMN, PRICE_COLUMN};
        public static final String[] DETAIL_PROJECTION = {"_Id", NAME_COLUMN, VENDOR_COLUMN, PRICE_COLUMN,
                COLOR_COLUMN, MPN_COLUMN, UPC_COLUMN, MANUFACTURER_COLUMN, URL_COLUMN};

        private JsonData() {};
    }

    public static final int ITEMS = 1;
    public static final int ITEMS_ID = 2;
    public static final int ITEMS_TYPE = 3;
    public static final int ITEMS_TYPE_ID = 4;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "items/", ITEMS);
        uriMatcher.addURI(AUTHORITY, "items/#", ITEMS_ID);
        uriMatcher.addURI(AUTHORITY, "items/type/*", ITEMS_TYPE);
        uriMatcher.addURI(AUTHORITY, "items/type/*/#", ITEMS_TYPE_ID);
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     * Implement this to handle query requests from clients.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        MatrixCursor listResult = new MatrixCursor(JsonData.LIST_PROJECTION);
        MatrixCursor detailResult = new MatrixCursor(JsonData.DETAIL_PROJECTION);

        String JSONString = FileManager.readFile(getContext(), MainActivity.mJsonFile);
        JSONObject query = null;
        JSONArray resultsArray = null;
        JSONArray siteDetails = null;
        JSONArray latestOffers = null;

        try {
            query = new JSONObject(JSONString);
            resultsArray = query.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (resultsArray == null) {
            return null;
        }

        switch (uriMatcher.match(uri)) {
            case ITEMS:
                Log.i("ITEMS URI", "Executed");
                for (int i = 0, j = resultsArray.length(); i < j; i++) {
                    try {
                        siteDetails = resultsArray.getJSONObject(i).getJSONArray("sitedetails");
                        latestOffers = siteDetails.getJSONObject(0).getJSONArray("latestoffers");

                        listResult.addRow(new Object[] {i+1,resultsArray.getJSONObject(i).get("name"),
                            latestOffers.getJSONObject(0).get("seller"), latestOffers.getJSONObject(0).get("price") });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return listResult;

            case ITEMS_ID:
                Log.i("ITEMS_ID URI", "Executed");
                // Get the last segment value from the uri in this case the string number
                String itemId = uri.getLastPathSegment();

                // Create an integer out of the string and check for correct int format
                int indexVal;
                try {
                    indexVal = Integer.parseInt(itemId);
                } catch (NumberFormatException e) {
                    Log.e("ITEMS_ID URI", "Incorrect format");
                    break;
                }

                // Check if the item id is with valid range of results
                if (indexVal < 0 || indexVal > resultsArray.length()) {
                    Log.e("ITEMS_ID URI", "ID number not within valid range");
                    break;
                }

                try {

                    siteDetails = resultsArray.getJSONObject(indexVal).getJSONArray("sitedetails");
                    latestOffers = siteDetails.getJSONObject(0).getJSONArray("latestoffers");

                    detailResult.addRow(new Object[]{indexVal,
                            resultsArray.getJSONObject(indexVal).get("name"),
                            latestOffers.getJSONObject(0).get("seller"),
                            latestOffers.getJSONObject(0).get("price"),
                            resultsArray.getJSONObject(indexVal).get("color"),
                            resultsArray.getJSONObject(indexVal).get("mpn"),
                            resultsArray.getJSONObject(indexVal).get("upc"),
                            resultsArray.getJSONObject(indexVal).get("manufacturer"),
                            siteDetails.getJSONObject(0).get("url")});

                    //Log.i("DETAIL RESULTS LOG", "name: " + resultsArray.getJSONObject(indexVal).get("name"));
                    //Log.i("DETAIL RESULTS LOG", "seller: " + latestOffers.getJSONObject(0).get("seller"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return detailResult;

            case ITEMS_TYPE:
                Log.i("ITEMS_TYPE URI", "Executed");
                // Get the last segment value from the uri in this case the string number
                String itemType = uri.getLastPathSegment();

                Log.i("ITEM_TYPE URI", "Type= " + itemType);
                // Filter by item type
                for (int i = 0, j = resultsArray.length(); i < j; i++) {

                    try {
                        //Log.i("ITEMS_TYPE CATS", resultsArray.getJSONObject(i).get("category").toString());
                        if (resultsArray.getJSONObject(i).get("category").equals(itemType)) {

                            siteDetails = resultsArray.getJSONObject(i).getJSONArray("sitedetails");
                            latestOffers = siteDetails.getJSONObject(0).getJSONArray("latestoffers");

                            listResult.addRow(new Object[]{i + 1, resultsArray.getJSONObject(i).get("name"),
                                    latestOffers.getJSONObject(0).get("seller"), latestOffers.getJSONObject(0).get("price")});
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return listResult;

            case ITEMS_TYPE_ID:
                Log.i("ITEMS_TYPE URI", "Executed");

                // Get all segment values
                List<String> uriSegments = uri.getPathSegments();
                Log.i("URI SEGMENTS", uriSegments.toString());

                // Get segment values
                String indexStr = uriSegments.get(3);
                int index = Integer.parseInt(indexStr);
                String filterString = uriSegments.get(2);

                // Create new filtered results array
                JSONArray filteredArray = new JSONArray();

                // Run through file results to pull out the correct filtered objects and place in filteredArray
                for (int i = 0, j = resultsArray.length(); i < j; i++) {
                    try {
                        //Log.i("ITEMS_TYPE CATS", resultsArray.getJSONObject(i).get("category").toString());
                        if (resultsArray.getJSONObject(i).get("category").equals(filterString)) {
                            filteredArray.put(resultsArray.getJSONObject(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                // Pass the correct product data from the filtered results array list
                try {
                    siteDetails = filteredArray.getJSONObject(index).getJSONArray("sitedetails");
                    latestOffers = siteDetails.getJSONObject(0).getJSONArray("latestoffers");

                    detailResult.addRow(new Object[]{index,
                            filteredArray.getJSONObject(index).get("name"),
                            latestOffers.getJSONObject(0).get("seller"),
                            latestOffers.getJSONObject(0).get("price"),
                            filteredArray.getJSONObject(index).get("color"),
                            filteredArray.getJSONObject(index).get("mpn"),
                            filteredArray.getJSONObject(index).get("upc"),
                            filteredArray.getJSONObject(index).get("manufacturer"),
                            siteDetails.getJSONObject(0).get("url")});

                    //Log.i("DETAIL RESULTS LOG", "name: " + resultsArray.getJSONObject(indexVal).get("name"));
                    //Log.i("DETAIL RESULTS LOG", "seller: " + latestOffers.getJSONObject(0).get("seller"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return detailResult;

        }

        return null;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p/>
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Override
    public String getType(Uri uri) {

//        switch (uriMatcher.match(uri)) {
//            case ITEMS:
//                return JsonData.CONTENT_TYPE;
//            case ITEMS_ID:
//                return JsonData.CONTENT_ITEM_TYPE;
//            case ITEMS_TYPE:
//                return JsonData.CONTENT_ITEM_TYPE;
//        }

        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
