package com.khn.foodition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Foodition_Main_Activity extends AppCompatActivity implements Foodition_Main_ActivityAdapter.MyRecyclerViewClickListener {

    public static final int REQUEST_CODE_FOOD_PAGE = 1;
    static MySQLite_foodition helper; //SQLite 선언
    static SQLiteDatabase db;//데이터베이스 선언
    static ArrayList<Foodition_Itemlist> foodlist = new ArrayList<>();
    static Foodition_Main_ActivityAdapter adapter = new Foodition_Main_ActivityAdapter(foodlist);
    FloatingActionButton fabMenu, fabAdd, fabIce, fabSun, fabRefri, fabChart;
    private boolean fabMenu_status = false;

    private MyApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodition_main);

        //데이터베이스 생성.
        helper = new MySQLite_foodition(
                Foodition_Main_Activity.this, // 현재 화면의 context
                "foodition3.db", // 파일명
                null, // 커서 팩토리
                1); // 버전 번호

        //알림창을 띄우기 위해(스태틱 때문에 자꾸 꼬임) 마이어플리케이션 클래스를 따로 만들어서 참조한다
        myApp = (MyApplication) getApplication();

        //id연동
        fabMenu = (FloatingActionButton) findViewById(R.id.fabMenu);
        fabIce = (FloatingActionButton) findViewById(R.id.fabIce);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabSun = (FloatingActionButton) findViewById(R.id.fabSun);
        fabRefri = (FloatingActionButton) findViewById(R.id.fabRefri);
        fabChart = (FloatingActionButton) findViewById(R.id.fabChart);

        //리스트뷰 id연동
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //식품리스트 호출
        recyclerView.setAdapter(adapter);
        selectFoodList();

        //리사이클러뷰 아이템 클릭시 해당 이름 수정/삭제 액티비티로 보내기
        adapter.setOnClickListener(new Foodition_Main_ActivityAdapter.MyRecyclerViewClickListener() {
            @Override
            public void onItemClicked(int position) {

                Foodition_Itemlist item = foodlist.get(position); // 아이템 리스트에서 해당 포지션의 아이템 객체를 가져옴
                Intent intentPage = new Intent(Foodition_Main_Activity.this, Foodition_FoodPage_Activity.class);
                intentPage.putExtra("itemName", item.getName()); // 가져온 아이템 객체에서 음식 이름을 추출하여 인텐트에 담음
                startActivity(intentPage);
            }
        });

        // 메인플로팅 버튼 클릭
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
            }
        });
        // 추가하기 플로팅 버튼 클릭
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAdd =
                        new Intent(Foodition_Main_Activity.this, Foodition_Add_Activity.class);
                startActivity(intentAdd);
            }
        });
        // 냉동고 플로팅 버튼 클릭
        fabIce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Foodition_Main_Activity.this, "냉동고리스트", Toast.LENGTH_SHORT).show();
                String iceName = "냉동고";
                selectList(iceName);
            }
        });
        // 실온보관 플로팅 버튼 클릭
        fabSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Foodition_Main_Activity.this, "실온보관리스트", Toast.LENGTH_SHORT).show();
                String sunName = "실온";
                selectList(sunName);
            }
        });
        // 냉장보관 플로팅 버튼 클릭
        fabRefri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Foodition_Main_Activity.this, "냉장보관리스트", Toast.LENGTH_SHORT).show();
                String autoName = "냉장고";
                selectList(autoName);
            }
        });
        // 차트 플로팅 버튼 클릭
        fabChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChart =
                        new Intent(Foodition_Main_Activity.this, Foodition_Chart_Activity.class);
                startActivity(intentChart);
                Toast.makeText(Foodition_Main_Activity.this, "차트보기", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //리사이클뷰 아이템을 클릭했을 때 해당 아이템 정보를 가지고 페이지이동을 한다
    public void onItemClicked(int position) {
        // 아이템을 클릭했을 때 처리할 로직을 작성
        Foodition_Itemlist item = foodlist.get(position); // 해당 포지션의 아이템 객체를 가져옴
        Intent intentPage = new Intent(this, Foodition_FoodPage_Activity.class);
        intentPage.putExtra("itemName", item.getName()); // 인텐트에 아이템 객체에서 음식 이름을 추출하여 담음
        startActivity(intentPage);
    }

    // 플로팅 액션 버튼 클릭시 애니메이션 효과
    public void toggleFab() {
        if (fabMenu_status) {
            // 플로팅 액션 버튼 닫기
            // 애니메이션 추가
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabIce, "translationY", 0f);
            fc_animation.start();
            ObjectAnimator fr_animation = ObjectAnimator.ofFloat(fabRefri, "translationY", 0f);
            fr_animation.start();
            ObjectAnimator fs_animation = ObjectAnimator.ofFloat(fabSun, "translationY", 0f);
            fs_animation.start();
            ObjectAnimator fa_animation = ObjectAnimator.ofFloat(fabAdd, "translationY", 0f);
            fa_animation.start();
            ObjectAnimator fh_animation = ObjectAnimator.ofFloat(fabChart, "translationY", 0f);
            fh_animation.start();
            // 메인 플로팅 이미지 변경
            fabMenu.setImageResource(R.drawable.fooditionmenu);

        } else {
            // 플로팅 액션 버튼 열기
            ObjectAnimator fc_animation = ObjectAnimator.ofFloat(fabIce, "translationY", -250f);
            fc_animation.start();
            ObjectAnimator fr_animation = ObjectAnimator.ofFloat(fabRefri, "translationY", -500f);
            fr_animation.start();
            ObjectAnimator fs_animation = ObjectAnimator.ofFloat(fabSun, "translationY", -750f);
            fs_animation.start();
            ObjectAnimator fa_animation = ObjectAnimator.ofFloat(fabAdd, "translationY", -1000f);
            fa_animation.start();
            ObjectAnimator fh_animation = ObjectAnimator.ofFloat(fabChart, "translationY", -1250f);
            fh_animation.start();
            // 메인 플로팅 이미지 변경
            fabMenu.setImageResource(R.drawable.fooditionmenu2);
        }
        // 플로팅 버튼 상태 변경
        fabMenu_status = !fabMenu_status;
    }

    public static void selectFoodList() //식품리스트 디비메서드
    {
        //초기화후 다시~!
        adapter.DataItem.clear();

        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.rawQuery("SELECT * FROM foodition3", null);

        while (c.moveToNext()) {

            int idx = c.getInt(0);
            String name = c.getString(1);
            String area = c.getString(2);
            String stdate = c.getString(3);
            String enddate = c.getString(4);
            String dday = c.getString(5);
            int img = c.getInt(6);

            foodlist.add(new Foodition_Itemlist(name, area, stdate, enddate, dday, img));
        }
        //RecyclerView를 새로고침
        adapter.notifyDataSetChanged();
        c.close();
        db.close();
    }

    //보관장소에 따른 리스트 디비메서드
    public void selectList(String areaname) {
        //초기화후 다시~!
        adapter.DataItem.clear();

        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.rawQuery("SELECT * FROM foodition3 where area = ?", new String[]{areaname});

        while (c.moveToNext()) {

            int idx = c.getInt(0);
            String name = c.getString(1);
            String area = c.getString(2);
            String stdate = c.getString(3);
            String enddate = c.getString(4);
            String dday = c.getString(5);
            int img = c.getInt(6);

            foodlist.add(new Foodition_Itemlist(name, area, stdate, enddate, dday, img));
        }
        // 데이터를 업데이트한 후 RecyclerView를 새로고침
        adapter.notifyDataSetChanged();
        c.close();
        db.close();
    }

}
