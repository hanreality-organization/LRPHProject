package com.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.friendCircleMain.domain.Alldevid;
import com.app.friendCircleMain.domain.Group;
import com.app.friendCircleMain.domain.UserFromGroup;
import com.app.friendCircleMain.domain.UserList;
import com.app.groupvoice.GroupInfo;
import com.app.model.Constant;
import com.app.model.Friend;
import com.app.model.PNUserInfo;
import com.app.request.GetAllGroupFromUserRequest;
import com.app.request.GetAllUserFromGroupRequest;
import com.app.request.GetDevIdFromIdRequest;
import com.app.sip.SipInfo;
import com.app.tools.PermissionUtils;
import com.app.views.CleanEditText;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.event.UserLoginFailEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.home.HomeActivity;
import com.punuo.sys.app.home.login.BaseSwipeBackLoginActivity;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.CommonUtil;
import com.punuo.sys.sdk.util.MMKVUtil;
import com.punuo.sys.sdk.util.StatusBarUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 用户登陆页
 */
@Route(path = HomeRouter.ROUTER_LOGIN_ACTIVITY)
public class LoginActivity extends BaseSwipeBackLoginActivity {
    private static final String TAG = "LoginActivity";
    private String groupname;
    private String groupid;
    private String appdevid;
    private Handler handler = new Handler();
    //网络连接失败窗口
    private AlertDialog newWorkConnectedDialog;
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    public static List<Activity> activityList = new LinkedList();

    @BindView(R2.id.num_input2)
    CleanEditText numInput2;
    @BindView(R2.id.password_input)
    CleanEditText passwordInput;
    @BindView(R2.id.hidepassword)
    ImageView hidepassword;
    @BindView(R2.id.vericode_login)
    TextView vericodeLogin;
    @BindView(R2.id.password_forget)
    TextView passwordForget;
    @BindView(R2.id.btn_login1)
    TextView loginBtn;
    @BindView(R2.id.tv_register)
    TextView tvRegister;
    @BindView(R2.id.layout_root)
    RelativeLayout layoutRoot;

    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        LoginActivity.activityList.add(this);
        boolean showed = MMKVUtil.getBoolean("privacy_dialog_showed", false);
        if (!showed) {
            showAboutDialog();
        }
        setUpSplash();
        initViews();
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
        View statusBar = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(this);
            statusBar.requestLayout();
        }
        EventBus.getDefault().register(this);
    }

    private void setUpSplash() {
        Subscription splash = Observable.timer(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> requestPermission());
        mCompositeSubscription.add(splash);
    }

    //检查网络是否连接
    public boolean isNetworkReachable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            SipInfo.isNetworkConnected = false;
        } else {
            SipInfo.isNetworkConnected = info.getState() == NetworkInfo.State.CONNECTED;
        }
        return SipInfo.isNetworkConnected;
    }

    public void showAboutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("服务协议和隐私政策");
        title.setPadding(0, CommonUtil.dip2px(12f), 0, CommonUtil.dip2px(12f));
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.common_title_bg));
        dialog.setCustomTitle(title);
        TextView content = new TextView(this);
        content.setPadding(CommonUtil.dip2px(12f), 0, CommonUtil.dip2px(12f), 0);
        content.setText(Html.fromHtml(
                "请你务必审慎阅读.充分理解“服务协议”和“隐私政策”各条款，包括但不限于:为了向你提供即时通讯、内容分享等服务，我们需要收集你的设备信息、操作日志等个人信息。你可以在“设置”中查看，变更，删除个人信息并管理你的授权你可阅读<a href=\"http://feeder.mengshitech.com/test/userPolicy.html\">《服务协议》</a>和<a href=\"http://feeder.mengshitech.com/test/privacy.html\">《隐私政策》</a>了解详细信息。如你同意，请点击同意开始接受我们的服务。"));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        dialog.setView(content);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        dialog.show();
        MMKVUtil.setBoolean("privacy_dialog_showed", true);
    }

    public void exit() {
        for (Activity act : activityList) {
            act.finish();
        }
        System.exit(0);

    }

    private void initViews() {
        numInput2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numInput2.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        numInput2.setText(AccountManager.getUserAccount());

        passwordInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordInput.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO) {
                clickLogin();
            }
            return false;
        });
        passwordInput.setText(AccountManager.getPassword());
        SipInfo.localSdCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/faxin/";
        isNetworkReachable();
    }

    private void clickLogin() {
        CharSequence userAccount = numInput2.getText();
        CharSequence passWord = passwordInput.getText();
        if (checkPhoneNumber(userAccount) && checkPassword(passWord)) {
            AccountManager.setUserAccount(userAccount);
            AccountManager.setPassword(passWord);
            showLoadingDialog();
            getUserId();
        }
    }


    //获取用户信息
    private void getUserInfo() {
        RequestListener<PNUserInfo> listener = new RequestListener<PNUserInfo>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNUserInfo result) {
                if (result != null && result.isSuccess()) {
                    UserInfoManager.setUserInfo(result.userInfo);
                    SipInfo.friends.clear();
                    getGroupInfo();
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户数据失败请重试");
                dismissLoadingDialog();
            }
        };
        UserInfoManager.getInstance().refreshUserInfo(listener);
    }

    //群组获取线程
    private GetAllGroupFromUserRequest mGetAllGroupFromUserRequest;

    //获取组信息
    private void getGroupInfo() {
        if (mGetAllGroupFromUserRequest != null && !mGetAllGroupFromUserRequest.isFinish()) {
            return;
        }
        groupname = null;
        groupid = null;
        appdevid = null;
        mGetAllGroupFromUserRequest = new GetAllGroupFromUserRequest();
        mGetAllGroupFromUserRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetAllGroupFromUserRequest.setRequestListener(new RequestListener<Group>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Group result) {
                if (result != null) {
                    if (result.groupList != null && !result.groupList.isEmpty()) {
                        //重构疑问：这个循环的意义
                        for (int i = 0; i < result.groupList.size(); i++) {
                            groupname = result.groupList.get(i).getGroup_name();
                            groupid = result.groupList.get(i).getGroupid();
                        }
                        Constant.devid1 = groupname;
                        Constant.groupid1 = groupid;
                        Constant.groupid = Constant.groupid1;
                        if (!TextUtils.isEmpty(Constant.groupid1)) {
                            SipInfo.paddevId = Constant.devid1;
                            getDevIdInfo();
                        } else {
                            dismissLoadingDialog();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                    } else {
                        dismissLoadingDialog();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户数据失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetAllGroupFromUserRequest);
    }

    private GetAllUserFromGroupRequest mGetAllUserFromGroupRequest;

    //获取组内用户
    private void getAllUserFormGroup() {
        if (mGetAllUserFromGroupRequest != null && !mGetAllUserFromGroupRequest.isFinish()) {
            return;
        }
        mGetAllUserFromGroupRequest = new GetAllUserFromGroupRequest();
        mGetAllUserFromGroupRequest.addUrlParam("groupid", Constant.groupid);
        mGetAllUserFromGroupRequest.setRequestListener(new RequestListener<UserFromGroup>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(UserFromGroup result) {
                if (result != null) {
                    if (result.userList != null && !result.userList.isEmpty()) {
                        for (int i = 0; i < result.userList.size(); i++) {
                            UserList userList = result.userList.get(i);
                            Friend friend = new Friend();
                            friend.setNickName(userList.getNickname());
                            friend.setPhoneNum(userList.getName());
                            friend.setUserId(userList.getUserid());
                            friend.setId(userList.getId());
                            friend.setAvatar(userList.getAvatar());
                            SipInfo.friends.add(friend);
                        }

                        GroupInfo.groupNum = "7000";
//                        String peer = peerElement.getFirstChild().getNodeValue();
                        GroupInfo.ip = "101.69.255.134";
//                        GroupInfo.port = 7000;
                        GroupInfo.level = "1";
                        SipInfo.devName = UserInfoManager.getUserInfo().nickname;
                        dismissLoadingDialog();
//                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        ToastUtils.showToastShort("获取用户数据失败请重试");
                        dismissLoadingDialog();
                    }
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetAllUserFromGroupRequest);
    }

    private GetDevIdFromIdRequest mGetDevIdFromIdRequest;

    //获取用户设备信息
    private void getDevIdInfo() {
        if (mGetDevIdFromIdRequest != null && !mGetDevIdFromIdRequest.isFinish()) {
            return;
        }
        mGetDevIdFromIdRequest = new GetDevIdFromIdRequest();
        mGetDevIdFromIdRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        Log.i(TAG, "getUserId" + UserInfoManager.getUserInfo().id);
        mGetDevIdFromIdRequest.setRequestListener(new RequestListener<Alldevid>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Alldevid result) {
                if (result != null) {
                    List<String> devIdLists = result.devid;
                    if (devIdLists.isEmpty()) {
//                        ToastUtils.showToast("获取设备id失败");
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        appdevid = devIdLists.get(0);
                        Log.i(TAG, "appdevid" + appdevid);
                        Constant.appdevid1 = appdevid;
                        //TODO 设备注册
                        if (!TextUtils.isEmpty(appdevid)) {
//                            SipInfo.devId = appdevid;
//                            Log.i(TAG, "SipInfodevid"+SipInfo.devId);
//                            SipURL local_dev = new SipURL(SipInfo.devId, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
//                            SipInfo.dev_from = new NameAddress(SipInfo.devId, local_dev);
//                            new Thread(devConnecting).start();
                            AccountManager.setDevId(appdevid);
                            registerDev();
                        }
                        //获取组内用户
                        getAllUserFormGroup();
                    }
                } else {
//                    ToastUtils.showToast("获取用户devid失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
//                ToastUtils.showToastShort("获取用户devid失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetDevIdFromIdRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R2.id.num_input2, R2.id.password_input, R2.id.hidepassword,
            R2.id.vericode_login, R2.id.password_forget, R2.id.btn_login1, R2.id.tv_register})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login1) {
            clickLogin();
        } else if (id == R.id.hidepassword) {
            if (passwordInput.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                hidepassword.setImageResource(R.drawable.ic_eye);
                passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else if (passwordInput.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                hidepassword.setImageResource(R.drawable.ic_hide);
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (id == R.id.vericode_login) {
            ARouter.getInstance().build(HomeRouter.ROUTER_VERIFY_CODE_LOGIN_ACTIVITY)
                    .navigation();
        } else if (id == R.id.password_forget) {
            ARouter.getInstance().build(HomeRouter.ROUTER_FORGET_PASSWORD_ACTIVITY)
                    .navigation();
        } else if (id == R.id.tv_register) {
            ARouter.getInstance().build(HomeRouter.ROUTER_REGISTER_ACTIVITY)
                    .navigation();
        }
    }

    private PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {

        }

        @Override
        public void onPermissionCancel() {
            Toast.makeText(LoginActivity.this, getString(R.string.alirtc_permission), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void requestPermission() {
        PermissionUtils.requestMultiPermissions(this,
                new String[]{
                        PermissionUtils.PERMISSION_CAMERA,
                        PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PermissionUtils.PERMISSION_RECORD_AUDIO,
                        PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, mGrant);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.CODE_MULTI_PERMISSION) {
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mGrant);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE_SETTING) {
            new Handler().postDelayed(this::requestPermission, 500);

        }
    }
    /** sip注册相关该页面只进行sip的注册不启动心跳包 心跳包在
     * {@link com.punuo.sys.app.home.HomeActivity}
     * 上开启
     */
    /**
     * 用户注册第一步
     */
    private void getUserId() {
        SipGetUserIdRequest getUserIdRequest = new SipGetUserIdRequest();
        SipUserManager.getInstance().addRequest(getUserIdRequest);
    }

    /**
     * 设备注册第一步
     */
    private void registerDev() {
        SipDevRegisterRequest sipDevRegisterRequest = new SipDevRegisterRequest();
        SipDevManager.getInstance().addRequest(sipDevRegisterRequest);
    }

    /**
     * 用户注册成功
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final LoginResponseUser model) {
        getUserInfo();
    }

    /**
     * 设备注册成功
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final LoginResponseDev model) {
        ARouter.getInstance().build(HomeRouter.ROUTER_HOME_ACTIVITY)
                .navigation();
        finish();
    }

    /**
     * 设备Sip服务重新注册事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterDevEvent event) {
        if (devLoginFailed) {
            devLoginFailed = false;
            return;
        }
        registerDev();
    }

    /**
     * 设备Sip服务注册失败事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DevLoginFailEvent event) {
        devLoginFailed = true;
    }

    /**
     * 用户Sip服务重新注册事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterUserEvent event) {
        if (userLoginFailed) {
            userLoginFailed = false;
            return;
        }
        getUserId();
    }

    /**
     * 用户Sip服务注册失败事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserLoginFailEvent event) {
        userLoginFailed = true;
    }
    /** sip注册相关该页面只进行sip的注册不启动心跳包 心跳包在
     * {@link com.punuo.sys.app.home.HomeActivity}
     * 上开启
     */
}
