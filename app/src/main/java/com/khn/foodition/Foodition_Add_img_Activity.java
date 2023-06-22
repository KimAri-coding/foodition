package com.khn.foodition;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class Foodition_Add_img_Activity extends AppCompatActivity {

    Button btnChoice;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodition_add_img);

        final GridView foodView = findViewById(R.id.foodView);
        MyGridAdapter gAdapter = new MyGridAdapter(this);

        foodView.setAdapter(gAdapter);

        btnChoice = (Button) findViewById(R.id.btnChoice);

    }
    public class MyGridAdapter extends BaseAdapter{
        Context context;
        public MyGridAdapter(Context c){
            context = c;
        }

        //기본 선택 이미지
        Integer[] picID = {
                R.drawable.fooditionpic, R.drawable.food1, R.drawable.food2, R.drawable.food3, R.drawable.food4,
                R.drawable.food5,R.drawable.food6, R.drawable.food7, R.drawable.food8, R.drawable.food9,
                R.drawable.food10, R.drawable.food11, R.drawable.food12, R.drawable.food1, R.drawable.food2,
                R.drawable.food3, R.drawable.food4, R.drawable.food5, R.drawable.food6, R.drawable.food7,
                R.drawable.food8, R.drawable.food9, R.drawable.food10, R.drawable.food11, R.drawable.food12
        };

        // BaseAdapter를 상속받은 클래스가 구현해야 할 함수들은
        // { getCount(), getItem(), getItemId(), getView() }
        // Ctrl + i 를 눌러 한꺼번에 구현할 수 있습니다.
        @Override
        public int getCount() {
            return picID.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);

            final int pos = position;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View dialogView = View.inflate(Foodition_Add_img_Activity.this, R.layout.imgdialog, null);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Foodition_Add_img_Activity.this, R.style.DialogStyle);
                    ImageView ivPic = dialogView.findViewById(R.id.ivpic);
                    ivPic.setImageResource(picID[pos]);
                    dlg.setView(dialogView);

                    // 버튼 처리 코드
                    Button btnChoice = dialogView.findViewById(R.id.btnChoice);
                    btnChoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 선택 버튼 클릭 시 처리할 로직
                            Intent intent = new Intent(Foodition_Add_img_Activity.this, Foodition_Add_Activity.class);
                            intent.putExtra("selectedImageId", picID[pos]);
                            startActivity(intent);
                            finish();
                        }
                    });

                    AlertDialog dialog = dlg.create();

                    // 다이얼로그 크기 변경
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = 1000;  // 가로 크기
                    lp.height = 1000;  // 세로 크기
                    dialog.show();
                    dialog.getWindow().setAttributes(lp);
                }
            });

            imageView.setImageResource(picID[position]);
            return imageView;
        }
    }
}