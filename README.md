# ALTADATA Java Client

[![Build status](https://github.com/altabering/altadata-java/workflows/build/badge.svg)](https://github.com/altabering/altadata-java/actions)
[![Maven central](https://img.shields.io/maven-central/v/io.altadata/altadata-java)](https://search.maven.org/artifact/io.altadata/altadata-java)

[ALTADATA](https://www.altadata.io) Java package provides convenient access to the ALTADATA API from applications
written in the Java language. With this Java package, developers can build applications around the ALTADATA API without
having to deal with accessing and managing requests and responses.

## Installing with Maven

To include altadata-java in your [Maven](http://maven.apache.org/) application, add a dependency on its artifacts to
your project's **pom.xml** file. For example,

```xml

<dependency>
    <groupId>io.altadata</groupId>
    <artifactId>altadata-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Quickstart

Obtain an API key in your dashboard and initialize the client:

```java
import io.altadata;

AltaData client=new AltaData("YOUR_API_KEY");
```

## Retrieving Data

You can get the entire data with the code below.

```java
ArrayList<JSONObject> data=client.get_data("PRODUCT_CODE").load();
```

## Retrieving Subscription Info

You can get your subscription info with the code below.

```java
ArrayList<JSONObject> subscription_info=client.list_subscription();
```

## Retrieving Data Header Info

You can get your data header with the code below.

```java
Set<String> header_info=client.get_header(test_product_code);
```

## Retrieving Data with Conditions

You can get data with using various conditions.

The columns you can apply these filter operations to are limited to the **filtered columns**.

> You can find the **filtered columns** in the data section of the data product page.

### equal condition

```java
String product_code="co_10_jhucs_03";

        ArrayList<JSONObject> data=client.get_data(product_code)
        .equal("province_state","Alabama")
        .load();
```

### not equal condition

```java
String product_code="co_10_jhucs_03";

        ArrayList<JSONObject> data=client.get_data(product_code)
        .not_equal("province_state","Montana")
        .load();
```

### in condition

```java
String product_code="co_10_jhucs_03";

        ArrayList<JSONObject> data=client.get_data(product_code)
        .condition_in("province_state",new String[]{"Montana","Utah"})
        .load();
```

> condition_value parameter of condition_in method must be Array

### not in condition

```java
String product_code="co_10_jhucs_03";

        ArrayList<JSONObject> data=client.get_data(product_code)
        .condition_not_in("province_state",new String[]{"Montana","Utah","Alabama"})
        .load();
```

> condition_value parameter of condition_not_in method must be Array

### sort operation

```java
String product_code="co_10_jhucs_03";
        String order_method="desc";

        ArrayList<JSONObject> data=client.get_data(product_code)
        .sort("reported_date",order_method)
        .load();
```

> Default value of order_method parameter is 'asc' and order_method parameter must be "asc" or "desc"

### select specific columns

```java
String product_code="co_10_jhucs_03";
        String[]selected_column={"reported_date","province_state","mortality_rate"};

        ArrayList<JSONObject> data=client.get_data(product_code)
        .select(selected_column)
        .load();
```

> selected_column parameter of select method must be Array

### get the specified amount of data

```java
String product_code="co_10_jhucs_03";
        int data_limit=20;

        ArrayList<JSONObject> data=client.get_data(product_code,data_limit)
        .load();
```

## Retrieving Data with Multiple Conditions

You can use multiple condition at same time.

```java
String product_code="co_10_jhucs_03";
        int data_limit=100;
        String order_method="desc";
        String[]selected_column={"reported_date","province_state","mortality_rate"};

        ArrayList<JSONObject> data=client.get_data(product_code,data_limit)
        .condition_in("province_state",new String[]{"Montana","Utah"})
        .sort("mortality_rate",order_method)
        .select(selected_column)
        .load();
```

## License

The gem is available as open source under the terms of
the [MIT License](https://github.com/altabering/altadata-java/blob/master/LICENSE).