package com.gess.textvideo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.gess.textvideo.TextVideoEditActivity.EDIT_CONTENT;
import static com.gess.textvideo.TextVideoEditActivity.REQUEST;
import static com.gess.textvideo.TextVideoEditActivity.RESULT;

public class MainActivity extends AppCompatActivity {

    private SimpleScaleView scrollView;
    private RelativeLayout ll_texts;
    private TextView edit;
    private View selectView;
    private Set<TextVideoBean> textVideoBeans;
    private int duration = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        scrollView = findViewById(R.id.vssv);
        scrollView.setMax(duration);//单位秒
        ll_texts = findViewById(R.id.ll_texts);
        addText();
    }

    public void addText() {
        final View view = View.inflate(this, R.layout.rl_text_item, null);
        addTextVideo(view);
        ImageView tvAdd = view.findViewById(R.id.tv_add);
        ImageView tvDelete = view.findViewById(R.id.tv_delete);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectView = view;
                addText();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_texts.getChildCount() >= 2) {
                    ll_texts.removeView(view);
                } else {
                    Toast.makeText(MainActivity.this, "文字视频至少有一段文字", Toast.LENGTH_LONG).show();
                }
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            private int startY;
            private long downTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下时的时间
                        downTime = System.currentTimeMillis();
                        //获取当前按下的坐标
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        //获取移动后的坐标
                        int moveY = (int) event.getRawY();
                        //拿到手指移动距离的大小
                        int move_bigY = moveY - startY;
                        if (System.currentTimeMillis() - downTime > 300) {
                            view.findViewById(R.id.tv_text).setBackground(getResources().getDrawable(R.drawable.bg_textvideo_red));
                            if ((view.getTop() - (view.getHeight() / 2) + move_bigY) <= -(view.getHeight() / 2)
                                    || (view.getBottom() + (view.getHeight() / 2) + move_bigY >= ll_texts.getHeight() + (view.getHeight() / 2))) {
                            } else {
                                view.offsetTopAndBottom(move_bigY);
                            }
                            computeMoment(view);
                        }
                        LogUtils.d(SimpleScaleView.TAG, "move_bigY = " + move_bigY);
                        startY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downTime < 100) {
                            startActivityForResult(new Intent(MainActivity.this, TextVideoEditActivity.class), REQUEST);
                            edit = view.findViewById(R.id.tv_text);
                        }
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        compute(view);
                        //重新定位
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = view.getLeft();
                        layoutParams.topMargin = view.getTop();
                        view.setLayoutParams(layoutParams);
                        view.findViewById(R.id.tv_text).setBackground(getResources().getDrawable(R.drawable.bg_textvideo_whilt));
                        break;
                }
                return true;//此处一定要返回true，否则监听不生效
            }
        });
        //添加位置确定
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(32));
        if (selectView != null) {
            if ((selectView.getBottom() + SizeUtils.dp2px(16)) >= (scrollView.getHeight() - SizeUtils.dp2px(8))) {
                layoutParams.topMargin = selectView.getTop() - selectView.getHeight() / 2;
            } else {
                layoutParams.topMargin = selectView.getTop() + selectView.getHeight() / 2;
            }
        }
        ll_texts.addView(view, layoutParams);
    }

    private void compute(View view) {
        TextVideoBean videoBean = getTextVideo(view.findViewById(R.id.tv_text).getTag().toString());
        long moment = (view.getTop() + view.getHeight() / 2) * duration * 1000 / ll_texts.getHeight();
        LogUtils.d(SimpleScaleView.TAG, "时刻 = " + moment);
        videoBean.setMoment(moment);
    }

    private void computeMoment(View view) {
        long moment = (view.getTop() + view.getHeight() / 2) * duration * 1000 / ll_texts.getHeight();
        float tvMoment = (float) moment / 1000f;
        ((TextView) findViewById(R.id.tv_time)).setText(tvMoment + "s");
    }

    private void addTextVideo(View view) {
        String forever = UUID.randomUUID().toString().replace("-", "");
        view.findViewById(R.id.tv_text).setTag(forever);
        if (textVideoBeans == null) {
            textVideoBeans = new HashSet<>();
        }
        TextVideoBean videoBean = new TextVideoBean();
        videoBean.setId(forever);
        videoBean.setContent("");
        videoBean.setMoment(0);
        textVideoBeans.add(videoBean);
    }

    private TextVideoBean getTextVideo(String id) {
        if (textVideoBeans != null && !textVideoBeans.isEmpty()) {
            for (TextVideoBean videoBean : textVideoBeans) {
                if (id.equals(videoBean.getId())) {
                    return videoBean;
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST && resultCode == RESULT) {
            edit.setText(data.getStringExtra(EDIT_CONTENT));
            getTextVideo(edit.getTag().toString()).setContent(data.getStringExtra(EDIT_CONTENT));
        }
    }
}
