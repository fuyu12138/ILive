package com.graduation.fuyu.ilive;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.graduation.fuyu.ilive.adapter.LiveRoomRecyclerAdapter;
import com.graduation.fuyu.ilive.pojo.LiveRoom;
import com.graduation.fuyu.ilive.service.LiveRoomPostService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TextView cancel;
    private TextView empty;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private LiveRoomRecyclerAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<LiveRoom> liveRooms=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_search);
        cancel=findViewById(R.id.activity_search_cancel);
        empty=findViewById(R.id.activity_search_tv_empty);
        searchView=findViewById(R.id.activity_search_search_action);
        recyclerView=findViewById(R.id.activity_search_rv);
        searchView.onActionViewExpanded();
        EditText textView = searchView
                .findViewById(
                        android.support.v7.appcompat.R.id.search_src_text
                );

        textView.setHintTextColor(
                ContextCompat.getColor(
                        SearchActivity.this,
                        R.color.colorGrey666666)
        );
        textView.setTextColor(
                ContextCompat.getColor(
                        SearchActivity.this,
                        R.color.colorGrey666666)
        );
        ImageView imageView=searchView.findViewById(
                android.support.v7.appcompat.R.id.search_close_btn
        );
        imageView.setImageDrawable(ContextCompat.getDrawable(SearchActivity.this,R.drawable.ic_clear_pink_24dp));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new SearchTask().execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mLayoutManager=new GridLayoutManager(SearchActivity.this,2);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter=new LiveRoomRecyclerAdapter(SearchActivity.this,liveRooms);
        recyclerView.setAdapter(adapter);
    }

    class SearchTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("keyword",strings[0]));
            return LiveRoomPostService.searchLiveRoom(params);
        }

        @Override
        protected void onPostExecute(String s) {
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
                for (int i=0;i<name.length();i++){
                    LiveRoom liveRoom=new LiveRoom();
                    liveRoom.setName(name.getString(i));
                    liveRoom.setRoomname(roomname.getString(i));
                    liveRoom.setLabel(label.getString(i));
                    liveRoom.setIntroduce(introduce.getString(i));
                    liveRoom.setCover(cover.getString(i));
                    liveRoom.setHead(head.getString(i));
                    liveRooms.add(liveRoom);
                }
                if (liveRooms.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                }else {
                    empty.setVisibility(View.GONE);
                }
                adapter.setData(liveRooms);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                }

        }
    }



    private void initWindow() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
}
