# Overview

> This section will guide you in creating a framework for integrating Traknpay Payment Gateway with your android app. 

![Overview](https://traknpaypg.github.io/doc/images/overview.png?raw=true)

-------------

# Sample App
> To understand the Traknpay payment flow, you can download our sample app [here](https://github.com/traknpaypg/traknpaypg.github.io).

-------------

# Prerequisites

1. You should be a registered and approved merchant with Traknpay. If not registered, please [register here!](https://biz.traknpay.in/auth/register)
2. You should have received the SALT and API key from Traknpay.

-------------

# Server Side Setup

> a. To prevent the data tampering(and ensure data integrity) between the your app and Traknpay, you will need to setup up an API in your server to calculate an encrypted value or checksum known as hash from the payment request parameters and SALT key before sending it to the Traknpay server.

```markdown
Traknpay uses **SHA512** cryptographic hash function to prevent data tampering. To calculate the 
hash, a secure private key known as **SALT key** will be provided by Traknpay that needs to be 
stored **very securely in your server**. Any compromise of the salt may lead to data tampering. 

# The hash generation code has 3 components:

1. **Concatenate** the request parameters(after **trimming** the blank spaces) separated by 
**pipeline** in the order given below:   

`hash_data="SALT|address_line_1|address_line_2|amount|api_key|city|country|currency|description
|email|hash|mode|name|order_id|phone|return_url|state|udf1|udf2|udf3|udf4|udf5|zip_code"`

2. Calculate the **hash** of the string value obtained in step 1 using **sha512** algorithm(all 
major languages would have an in-house function to calculate the hash using SHA-512).

3. Change the hash value obtained in step 2 to **UPPERCASE** and response the hash value to the 
android app.

```

> Sample Hash Generation of Payment Request for different languages has been given below:

```java
**Java Servlet Sample Code**

public class PaymentRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		// TODO Auto-generated method stub
		String salt = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; 
		
		String [] hash_columns = {"address_line_1", "address_line_2", "amount", "api_key", 
		"city", "country", "currency","description", "email", "mode", "name", "order_id", 
		"phone", "return_url", "state", "udf1", "udf2", "udf3", "udf4","udf5", "zip_code"};
		
		String hash_data = salt;
		
		for( int i = 0; i < hash_columns.length; i++)
		{
			if(request.getParameter(hash_columns[i]).length() > 0 ){
				hash_data += '|' + request.getParameter(hash_columns[i]).trim();
			}    
			
		}
		
		String hash = "";
		try {
			 hash = getHashCodeFromString(hash_data);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("hash", hash);
             	jsonResponse.addProperty("status", "Kargopolov");
       		jsonResponse.addProperty("responseCode", "Kargopolov");


		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		writer.print(jsonResponse);
        	writer.flush();

	}
	
	private static String getHashCodeFromString(String str) throws NoSuchAlgorithmException, 
	UnsupportedEncodingException {
			
		MessageDigest md = MessageDigest.getInstance("SHA-512");
	    	md.update(str.getBytes("UTF-8"));
	    	byte byteData[] = md.digest();

	    	//convert the byte to hex format method 1
	    	StringBuffer hashCodeBuffer = new StringBuffer();
	    	for (int i = 0; i < byteData.length; i++) {
	            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
		    .substring(1));
	    	}
		return hashCodeBuffer.toString().toUpperCase();
	}
	
}

```

```php
**PHP Sample Code**

try {
	$salt="XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	$params["api_key"]=trim($_POST["api_key"]);
	$params["amount"]=trim($_POST["amount"]);
	$params["email"]=trim($_POST["email"]);
	$params["name"]=trim($_POST["name"]);
	$params["phone"]=trim($_POST["phone"]);
	$params["order_id"]=trim($_POST["order_id"]);
	$params["currency"]=trim($_POST["currency"]);
	$params["description"]=trim($_POST["description"]);
	$params["city"]=trim($_POST["city"]);
	$params["state"]=trim($_POST["state"]);
	$params["address_line_1"]=trim($_POST["address_line_1"]);
	$params["address_line_2"]=trim($_POST["address_line_2"]);
	$params["zip_code"]=trim($_POST["zip_code"]);
	$params["country"]=trim($_POST["country"]);
	$params["return_url"]=trim($_POST["return_url"];)
	$params["mode"]=trim($_POST["mode"]);
	if(!empty($_POST["udf1"])) $params["udf1"]=trim($_POST["udf1"]);
	if(!empty($_POST["udf2"])) $params["udf2"]=trim($_POST["udf2"]);
	if(!empty($_POST["udf3"])) $params["udf3"]=trim($_POST["udf3"]);
	if(!empty($_POST["udf4"])) $params["udf4"]=trim($_POST["udf4"]);
	if(!empty($_POST["udf5"])) $params["udf5"]=trim($_POST["udf5"]);

	$hash_columns = [
		'name',
		'phone',
		'email',
		'description',
		'amount',
		'api_key',
		'order_id',
		'currency',
		'city',
		'state',
		'address_line_1',
		'address_line_2',
		'country',
		'zip_code',
		'return_url',
		'hash',
		'mode',
		'udf1',
		'udf2',
		'udf3',
		'udf4',
		'udf5'
	];

	sort($hash_columns);
	$hash_data = $salt;

	foreach ($hash_columns as $column) {
		if (isset($params[$column])) {
			if (strlen($params[$column]) > 0) {
				$hash_data .= '|' . $params[$column];
			}
		}
	}

	$hash = null;
	if (strlen($hash_data) > 0) {
		$hash = strtoupper(hash("sha512", $hash_data));
	}

	$output['hash'] = $hash;
	$output['status']=0;
	$output['responseCode']="Hash Created Successfully";

}catch(Exception $e) {
	$output['hash'] = "INVALID";
	$output['status']=1;
	$output['responseCode']=$e->getMessage();
}

echo json_encode($output);

```

```csharp
**ASP.NET Sample Code**

public partial class PaymentRequest : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string jsonResponse = "";
            try
            {
                string hash_string = string.Empty;
                string hashValue = string.Empty;
                string SALT = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

                hash_string = SALT;
                hash_string += '|' + Request.Form["address_line_1"].Trim();
                hash_string += '|' + Request.Form["address_line_2"].Trim();
                hash_string += '|' + Request.Form["amount"].Trim();
                hash_string += '|' + Request.Form["api_key"].Trim();
                hash_string += '|' + Request.Form["city"].Trim();
                hash_string += '|' + Request.Form["country"].Trim();
                hash_string += '|' + Request.Form["currency"].Trim();
                hash_string += '|' + Request.Form["description"].Trim();
                hash_string += '|' + Request.Form["email"].Trim();
                hash_string += '|' + Request.Form["mode"].Trim();
                hash_string += '|' + Request.Form["name"].Trim();
                hash_string += '|' + Request.Form["order_id"].Trim();
                hash_string += '|' + Request.Form["phone"].Trim();
                hash_string += '|' + Request.Form["return_url"].Trim();
                hash_string += '|' + Request.Form["state"].Trim();
                if (!string.IsNullOrEmpty(Request.Form["udf1"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf1"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf2"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf2"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf3"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf3"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf4"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf4"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf5"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf5"].Trim();
                }
                hash_string += '|' + Request.Form["zip_code"].Trim();

                hash_string = hash_string.Substring(0, hash_string.Length);
                hashValue = Generatehash512(hash_string).ToUpper();       

			  
	        var columns = new Dictionary<string, string>
                {
                    { "hash", hashValue},
                    { "status", 0},
                    { "responseCode", "Hash Created Successfully"},
                };                
                var jsSerializer = new JavaScriptSerializer();             
                var jsonString = jsSerializer.Serialize(columns);
			    
	       return jsonString;

            }catch (Exception ex){
                
	        var columns = new Dictionary<string, string>
               {
                   { "hash", "INVALID"},
                   { "status", 1},
                   { "responseCode", ex.Message},
               };            
               var jsSerializer = new JavaScriptSerializer();               
               var jsonString = jsSerializer.Serialize(columns);
			   
	       return jsonString;
           }
        }
        
        public string Generatehash512(string text)
        {

            byte[] message = Encoding.UTF8.GetBytes(text);

            UnicodeEncoding UE = new UnicodeEncoding();
            byte[] hashValue;
            SHA512Managed hashString = new SHA512Managed();
            string hex = "";
            hashValue = hashString.ComputeHash(message);
            foreach (byte x in hashValue)
            {
                hex += String.Format("{0:x2}", x);
            }
            return hex;

        }
}
```
-------------

> Request to your server's Payment Request API would look like below:

```javascript
{
    "amount": "2.00",
    "email": "test@gmail.com",
    "name": "Test Name",
    "phone": "9876543210",
    "order_id": "12",
    "currency": "INR",
    "description": "test",
    "city": "city",
    "state": "state",
    "zip_code": "123456",
    "country": "IND",
    "return_url": "https://yourserver.com/response_page.php",
    "mode": "TEST",
    "udf1": "udf1",
    "udf2": "udf2",
    "udf3": "udf3",
    "udf4": "udf4",
    "udf5": "udf5",
    "address_line_1": "addl1",
    "address_line_2": "addl2",
    "api_key": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXXX"
}

```
-------------

> b. Your server should be ready to receive the payment parameters. This means you must have a API in your server that receives the response from Traknpay on payment completion. 

```markdown

# Response code have the below 2 components:

1. Your response must have the code to extract the hash from the Traknpay payment response 
and verify the hash to ensure no data tampering existed between Traknpay server and your 
server. You must again use SHA-512 algorithm to verify the hash.

2. If you are using the webview code given in the following section, then you must response 
the response fields that you need in a json format. 

```

```markdown

# Traknpay Recommendations:

At the very least, you should reverify the amount and order id field on your payment response 
API with the actual values of the amount and order id during payment initiation in your android app.

 
```

> Sample response API code for ASP.NET and PHP is given below for reference:

```csharp
**ASP.NET Sample Response API Code**

namespace ResponseHandler_ASP_NET
{
    [ValidationProperty("false")]
    public partial class ResponseHandling : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            try
            {

                string hash = string.Empty;
                string[] keys = Request.Form.AllKeys;
                Array.Sort(keys);
                string hash_string = string.Empty;
                hash_string = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
                foreach (string hash_var in keys)
                {
                    if (Request.Form[hash_var] != "" && hash_var != "hash")
                    {
                        hash_string = hash_string + '|';
                        hash_string = hash_string + Request.Form[hash_var];
                    }
                }
                hash = Generatehash512(hash_string).ToUpper();       
                string b = Request.Form["hash"];
                if (Request.Form["hash"] == hash)
                {
                    if (Request.Form["response_code"] == "0")
                    {
                        Dictionary<string, string> data = new Dictionary<string, 
			                                               string>();

				        foreach(string item  in keys)
				        {
				        	data[item] = Request.Form[item];
				        }
				        string json = Newtonsoft.Json.JsonConvert.
					          SerializeObject(data, 				
					          Newtonsoft.Json.Formatting.Indented);
				        Response.Write(json);
                    }
                    else if(Request.Form["response_message"] == "Transaction Failed"){

                        Response.Write("Transaction is unsuccessful");
                    }
                    else
                    {
                        string response_message = Request.Form["response_message"];
                        int startIndex=response_message.IndexOf(" - ")+2;
                        int length = response_message.Length - startIndex;
                        response_message = response_message.Substring(startIndex, 
			 						    length);
                        Response.Write("Correct the below error <br />");
                        Response.Write(response_message);
                    }
                }

                else
                {
                    Response.Write("Hash value did not matched");
                }
            }

            catch (Exception ex)
            {
                Response.Write("<span style='color:red'>" + ex.Message + "</span>");

            }
        }
       
        public string Generatehash512(string text)
        {

            byte[] message = Encoding.UTF8.GetBytes(text);

            UnicodeEncoding UE = new UnicodeEncoding();
            byte[] hashValue;
            SHA512Managed hashString = new SHA512Managed();
            string hex = "";
            hashValue = hashString.ComputeHash(message);
            foreach (byte x in hashValue)
            {
                hex += String.Format("{0:x2}", x);
            }
            return hex;

        }
    }
    
```


```php
**PHP Sample Response API Code**

if(!empty($_POST)){

		if(validResponse($_POST)){
			$response = $_POST;
			$salt = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; 
			if(isset($salt) && !empty($salt)){
				$response['calculated_hash']=hashCalculate($salt, $response);
				$response['valid_hash'] = ($response['hash']==$response[
				        'calculated_hash'])?'Yes':'No';
			} else {
				$response['valid_hash']='No Hash Found';
			}

			header('Content-Type: application/json');
			if($response['valid_hash']=='Yes'){
				echo json_encode([
					'order_id'=>$_POST['order_id'],
					'amount'=>$_POST['amount'],
					'transaction_id'=>$_POST['transaction_id'],
					'response_message'=>$_POST['response_message'],
					'response_code'=>$_POST['response_code'],
				]);
			}else{
				echo json_encode(['error'=>'Hash Mismatch']);
			}
		}else {
			echo json_encode(['error'=>'Missing Mandatory Keys in Response']);
		}
}else{
	echo json_encode(['error'=>'Invalid Response']);
}

function validResponse($response){
	$mandatory_keys = [
		'order_id',
		'amount',
		'currency',
		'description',
		'name',
		'email',
		'phone',
		'city',
		'country',
		'zip_code',
		'return_url',
		'hash',
		'response_message',
		'response_code',
		'transaction_id',
	];

	$verified_values=array();

	foreach ($mandatory_keys as $key){
		array_push($verified_values,array_key_exists($key,$response)? "true":"false");
	}

	return !in_array("false",$verified_values, true);

}


function hashCalculate($salt,$input){
	unset($input['hash']);
	ksort($input);

	$hash_data = $salt;

	foreach ($input as $key=>$value) {
		if (strlen($value) > 0) {
			$hash_data .= '|' . $value;
		}
	}

	$hash = null;
	if (strlen($hash_data) > 0) {
		$hash = strtoupper(hash("sha512", $hash_data));
	}

	return $hash;
}

```
-------------
    
# Webview Sample Code

> Once the payment is initiated, collect the payment fields, calculate the hash from your server and form the url post parameters. Sample code given below: 

```java

    StringBuffer requestParams=new StringBuffer("api_key="+URLDecoder.decode(SampleAppConstants.PG_API_KEY, "UTF-8"));
    requestParams.append("&amount="+URLDecoder.decode("2.00", "UTF-8"));
    requestParams.append("&email="+URLDecoder.decode("test@gmail.com", "UTF-8"));
    requestParams.append("&name="+URLDecoder.decode("Test Name", "UTF-8"));
    requestParams.append("&phone="+URLDecoder.decode("9876543210", "UTF-8"));
    requestParams.append("&order_id="+URLDecoder.decode("12", "UTF-8"));
    requestParams.append("&currency="+URLDecoder.decode(SampleAppConstants.PG_CURRENCY, "UTF-8"));
    requestParams.append("&description="+URLDecoder.decode("test", "UTF-8"));
    requestParams.append("&city="+URLDecoder.decode("city", "UTF-8"));
    requestParams.append("&state="+URLDecoder.decode("state", "UTF-8"));
    requestParams.append("&address_line_1="+URLDecoder.decode("addl1", "UTF-8"));
    requestParams.append("&address_line_2="+URLDecoder.decode("addl2", "UTF-8"));
    requestParams.append("&zip_code="+URLDecoder.decode("123456", "UTF-8"));
    requestParams.append("&country="+URLDecoder.decode(SampleAppConstants.PG_COUNTRY, "UTF-8"));
    requestParams.append("&return_url="+URLDecoder.decode(SampleAppConstants.PG_RETURN_URL, "UTF-8"));
    requestParams.append("&mode="+URLDecoder.decode(SampleAppConstants.PG_MODE, "UTF-8"));
    requestParams.append("&udf1="+URLDecoder.decode("udf1", "UTF-8"));
    requestParams.append("&udf2="+URLDecoder.decode("udf2", "UTF-8"));
    requestParams.append("&udf3="+URLDecoder.decode("udf3", "UTF-8"));
    requestParams.append("&udf4="+URLDecoder.decode("udf4", "UTF-8"));
    requestParams.append("&udf5="+URLDecoder.decode("udf5", "UTF-8"));
    requestParams.append("&hash="+URLDecoder.decode("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "UTF-8"));  
           
```

> Post the parameters to the Traknpay Payment URL and intercept the response page to receive the paramters.

```java

    WebSettings webSettings = webview.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setDomStorageEnabled(true);
    webview.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
    webview.postUrl(SampleAppConstants.PG_HOSTNAME+"/v1/paymentrequest",requestParams.toString().getBytes());

```


| Tables        | Are           | Cool  |
| ------------- |:-------------:| -----:|
| col 3 is      | right-aligned | $1600 |
| col 2 is      | centered      |   $12 |
| zebra stripes | are neat      |    $1 |
