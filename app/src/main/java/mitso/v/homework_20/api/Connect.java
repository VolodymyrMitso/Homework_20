package mitso.v.homework_20.api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.interfaces.IModelResponse;

public class Connect {

    private final String LOG_TAG = "CONNECT_CLASS_LOG_TAG";

    private static Connect      sInstance;
    private AsyncHttpClient     mClient;

    public static Connect getInstance() {

        if (sInstance == null) {
            sInstance = new Connect();
            sInstance.mClient = new AsyncHttpClient();
        }

        return sInstance;
    }

    public void getRequest(final IModelResponse modelResponse, final IConnectCallback callback) {

        Log.e(LOG_TAG, ApiConstants.URL_SERVER);

        mClient.get(ApiConstants.URL_SERVER, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        parseData(response, modelResponse, callback);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        parseData(response, modelResponse, callback);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        callback.onFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        callback.onFailure(throwable);
                    }
                }
        );
    }

    private void parseData(Object jsonObject, IModelResponse modelObject, IConnectCallback callback) {

        if (null != modelObject) {
            try {
                modelObject.configure(jsonObject);
                callback.onSuccess(modelObject);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
