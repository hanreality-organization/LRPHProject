package com.punuo.sys.app.compat.when_page;


import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.punuo.sys.app.compat.R;
import com.punuo.sys.app.compat.splash.SplashActivity;
import com.punuo.sys.sdk.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：AppWhenThePage
 * package：com.luck.app.page.when_page
 * email：893855882@qq.com
 * data：2017/2/22
 */
public class PageFrameLayout extends FrameLayout implements ViewPager.OnPageChangeListener {
    private List<PageFragment> fragments = new ArrayList<>();
    private ImageView[] iv_vp = null;
    private LinearLayout dot_ll;
    private int dot_on, dot_off;

    public PageFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     * 设置资源文件并初始化控件
     *
     * @param layoutIds
     */
    public void setUpViews(int layoutIds[], int dot_on, int dot_off) {
        this.dot_on = dot_on;
        this.dot_off = dot_off;
        iv_vp = new ImageView[layoutIds.length];
        dot_ll = new LinearLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                CommonUtil.dip2px(35f));
        dot_ll.setGravity(Gravity.CENTER);
        params.gravity = Gravity.BOTTOM;
        dot_ll.setLayoutParams(params);
        dot_ll.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 0, 8, 0);

        for (int i = 0; i < layoutIds.length; i++) {
            PageFragment pageFragment = new PageFragment();
            Bundle args = new Bundle();
            args.putInt("index", i);
            args.putInt("count", layoutIds.length);
            args.putInt("layoutId", layoutIds[i]);

            pageFragment.setArguments(args);
            fragments.add(pageFragment);
            ImageView view = new ImageView(getContext());
            view.setImageResource(dot_on);
            view.setLayoutParams(lp);
            iv_vp[i] = view;
            dot_ll.addView(iv_vp[i]);
        }

        setSelectVp(0);
        SplashActivity activity = (SplashActivity) getContext();
        ViewPager viewPager = new ViewPager(getContext());
        viewPager.setId(R.id.id_page);
        viewPager.setAdapter(new PageFragmentAdapter(activity.getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(this);
        addView(viewPager);
        addView(dot_ll);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == fragments.size() - 1) {
            dot_ll.setAlpha(0);
        } else {
            dot_ll.setAlpha(1);
        }

    }

    @Override
    public void onPageSelected(int position) {
        setSelectVp(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 切换轮播图点
     *
     * @param index
     */
    public void setSelectVp(int index) {
        for (int i = 0; i < iv_vp.length; i++) {
            if (i == index) {
                iv_vp[i].setImageResource(dot_on);
            } else {
                iv_vp[i].setImageResource(dot_off);
            }
        }
    }
}
