package com.example.ezhospital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.ezhospital.Adapter.LookbookAdapter;
import com.example.ezhospital.Adapter.MyViewPagerAdapter;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingActivity extends AppCompatActivity {

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.btn_prev_step)
    ViewPager btn_prev_step;
    @BindView(R.id.btn_next_step)
    ViewPager btn_next_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        //ButterKnife.bind(BookingActivity.this);
        
        setupStepView();
        setColorButton();

        //view
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0)
                    btn_prev_step.setEnabled(false);
                else
                    btn_prev_step.setEnabled(true);

                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled()){
            btn_next_step.setBackgroundResource(R.color.gray);
        }else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_prev_step.isEnabled()){
            btn_prev_step.setBackgroundResource(R.color.gray);
        }else {
            btn_prev_step.setBackgroundResource(android.R.color.darker_gray);

        }
    }

    private void setupStepView() {
        List<String> stepList=new ArrayList<>();
        stepList.add("Salon");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }
}