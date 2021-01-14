package com.app.ui.message;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.app.adapter.SystemNotifyAdapter;
import com.punuo.sys.app.home.model.PostNewModel;
import com.punuo.sys.app.home.request.GetNewsRequest;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = HomeRouter.ROUTER_SYSTEM_NOTIFY_ACTIVITY)
public class SystemNotifyActivity extends BaseSwipeBackActivity {
    private static final String TAG = "SystemNotifyActivity";
    @BindView(R2.id.rv_systemNotify)
    RecyclerView rvSystemNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notify);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSystemNotify.setLayoutManager(layoutManager);
        getData();
    }

    private void getData() {
        GetNewsRequest request = new GetNewsRequest();
        request.setRequestListener(new RequestListener<List<PostNewModel>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(List<PostNewModel> result) {
                if (result != null) {
                    SystemNotifyAdapter adapter = new SystemNotifyAdapter(result);
                    rvSystemNotify.setAdapter(adapter);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }
}
