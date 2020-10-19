package com.example.ezhospital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class MapActivity extends AppCompatActivity {

    Button btn_b3,btn_b2,btn_b1,btn_lv1,btn_lv2,btn_lv3,btn_lv4,btn_lv6;
    ImageViewZoom hospital_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btn_b3=findViewById(R.id.btn_b3);
        btn_b2=findViewById(R.id.btn_b2);
        btn_b1=findViewById(R.id.btn_b1);
        btn_lv1=findViewById(R.id.btn_1);
        btn_lv2=findViewById(R.id.btn_2);
        btn_lv3=findViewById(R.id.btn_3);
        btn_lv4=findViewById(R.id.btn_4);
        btn_lv6=findViewById(R.id.btn_6);
        hospital_map=findViewById(R.id.hospital_map);

        btn_b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level9);
                hospital_map.getBase64();
            }
        });
        btn_b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level8);
                hospital_map.getBase64();
            }
        });
        btn_b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level7);
                hospital_map.getBase64();
            }
        });
        btn_lv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level3);
                hospital_map.getBase64();
            }
        });
        btn_lv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level1);
                hospital_map.getBase64();
            }
        });
        btn_lv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level2);
                hospital_map.getBase64();
            }
        });
        btn_lv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level4);
                hospital_map.getBase64();
            }
        });
        btn_lv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospital_map.setImageResource(R.drawable.level5_6);
                hospital_map.getBase64();
            }
        });
    }
}