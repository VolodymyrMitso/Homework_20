package mitso.v.homework_20.api.models.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import mitso.v.homework_20.api.ApiConstants;
import mitso.v.homework_20.api.interfaces.IModelResponse;

public class JsonOrganization implements Serializable, IModelResponse {

    private String                  id;
    private Integer                 oldId;
    private Integer                 orgType;
    private Boolean                 branch;
    private String                  title;
    private String                  regionId;
    private String                  cityId;
    private String                  phone;
    private String                  address;
    private String                  link;
    private ArrayList<JsonCurrency> currencies;

    @Override
    public void configure(Object _object) throws JSONException, ParseException {

        JSONObject jsonObject = (JSONObject) _object;

        if (jsonObject.has(ApiConstants.ORGANIZATION_ID_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_ID_KEY))
            id = jsonObject.getString(ApiConstants.ORGANIZATION_ID_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_OLD_ID_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_OLD_ID_KEY))
            oldId = jsonObject.getInt(ApiConstants.ORGANIZATION_OLD_ID_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_ORG_TYPE_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_ORG_TYPE_KEY))
            orgType = jsonObject.getInt(ApiConstants.ORGANIZATION_ORG_TYPE_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_BRANCH_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_BRANCH_KEY))
            branch = jsonObject.getBoolean(ApiConstants.ORGANIZATION_BRANCH_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_TITLE_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_TITLE_KEY))
            title = jsonObject.getString(ApiConstants.ORGANIZATION_TITLE_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_REGION_ID_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_REGION_ID_KEY))
            regionId = jsonObject.getString(ApiConstants.ORGANIZATION_REGION_ID_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_CITY_ID_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_CITY_ID_KEY))
            cityId = jsonObject.getString(ApiConstants.ORGANIZATION_CITY_ID_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_PHONE_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_PHONE_KEY))
            phone = jsonObject.getString(ApiConstants.ORGANIZATION_PHONE_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_ADDRESS_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_ADDRESS_KEY))
            address = jsonObject.getString(ApiConstants.ORGANIZATION_ADDRESS_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_LINK_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_LINK_KEY))
            link = jsonObject.getString(ApiConstants.ORGANIZATION_LINK_KEY);

        if (jsonObject.has(ApiConstants.ORGANIZATION_CURRENCIES_KEY) && !jsonObject.isNull(ApiConstants.ORGANIZATION_CURRENCIES_KEY)) {
            JSONObject nestedJsonObject = jsonObject.getJSONObject(ApiConstants.ORGANIZATION_CURRENCIES_KEY);
            currencies = new ArrayList<>();

            Iterator<?> keys = nestedJsonObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject currencyNestedJsonObject = nestedJsonObject.getJSONObject(key);

                JsonCurrency jsonCurrency = new JsonCurrency();
                jsonCurrency.setName(key);

                if (currencyNestedJsonObject.has(ApiConstants.CURRENCY_ASK_KEY) && !currencyNestedJsonObject.isNull(ApiConstants.CURRENCY_ASK_KEY))
                    jsonCurrency.setAsk(currencyNestedJsonObject.getString(ApiConstants.CURRENCY_ASK_KEY));

                if (currencyNestedJsonObject.has(ApiConstants.CURRENCY_BID_KEY) && !currencyNestedJsonObject.isNull(ApiConstants.CURRENCY_BID_KEY))
                    jsonCurrency.setBid(currencyNestedJsonObject.getString(ApiConstants.CURRENCY_BID_KEY));

                currencies.add(jsonCurrency);
            }
        }
    }

    @Override
    public String toString() {
        String result = "   ORGANIZATION INFO:\n" +
                        "   id = " + id + "\n" +
                        "   oldId = " + oldId + "\n" +
                        "   orgType = " + orgType + "\n" +
                        "   branch = " + branch + "\n" +
                        "   title = " + title + "\n" +
                        "   regionId = " + regionId + "\n" +
                        "   cityId = " + cityId + "\n" +
                        "   phone = " + phone + "\n" +
                        "   address = " + address + "\n" +
                        "   link = " + link + "\n" +
                        "   currencies = \n";
        for (int i = 0; i < currencies.size(); i++)
            result += currencies.get(i).toString();

        return result;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRegionId() {
        return regionId;
    }

    public String getCityId() {
        return cityId;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getLink() {
        return link;
    }

    public ArrayList<JsonCurrency> getCurrencies() {
        return currencies;
    }
}
