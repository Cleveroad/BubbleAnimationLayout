package com.cleveroad.example.bubbleanimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class CallActivity extends AppCompatActivity {

    private static final String EXTRA_ELEMENT_ID = "element_name";
    private static final String EXTRA_USER = "user";

    public static void start(Activity activity, View view, String elementName, MainActivity.User user) {
        Intent intent = new Intent(activity, CallActivity.class);
        intent.putExtra(EXTRA_ELEMENT_ID, elementName);
        intent.putExtra(EXTRA_USER, user);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, elementName);
        activity.startActivity(intent, options.toBundle());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        String elementName = getIntent().getStringExtra(EXTRA_ELEMENT_ID);
        if (getString(R.string.call).equals(elementName)) {
            findViewById(R.id.iv_call).setBackgroundResource(R.drawable.circle_red);
        } else if (getString(R.string.write).equals(elementName)) {
            findViewById(R.id.iv_write).setBackgroundResource(R.drawable.circle_red);
        } else if (getString(R.string.favorite).equals(elementName)) {
            findViewById(R.id.iv_favorite).setBackgroundResource(R.drawable.circle_red);
        }
        MainActivity.User user = getIntent().getParcelableExtra(EXTRA_USER);
        Glide.with(this)
                .load(user.getAvatarUrl())
                .into((ImageView) findViewById(R.id.iv_avatar));
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.iv_call).setTransitionName(null);
        findViewById(R.id.iv_write).setTransitionName(null);
        findViewById(R.id.iv_favorite).setTransitionName(null);
        super.onBackPressed();
    }
}
