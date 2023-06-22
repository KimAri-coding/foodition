package com.khn.foodition;

import static android.content.ContentValues.TAG;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Foodition_Add_Activity extends AppCompatActivity {

    static MySQLite_foodition helper; //SQLite 선언
    static SQLiteDatabase db;//데이터베이스 선언
    EditText etName;
    TextView tvArea, tvD_day, tvStDate, tvEndDate;
    Button btnAdd, btnStDate, btnEndDate;
    ImageButton btnImg;
    String[] items = {"냉장고", "냉동고", "실온"};

    public String name, area, stdate, enddate, dday;
    public int img;

    DatePickerDialog datePickerDialog;

    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    private Calendar mCalendar;
    private Calendar dCalendar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodition_add);

        //데이터베이스 생성.
        helper = new MySQLite_foodition(
                Foodition_Add_Activity.this, // 현재 화면의 context
                "foodition3.db", // 파일명
                null, // 커서 팩토리
                1); // 버전 번호

        // 한국어 설정 (ex: date picker)
        Locale.setDefault(Locale.KOREAN);

        //id연동
        etName = (EditText) findViewById(R.id.etName);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvD_day = (TextView) findViewById(R.id.tvD_day);
        tvStDate = (TextView) findViewById(R.id.tvStDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);

        btnImg = (ImageButton) findViewById(R.id.btnImg);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnStDate = (Button) findViewById(R.id.btnStDate);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);

        //버튼 이벤트 연동
        findViewById(R.id.btnImg).setOnClickListener(mClick);
        findViewById(R.id.btnAdd).setOnClickListener(mClick);
        findViewById(R.id.btnStDate).setOnClickListener(mClick);
        findViewById(R.id.btnEndDate).setOnClickListener(mClick);

        // 전달받은 이미지 ID 가져오기
        int selectedImageId  = getIntent().getIntExtra("selectedImageId", -1);

        // 선택한 이미지가 없는 경우, 기본 이미지 표시
        if (selectedImageId == -1) {
            btnImg.setImageResource(R.drawable.fooditionpic);
        } else {
            btnImg.setImageResource(selectedImageId);
        }
        //스피너 설정//
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        // 스피너에 어댑터 설정
        spinner.setAdapter(adapter);

        // 스피너에서 선택 했을 경우 이벤트 처리
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvArea.setText(items[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvArea.setText("냉장고");
            }
        });

        //캘린더 초기화 (안 하면 널값 때문에 에러뜸)
        mCalendar = Calendar.getInstance();
        // 캘린더 버튼 클릭시 date picker 호출
        btnStDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {

                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(Foodition_Add_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //1월은 0부터 시작하기 때문에 +1을 해준다
                        month = month + 1;
                        String date = year + " / " + month + " / " + dayOfMonth;
                        tvStDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        dCalendar = Calendar.getInstance();
        // 캘린더 버튼 클릭시 date picker 호출
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {

                int year = dCalendar.get(Calendar.YEAR);
                int month = dCalendar.get(Calendar.MONTH);
                int day = dCalendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(Foodition_Add_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //1월은 0부터 시작하기 때문에 +1을 해준다
                        month = month + 1;
                        String date = year + " / " + month + " / " + dayOfMonth;
                        tvEndDate.setText(date);
                        tvD_day.setText(getDday(year, month, dayOfMonth, mCalendar));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    // D-day 반환
    private String getDday(int a_year, int a_monthOfYear, int a_dayOfMonth, Calendar mCalendar) {

        // mCalendar를 복사하여 값을 변경해준다
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(a_year, a_monthOfYear-1, a_dayOfMonth);

        // D-day를 구하기 위해 millisecond으로 환산하여 d-day에서 today의 차를 구한다.
        final long dday = dCalendar.getTimeInMillis() / ONE_DAY; //유통기한 날짜
        final long today = mCalendar.getTimeInMillis() / ONE_DAY; //보관날짜
        long result = dday - today;

        // 출력 시 d-day에 맞게 표시
        final String strFormat;
        if (result > 0) {
            strFormat = "D-%d";
        } else if (result == 0) {
            strFormat = "D-Day";
        } else {
            result *= -1;
            strFormat = "D+%d";
        }
        final String strCount = (String.format(strFormat, result));
        return strCount;
    }
    //버튼 이벤트 처리
    View.OnClickListener mClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.btnImg:
                    Intent intentImg =
                            new Intent(Foodition_Add_Activity.this, Foodition_Add_img_Activity.class);
                    startActivity(intentImg);
                    finish();
                    break;
                case R.id.btnAdd:
                    if(etName.getText().toString().equals(""))
                    {Toast.makeText(Foodition_Add_Activity.this, "식품이름을 입력해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    name = etName.getText().toString().trim();
                    if(tvArea.getText().toString().equals(""))
                    {Toast.makeText(Foodition_Add_Activity.this, "보관장소를 선택해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    area = tvArea.getText().toString().trim();
                    if(tvStDate.getText().toString().equals(""))
                    {Toast.makeText(Foodition_Add_Activity.this, "보관시작 날짜를 선택해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    stdate = tvStDate.getText().toString().trim();
                    if(tvEndDate.getText().toString().equals(""))
                    {Toast.makeText(Foodition_Add_Activity.this, "기한날짜를 선택해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    enddate = tvEndDate.getText().toString().trim();
                    dday = tvD_day.getText().toString().trim();
                    img = getIntent().getIntExtra("selectedImageId", -1);

                    // 디비에 삽입..
                    insert(name, area, stdate, enddate, dday, img);

                    //입력칸 빈칸으로 후처리
                    etName.setText("");
                    tvArea.setText("");
                    tvStDate.setText("");
                    tvEndDate.setText("");
                    tvD_day.setText("");

                    Toast.makeText(Foodition_Add_Activity.this, "식품추가가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //데이타베이스 메서드 처리  ////////////////////////////
    public void insert(String name, String area, String stdate, String enddate, String dday, int img) {

        SQLiteDatabase db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        //값들을 컨트롤 하려고 클래스 생성
        ContentValues values = new ContentValues();

        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("name", name);
        values.put("area", area);
        values.put("st_date", stdate);
        values.put("end_date", enddate);
        values.put("d_date", dday);
        values.put("foodimg", img);
        db.insert("foodition3", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)

        db.close();
        Log.d(TAG, name+"/"+area+"/"+stdate+"/"+enddate+"/"+dday+"/"+img+" 의 정보로 디비저장완료.");

        //식품추가 후 리스트 다시 불러오기(새로고침처럼)
        Foodition_Main_Activity.selectFoodList();
    }

}