package com.example.manufacture.annotation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.manufacture.R;
import com.yang.apt_annotation.BindView;
import com.yang.apt_library.BindViewTools;

public class AnnotationActivity extends AppCompatActivity {

    @BindView(R.id.annotation_tv)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        BindViewTools.bind(this);
        mTextView.setText("bind TextView success");
    }
}
