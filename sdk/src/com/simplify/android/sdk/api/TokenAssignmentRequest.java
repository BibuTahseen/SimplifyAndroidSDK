package com.simplify.android.sdk.api;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: paul.hawke
 * Date: 6/13/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenAssignmentRequest extends AsyncTask<Card, Void, CardToken> {
    public static final String API_KEY = "sbpb_OTY1YmI4N2UtYTJiOS00ZWUzLTliMGItZTFmYzQ2OTRmYmQ3";
    public static final String URL_BASE = "https://sandbox.simplify.com/v1/api";
    private TokenAssignmentListener listener;
    private int statusCode;
    private String message;
    private final Gson gson;

    public TokenAssignmentRequest(TokenAssignmentListener listener) {
        this.listener = listener;
        this.gson = new Gson();
    }

    @Override
    protected CardToken doInBackground(Card... params) {
        CardToken token = null;
        try {
            String urlStr = new UrlBuilder(URL_BASE)
                    .addPath("/payment/cardToken")
                    .addParam("key", API_KEY)
                    .addParam("card.number", params[0].getNumber())
                    .addParam("card.cvc", params[0].getCvc())
                    .addParam("card.expMonth", "" + params[0].getExpMonth())
                    .addParam("card.expYear", "" + params[0].getExpYear())
                    .addOptionalParam("card.addressCity", "" + params[0].getAddressCity())
                    .addOptionalParam("card.addressCountry", "" + params[0].getAddressCountry())
                    .addOptionalParam("card.addressLine1", "" + params[0].getAddressLine1())
                    .addOptionalParam("card.addressLine2", "" + params[0].getAddressLine2())
                    .addOptionalParam("card.addressState", "" + params[0].getAddressState())
                    .addOptionalParam("card.addressZip", "" + params[0].getAddressCity())
                    .addOptionalParam("card.name", "" + params[0].getName())
                    .build();

            token = doGet(urlStr);
        } catch (HttpResponseException e) {
            statusCode = e.getStatusCode();
            message = e.getMessage();
        }
        return token;
    }

    private CardToken doGet(String urlStr) throws HttpResponseException {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(urlStr);
            String responseBody = httpclient.execute(httpget, new BasicResponseHandler());
            return gson.fromJson(responseBody, CardToken.class);
        } catch (IOException e) {
            return null;
        } catch (JsonSyntaxException e) {
            return null;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    @Override
    protected void onPostExecute(CardToken cardToken) {
        if (cardToken != null) {
            listener.tokenAssigned(cardToken);
        } else {
            listener.handleError(statusCode, message);
        }
    }
}