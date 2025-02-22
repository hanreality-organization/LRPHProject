package com.punuo.sys.app.home.account;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.views.CleanEditText;
import com.punuo.sys.sdk.router.HomeRouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.SMSSDK;

/**
 * 密码重置页
 */
@Route(path = HomeRouter.ROUTER_FORGET_PASSWORD_ACTIVITY)
public class ForgetPasswordActivity extends BaseSwipeBackLoginActivity {
    private static final String TAG = "ForgetPasswordActivity";
    private CharSequence userAccount;

    @BindView(R2.id.num_input3)
    CleanEditText numInput3;
    @BindView(R2.id.verificode_input1)
    CleanEditText verificodeInput1;
    @BindView(R2.id.verificode_get)
    TextView verificodeGet;
    @BindView(R2.id.btn_nextstep)
    TextView btnNextstep;
    @BindView(R2.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        targetView = verificodeGet;
        initViews();

    }

    @Override
    public void onVerifyCodeSuccess() {
        ARouter.getInstance().build(HomeRouter.ROUTER_APPLY_PASSWORD_ACTIVITY)
                .withString("userAccount", userAccount.toString())
                .navigation();
        finish();
    }

    private void initViews() {
        numInput3.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        verificodeInput1.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        verificodeGet.setOnClickListener(v -> {
            userAccount = numInput3.getText();
            if (checkPhoneNumber(userAccount)) {
               getVerifyCode(userAccount);
            }
        });
        btnNextstep.setOnClickListener(v -> {
            userAccount = numInput3.getText();
            CharSequence code = verificodeInput1.getText();
            if (checkPhoneNumber(userAccount) && checkCode(code)) {
                SMSSDK.submitVerificationCode("86", userAccount.toString(), code.toString());
            }
        });
        ivBack.setOnClickListener(v->{
            scrollToFinishActivity();
        });
    }
}
