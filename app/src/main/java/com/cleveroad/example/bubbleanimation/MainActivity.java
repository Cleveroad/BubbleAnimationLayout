package com.cleveroad.example.bubbleanimation;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cleveroad.bubbleanimation.BubbleAnimationLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<User> users = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            users.add(new User("Professor Flammarion", "The Outline of Science written by J. Arthur Thomson. Original copyright 1922 by G. P. Putnam's Sons.", "1h ago", "http://res.freestockphotos.biz/pictures/13/13376-vintage-portrait-of-professor-flammarion-pv.jpg"));
            users.add(new User("Girl of Chin Hills", "The girls of the Chin Hills, in Upper Burma — southwest of the Triangle", "10h ago", "https://c2.staticflickr.com/8/7312/9517444900_fb77596c04_b.jpg"));
            users.add(new User("Cat", "", "10 Mar 2016", "http://photos1.blogger.com/blogger/1708/509/1024/100_2219.jpg"));
            users.add(new User("Professor Charles Richet", "The Outline of Science written by J. Arthur Thomson. Original copyright 1922 by G. P. Putnam's Sons.", "10 Mar 2016", "http://res.freestockphotos.biz/pictures/13/13375-vintage-portrait-of-professor-charles-richet-pv.jpg"));
            users.add(new User("Old man", "", "9 Mar 2016", "https://farm6.staticflickr.com/5169/5335482322_7e642957df_o.jpg"));
            users.add(new User("Girl", "", "9 Mar 2016", "https://c5.staticflickr.com/1/734/22995131204_f402568006_b.jpg"));
            users.add(new User("Some man", "", "8 Mar 2016", "https://static.pexels.com/photos/35183/people-homeless-man-male.jpg"));
        }

        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.rv_users);
        rvUsers.setHasFixedSize(true);
        final NonScrollLinearLayoutManager layoutManager = new NonScrollLinearLayoutManager(this);
        rvUsers.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new UsersAdapter(this, users, new OnUserStateChangeListener() {
            @Override
            public void userStateChanged(boolean state) {
                layoutManager.mCanScroll = state;
            }
        });
        rvUsers.setAdapter(adapter);
    }

    private static class NonScrollLinearLayoutManager extends LinearLayoutManager {

        private boolean mCanScroll = true;

        public NonScrollLinearLayoutManager(Context context) {
            super(context);
        }

        @SuppressWarnings("unused")
        public NonScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @SuppressWarnings("unused")
        public NonScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean canScrollHorizontally() {
            return super.canScrollHorizontally() && mCanScroll;
        }

        @Override
        public boolean canScrollVertically() {
            return super.canScrollVertically() && mCanScroll;
        }
    }

    private static class UsersAdapter extends RecyclerView.Adapter<UserHolder> {

        @NonNull
        List<User> mUsers;
        LayoutInflater mInflater;
        @Nullable
        OnUserStateChangeListener mChangeListener;

        public UsersAdapter(Context context, @NonNull List<User> users, @Nullable OnUserStateChangeListener changeListener) {
            mUsers = users;
            mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            mChangeListener = changeListener;
        }

        @Override
        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserHolder(mInflater.inflate(R.layout.list_item_user, parent, false), mChangeListener);
        }

        @Override
        public void onBindViewHolder(UserHolder holder, int position) {
            User user = mUsers.get(position);
            holder.mBalBaseView.setTag(user);
            holder.mTvName.setTag(user);
            holder.mTvDescription.setTag(user);
            holder.mTvDate.setTag(user);
            holder.mTvName.setText(user.mName);
            holder.mTvDescription.setText(user.mDescription);
            holder.mTvDate.setText(user.mDate);
            Glide.with(holder.mIvAvatar.getContext())
                    .load(user.mAvatarUrl)
                    .into(holder.mIvAvatar);
            if (user.mChecked) {
                holder.mBalBaseView.showContextView();
            } else {
                holder.mBalBaseView.showBaseView();
            }
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }
    }

    private static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        BubbleAnimationLayout mBalBaseView;
        TextView mTvName;
        TextView mTvDescription;
        TextView mTvDate;
        ImageView mIvAvatar;
        View mIvCall;
        View mIvWrite;
        View mIvFavorite;

        OnUserStateChangeListener mChangeListener;

        public UserHolder(View itemView, OnUserStateChangeListener changeListener) {
            super(itemView);
            mBalBaseView = (BubbleAnimationLayout) itemView;
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            mTvDate = (TextView) itemView.findViewById(R.id.tv_date);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mIvWrite = itemView.findViewById(R.id.iv_write);
            mIvWrite.setOnClickListener(this);
            mIvCall = itemView.findViewById(R.id.iv_call);
            mIvCall.setOnClickListener(this);
            mIvFavorite = itemView.findViewById(R.id.iv_favorite);
            mIvFavorite.setOnClickListener(this);
            mBalBaseView.addAnimationEndListener(new BubbleAnimationLayout.BubbleAnimationEndListener() {
                @Override
                public void onEndAnimation(boolean isForwardAnimation, Animator animation) {
                    mIvWrite.setVisibility(View.VISIBLE);
                    mIvCall.setVisibility(View.VISIBLE);
                    mIvFavorite.setVisibility(View.VISIBLE);
                    mIvWrite.setAlpha(1.0f);
                    mIvCall.setAlpha(1.0f);
                    mIvFavorite.setAlpha(1.0f);
                    mIvWrite.setRotation(0.0f);
                    mIvCall.setRotation(0.0f);
                    mIvFavorite.setRotation(0.0f);
                    User user = (User) mBalBaseView.getTag();
                    user.mChecked = isForwardAnimation;
                }
            });
            mChangeListener = changeListener;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_write: {
                    User user = (User) mBalBaseView.getTag();
                    mIvCall.setVisibility(View.INVISIBLE);
                    mIvFavorite.setVisibility(View.INVISIBLE);
                    pressContextItem(view, 360.0f, view.getContext().getString(R.string.write), user);
                    break;
                }
                case R.id.iv_call: {
                    User user = (User) mBalBaseView.getTag();
                    mIvWrite.setVisibility(View.INVISIBLE);
                    mIvFavorite.setVisibility(View.INVISIBLE);
                    pressContextItem(view, 137.0f, view.getContext().getString(R.string.call), user);
                    break;
                }
                case R.id.iv_favorite: {
                    User user = (User) mBalBaseView.getTag();
                    mIvCall.setVisibility(View.INVISIBLE);
                    mIvWrite.setVisibility(View.INVISIBLE);
                    pressContextItem(view, 360.0f, view.getContext().getString(R.string.favorite), user);
                    break;
                }
            }
        }

        private void pressContextItem(final View view, final float rotation, final String elementName, final User user) {
            if (mChangeListener != null) {
                mChangeListener.userStateChanged(false);
            }
            view.setBackgroundResource(R.drawable.circle_red);
            mBalBaseView.hideBubbledViewWithAnimation(new BubbleAnimationLayout.BubbleAnimationEndListener() {
                @Override
                public void onEndAnimation(final boolean isForwardAnimation, Animator animation) {
                    view.animate().rotation(rotation)
                            .setDuration(500)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    user.mChecked = false;
                                    mBalBaseView.resetView();
                                    if (mChangeListener != null) {
                                        mChangeListener.userStateChanged(true);
                                    }
                                    CallActivity.start((MainActivity) mBalBaseView.getContext(), view, elementName, user);
                                }
                            });
                }
            });
        }
    }

    private interface OnUserStateChangeListener {
        void userStateChanged(boolean state);
    }

    public static class User implements Parcelable {
        public static final Creator<User> CREATOR = new Creator<User>() {
            @Override
            public User createFromParcel(Parcel in) {
                return new User(in);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };
        private String mName;
        private String mDescription;
        private String mDate;
        private String mAvatarUrl;
        private boolean mChecked;

        public User(String name, String description, String date, String avatarUrl) {
            mName = name;
            mDescription = description;
            mDate = date;
            mAvatarUrl = avatarUrl;
        }

        protected User(Parcel in) {
            mName = in.readString();
            mDescription = in.readString();
            mDate = in.readString();
            mAvatarUrl = in.readString();
            mChecked = in.readByte() != 0;
        }

        public String getAvatarUrl() {
            return mAvatarUrl;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mName);
            parcel.writeString(mDescription);
            parcel.writeString(mDate);
            parcel.writeString(mAvatarUrl);
            parcel.writeByte((byte) (mChecked ? 1 : 0));
        }
    }
}
