package com.example.useretrofit2.myproject;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.useretrofit2.ListViewCustomAdapter;
import com.example.useretrofit2.R;
import com.example.useretrofit2.downLoad2App;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyProjectActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    int pos;//재생 멈춘 시점
    boolean initial_play = true;
    private ImageButton play;
    private ImageButton pause;
    String duration;
    TextView show_time;
    int played_sec = 0;
    int played_minute = 0;
    SeekBar sb;//음악 재생위치를 나타내는 시크바
    boolean isPlaying = false; // 재생중?
    private ListView listView;
    private ListViewMyProjectAdapter adapter;



    class MyThread extends Thread {
        @Override
        public void run(){//Thread 시작할때 콜백되는 메서드
            //시크바 막대기 조금씩 움직이기 (노래 끝날 때 까지 반복)
            while(isPlaying){
                sb.setProgress(mediaPlayer.getCurrentPosition());
                int played = (mediaPlayer.getCurrentPosition()) / 1000;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(played_minute != played / 60 || played_sec != played % 60) {
                            if(played % 60 < 10) show_time.setText(played / 60 + ":0" + played % 60 + " / " + duration);
                            else show_time.setText(played / 60 + ":" + played % 60 + " / " + duration);

                        }
                    }
                });

                played_minute = played / 60;
                played_sec = played % 60;

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myproject);


        show_time = (TextView) findViewById(R.id.show_play_time);
        sb = (SeekBar) findViewById(R.id.myproject_seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(seekBar.getMax() == progress){//재생 끝났을 때
                    isPlaying = false;
                    mediaPlayer.stop();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlaying = true;
                int new_pos = seekBar.getProgress(); // 사용자가 움직여놓은 위치
                mediaPlayer.seekTo(new_pos);
                mediaPlayer.start();
                new MyThread().start();
            }
        });

        mediaPlayer = MediaPlayer.create(MyProjectActivity.this, R.raw.meteor);
        mediaPlayer.setLooping(false);//무한반복 x

        int playtime = mediaPlayer.getDuration(); // 노래 재생시간(ms)
        int minute = (playtime / 1000) / 60;
        int sec = (playtime / 1000) % 60;
        duration = minute + ":" + sec;
        show_time.setText("0:00 / " + duration);

        play = (ImageButton) findViewById(R.id.myproject_play);
        pause = (ImageButton) findViewById(R.id.myproject_pause);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(initial_play) {//최초 play

                    mediaPlayer.start();


                    sb.setMax(playtime); // 시크바 최대 범위를 노래 재생시간으로 설정
                    new MyThread().start(); // 시크바 그려줄 thread 시작
                    isPlaying = true;
                    initial_play = false;
                }

                else{
                    mediaPlayer.seekTo(pos);//멈춘 시점부터 재시작
                    mediaPlayer.start();
                    isPlaying = true;
                    new MyThread().start();
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                isPlaying = false;
            }
        });




        listView = (ListView) findViewById(R.id.lv_myproject_commit);
        adapter = new ListViewMyProjectAdapter();
        adapter.setActivity(MyProjectActivity.this);

        /*ListViewMyProjectDTO dto_1 = new ListViewMyProjectDTO();
        dto_1.setName("Vocal Added");
        dto_1.setMp3_id(R.raw.fiction);
        adapter.addItem(dto_1);

        ListViewMyProjectDTO dto_2 = new ListViewMyProjectDTO();
        dto_2.setName("Drum Added");
        dto_2.setMp3_id(R.raw.meteor);
        adapter.addItem(dto_2);

        ListViewMyProjectDTO dto_3 = new ListViewMyProjectDTO();
        dto_3.setName("Guitar Added");
        dto_3.setMp3_id(R.raw.home);
        adapter.addItem(dto_3);

        ListViewMyProjectDTO dto_4 = new ListViewMyProjectDTO();
        dto_4.setName("Keyboard Added");
        dto_4.setMp3_id(R.raw.empty);
        adapter.addItem(dto_4);

        ListViewMyProjectDTO dto_5 = new ListViewMyProjectDTO();
        dto_5.setName("Vocal2 Added");
        dto_5.setMp3_id(R.raw.missing);
        adapter.addItem(dto_5);

        ListViewMyProjectDTO dto_6 = new ListViewMyProjectDTO();
        dto_6.setName("Drum2 Added");
        dto_6.setMp3_id(R.raw.stupid);
        adapter.addItem(dto_6);

        ListViewMyProjectDTO dto_7 = new ListViewMyProjectDTO();
        dto_7.setName("Guitar2 Added");
        dto_7.setMp3_id(R.raw.ifyou);
        adapter.addItem(dto_7);

        ListViewMyProjectDTO dto_8 = new ListViewMyProjectDTO();
        dto_8.setName("Keyboard2 Added");
        dto_8.setMp3_id(R.raw.empty);
        adapter.addItem(dto_8);*/

        listView.setAdapter(adapter);



        Button reqSend = (Button) findViewById(R.id.reqSendBtn);
        reqSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyProjectTask mytask = (MyProjectTask) new MyProjectTask().execute("http://192.168.0.112:3001/api/project/detail");

            }
        });



    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }




    public class MyProjectTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                //JSONObject 만들고 key value 형식으로 값 저장
                JSONObject jsonObject = new JSONObject();

                if(urls[0].contains("/api/project/detail")){//project detail request
                    jsonObject.accumulate("projectID", "sample_project");
                }

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {

                    URL url = new URL(urls[0]);
                    //연결
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST 방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application jSON 형식으로 전송
                    con.setRequestProperty("Accept", "application/json");
                    //con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream 으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미

                    con.connect();

                    //서버로 보내기위해서 스트림 생성
                    OutputStream outStream = con.getOutputStream();

                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    System.out.println("@@@@@@@@@@@@@@@@@end of asynctask");

                    //서버로 부터 받은 값을 리턴
                    return buffer.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @SuppressLint("ResourceType")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //System.out.println(result);
            try {
                JSONObject json = new JSONObject(result);
                JSONArray json_array = json.getJSONArray("commits");

                for(int i=0;i<json_array.length();i++){
                    JSONObject commitObject = json_array.getJSONObject(i);//commitObject includes date, artistID, commitID, category. We need commitID.

                    String mp3_name = commitObject.getString("commitID");

                    //downLoad2App down2app = (downLoad2App) new downLoad2App().execute(mp3_name + ".mp3");

                    ListViewMyProjectDTO dto = new ListViewMyProjectDTO();
                    dto.setName(commitObject.getString("commitID"));
                    dto.setMp3_name(commitObject.getString("commitID"));

                    adapter.addItem(dto);

                    adapter.notifyDataSetChanged();

                    System.out.println("mp3 file name : " + commitObject.getString("commitID"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //System.out.println(result);

        }

    }


}
