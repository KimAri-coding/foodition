package com.khn.foodition;

import static com.khn.foodition.Foodition_Main_Activity.adapter;
import static com.khn.foodition.Foodition_Main_Activity.foodlist;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Foodition_Chart_Activity extends AppCompatActivity {

    MySQLite_foodition helper; //SQLite 선언
    SQLiteDatabase db;//데이터베이스 선언

    // 각 area별 개수를 저장할 변수
    int auto = 0;
    int ice = 0;
    int sun = 0;
    int all = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodition_chart);

        //데이터베이스 생성.
        helper = new MySQLite_foodition(
                Foodition_Chart_Activity.this, // 현재 화면의 context
                "foodition3.db", // 파일명
                null, // 커서 팩토리
                1); // 버전 번호

        auto = selectCount("냉장고");
        ice = selectCount("냉동고");
        sun = selectCount("실온");
        all = auto + ice + sun;

        //id연동
        PieChart pieChart = findViewById(R.id.pieChart);
        TextView tvAll = findViewById(R.id.tvAll);
        TextView tvAuto = findViewById(R.id.tvAuto);
        TextView tvIce = findViewById(R.id.tvIce);
        TextView tvSun = findViewById(R.id.tvSun);

        //갯수세팅
        tvAll.setText(all+"개");
        tvAuto.setText(auto+"개");
        tvIce.setText(ice+"개");
        tvSun.setText(sun+"개");

        //샘플데이터
        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(auto, "냉장"));
        visitors.add(new PieEntry(ice, "냉동"));
        visitors.add(new PieEntry(sun, "실온"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "");
        //냉장색상
        int red1 = 45;
        int green1 = 255;
        int blue1 = 250;
        int color1 = Color.rgb(red1, green1, blue1);
        //냉동색상
        int red2 = 0;
        int green2 = 84;
        int blue2 = 255;
        int color2 = Color.rgb(red2, green2, blue2);
        //실온색상
        int red3 = 29;
        int green3 = 219;
        int blue3 = 22;
        int color3 = Color.rgb(red3, green3, blue3);

        //색상배열
        int[] colors = {color1, color2, color3};
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterTextSize(22f);
        pieChart.setCenterText("Foodition");
        pieChart.animate();

        pieChart.getLegend().setTextColor(Color.WHITE);
        pieChart.getLegend().setTextSize(18f);
    }

    //데이터베이스에 저장된 area 이름에 따른 항목갯수 구하기
    public int selectCount(String areaname) {
        int count = 0;
        try {
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM foodition3 WHERE area = ?" ,new String[]{areaname});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
