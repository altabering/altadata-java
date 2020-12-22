package io.altadata;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AltaDataTest {
    static final String test_api_key = System.getenv("TEST_API_KEY");
    static final String test_product_code = "co_10_jhucs_03";
    static AltaData client = new AltaData(test_api_key);

    @Test
    void getSubscriptionInfo() throws IOException, InterruptedException {
        if (test_api_key == null) {
            return;
        }

        ArrayList<String> expected_result = new ArrayList<>(
                Arrays.asList("CO_10_JHUCS_04", "CO_08_UNXXX_04", "CO_10_JHUCS_03", "CO_07_IDEAX_02")
        );

        ArrayList<String> product_code_array = new ArrayList<>();
        ArrayList<JSONObject> subscription_info = client.list_subscription();

        for (JSONObject jsonObject : subscription_info) {
            product_code_array.add((String) jsonObject.getJSONObject("offer").get("code"));
        }

        Assertions.assertEquals(expected_result, product_code_array);
    }

    @Test
    void getHeaderInfo() throws IOException, InterruptedException {
        if (test_api_key == null) {
            return;
        }

        Set<String> expected_result = new HashSet<>(
                Arrays.asList(
                        "reported_date", "peak_confirmed_1d_flag", "lng", "new_confirmed", "mortality_rate",
                        "people_hospitalized", "province_state", "active", "confirmed", "population", "people_tested",
                        "recovered", "incidence_rate", "prev_deaths_1d", "new_deaths", "hospitalization_rate", "testing_rate",
                        "most_deaths_1d_flag", "lat", "deaths", "prev_confirmed_1d"
                )
        );

        Set<String> header_info = client.get_header(test_product_code);

        Assertions.assertEquals(expected_result, header_info);
    }

    @Test
    void getSortedData() throws Exception {
        if (test_api_key == null) {
            return;
        }

        String expected_result = "2020-04-12";
        ArrayList<JSONObject> data = client.get_data(test_product_code, 10)
                .sort("reported_date", "asc")
                .load();

        Assertions.assertEquals(expected_result, data.get(0).get("reported_date"));
    }

    @Test
    void getSelectedData() throws Exception {
        if (test_api_key == null) {
            return;
        }

        String[] selected_columns = {"reported_date", "province_state", "mortality_rate"};
        Set<String> expected_result = new HashSet<>(Arrays.asList(selected_columns));

        ArrayList<JSONObject> data = client.get_data(test_product_code, 250)
                .select(selected_columns)
                .load();

        Assertions.assertEquals(expected_result, data.get(0).keySet());
    }

    @Test
    void getDataWithInCondition() throws Exception {
        if (test_api_key == null) {
            return;
        }

        ArrayList<String> province_state_array = new ArrayList<>();

        String[] condition_value = {"Montana", "Utah"};
        Set<String> expected_result = new HashSet<>(Arrays.asList(condition_value));

        ArrayList<JSONObject> data = client.get_data(test_product_code)
                .condition_in("province_state", condition_value)
                .load();

        for (JSONObject jsonObject : data) {
            province_state_array.add((String) jsonObject.get("province_state"));
        }

        Assertions.assertEquals(expected_result, new HashSet<>(province_state_array));
    }

    @Test
    void getDataWithNotInCondition() throws IOException, InterruptedException {
        if (test_api_key == null) {
            return;
        }

        ArrayList<String> province_state_array = new ArrayList<>();
        String[] condition_value = {"Montana", "Utah", "Alabama"};
        boolean flag = true;

        ArrayList<JSONObject> data = client.get_data(test_product_code)
                .condition_not_in("province_state", condition_value)
                .load();

        for (JSONObject jsonObject : data) {
            province_state_array.add((String) jsonObject.get("province_state"));
        }

        for (String province_state : condition_value) {
            if (province_state_array.contains(province_state)) {
                flag = false;
                break;
            }
        }

        Assertions.assertTrue(flag);
    }

    @Test
    void getDataWithEqual() throws IOException, InterruptedException {
        if (test_api_key == null) {
            return;
        }

        ArrayList<String> province_state_array = new ArrayList<>();
        String condition_value = "Montana";
        boolean flag = false;

        ArrayList<JSONObject> data = client.get_data(test_product_code)
                .equal("province_state", condition_value)
                .load();

        for (JSONObject jsonObject : data) {
            province_state_array.add((String) jsonObject.get("province_state"));
        }

        if (province_state_array.contains(condition_value)) {
            flag = true;
        }

        Assertions.assertTrue(flag);
    }

    @Test
    void getDataWithNotEqual() throws IOException, InterruptedException {
        if (test_api_key == null) {
            return;
        }

        ArrayList<String> province_state_array = new ArrayList<>();
        String condition_value = "Utah";
        boolean flag = true;

        ArrayList<JSONObject> data = client.get_data(test_product_code)
                .not_equal("province_state", condition_value)
                .load();

        for (JSONObject jsonObject : data) {
            province_state_array.add((String) jsonObject.get("province_state"));
        }

        if (province_state_array.contains(condition_value)) {
            flag = false;
        }

        Assertions.assertTrue(flag);
    }

    @Test
    void getDataWithLimit() throws Exception {
        if (test_api_key == null) {
            return;
        }

        int data_limit = 25;
        ArrayList<JSONObject> data = client.get_data(test_product_code, data_limit).load();

        Assertions.assertEquals(data_limit, data.size());
    }
}
