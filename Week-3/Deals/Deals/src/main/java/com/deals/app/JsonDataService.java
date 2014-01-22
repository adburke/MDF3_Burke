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
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import semantics3.api.Products;

import org.json.JSONObject;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class JsonDataService extends IntentService {

    public static final String MESSENGER_KEY = "messenger";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public JsonDataService() {
        super("JsonDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("Intent", "JsonDataService started");

        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger) extras.get(MESSENGER_KEY);


        Products products = new Products(
                "SEM3F98291C7F783DF843B12AA522D7D544C",
                "ZGY1MTRmMDlhYTU1NWE5ZjYyNDVlNzcwMTMxOTY1MGU"
        );

        /* Build the query */
        products
                .productsField("brand", "Apple");

        /* Make the query */
        JSONObject results = null;
        try {
            results = products.getProducts();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* View the results of the query */
        if (results != null) {
            Log.i("Results: ", results.toString());

            // Write to file now

            Message messege = Message.obtain();
            if (messege != null) {
                messege.arg1 = Activity.RESULT_OK;
                messege.obj = results.toString();
            } else {
                messege.arg1 = Activity.RESULT_CANCELED;
                messege.obj = null;
            }
            try {
                messenger.send(messege);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

}
