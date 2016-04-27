package mitso.v.homework_20.api;

import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.json.JsonData;

public class Api {

    public final String LOG_TAG = "API_GET_LOG_TAG";

    public void getData(IConnectCallback callback) {
        Connect.getInstance().getRequest(new JsonData(), callback);
    }
}
