package uncc.sivalingam.chat_room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
EditText fname,lname,emailSign, chPwd, rptPwd;
    Button cancel, signup;
    Response response=null;
    private SharedPreferences.Editor sharedPrefEditor=null;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        fname = (EditText)findViewById(R.id.fnameSignUp);
        lname = (EditText)findViewById(R.id.lnameSignUp);
        emailSign = (EditText)findViewById(R.id.emailSignUp);
        chPwd = (EditText)findViewById(R.id.passwordSignUp);
        rptPwd = (EditText)findViewById(R.id.checkpasswordSignUp);
        signup = (Button)findViewById(R.id.signButtonUp);
        cancel = (Button)findViewById(R.id.cancelButton);
        sharedPrefEditor = getSharedPreferences(MainActivity.SHARED_FILE,MODE_PRIVATE).edit();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidInput()){

                    String pwd = chPwd.getText().toString();
                    String rptpwd = rptPwd.getText().toString();
                    String fName = fname.getText().toString();
                    String lName = lname.getText().toString();
                    String email = emailSign.getText().toString();
                    if(!email.contains("@")){
                        Toast.makeText(SignUpActivity.this, "Enter a valid E-mailId", Toast.LENGTH_SHORT).show();
                        emailSign.setText("");
                    }
                    else if(!pwd.equals(rptpwd)){
                        // Toast ask  both pwd doesn't match
                        Toast.makeText(SignUpActivity.this, "Passwords Doesn't Match", Toast.LENGTH_SHORT).show();
                        rptPwd.setText("");
                        chPwd.setText("");
                    }
                    else{
                        try {
                            registerUser(fName,lName,email,pwd);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                }
                else{
                    // Toast to ask the user to enter valid inputs
                    Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



    // Check Whether the input is Valid
    private boolean isValidInput() {
        if(fname!=null && lname!=null && emailSign!=null && chPwd!=null && rptPwd!=null && fname.getText().length()!=0 && lname.getText().length()!=0 && emailSign.getText().length()!=0 && chPwd.getText().length()!=0 && rptPwd.getText().length()!=0  ){
            return true;
        }
        return false;
    }

    private void registerUser(String fName,String lName,String email,String pwd) throws JSONException, IOException {
        RequestBody requestbody = new FormBody.Builder().add("email",email).add("password",pwd).add("fname",fName).add("lname",lName).build();

        Request request = new Request.Builder().url(MainActivity.STATIC_URL+"/signup").post(requestbody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // 200 status code
                SignUpActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            paresJson(response.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    private void paresJson(String s) throws JSONException {
        JSONObject root = new JSONObject(s);
        String status = root.getString("status");
        if (status.equals(MainActivity.STATUS_OK)) {
            String token= root.getString("token");
            String fname= root.getString("token");
            String lname= root.getString("token");
            User userLogedIn = new User(fname, lname, root.getString("user_email"), root.getInt("user_id"),token );
// Storing data in shared Preferences
            Gson gson = new Gson();
            String user = gson.toJson(userLogedIn);
            sharedPrefEditor.putString(MainActivity.USER,user);
            sharedPrefEditor.putString(MainActivity.USER_TOKEN,token);
            sharedPrefEditor.putString(MainActivity.USER_NAME,fname+" "+lname);
            sharedPrefEditor.apply();
            Intent i = new Intent(SignUpActivity.this, MessageThreadsActivity.class);
            startActivity(i);
        }
        else if (status.equals(MainActivity.STATUS_ERR)){
            String msg = root.getString("message");
            if(msg.contains(emailSign.getText().toString()))
            Toast.makeText(SignUpActivity.this,"Email already exists",Toast.LENGTH_LONG).show();
        }
    }
}
