package com.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.SipURL;


/**
 * Created by maojianhui on 2018/6/27.
 * 双向视频第一个页面
 */

public class VideoDial extends BaseActivity implements View.OnClickListener{
    private SoundPool soundPool;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private int streamId;

    private String TAG;
    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    String avaPath = SdCard + "/fanxin/Files/Camera/Images/";

    String devId = SipInfo.paddevId;
    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_call1);

        EventBus.getDefault().register(this);
        initview();
        soundPool=new SoundPool(10, android.media.AudioManager.STREAM_MUSIC,5);
        final int sourceid=soundPool.load(this,R.raw.videowait,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                    public void onLoadComplete(
                            SoundPool soundPool,
                            int sampleId, int status) {
                        // TODO Auto-generated method stub
                        Log.d("xx", "11");
                        streamId=soundPool.play(sourceid, 1, 1, 0,
                                4, 1);
                    }
                });

        //改变状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }
      public void stopSound(int id){
        soundPool.stop(id);
    }

    public void initview(){
        pref= PreferenceManager.getDefaultSharedPreferences(this);

        String vatar_temp=pref.getString("name","");
        ImageView hangup=(ImageView)findViewById(R.id.iv_hangup);
        hangup.setOnClickListener(this);
        TextView quxiao=(TextView)findViewById(R.id.quxiao);
        quxiao.setVisibility(View.VISIBLE);
        TextView tv_videostaus=(TextView)findViewById(R.id.tv_videostaus);
        tv_videostaus.setText("正在等待对方接受邀请...");
        tv_videostaus.setVisibility(View.VISIBLE);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("取消")) {
            Log.i(TAG, "message is " + event.getMessage());
            // 更新界面
//            stopSound(streamId);
            finish();
        }else if(event.getMessage().equals("开始视频")){
            finish();
        }
        else if(event.getMessage().equals("忙线中")) {
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("对方忙...");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }
    }
    @Override
    protected void onDestroy() {
        stopSound(streamId);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.iv_hangup) {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("cancel", AccountManager.getBindDevId());
            SipUserManager.getInstance().addRequest(replyRequest);
            finish();
        }

    }


}
