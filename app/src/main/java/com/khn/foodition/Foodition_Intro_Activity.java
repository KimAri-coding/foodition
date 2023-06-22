package com.khn.foodition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import me.relex.circleindicator.CircleIndicator3;

public class Foodition_Intro_Activity extends AppCompatActivity {
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 4;
    private CircleIndicator3 Indicator;
    Button btnStart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodition_intro);

        //버튼 연동
        btnStart = findViewById(R.id.btnStart);
        //ViewPager2
        mPager = findViewById(R.id.mPager);
        //Adapter
        pagerAdapter = new MyAdapter(Foodition_Intro_Activity.this, num_page);
        mPager.setAdapter(pagerAdapter);
        //Indicator
        Indicator = findViewById(R.id.indicator);
        Indicator.setViewPager(mPager);
        Indicator.createIndicators(num_page, 0);
        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(3);

        //이벤트 배너 선택된 페이지의 변화하는 상태에 대응하기 위한 콜백 인터페이스.
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }
            //새페이지가 선택될 때 호출된다
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(headerRunnable);
                handler.postDelayed(headerRunnable, 5000); // Slide duration 3 seconds
                Indicator.animatePageSelected(position % num_page);
            }
            //핸들러로 화면전환하기
            Handler handler = new Handler();
            Runnable headerRunnable = new Runnable() {
                @Override
                public void run() {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                }
            };
        });
        final float pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);
        mPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float myOffset = position * -(2 * pageOffset + pageMargin);
                if (mPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(mPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.setTranslationX(-myOffset);
                    } else {
                        page.setTranslationX(myOffset);
                    }
                } else {
                    page.setTranslationY(myOffset);
                }
            }
        });
        // 시작하기 버튼 클릭
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain =
                        new Intent(Foodition_Intro_Activity.this, Foodition_Main_Activity.class);
                startActivity(intentMain);
            }
        });
    }
}