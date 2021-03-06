package mitso.v.homework_20.api;

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

    private static Connect      sInstance;
    private AsyncHttpClient     mClient;

    public static Connect getInstance() {

        if (sInstance == null) {
            sInstance = new Connect();
            sInstance.mClient = new AsyncHttpClient();
        }

        return sInstance;
    }

    public void getRequest(final IModelResponse _modelResponse, final IConnectCallback _callback) {

        mClient.get(ApiConstants.URL_SERVER, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        parseData(response, _modelResponse, _callback);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        parseData(response, _modelResponse, _callback);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        _callback.onFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        _callback.onFailure(throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        _callback.onFailure(throwable);
                    }
                }
        );
    }

    private void parseData(Object _jsonObject, IModelResponse _modelObject, IConnectCallback _callback) {

        if (null != _modelObject) {
            try {
                _modelObject.configure(_jsonObject);
                _callback.onSuccess(_modelObject);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
