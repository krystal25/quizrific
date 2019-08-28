package com.example.dcris.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dcris.myapplication.misc.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameET,emailET,passET,firstnameET,lastnameET;
    private Button createAccBtn;
    private  TextView wrongTV;
    private RadioGroup usertypeRG;
    private RadioButton usertypeRB;
    private RequestQueue requestQueue;
    private static final String URL = "http://" + Common.serverIP + "/quizrific/user_registration.php";
    private StringRequest request;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usertypeRG = findViewById(R.id.signup_rg_accType);

        usernameET = findViewById(R.id.signup_et_user);
        emailET = findViewById(R.id.signup_et_email);
        passET = findViewById(R.id.signup_et_pass);
        firstnameET= findViewById(R.id.signup_et_firstname);
        lastnameET = findViewById(R.id.signup_et_lastname);

        wrongTV = findViewById(R.id.signup_tv_wrong);
        createAccBtn= findViewById(R.id.signup_btn_createAccount);


        createAccBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                wrongTV.setText("");

                if(usernameET.getText().toString().equals("") ||
                        emailET.getText().toString().equals("") || passET.getText().toString().equals("") ||
                    firstnameET.getText().toString().equals("") ||
                        lastnameET.getText().toString().equals("")
                )
                {
                    wrongTV.setText("Complete all the fields");
                    return;
                }

                if(!validate(emailET.getText().toString())){
                    wrongTV.setText("Invalid e-mail address");
                    return;
                }


                // get selected radio button from radioGroup
                int selectedId = usertypeRG.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                usertypeRB = findViewById(selectedId);
                if(selectedId < 0){
                    wrongTV.setText("Select type of account");
                    return;
                }

               // Toast.makeText(getApplicationContext(),
                //        usertypeRB.getText(), Toast.LENGTH_SHORT).show();

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Toast.makeText(getApplicationContext(),jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
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
                        HashMap<String,String> hashMap = new HashMap<String,String>();
                        hashMap.put("email",emailET.getText().toString());
                        hashMap.put("username",usernameET.getText().toString());
                        hashMap.put("password",passET.getText().toString());
                        hashMap.put("firstname",firstnameET.getText().toString());
                        hashMap.put("lastname",lastnameET.getText().toString());
                        hashMap.put("usertype",usertypeRB.getText().toString().toLowerCase());
                        return hashMap;
                        //return super.getParams();
                    }
                };
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

            }

        });

    }
}
