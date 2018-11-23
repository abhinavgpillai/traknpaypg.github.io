package com.test.pg.sampleapp;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Abhinav G Pillai, Omniware Technologies Pvt Ltd
 */

public class TraknpaySdkInitializer {

    private static String mode = "TEST";

    public static String getMode() {
        return mode;
    }

    public static class PaymentParam {

        Builder builder = null;
        private HashMap<String, String> params = new LinkedHashMap<>();

        private String packageName = "";
        private String className = "";

        private String pipedHash = "";

        public String getReturnURL() {
            HashMap<String, String> params = getParams();
            return params.get(TraknpayConstants.RETURN_URL);
        }

        public void setReturnPackage(String packageName) {
            this.packageName = packageName;
        }

        public void setReturnClass(String className) {
            this.className = className;
        }

        public String getReturnActivityPackage() {
            return this.packageName;
        }

        public String getReturnActivityClass() {
            return this.className;
        }

        public String buildParamsForPayment() {
            HashMap<String, String> params = getParams();

            StringBuffer postParamsBuffer = new StringBuffer();
            postParamsBuffer.append(concatParams(TraknpayConstants.API_KEY, params.get(TraknpayConstants.API_KEY)));
            postParamsBuffer.append(concatParams(TraknpayConstants.AMOUNT, params.get(TraknpayConstants.AMOUNT)));
            postParamsBuffer.append(concatParams(TraknpayConstants.EMAIL, params.get(TraknpayConstants.EMAIL)));
            postParamsBuffer.append(concatParams(TraknpayConstants.NAME, params.get(TraknpayConstants.NAME)));
            postParamsBuffer.append(concatParams(TraknpayConstants.PHONE, params.get(TraknpayConstants.PHONE)));
            postParamsBuffer.append(concatParams(TraknpayConstants.ORDER_ID, params.get(TraknpayConstants.ORDER_ID)));
            postParamsBuffer.append(concatParams(TraknpayConstants.CURRENCY, params.get(TraknpayConstants.CURRENCY)));
            postParamsBuffer.append(concatParams(TraknpayConstants.DESCRIPTION, params.get(TraknpayConstants.DESCRIPTION)));
            postParamsBuffer.append(concatParams(TraknpayConstants.CITY, params.get(TraknpayConstants.CITY)));
            postParamsBuffer.append(concatParams(TraknpayConstants.STATE, params.get(TraknpayConstants.STATE)));
            postParamsBuffer.append(concatParams(TraknpayConstants.ADDRESS_LINE1, params.get(TraknpayConstants.ADDRESS_LINE1)));
            postParamsBuffer.append(concatParams(TraknpayConstants.ADDRESS_LINE2, params.get(TraknpayConstants.ADDRESS_LINE2)));
            postParamsBuffer.append(concatParams(TraknpayConstants.ZIPCODE, params.get(TraknpayConstants.ZIPCODE)));
            postParamsBuffer.append(concatParams(TraknpayConstants.COUNTRY, params.get(TraknpayConstants.COUNTRY)));
            postParamsBuffer.append(concatParams(TraknpayConstants.RETURN_URL, params.get(TraknpayConstants.RETURN_URL)));
            postParamsBuffer.append(concatParams(TraknpayConstants.MODE, params.get(TraknpayConstants.MODE)));
            postParamsBuffer.append(concatParams(TraknpayConstants.HASH, params.get(TraknpayConstants.HASH)));
            postParamsBuffer.append(concatParams(TraknpayConstants.UDF1, params.get(TraknpayConstants.UDF1)));
            postParamsBuffer.append(concatParams(TraknpayConstants.UDF2, params.get(TraknpayConstants.UDF2)));
            postParamsBuffer.append(concatParams(TraknpayConstants.UDF3, params.get(TraknpayConstants.UDF3)));
            postParamsBuffer.append(concatParams(TraknpayConstants.UDF4, params.get(TraknpayConstants.UDF4)));
            postParamsBuffer.append(concatParams(TraknpayConstants.UDF5, params.get(TraknpayConstants.UDF5)));

            String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

            return postParams;
        }

        protected String concatParams(String key, String value) {
            return key + "=" + value + "&";
        }

        public String getFormDataForPayment(){

            StringBuilder sb = new StringBuilder();
            HashMap<String, String> postData = getParams();

            try{
                String url="";
                if(postData.get(TraknpayConstants.MODE)=="TEST"){
                    url= URLDecoder.decode(TraknpayConstants.TEST_PAYMENT_URL, "UTF-8");
                }else{
                    url= URLDecoder.decode(TraknpayConstants.PAYMENT_URL, "UTF-8");
                }

                System.out.println(url);
                sb.append("<html>");
                sb.append("<body OnLoad=\"OnLoadEvent();\">");
                sb.append(String.format("<form name=\"form1\" action=\"%s\" method=\"%s\">", url, "post"));
                for (Map.Entry<String, String> item : postData.entrySet()) {

                    if(item.getKey().equals("return_url")){
                        sb.append(String.format("<input name=\"%s\" type=\"hidden\" value=\"%s\" />", item.getKey(), URLDecoder.decode(item.getValue(), "UTF-8")));
                    }else{
                        sb.append(String.format("<input name=\"%s\" type=\"hidden\" value=\"%s\" />", item.getKey(), item.getValue()));
                    }
                }
                sb.append("</form><script language=\"JavaScript\"> function OnLoadEvent() { document.form1.submit(); }</script></body></html>");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }

            return sb.toString();
        }

        private PaymentParam(Builder builder) {

            this.builder=builder;
            if (TextUtils.isEmpty(builder.api_key))
                throw new RuntimeException("Merchant API Key missing");
            else
                params.put(TraknpayConstants.API_KEY, builder.api_key);

            if (Double.parseDouble(builder.amount) <= 0 || Double.parseDouble(builder.amount) > 1000000)
                throw new RuntimeException("Amount should be greater 0 and  less than 1000000.00  ");
            else
                params.put(TraknpayConstants.AMOUNT, builder.amount + "");

            if (TextUtils.isEmpty(builder.email))
                throw new RuntimeException(" Merchant id missing ,setDebugMerchantId() ");
            else
                params.put(TraknpayConstants.EMAIL, builder.email);

            if (TextUtils.isEmpty(builder.name))
                throw new RuntimeException("first name is missing");
            else
                params.put(TraknpayConstants.NAME, builder.name);

            if (TextUtils.isEmpty(builder.phone))
                throw new RuntimeException("phone is missing");
            else
                params.put(TraknpayConstants.PHONE, builder.phone);

            if (TextUtils.isEmpty(builder.order_id)) {
                throw new RuntimeException("Order Id missing");
            } else {
                params.put(TraknpayConstants.ORDER_ID, builder.order_id);
            }

            if (TextUtils.isEmpty(builder.currency)) {
                throw new RuntimeException("Order Id missing");
            } else {
                params.put(TraknpayConstants.CURRENCY, builder.currency);
            }

            if (TextUtils.isEmpty(builder.description)) {
                throw new RuntimeException("Description missing");
            } else {
                params.put(TraknpayConstants.DESCRIPTION, builder.description);
            }

            if (TextUtils.isEmpty(builder.city)) {
                throw new RuntimeException("City missing");
            } else {
                params.put(TraknpayConstants.CITY, builder.city);
            }

            if (TextUtils.isEmpty(builder.state)) {
                throw new RuntimeException("State missing");
            } else {
                params.put(TraknpayConstants.STATE, builder.state);
            }

            if (TextUtils.isEmpty(builder.address_line_1)) {
                throw new RuntimeException("Address Line 1 missing");
            } else {
                params.put(TraknpayConstants.ADDRESS_LINE1, builder.address_line_1);
            }

            if (TextUtils.isEmpty(builder.address_line_2)) {
                throw new RuntimeException("Address Line 2 missing");
            } else {
                params.put(TraknpayConstants.ADDRESS_LINE2, builder.address_line_2);
            }


            if (TextUtils.isEmpty(builder.zip_code)) {
                throw new RuntimeException("Zip Code missing");
            } else {
                params.put(TraknpayConstants.ZIPCODE, builder.zip_code);
            }

            if (TextUtils.isEmpty(builder.country)) {
                throw new RuntimeException("Country missing");
            } else {
                params.put(TraknpayConstants.COUNTRY, builder.country);
            }

            if (TextUtils.isEmpty(builder.return_url)) {
                throw new RuntimeException("Return URL missing");
            } else {
                params.put(TraknpayConstants.RETURN_URL, builder.return_url);
            }

            if (TextUtils.isEmpty(builder.mode)) {
                throw new RuntimeException("Mode missing");
            } else {
                params.put(TraknpayConstants.MODE, builder.mode);
            }


            if (builder.udf1 != null)
                params.put(TraknpayConstants.UDF1, builder.udf1);

            if (builder.udf2 != null)
                params.put(TraknpayConstants.UDF2, builder.udf2);

            if (builder.udf3 != null)
                params.put(TraknpayConstants.UDF3, builder.udf3);

            if (builder.udf4 != null)
                params.put(TraknpayConstants.UDF4, builder.udf4);

            if (builder.udf5 != null)
                params.put(TraknpayConstants.UDF5, builder.udf5);

            if (TextUtils.isEmpty(builder.packageName)) {
                throw new RuntimeException("Return Activity Package Name missing");
            } else {
                setReturnPackage(builder.packageName);
            }

            if (TextUtils.isEmpty(builder.className)) {
                throw new RuntimeException("Return Activity Package Class missing");
            } else {
                setReturnClass(builder.className);
            }

            if (getMode()=="TEST")
                Log.d("hashSeq", pipedHash);

            String hash = hashCal(pipedHash);

            if (getMode()=="TEST")
                Log.d("hash", hash);


            if (getMode()=="TEST")
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    Log.d("param : ", key + " - " + value);
                }
        }

        private static String hashCal(String str) {
            byte[] hashseq = str.getBytes();
            StringBuilder hexString = new StringBuilder();
            try {
                MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
                algorithm.reset();
                algorithm.update(hashseq);
                byte messageDigest[] = algorithm.digest();
                for (byte aMessageDigest : messageDigest) {
                    String hex = Integer.toHexString(0xFF & aMessageDigest);
                    if (hex.length() == 1) {
                        hexString.append("0");
                    }
                    hexString.append(hex);
                }
            } catch (NoSuchAlgorithmException ignored) {
            }
            return hexString.toString();
        }

        public HashMap<String, String> getParams() {
            return params;
        }

        public void setMerchantHash(String serverCalculatedHash) {
            params.put(TraknpayConstants.HASH, serverCalculatedHash);
        }

        @Override
        public String toString() {
            return pipedHash;
        }

        public static class Builder {

            private String api_key;
            private String amount = "0.00";
            private String email;
            private String name;
            private String phone;
            private String order_id;
            private String currency;
            private String description;
            private String city;
            private String state;
            private String zip_code;
            private String country;
            private String return_url;
            private String address_line_1;
            private String address_line_2;
            private String mode;
            private String udf1 = "";
            private String udf2 = "";
            private String udf3 = "";
            private String udf4 = "";
            private String udf5 = "";
            private String packageName = "";
            private String className = "";

            private String hash;


            public Builder() {
            }

            public Builder setAPiKey(String api_key) {
                this.api_key = api_key;
                return this;
            }

            public Builder setAmount(String amount) {
                this.amount = amount;
                return this;
            }

            public Builder setEmail(String email) {
                this.email = email;
                return this;
            }

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            public Builder setPhone(String phone) {
                this.phone = phone;
                return this;
            }

            public Builder setOrderId(String order_id) {
                this.order_id = order_id;
                return this;
            }

            public Builder setCurrency(String currency) {
                this.currency = currency;
                return this;
            }

            public Builder setDescription(String description) {
                this.description = description;
                return this;
            }

            public Builder setCity(String city) {
                this.city = city;
                return this;
            }

            public Builder setState(String state) {
                this.state = state;
                return this;
            }

            public Builder setZipCode(String zip_code) {
                this.zip_code = zip_code;
                return this;
            }

            public Builder setCountry(String country) {
                this.country = country;
                return this;
            }

            public Builder setReturnUrl(String return_url) {
                try{
                    this.return_url = URLEncoder.encode(return_url, "UTF-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                return this;

            }

            public Builder setMode(String mode) {
                this.mode = mode;
                return this;
            }

            public Builder setUdf1(String udf1) {
                this.udf1 = udf1;
                return this;
            }

            public Builder setUdf2(String udf2) {
                this.udf2 = udf2;
                return this;
            }

            public Builder setUdf3(String udf3) {
                this.udf3 = udf3;
                return this;
            }

            public Builder setUdf4(String udf4) {
                this.udf4 = udf4;
                return this;
            }

            public Builder setUdf5(String udf5) {
                this.udf5 = udf5;
                return this;
            }

            public Builder setReturnActivityPackage(String packageName) {
                this.packageName = packageName;
                return this;
            }

            public Builder setReturnActivityClass(String className) {
                this.className = className;
                return this;
            }

            public Builder setAddressLine1(String address_line_1) {
                this.address_line_1 = address_line_1;
                return this;
            }

            public Builder setAddressLine2(String address_line_2) {
                this.address_line_2 = address_line_2;
                return this;
            }

            public String buildParamsForhash(PaymentParam paymentParam) {
                HashMap<String, String> params = paymentParam.getParams();

                StringBuffer postParamsBuffer = new StringBuffer();
                postParamsBuffer.append(concatParams(TraknpayConstants.API_KEY, params.get(TraknpayConstants.API_KEY)));
                postParamsBuffer.append(concatParams(TraknpayConstants.AMOUNT, params.get(TraknpayConstants.AMOUNT)));
                postParamsBuffer.append(concatParams(TraknpayConstants.EMAIL, params.get(TraknpayConstants.EMAIL)));
                postParamsBuffer.append(concatParams(TraknpayConstants.NAME, params.get(TraknpayConstants.NAME)));
                postParamsBuffer.append(concatParams(TraknpayConstants.PHONE, params.get(TraknpayConstants.PHONE)));
                postParamsBuffer.append(concatParams(TraknpayConstants.ORDER_ID, params.get(TraknpayConstants.ORDER_ID)));
                postParamsBuffer.append(concatParams(TraknpayConstants.CURRENCY, params.get(TraknpayConstants.CURRENCY)));
                postParamsBuffer.append(concatParams(TraknpayConstants.DESCRIPTION, params.get(TraknpayConstants.DESCRIPTION)));
                postParamsBuffer.append(concatParams(TraknpayConstants.CITY, params.get(TraknpayConstants.CITY)));
                postParamsBuffer.append(concatParams(TraknpayConstants.STATE, params.get(TraknpayConstants.STATE)));
                postParamsBuffer.append(concatParams(TraknpayConstants.ADDRESS_LINE1, params.get(TraknpayConstants.ADDRESS_LINE1)));
                postParamsBuffer.append(concatParams(TraknpayConstants.ADDRESS_LINE2, params.get(TraknpayConstants.ADDRESS_LINE2)));
                postParamsBuffer.append(concatParams(TraknpayConstants.ZIPCODE, params.get(TraknpayConstants.ZIPCODE)));
                postParamsBuffer.append(concatParams(TraknpayConstants.COUNTRY, params.get(TraknpayConstants.COUNTRY)));
                postParamsBuffer.append(concatParams(TraknpayConstants.RETURN_URL, params.get(TraknpayConstants.RETURN_URL)));
                postParamsBuffer.append(concatParams(TraknpayConstants.MODE, params.get(TraknpayConstants.MODE)));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF1, params.get(TraknpayConstants.UDF1)));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF2, params.get(TraknpayConstants.UDF2)));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF3, params.get(TraknpayConstants.UDF3)));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF4, params.get(TraknpayConstants.UDF4)));
                postParamsBuffer.append(concatParams(TraknpayConstants.UDF5, params.get(TraknpayConstants.UDF5)));

                String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

                System.out.println("postParams : "+postParams);
                return postParams;
            }

            public PaymentParam build() {
                return new PaymentParam(this);
            }

            protected String concatParams(String key, String value) {
                return key + "=" + value + "&";
            }
        }
    }


}
