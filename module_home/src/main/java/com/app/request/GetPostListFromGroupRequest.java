package com.app.request;

import com.punuo.sys.app.home.friendCircle.domain.FriendsMicro;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetPostListFromGroupRequest extends BaseRequest<FriendsMicro> {

    public GetPostListFromGroupRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/posts/getPostListFromGroup");
    }
}
