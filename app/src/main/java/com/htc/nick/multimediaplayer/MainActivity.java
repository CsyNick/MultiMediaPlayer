package com.htc.nick.multimediaplayer;



import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.htc.nick.Base.Constants;
import com.htc.nick.fragment.AudioFragment;
import com.htc.nick.fragment.PhotoFragment;
import com.htc.nick.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainView {

    private FragmentTabHost mTabHost;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private Class mClass[] = {AudioFragment.class,VideoFragment.class,PhotoFragment.class};
    private Fragment mFragment[] = {new AudioFragment(),new VideoFragment(),new PhotoFragment()};
    private String mTitles[] = {"Audio","Video","Photo"};
    private int mImages[] = {
            R.mipmap.headset,
            R.mipmap.video,
            R.mipmap.photo,
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       init();
    }

    private void init() {

        initView();

        initEvent();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mFragmentList = new ArrayList<>();

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0;i < mFragment.length;i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec,mClass[i],null);
            mFragmentList.add(mFragment[i]);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#B5ADB6"));
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }

    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);

        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);

        return view;
    }

    private void initEvent() {

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.WRITE_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   // init();
                }
                break;

            default:
                break;
        }
    }
}
