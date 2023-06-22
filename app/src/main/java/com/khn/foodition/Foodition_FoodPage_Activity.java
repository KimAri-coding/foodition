package com.khn.foodition;

import static android.content.ContentValues.TAG;

import static com.khn.foodition.Foodition_Main_Activity.adapter;
import static com.khn.foodition.Foodition_Main_Activity.foodlist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class Foodition_FoodPage_Activity extends AppCompatActivity {

    static MySQLite_foodition helper; //SQLite 선언
    static SQLiteDatabase db;//데이터베이스 선언
    EditText etName;
    TextView tvArea, tvD_day, tvStDate, tvEndDate;
    Button btnStDate, btnEndDate, btnEdit, btnDel;
    ImageButton btnImg;
    int selectedImageId;

    static final int REQUEST_EDIT_IMAGE = 1;
    Spinner spinner; // 스피너 변수 선언
    String[] items = {"냉장고", "냉동고", "실온"};

    public String name, area, stdate, enddate, dday;
    public int img;

    DatePickerDialog datePickerDialog;

    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    private Calendar mCalendar;
    private Calendar dCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodition_food_page);

        //데이터베이스 생성.
        helper = new MySQLite_foodition(
                Foodition_FoodPage_Activity.this, // 현재 화면의 context
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
        btnStDate = (Button) findViewById(R.id.btnStDate);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDel = (Button) findViewById(R.id.btnDel);

        //버튼 이벤트 연동
        findViewById(R.id.btnImg).setOnClickListener(mClick);
        findViewById(R.id.btnStDate).setOnClickListener(mClick);
        findViewById(R.id.btnEndDate).setOnClickListener(mClick);
        findViewById(R.id.btnEdit).setOnClickListener(mClick);
        findViewById(R.id.btnDel).setOnClickListener(mClick);

        // 다음 액티비티에서 데이터를 받아와 사용
        Intent intentPage = getIntent();
        String itemName = intentPage.getStringExtra("itemName");
        etName.setText(itemName);

        //알림창을 클릭했을 때 데이터를 받아와 사용
        Intent intentEvent = getIntent();
        String eventname = intentEvent.getStringExtra("Eventname");

        // 스피너 설정
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //setSelection을 이용해서 tvArea값이 변경될 때 스피너도 값이 변경될 수 있도록 함
        spinner.setSelection(adapter.getPosition(area));

        // 스피너에서 선택 했을 경우 이벤트 처리
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = items[position];
                String tvAreaText = getTvAreaText();
                if (!selectedItem.equals(tvAreaText)) { // 스피너에서 선택한 값이 tvArea의 값과 다를 경우에만 업데이트
                    tvArea.setText(selectedItem);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //식품 이름을 통해 해당 식품 정보를 불러오는 메서드
        findFood(itemName);
        //알림창을 클릭했을 때 해당 식품 정보를 불러오는 메서드
        eventFood(eventname);

        //캘린더 초기화 (안 하면 널값 때문에 에러뜸)
        mCalendar = Calendar.getInstance();
        // 캘린더 버튼 클릭시 date picker 호출
        btnStDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {

                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(Foodition_FoodPage_Activity.this, new DatePickerDialog.OnDateSetListener() {
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

                datePickerDialog = new DatePickerDialog(Foodition_FoodPage_Activity.this, new DatePickerDialog.OnDateSetListener() {
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
    //스피너 때문에 tvArea 값 리턴하는 메서드
    public String getTvAreaText() {
        return tvArea.getText().toString();
    }

    // D-day 반환
    private String getDday(int a_year, int a_monthOfYear, int a_dayOfMonth, Calendar mCalendar) {

        // mCalendar를 복사하여 값을 변경해준다
        Calendar dCalendar = Calendar.getInstance();
        dCalendar.set(a_year, a_monthOfYear-1, a_dayOfMonth);

        // D-day를 구하기 위해 millisecond으로 환산하여 d-day에서 today의 차를 구한다.
        final long dday = dCalendar.getTimeInMillis() / ONE_DAY;
        final long today = mCalendar.getTimeInMillis() / ONE_DAY;
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

    // 이미지버튼 변경을 위한 ActivityResultLauncher 객체 생성
    ActivityResultLauncher<Intent> editImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // 전달받은 이미지 ID 가져오기
                    selectedImageId  = result.getData().getIntExtra("editImageId", -1);
                    btnImg.setImageResource(selectedImageId);
                    img = selectedImageId;
                }
            });

    //버튼 이벤트 처리
    View.OnClickListener mClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.btnImg:
                    Intent intentEditImg = new Intent(Foodition_FoodPage_Activity.this, Foodition_Edit_img_Activity.class);
                    editImageLauncher.launch(intentEditImg);
                    break;

                case R.id.btnEdit:
                    if(etName.getText().toString().equals(""))
                    {Toast.makeText(Foodition_FoodPage_Activity.this, "식품이름을 입력해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    name = etName.getText().toString().trim();
                    if(tvArea.getText().toString().equals(""))
                    {Toast.makeText(Foodition_FoodPage_Activity.this, "보관장소를 선택해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    area = tvArea.getText().toString().trim();
                    if(tvStDate.getText().toString().equals(""))
                    {Toast.makeText(Foodition_FoodPage_Activity.this, "보관시작 날짜를 선택해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    stdate = tvStDate.getText().toString().trim();
                    if(tvEndDate.getText().toString().equals(""))
                    {Toast.makeText(Foodition_FoodPage_Activity.this, "기한날짜를 선택해주세요!", Toast.LENGTH_SHORT).show();  return;}
                    enddate = tvEndDate.getText().toString().trim();
                    dday = tvD_day.getText().toString().trim();

                    // 추가된 코드
                    Log.d("FOODITION", "img value in btnEdit: " + img);

                    // 업데이트 디비 메서드 호출..
                    Update(name, area, stdate, enddate, dday, img);

                    Toast.makeText(Foodition_FoodPage_Activity.this, "정보수정이 완료되었습니다!", Toast.LENGTH_SHORT).show();

                    //정보수정 후 리스트 다시 불러오기(새로고침처럼)
                    Foodition_Main_Activity.selectFoodList();
                    break;

                case R.id.btnDel:
                    //삭제 디비 메서드 호출
                    String delname = etName.getText().toString();
                    delete(delname);
                    break;
            }
        }
    };
    //식품정보 불러오는 메서드
    public void findFood(String findname) {

        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용
        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.rawQuery("SELECT * FROM foodition3 where name = '"+findname+"'", null);

        while (c.moveToNext()) {
            int idx = c.getInt(0);
            String name = c.getString(1);
            String area = c.getString(2);
            String stdate = c.getString(3);
            String enddate = c.getString(4);
            String dday = c.getString(5);
            int img = Integer.parseInt(c.getString(6));

            if (img == 0) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fooditionpic);
                btnImg.setImageDrawable(drawable); // 기본 이미지로 설정
            } else {
                Drawable drawable = ContextCompat.getDrawable(this, img);
                btnImg.setImageDrawable(drawable);
            }
            etName.setText(name);
            tvArea.setText(area);

            //setSelection을 통해 area값을 설정해줘서 같이 값을 바꿔준다
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            int spinnerPosition = adapter.getPosition(area);
            spinner.setSelection(spinnerPosition);

            tvStDate.setText(stdate);
            tvEndDate.setText(enddate);
            tvD_day.setText(dday);
        }
        // 데이터를 업데이트한 후 RecyclerView를 새로고침
        adapter.notifyDataSetChanged();
        c.close();
        db.close();
    }
    //알림창 클릭했을 때 식품정보 불러오는 메서드
    public void eventFood(String findname) {

        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용
        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        Cursor c = db.rawQuery("SELECT * FROM foodition3 where name = '"+findname+"'", null);

        while (c.moveToNext()) {
            int idx = c.getInt(0);
            String name = c.getString(1);
            String area = c.getString(2);
            String stdate = c.getString(3);
            String enddate = c.getString(4);
            String dday = c.getString(5);
            int img = Integer.parseInt(c.getString(6));

            if (img == 0) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fooditionpic);
                btnImg.setImageDrawable(drawable); // 기본 이미지로 설정
            } else {
                Drawable drawable = ContextCompat.getDrawable(this, img);
                btnImg.setImageDrawable(drawable);
            }
            etName.setText(name);
            tvArea.setText(area);

            //setSelection을 통해 area값을 설정해줘서 같이 값을 바꿔준다
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            int spinnerPosition = adapter.getPosition(area);
            spinner.setSelection(spinnerPosition);

            tvStDate.setText(stdate);
            tvEndDate.setText(enddate);
            tvD_day.setText(dday);
        }
        // 데이터를 업데이트한 후 RecyclerView를 새로고침
        adapter.notifyDataSetChanged();
        c.close();
        db.close();
    }

    //수정메서드
    public void Update(String name, String area, String stdate, String enddate, String dday, int img)
    {
        SQLiteDatabase db = helper.getWritableDatabase(); // db객체를 얻어온다. 쓰기 전용

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("area", area);
        values.put("st_date", stdate);
        values.put("end_date", enddate);
        values.put("d_date", dday);
        values.put("foodimg", img);

        //입력칸 빈칸으로 후처리
        etName.setText("");
        tvArea.setText("");
        tvStDate.setText("");
        tvEndDate.setText("");
        tvD_day.setText("");

        db.update("foodition3", values, "name='"+name+"'", null);
        db.close();
        //삭제 후 리스트 다시 불러오기(새로고침처럼)
        Foodition_Main_Activity.selectFoodList();
    }

    //삭제 메서드
    public void delete(String delname) {
        db = helper.getWritableDatabase();
        db.delete("foodition3", "name='"+delname+"'", null);
        Log.d("db", delname + "가 정상적으로 삭제 되었습니다.");
        Toast.makeText(getApplicationContext(), delname+"의 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        db.close();

        //입력칸 빈칸으로 후처리
        etName.setText("");
        tvArea.setText("");
        tvStDate.setText("");
        tvEndDate.setText("");
        tvD_day.setText("");

        //삭제 후 리스트 다시 불러오기(새로고침처럼)
        Foodition_Main_Activity.selectFoodList();
    }

}