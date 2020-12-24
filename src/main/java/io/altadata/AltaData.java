package io.altadata;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Set;

/**
 * This class contains all of the AltaData API logic.
 */
public class AltaData {
    static final String DATA_API_URL = "https://www.altadata.io/data/api/";
    static final String SUBSCRIPTION_API_URL = "https://www.altadata.io/subscription/api/subscriptions";

    private String api_key;
    private String data_request_url;
    private int limit;

    /**
     * Get AltaData API key
     *
     * @return AltaData API key
     */
    public String getApi_key() {
        return api_key;
    }

    /**
     * Set AltaData API key
     *
     * @param api_key AltaData API key
     */
    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    /**
     * Get AltaData API request URL
     *
     * @return AltaData API request URL
     */
    public String getData_request_url() {
        return data_request_url;
    }

    /**
     * Set AltaData API request URL
     *
     * @param data_request_url AltaData API request URL
     */
    public void setData_request_url(String data_request_url) {
        this.data_request_url = data_request_url;
    }

    /**
     * Get data limit parameter
     *
     * @return data limit parameter
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set data limit parameter
     *
     * @param limit data limit parameter
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Initialize object with Altadata API Key
     *
     * @param api_key Altadata API Key
     */
    public AltaData(String api_key) {
        setApi_key(api_key);
    }

    /**
     * Initializes retrieve data process with data limit
     *
     * @param product_code Data product code
     * @param limit        number of rows you want to retrieve
     * @return class itself
     * @throws Exception It occurs when the limit parameter is not selected properly
     */
    public AltaData get_data(String product_code, Integer limit) throws Exception {
        setData_request_url(DATA_API_URL + product_code + "/?format=json&api_key=" + getApi_key());

        if (limit != null) {
            if (limit < 1) {
                throw new Exception("limit parameter must be greater than 0");
            }
        }

        int current_limit = limit == null ? -1 : limit;
        setLimit(current_limit);

        return this;
    }

    /**
     * Initializes retrieve data process
     *
     * @param product_code Data product code
     * @return class itself
     */
    public AltaData get_data(String product_code) {
        setData_request_url(DATA_API_URL + product_code + "/?format=json&api_key=" + getApi_key());
        setLimit(-1);

        return this;
    }

    /**
     * Fetch data with configurations given before
     *
     * @return all data as ArrayList<JSONObject>
     * @throws IOException          It may occur when making an http request.
     * @throws InterruptedException It may occur when making an http request.
     */
    public ArrayList<JSONObject> load() throws IOException, InterruptedException {
        ArrayList<JSONObject> data = new ArrayList<>();
        int page = 1;
        int total_size = 1;
        int limit = getLimit();

        while (true) {
            String response_json = request(getData_request_url() + "&page=" + page);

            if (response_json.contains("}")) {
                String[] arrayJson = response_json.split("},");

                for (int i = 0; i < arrayJson.length; i++) {
                    if (i != arrayJson.length - 1) {
                        String json_item = arrayJson[i] + "}";
                        JSONObject jo = new JSONObject(json_item);
                        data.add(jo);
                    } else {
                        String json_item = arrayJson[i];
                        JSONObject jo = new JSONObject(json_item);
                        data.add(jo);
                    }
                }

                if (limit != -1) {
                    total_size += arrayJson.length;
                    if (total_size > limit) {
                        break;
                    }
                }

                page += 1;
            } else {
                break;
            }
        }

        if (limit != -1) {
            ArrayList<JSONObject> limited_data = new ArrayList<>();

            for (int i = 0; i < limit; i++) {
                limited_data.add(data.get(i));
            }

            return limited_data;
        }

        return data;
    }

    /**
     * Sort data by given column and method in the retrieve data process
     *
     * @param order_column column to which the order is applied
     * @param order_method sorting method. Possible values: "asc" or "desc"
     * @return class itself
     * @throws Exception It occurs when the order_method parameter is not selected properly
     */
    public AltaData sort(String order_column, String order_method) throws Exception {
        if (order_method.equals("asc") || order_method.equals("desc")) {
            setData_request_url(getData_request_url() + "&order_by=" + order_column + "_" + order_method);

            return this;
        } else {
            throw new Exception("order_method parameter must be 'asc' or 'desc'");
        }
    }

    /**
     * 'Equal' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData equal(String condition_column, String condition_value) {
        setData_request_url(getData_request_url() + "&" + condition_column + "_eq=" + condition_value);

        return this;
    }

    /**
     * 'Not Equal' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData not_equal(String condition_column, String condition_value) {
        setData_request_url(getData_request_url() + "&" + condition_column + "_neq=" + condition_value);

        return this;
    }

    /**
     * 'Greater than' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData greater_than(String condition_column, String condition_value) {
        setData_request_url(getData_request_url() + "&" + condition_column + "_gt=" + condition_value);

        return this;
    }

    /**
     * 'Greater than equal' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData greater_than_equal(String condition_column, String condition_value) {
        setData_request_url(getData_request_url() + "&" + condition_column + "_gte=" + condition_value);

        return this;
    }

    /**
     * 'Less than' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData less_than(String condition_column, String condition_value) {
        setData_request_url(getData_request_url() + "&" + condition_column + "_lt=" + condition_value);

        return this;
    }

    /**
     * 'Less than equal' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData less_than_equal(String condition_column, String condition_value) {
        setData_request_url(getData_request_url() + "&" + condition_column + "_lte=" + condition_value);

        return this;
    }

    /**
     * Select specific columns in the retrieve data process
     *
     * @param selected_column list of columns to select
     * @return class itself
     */
    public AltaData select(String[] selected_column) {
        String selected_column_text = String.join(",", selected_column);
        setData_request_url(getData_request_url() + "&columns=" + selected_column_text);

        return this;
    }

    /**
     * 'In' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData condition_in(String condition_column, String[] condition_value) {
        String condition_value_text = String.join(",", condition_value);
        setData_request_url(getData_request_url() + "&" + condition_column + "_in=" + condition_value_text);

        return this;
    }

    /**
     * 'Not in' condition by given column and value in the retrieve data process
     *
     * @param condition_column column to which the condition will be applied
     * @param condition_value  value to use with condition
     * @return class itself
     */
    public AltaData condition_not_in(String condition_column, String[] condition_value) {
        String condition_value_text = String.join(",", condition_value);
        setData_request_url(getData_request_url() + "&" + condition_column + "_notin=" + condition_value_text);

        return this;
    }

    /**
     * Retrieves customer's subscription info
     *
     * @return subscription info as ArrayList<JSONObject>
     * @throws IOException          It may occur when making an http request.
     * @throws InterruptedException It may occur when making an http request.
     */
    public ArrayList<JSONObject> list_subscription() throws IOException, InterruptedException {
        ArrayList<JSONObject> data = new ArrayList<>();

        String request_url = SUBSCRIPTION_API_URL + "?api_key=" + getApi_key();
        String response_json = request(request_url);

        String[] arrayJson = response_json.split("},\\{");

        for (int i = 0; i < arrayJson.length; i++) {
            if (i == 0) {
                String json_item = arrayJson[i] + "}";
                JSONObject jo = new JSONObject(json_item);
                data.add(jo);
            } else if (i == arrayJson.length - 1) {
                String json_item = "{" + arrayJson[i];
                JSONObject jo = new JSONObject(json_item);
                data.add(jo);
            } else {
                String json_item = "{" + arrayJson[i] + "}";
                JSONObject jo = new JSONObject(json_item);
                data.add(jo);
            }
        }

        return data;
    }

    /**
     * Get data header as a Set<String>
     *
     * @param product_code Data product code
     * @return header info as Set<String>
     * @throws IOException          It may occur when making an http request.
     * @throws InterruptedException It may occur when making an http request.
     */
    public Set<String> get_header(String product_code) throws IOException, InterruptedException {
        String request_url = DATA_API_URL + product_code + "/?format=json&api_key=" + getApi_key() + "&page=1";
        String response_json = request(request_url);

        String[] arrayJson = response_json.split("},");
        String json_item = arrayJson[0] + "}";

        JSONObject jo = new JSONObject(json_item);

        return jo.keySet();
    }

    /**
     * Request API and parse the result
     *
     * @param request_url request url to be sent to api
     * @return Parsed API response as String
     * @throws IOException          It may occur when making an http request.
     * @throws InterruptedException It may occur when making an http request.
     */
    private String request(String request_url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .header("accept", "application/json")
                .uri(URI.create(request_url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body().replace("[", "").replace("]", "");
    }

}
