package com.project.chitchat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton,backButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    TextView receiver;
    Uri alarmSound;
    MediaPlayer mp;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(UserDetails.chatWith);
        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);


        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mp = MediaPlayer.create(Chat.this,alarmSound);
        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://chit-chatz-d65d4.firebaseio.com/messages/" + SaveSharedPreference.getUserName(this) + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://chit-chatz-d65d4.firebaseio.com/messages/" + UserDetails.chatWith + "_" + SaveSharedPreference.getUserName(this));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                flag = 1;
                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", SaveSharedPreference.getUserName(Chat.this));
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(SaveSharedPreference.getUserName(Chat.this))){
                    addMessageBox(message, 1);
                }
                else{
                    addMessageBox(message, 2);
                    if(flag!=0)
                    {
                        mp.start();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.cellshape);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.cellshape);
        }
        textView.setTextSize(15);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(15,20,15,20);
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Chat.this,Users.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
