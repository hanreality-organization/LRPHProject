package com.app.request;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

import okhttp3.MediaType;

public class UploadPhotoRequest extends BaseRequest<PNBaseModel> {
    public UploadPhotoRequest(){
        setRequestType(RequestType.UPLOAD);
        setRequestPath("/addPics");
        contentType(MediaType.parse("multipart/form-data; charset=utf-8"));
    }
}
