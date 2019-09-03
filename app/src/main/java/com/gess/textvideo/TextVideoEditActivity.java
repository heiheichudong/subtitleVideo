package com.gess.textvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TextVideoEditActivity extends AppCompatActivity {

    public final static String EDIT_CONTENT = "edit_content";
    public final static int RESULT = 22;
    public final static int REQUEST = 23;//requestCode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_video_edit);
        findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rusult = ((EditText) findViewById(R.id.et_edit)).getText().toString().trim();
                setResult(RESULT,new Intent().putExtra(EDIT_CONTENT,rusult));
                finish();
            }
        });
    }
}
