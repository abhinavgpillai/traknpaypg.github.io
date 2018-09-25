package com.test.pg.sampleapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;

public class PaymentActivity extends AppCompatActivity {
    ProgressBar pb;
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        webview = findViewById(R.id.webview);
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        try{
            StringBuffer urlWithParams=new StringBuffer("api_key="+URLDecoder.decode(SampleAppConstants.PG_API_KEY, "UTF-8"));
            urlWithParams.append("&amount="+URLDecoder.decode("2.00", "UTF-8"));
            urlWithParams.append("&email="+URLDecoder.decode("test@gmail.com", "UTF-8"));
            urlWithParams.append("&name="+URLDecoder.decode("Test Name", "UTF-8"));
            urlWithParams.append("&phone="+URLDecoder.decode("9876543210", "UTF-8"));
            urlWithParams.append("&order_id="+URLDecoder.decode("12", "UTF-8"));
            urlWithParams.append("&currency="+URLDecoder.decode(SampleAppConstants.PG_CURRENCY, "UTF-8"));
            urlWithParams.append("&description="+URLDecoder.decode("test", "UTF-8"));
            urlWithParams.append("&city="+URLDecoder.decode("city", "UTF-8"));
            urlWithParams.append("&state="+URLDecoder.decode("state", "UTF-8"));
            urlWithParams.append("&address_line_1="+URLDecoder.decode("addl1", "UTF-8"));
            urlWithParams.append("&address_line_2="+URLDecoder.decode("addl2", "UTF-8"));
            urlWithParams.append("&zip_code="+URLDecoder.decode("123456", "UTF-8"));
            urlWithParams.append("&country="+URLDecoder.decode(SampleAppConstants.PG_COUNTRY, "UTF-8"));
            urlWithParams.append("&return_url="+URLDecoder.decode(SampleAppConstants.PG_RETURN_URL, "UTF-8"));
            urlWithParams.append("&mode="+URLDecoder.decode(SampleAppConstants.PG_MODE, "UTF-8"));
            urlWithParams.append("&udf1="+URLDecoder.decode("udf1", "UTF-8"));
            urlWithParams.append("&udf2="+URLDecoder.decode("udf2", "UTF-8"));
            urlWithParams.append("&udf3="+URLDecoder.decode("udf3", "UTF-8"));
            urlWithParams.append("&udf4="+URLDecoder.decode("udf4", "UTF-8"));
            urlWithParams.append("&udf5="+URLDecoder.decode("udf5", "UTF-8"));
            urlWithParams.append("&hash="+URLDecoder.decode(SampleAppConstants.HASH, "UTF-8"));


            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    pb.setVisibility(View.GONE);

                    if(url.equals(SampleAppConstants.PG_RETURN_URL)){
                        view.setVisibility(View.GONE);
                        view.loadUrl("javascript:HtmlViewer.showHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                        long millis = System.currentTimeMillis();
                        long seconds = millis / 1000;
                        Log.i("PaymentActivity:", "onPageFinished : " + seconds);
                    }

                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    pb.setVisibility(View.VISIBLE);
                    long millis = System.currentTimeMillis();
                    long seconds = millis / 1000;
                    Log.i("PaymentActivity:", "onPageStarted : " + seconds);
                }

            });

            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
            webview.postUrl(SampleAppConstants.PG_HOSTNAME+"/v1/paymentrequest",urlWithParams.toString().getBytes());

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(getBaseContext(), exceptionAsString,Toast.LENGTH_SHORT).show();
        }


    }

    class MyJavaScriptInterface {

        private Context ctx;
        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {

            long millis = System.currentTimeMillis();
            long seconds = millis / 1000;
            Log.i("Payment Acitiviy:", "showHTML Started : " + seconds);

            Intent intent=new Intent(getBaseContext(), ResponseActivity.class);
            intent.putExtra("payment_response", Html.fromHtml(html).toString());
            startActivity(intent);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
