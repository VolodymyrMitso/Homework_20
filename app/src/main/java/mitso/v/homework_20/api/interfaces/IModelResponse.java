package mitso.v.homework_20.api.interfaces;

import org.json.JSONException;

import java.text.ParseException;

public interface IModelResponse {

    void configure(Object object) throws JSONException, ParseException;
}

