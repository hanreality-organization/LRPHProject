package com.punuo.sip;

import com.punuo.sip.user.model.MediaData;
import com.punuo.sip.user.model.QueryResponse;

/**
 * Created by han.chen.
 * Date on 2019-09-18.
 **/
public class H264Config {
    /**
     * video width
     */
    public static int VIDEO_WIDTH = 640;

    /**
     * video height
     */
    public static int VIDEO_HEIGHT = 480;

    /**
     * video frame rate
     */
    public static int FRAME_RATE = 10;
    /**
     * video type
     */
    public static int VIDEO_TYPE = 2;
    /**
     * Default video rtmp address
     */
    public static String RTMP_STREAM = "rtmp://101.69.255.130:1936/hls/310023005801930001";

    public static String rtpIp = "101.69.255.134";
    public static int rtpPort;
    public static byte[] magic;

    public static void initQueryData(QueryResponse queryData) {

    }

    public static void initMediaData(MediaData mediaData) {
        rtpIp = mediaData.getIp();
        rtpPort = mediaData.getPort();
        magic = mediaData.getMagic();
    }

    public static byte[] getMagic() {
        return magic;
    }
}
