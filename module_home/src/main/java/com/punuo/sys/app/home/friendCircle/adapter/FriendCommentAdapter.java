package com.punuo.sys.app.home.friendCircle.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.punuo.sys.app.home.friendCircle.domain.FirstMicroListFriendComment;
import com.punuo.sys.app.home.friendCircle.viewholder.FriendCommentViewHolder;
import com.punuo.sys.sdk.recyclerview.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendCommentAdapter extends BaseRecyclerViewAdapter<FirstMicroListFriendComment> {
    public FriendCommentAdapter(Context context, List<FirstMicroListFriendComment> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendCommentViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendCommentViewHolder) {
            ((FriendCommentViewHolder) baseViewHolder).bind(getItem(position), position);
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
