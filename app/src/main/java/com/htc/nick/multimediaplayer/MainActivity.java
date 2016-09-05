package com.htc.nick.multimediaplayer;



import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.htc.nick.Base.Constants;
import com.htc.nick.Page.SlideShowPlayer.SlideShowPlayerActivity;
import com.htc.nick.fragment.ImageGridFragment;
import com.htc.nick.fragment.SongFragment;
import com.htc.nick.fragment.VideoGridFragment;
import com.htc.nick.logger.Log;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainView {

    private FragmentTabHost mTabHost;
    private ViewPager mViewPager;
    private TabPageIndicator indicator;
    private List<Fragment> mFragmentList;

//    @Pref
//    protected Preference preference;
    Menu menu;
    private Class mClass[] = {SongFragment.class,VideoGridFragment.class,ImageGridFragment.class};
    private Fragment mFragment[] = {new SongFragment(),new VideoGridFragment(),new ImageGridFragment()};
    private String mTitles[] = {"Music","Video","Photo"};
    private int mImages[] = {
            R.mipmap.headset,
            R.mipmap.video,
            R.mipmap.photo,
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_music);
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


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
        } else {
            mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    Fragment fragment;
                    switch (position) {
                        case 0:
                            fragment = SongFragment.getInstance();
                            break;
                        case 1:
                            fragment = VideoGridFragment.getInstance();
                            break;
                        case 2:
                            fragment = ImageGridFragment.getInstance();
                            break;
                        default:
                            fragment = null;
                            break;
                    }
                    return fragment;
                }

                @Override
                public int getCount() {
                    return mFragmentList.size();
                }
            });
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
    private void setupViewPager() {

        //setup viewpager
        mViewPager = (ViewPager) findViewById(R.id.viwepager);
        mViewPager.setAdapter(new MainActivityViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);

        //setup indicator
        indicator = (TabPageIndicator) findViewById(R.id.title);
        indicator.setViewPager(mViewPager);

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
         SharedPreferences sharedPreferences  = getSharedPreferences("TabPosition",0);
        mTabHost.setCurrentTab(sharedPreferences.getInt("TabPosition",0));
        mViewPager.setCurrentItem(sharedPreferences.getInt("TabPosition",0));
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
                SharedPreferences sharedPreferences  = getSharedPreferences("TabPosition",0);
                sharedPreferences.edit().putInt("TabPosition",position).commit();
                mTabHost.setCurrentTab(position);
                if(menu!=null) {
                    if (position == 2) {
                        menu.findItem(R.id.slideshow).setVisible(true);
                    } else {
                        menu.findItem(R.id.slideshow).setVisible(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu =menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.slideshow:
                // User chose the "Settings" item, show the app settings UI...
                Intent in = new Intent();
                in.setClass(this, SlideShowPlayerActivity.class);
                startActivity(in);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public class MainActivityViewPagerAdapter extends FragmentPagerAdapter {
        public MainActivityViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
