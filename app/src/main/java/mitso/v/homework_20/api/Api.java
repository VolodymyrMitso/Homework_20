package mitso.v.homework_20.api;

import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.constansts.Constants;

public class Api {

    public final String LOG_TAG = Constants.API_GET_LOG_TAG;

    public void getData(IConnectCallback _callback) {
        Connect.getInstance().getRequest(new JsonData(), _callback);
    }
}
