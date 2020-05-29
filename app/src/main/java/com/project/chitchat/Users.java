package com.project.chitchat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Users extends AppCompatActivity {

    ListView usersList;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        if(SaveSharedPreference.getUserName(Users.this).length() == 0)
        {
            Intent i = new Intent(Users.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            ActionBar actionBar = this.getSupportActionBar();
            actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            ImageView imageView = new ImageView(actionBar.getThemedContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.ic_exit);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            layoutParams.rightMargin = 40;
            imageView.setLayoutParams(layoutParams);
            actionBar.setCustomView(imageView);

            usersList = (ListView)findViewById(R.id.usersList);
            noUsersText = (TextView)findViewById(R.id.noUsersText);
            logout = findViewById(R.id.logout);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveSharedPreference.clearUserName(Users.this);
                    Toast.makeText(Users.this,"Logged out successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Users.this,MainActivity.class));
                    finish();
                }
            });

            pd = new ProgressDialog(Users.this);
            pd.setMessage("Loading...");
            pd.show();

            String url = "https://chit-chatz-d65d4.firebaseio.com/userss.json";

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    doOnSuccess(s);
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                    pd.dismiss();
                    Toast.makeText(Users.this,"Network Error",Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(Users.this);
            rQueue.add(request);

            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserDetails.chatWith = al.get(position);
                    startActivity(new Intent(Users.this, Chat.class));
                    finish();
                }
            });
        }
    }

    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(SaveSharedPreference.getUserName(this))) {
                    al.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, al){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){

                    View view = super.getView(position, convertView, parent);

                    TextView text = (TextView) view.findViewById(android.R.id.text1);

                    text.setTextColor(Color.parseColor("#ffffff"));
                    text.setTextSize(25);
                    return view;
                }
            });
        }
        pd.dismiss();
    }
}
