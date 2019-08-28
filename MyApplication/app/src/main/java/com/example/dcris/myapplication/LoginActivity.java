package com.example.dcris.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.KeyValueDB;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    private EditText usernameET,passET;
    private Button loginBtn;
    private TextView wrongTV;
    private TextView createAccTV;
    private RequestQueue requestQueue;
    private static final String URL = "http://" + Common.serverIP + "/quizrific/user_login.php";
    private StringRequest request;

    public static final String SHARED_PREFS = "sharedPrefs";
    private String username =null;
    private String userType=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wrongTV = findViewById(R.id.main_tv_wrong);
        usernameET = findViewById(R.id.main_et_user);
        passET = findViewById(R.id.main_et_pass);
        loginBtn = findViewById(R.id.main_btn_login);

        loadData();

        //requestQueue = VolleySingleton.getInstance(this).getRequestQueue(); //initialize

        createAccTV = findViewById(R.id.main_tv_createAcc);
        createAccTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Intent launchActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(launchActivity);
            }
        });

       loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wrongTV.setText("");


                if (usernameET.getText().toString().equals("") || passET.getText().toString().equals("")) {
                    wrongTV.setText("Complete all the fields");
                    return;
                }

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                username = usernameET.getText().toString();
                                //Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                if (jsonObject.getString("success").equals("professor")) {
                                    goToProfMenu();
                                   userType = jsonObject.getString("success");
                                } else if (jsonObject.getString("success").equals("student")) {
                                    goToMenu();
                                    userType = jsonObject.getString("success");
                                }
                                KeyValueDB.setUsername(getApplicationContext(), username);
                                KeyValueDB.setUsertype(getApplicationContext(), userType);
                               saveData(userType);
                            } else {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }){
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("username",usernameET.getText().toString());
                        hashMap.put("password",passET.getText().toString());
                        return hashMap;
                        //return super.getParams();
                    }
                };
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        });

            }

    private void goToProfMenu() {
        Intent i = new Intent(this,MenuProfActivity.class);
        startActivity(i);
       finish();
    }

    public void goToMenu(){
        Intent i = new Intent(this,MenuActivity.class);
        startActivity(i);
        finish();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if(sharedPreferences.getBoolean("logged",false)){
            String userType = sharedPreferences.getString("usertype","");
            if(userType.equals("student")){
                goToMenu();
            }else if(userType.equals("professor")){
                goToProfMenu();
            }
        }
    }

    public void saveData(String userType) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("logged", true);
        editor.putString("usertype", userType);

        editor.apply();
    }


}
