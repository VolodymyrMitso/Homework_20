package mitso.v.homework_20.api.models.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mitso.v.homework_20.api.ApiConstants;
import mitso.v.homework_20.api.interfaces.IModelResponse;

public class JsonData implements Serializable, IModelResponse {

    private String sourceId;
    private Date date;
    private List<JsonOrganization> organizations;
    private Map<String, String> orgTypes;
    private Map<String, String> currencies;
    private Map<String, String> regions;
    private Map<String, String> cities;

    private SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat("dd MMMM yyyy - HH:mm:ss");

    @Override
    public void configure(Object object) throws JSONException, ParseException {

        JSONObject jsonObject = (JSONObject) object;

        if (jsonObject.has(ApiConstants.DATA_SOURCE_ID_KEY) && !jsonObject.isNull(ApiConstants.DATA_SOURCE_ID_KEY))
            sourceId = jsonObject.getString(ApiConstants.DATA_SOURCE_ID_KEY);

        if (jsonObject.has(ApiConstants.DATA_DATE_KEY) && !jsonObject.isNull(ApiConstants.DATA_DATE_KEY)) {
            String string = jsonObject.getString(ApiConstants.DATA_DATE_KEY);
            date = simpleDateFormatIn.parse(string);
        }

        if (jsonObject.has(ApiConstants.DATA_ORGANIZATIONS_KEY) && !jsonObject.isNull(ApiConstants.DATA_ORGANIZATIONS_KEY)) {
            JSONArray nestedJsonArray = jsonObject.getJSONArray(ApiConstants.DATA_ORGANIZATIONS_KEY);
            organizations = new ArrayList<>();
            for (int i = 0; i < nestedJsonArray.length(); i++) {
                JSONObject nestedJsonObject = nestedJsonArray.getJSONObject(i);
                JsonOrganization jsonOrganization = new JsonOrganization();
                jsonOrganization.configure(nestedJsonObject);
                organizations.add(jsonOrganization);
            }
        }

        if (jsonObject.has(ApiConstants.DATA_ORG_TYPES_KEY) && !jsonObject.isNull(ApiConstants.DATA_ORG_TYPES_KEY)) {
            JSONObject nestedJsonObject = jsonObject.getJSONObject(ApiConstants.DATA_ORG_TYPES_KEY);
            orgTypes = new LinkedHashMap<>();
            jsonObjectToMap(nestedJsonObject, orgTypes);
        }

        if (jsonObject.has(ApiConstants.DATA_CURRENCIES_KEY) && !jsonObject.isNull(ApiConstants.DATA_CURRENCIES_KEY)) {
            JSONObject nestedJsonObject = jsonObject.getJSONObject(ApiConstants.DATA_CURRENCIES_KEY);
            currencies = new LinkedHashMap<>();
            jsonObjectToMap(nestedJsonObject, currencies);
        }

        if (jsonObject.has(ApiConstants.DATA_REGIONS_KEY) && !jsonObject.isNull(ApiConstants.DATA_REGIONS_KEY)) {
            JSONObject nestedJsonObject = jsonObject.getJSONObject(ApiConstants.DATA_REGIONS_KEY);
            regions = new LinkedHashMap<>();
            jsonObjectToMap(nestedJsonObject, regions);
        }

        if (jsonObject.has(ApiConstants.DATA_CITIES_KEY) && !jsonObject.isNull(ApiConstants.DATA_CITIES_KEY)) {
            JSONObject nestedJsonObject = jsonObject.getJSONObject(ApiConstants.DATA_CITIES_KEY);
            cities = new LinkedHashMap<>();
            jsonObjectToMap(nestedJsonObject, cities);
        }
    }

    private void jsonObjectToMap(JSONObject jsonObject, Map<String, String> map) throws JSONException {

        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jsonObject.getString(key);
            map.put(key, value);
        }
    }

    public String print_1() {

        String result = "DATA INFO:\n" +
                        "sourceId = " + sourceId + "\n" +
                        "date = " + simpleDateFormatOut.format(date) + "\n";
        result += "orgTypes = \n";
        for (Map.Entry<String, String> pair : orgTypes.entrySet())
            result += "    " + pair.getKey() + " | " + pair.getValue() + "\n";
        result += "currencies = \n";
        for (Map.Entry<String, String> pair : currencies.entrySet())
            result += "    " + pair.getKey() + " | " + pair.getValue() + "\n";

        return result;
    }

    public String print_2() {

        String result = "";

        result += "regions = \n";
        for (Map.Entry<String, String> pair : regions.entrySet())
            result += "    " + pair.getKey() + " | " + pair.getValue() + "\n";
        result += "cities = \n";
        for (Map.Entry<String, String> pair : cities.entrySet())
            result += "    " + pair.getKey() + " | " + pair.getValue() + "\n";

        return result;
    }

    public String print_3() {

        String result = "";

        result += "organizations = \n";
        result += organizations.get(0) + "\n";
        result += organizations.get(organizations.size() - 1) + "\n";

        return result;
    }

    public List<JsonOrganization> getOrganizations() {
        return organizations;
    }

    public Map<String, String> getCurrencies() {
        return currencies;
    }

    public Map<String, String> getRegions() {
        return regions;
    }

    public Map<String, String> getCities() {
        return cities;
    }
}
