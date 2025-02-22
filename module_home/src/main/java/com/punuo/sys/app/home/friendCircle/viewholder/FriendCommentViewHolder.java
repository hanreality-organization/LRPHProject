package com.punuo.sys.app.home.friendCircle.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.R;
import com.app.R2;
import com.punuo.sys.app.home.friendCircle.domain.FirstMicroListFriendComment;
import com.punuo.sys.sdk.recyclerview.BaseViewHolder;
import com.punuo.sys.sdk.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendCommentViewHolder extends BaseViewHolder<FirstMicroListFriendComment> {
    @BindView(R2.id.friend_name)
    TextView mFriendName;
    @BindView(R2.id.friend_comment)
    TextView mFriendComment;

    public FriendCommentViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.friend_commit_item, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void bindData(FirstMicroListFriendComment data, int position) {
        ViewUtil.setText(mFriendName, data.replyName + ":");
        ViewUtil.setText(mFriendComment, data.comment);
    }
}
