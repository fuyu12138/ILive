package com.graduation.fuyu.ilive;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.graduation.fuyu.ilive.Manager.ChatRoomManager;
import com.graduation.fuyu.ilive.adapter.FragmentAdapter;
import com.graduation.fuyu.ilive.connection.XMPPConnectionManager;
import com.graduation.fuyu.ilive.fragment.FocusFragment;
import com.graduation.fuyu.ilive.fragment.LiveFragment;
import com.graduation.fuyu.ilive.pojo.ChatMessage;
import com.graduation.fuyu.ilive.pojo.ChatRoom;
import com.graduation.fuyu.ilive.pojo.Label;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.pojo.User;
import com.graduation.fuyu.ilive.service.FocusPostService;
import com.graduation.fuyu.ilive.service.LabelPostService;
import com.graduation.fuyu.ilive.service.LiveRoomPostService;
import com.graduation.fuyu.ilive.util.JidTransform;
import com.graduation.fuyu.ilive.util.ToUri;
import com.graduation.fuyu.ilive.widget.CircleImageView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.util.XmppStringUtils;
import org.w3c.dom.Text;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static com.graduation.fuyu.ilive.Manager.ChatRoomManager.CONFERENCE;

/**
 * app主界面
 * 由侧边栏+toolbar+viewPager构成
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private LiveFragment liveFragment;
    private FocusFragment focusFragment;
//    private TextView tv_live;
//    private TextView tv_focus;
    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private Toolbar toolbar;

    private NavigationView navigationView;
    private CircleImageView circleImageView;
    private TextView userName;
//    private SearchView searchView;
    //是否刷新中
//    private Boolean isRefresh=false;
//    private SwipeRefreshLayout mSwipeLayout;
//    private ImageView iv_top_person;
    private String username;
    private RelativeLayout rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.activity_main_toolbar);
        mDrawerLayout=findViewById(R.id.activity_main_dl);
//        tv_live = findViewById(R.id.activity_main_tv_live);
//        tv_focus = findViewById(R.id.activity_main_tv_focus);
        viewPager = findViewById(R.id.activity_main_vp);
//        iv_top_person=findViewById(R.id.activity_main_top_iv_person);
        rl=findViewById(R.id.searchlayout_rl);

        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences=getSharedPreferences("ilive", Context.MODE_APPEND);
        username=sharedPreferences.getString("username","匿名");
        /*
        =========================ViewPager+Fragment======================================
         */
        liveFragment = new LiveFragment();
        focusFragment = new FocusFragment();
        mFragmentList.add(liveFragment);
        mFragmentList.add(focusFragment);
        mFragmentAdapter = new FragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setCurrentItem(0);
//        viewPager.addOnPageChangeListener(new ViewPagerChangeListener());
        /*
        ============================ToolBar+SearchView==============================================
         */
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBarDrawerToggle mDrawerToggle =new android.support.v7.app.ActionBarDrawerToggle(
                this,mDrawerLayout,toolbar,R.string.blank,R.string.blank);
        mDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_person_white_36dp);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mTabLayout=findViewById(R.id.activity_main_tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
//                Toast.makeText(MainActivity.this,"search",Toast.LENGTH_SHORT).show();
            }
        });
        //searchView设置字体颜色
//        searchView=findViewById(R.id.activity_main_search_action);
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(intent);
//            }
//        });
//        searchView.onActionViewExpanded();
//        EditText textView = searchView
//                .findViewById(
//                        android.support.v7.appcompat.R.id.search_src_text
//                );
//
//        textView.setHintTextColor(
//                ContextCompat.getColor(
//                        MainActivity.this,
//                        R.color.colorGrey666666)
//        );
//        textView.setClickable(false);
//        textView.setTextColor(
//                ContextCompat.getColor(
//                        MainActivity.this,
//                        R.color.colorGrey666666)
//        );
//        ImageView imageView=searchView.findViewById(
//                android.support.v7.appcompat.R.id.search_close_btn
//        );
//        imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_clear_pink_24dp));
//        //searchView阻止自动获取焦点
////        searchView.setFocusable(true);
////        searchView.setIconifiedByDefault(true);
//        searchView.clearFocus();
        /*
        ===========================重写的顶部菜单栏=========================================
         */
//        iv_top_person.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
        /*
        ================================侧边栏================================================
         */
        navigationView=findViewById(R.id.activity_main_navigationView);
        View view = navigationView.inflateHeaderView(R.layout.activity_main_header_navigation);
        circleImageView=view.findViewById(R.id.activity_main_header_circle);
        userName=view.findViewById(R.id.activity_main_header_username);
        userName.setText(username);
//        circleImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.mipmap.ic_launcher_round));
        User user=new User();
        Glide.with(this).load("http://139.196.124.195:8080/LoginServer/head/"+ user.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(circleImageView);
        int[][] states = new int[][]{new int[]{ -android.R.attr.state_checked},new int[]{android.R.attr.state_checked}};
        int[] colors = new int[]{getResources().getColor(R.color.colorGrey666666),getResources().getColor(R.color.colorAccent)};
        ColorStateList csl = new ColorStateList(states, colors);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);
//        navigationView.setItemIconTintList(getResources().getColorStateList(R.drawable.item_menu_selector,null));
//        navigationView.setItemTextColor(getResources().getColorStateList(R.color.colorAccent));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        item.setChecked(true);
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_gallery:
                        item.setChecked(true);
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_live_mine:
                        new getLiveRoomByName().execute();
                        return false;
                    case R.id.nav_share:
                        new disconnectTask().execute();
                        return false;
                    case R.id.nav_live_camera:
                        requestCamera();
//                        Intent intent=new Intent(MainActivity.this,CameraLiveActivity.class);
//                        startActivity(intent);
                        return false;
                    case R.id.nav_live_setting:
                        Intent intent=new Intent(MainActivity.this,LiveSettingActivity.class);
                        startActivity(intent);
                        return false;
//                    case R.id.nav_share:
//                        break;
                    default:
                        Toast.makeText(MainActivity.this,"还没有实现呢 -_-#",Toast.LENGTH_SHORT).show();
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SetHeadActivity.class);
                startActivity(intent);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0){
                    MenuItem item = navigationView.getMenu().findItem(R.id.nav_home);
                    item.setChecked(true);
                }else if (position==1){
                    MenuItem item = navigationView.getMenu().findItem(R.id.nav_gallery);
                    item.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    class createOrJoinTask extends AsyncTask<Void,Void,Boolean> {
        private MultiUserChat multiUserChat;
        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = XMPPConnectionManager.getConnection();
            String name=connection.getUser();
            try {
                multiUserChat= ChatRoomManager.createOrJoinLiveRoom(connection,name, "房间的描述");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Intent intent=new Intent(MainActivity.this,LiveRoomActivity.class);
                LiveRoomActivity.from_RoomList=multiUserChat;
                LiveRoomActivity.mLiveRoom=mLiveRoom;
                startActivity(intent);
            }else {
                Toast.makeText(MainActivity.this,"join fail",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private LiveRoom mLiveRoom=new LiveRoom();
    @SuppressLint("StaticFieldLeak")
    class getLiveRoomByName extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("liveName", username));
            return LiveRoomPostService.getLiveRoomByName(params);
        }

        @Override
        protected void onPostExecute(String s) {
            // TODO: 18-5-22 判断
//            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
            JSONObject mJSONObject;
            List<LiveRoom> liveRooms=new ArrayList<>();
            try {
                mJSONObject = new JSONObject(s);
                JSONArray name=mJSONObject.getJSONArray("name");
                JSONArray roomname=mJSONObject.getJSONArray("roomname");
                JSONArray label=mJSONObject.getJSONArray("label");
                JSONArray introduce=mJSONObject.getJSONArray("introduce");
                JSONArray cover=mJSONObject.getJSONArray("cover");
                JSONArray head=mJSONObject.getJSONArray("head");

                if (name.length()!=0){
                    mLiveRoom.setName(name.getString(0));
                    mLiveRoom.setRoomname(roomname.getString(0));
                    mLiveRoom.setLabel(label.getString(0));
                    mLiveRoom.setIntroduce(introduce.getString(0));
                    mLiveRoom.setCover(cover.getString(0));
                    mLiveRoom.setHead(head.getString(0));
                }else {
                    mLiveRoom.setName(username);
                    mLiveRoom.setRoomname(username+"的房间");
                    mLiveRoom.setIntroduce(username+"的房间");
                }
                new createOrJoinTask().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    class disconnectTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = XMPPConnectionManager.getConnection();
            try {
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(MainActivity.this,"disconnect fail",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getUsername(){
        return username;
    }

    /**
     * actionbar的沉浸式透明效果实现
     * 但是还是没有发现完美方法能去除actionbar自带的灰度
     */
    private void initWindow(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        /*
        ==========这个方法会强制设置ActionBar颜色和界面颜色不协调=================
         */
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorAccent));
//        tintManager.setStatusBarTintEnabled(true);
        /*
        ==========这个方法会强制设置ActionBar颜色因而没有自带的灰度效果，也就无所谓考虑透明度问题，和界面颜色不协调=================
         */
//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                 | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);
    }
    private void requestCamera() {
        PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                new JoinChatTask().execute();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了访问摄像头", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA}, false, null);
    }

    private MultiUserChat multiUserChat;
    private String getMsg;
    private String getName;
    @SuppressLint("StaticFieldLeak")
    class JoinChatTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            AbstractXMPPConnection connection = XMPPConnectionManager.getConnection();
            String nickname = XmppStringUtils.parseBareJid(connection.getUser());
            try {
                multiUserChat= ChatRoomManager.joinChatRoom(connection,username+"@conference.izf813mgctz7h8z", nickname);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                CameraLiveActivity.muli=multiUserChat;
                Intent intent=new Intent(MainActivity.this,CameraLiveActivity.class);
                startActivity(intent);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        searchView.clearFocus();
        User user=new User();
        Glide.with(this).load("http://139.196.124.195:8080/LoginServer/head/"+ user.getHead()).dontAnimate().placeholder(R.mipmap.header).error(R.mipmap.header).into(circleImageView);

    }

    /**
     * 按后退键时，强制退出程序，不然下次登录不上
     * 其实是连接没有释放，直接置空下次用新的连接就好了
     */
    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                XMPPConnectionManager.abstractXMPPConnection=null;
                finish();
//                System.exit(0);
            }
        }
    }
}
