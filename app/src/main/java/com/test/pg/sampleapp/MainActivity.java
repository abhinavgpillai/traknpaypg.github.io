package com.test.pg.sampleapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    ProgressBar pb;
    private String paymentParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);

        Button clickButton = (Button) findViewById(R.id.ee);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);

                //Since Order ID should be unique, we will use a random integer as order ID during every payment.
                Random rnd = new Random();
                int n = 100000 + rnd.nextInt(900000);
                SampleAppConstants.PG_ORDER_ID=Integer.toString(n);



                //Make query string parameters from the user input.
                StringBuffer postParamsBuffer = new StringBuffer();
                postParamsBuffer.append(concatParams(TraknpayConstants.API_KEY, SampleAppConstants.PG_API_KEY));
                postParamsBuffer.append(concatParams(TraknpayConstants.AMOUNT, SampleAppConstants.PG_AMOUNT));
                postParamsBuffer.append(concatParams(TraknpayConstants.EMAIL, SampleAppConstants.PG_EMAIL));
                postParamsBuffer.append(concatParams(TraknpayConstants.NAME, SampleAppConstants.PG_NAME));
                postParamsBuffer.append(concatParams(TraknpayConstants.PHONE, SampleAppConstants.PG_PHONE));
                postParamsBuffer.append(concatParams(TraknpayConstants.ORDER_ID, SampleAppConstants.PG_ORDER_ID));
                postParamsBuffer.append(concatParams(TraknpayConstants.CURRENCY, SampleAppConstants.PG_CURRENCY));
                postParamsBuffer.append(concatParams(TraknpayConstants.DESCRIPTION, SampleAppConstants.PG_DESCRIPTION));
                postParamsBuffer.append(concatParams(TraknpayConstants.CITY, SampleAppConstants.PG_CITY));
                postParamsBuffer.append(concatParams(TraknpayConstants.STATE, SampleAppConstants.PG_STATE));
                postParamsBuffer.append(concatParams(TraknpayConstants.ADDRESS_LINE1, SampleAppConstants.PG_ADD_1));
                postParamsBuffer.append(concatParams(TraknpayConstants.ADDRESS_LINE2, SampleAppConstants.PG_ADD_2));
                postParamsBuffer.append(concatParams(TraknpayConstants.ZIPCODE, SampleAppConstants.PG_ZIPCODE));
                postParamsBuffer.append(concatParams(TraknpayConstants.COUNTRY, SampleAppConstants.PG_COUNTRY));
                postParamsBuffer.append(concatParams(TraknpayConstants.RETURN_URL, SampleAppConstants.PG_RETURN_URL));
                postParamsBuffer.append(concatParams(TraknpayConstants.MODE, SampleAppConstants.PG_MODE));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF1, SampleAppConstants.PG_UDF1));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF2, SampleAppConstants.PG_UDF2));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF3, SampleAppConstants.PG_UDF3));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF4, SampleAppConstants.PG_UDF4));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF5, SampleAppConstants.PG_UDF5));

                String hashParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

                //Now use the query string obtained above to POST the payment details to your WebServer's Hash API.
                GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
                getHashesFromServerTask.execute(hashParams);

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        pb.setVisibility(View.GONE);
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            JSONObject response=null;
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL(SampleAppConstants.PG_HASH_URL);

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                response = new JSONObject(responseStringBuffer.toString());
                if(response.has("status")){
                    if(response.getInt("status")==0){
                        merchantHash=response.getString("hash");
                    }
                }

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    System.out.println(key+" : "+response.getString(key));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);
            pb.setVisibility(View.GONE);
            if(merchantHash.isEmpty() || merchantHash.equals("")){
                Toast.makeText(MainActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(MainActivity.this, "generated hash: "+merchantHash, Toast.LENGTH_SHORT).show();
                System.out.println("Calculated Hash : "+merchantHash);

                //Now send the Hash to the Payment Activity.
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("calculatedHash", merchantHash);
                startActivity(intent);

            }

        }

    }

}
