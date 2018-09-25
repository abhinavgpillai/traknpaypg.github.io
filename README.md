## Overview

This section will guide you in creating a framework for integrating Traknpay Payment Gateway with your android app. 

![Overview](https://traknpaypg.github.io/doc/images/overview.png?raw=true)

## Sample App
To understand the Traknpay payment flow, you can download our sample app [here](https://github.com/traknpaypg/traknpaypg.github.io).

### Prerequisites

1. You should be a registered and approved merchant with Traknpay. If not registered, please [register here!](https://biz.traknpay.in/auth/register)
2. You should have received the SALT and API key from Traknpay.

### Server Side Setup

To prevent the data tampering(and ensure data integrity) between the your app and Traknpay, you will need to setup up a API to calculate encrypted value (checksum) known as hash from the payment request parameters and secure SALT key(provided by Traknpay) before sending it to the Traknpay server.

Hash Generation for Payment Request
Use the following sample java sequence to generate a request hash.

```markdown

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
    "return_url": "https://ecaas.traknpay.in/response_page.php",
    "mode": "TEST",
    "udf1": "udf1",
    "udf2": "udf2",
    "udf3": "udf3",
    "udf4": "udf4",
    "udf5": "udf5",
    "address_line_1": "addl1",
    "address_line_2": "addl2",
    "api_key": "ce937655-4421-4c6b-b4fb-b57785ea55c4"
}

```
### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/traknpaypg/traknpaypg.github.io/settings). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://help.github.com/categories/github-pages-basics/) or [contact support](https://github.com/contact) and we’ll help you sort it out.
