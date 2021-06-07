package com.punuo.sip.user.request;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

public class SipOnLineRequest extends BaseUserSipRequest {
    public SipOnLineRequest(){
        setSipRequestType(SipRequestType.Notify);
        setTargetResponse("is_online_response");
    }

    @Override
    public String getBody(){
        JSONObject body = new JSONObject();
        try {
            body.put("is_online", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
