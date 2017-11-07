package uncc.sivalingam.chat_room;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    static String STATIC_URL = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api";
    static String STATUS_OK = "ok";
    static String STATUS_ERR = "error";
    static String USER_TOKEN = "";
    EditText emailLogin, pwdLogin;
    Button login, signUp;
    boolean validUsr;
    String emailUser, pwdUser;
    Response response = null;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Chat Room");


        emailLogin = (EditText) findViewById(R.id.loginEmail);
        pwdLogin = (EditText) findViewById(R.id.loginPassword);
        login = (Button) findViewById(R.id.loginButton);
        signUp = (Button) findViewById(R.id.signupButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailLogin != null && pwdLogin != null && emailLogin.getText().toString().length() != 0 && pwdLogin.getText().toString().length() != 0) {
                    // if the input is not empty
                    emailUser = emailLogin.getText().toString();
                    pwdUser = pwdLogin.getText().toString();
                    validUsr = validUser(emailUser, pwdUser); // check valid user or not
                    if (validUsr) {
                        // redirect to Message Thread
                        Intent i = new Intent(MainActivity.this, MessageThreadsActivity.class);
                        startActivity(i);
                    } else {
                        // invalid user ask him to signUp
                        Toast.makeText(MainActivity.this, "Invaid user please SignUp", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // if the inputs are empty ask him to enter a valid Input
                    Toast.makeText(MainActivity.this, "Please enter a valid input", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Check whether the input is Empty or Not


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Using Intent go to SignUpActivity to perform SignUp Action
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });



    }

    private boolean validUser(String email, String pwd) {
// check user by an API call
        // Build Request for the POST Method
        Log.d("MainActivity",email);
        RequestBody requestBody = new FormBody.Builder().add("email", email).add("password", pwd).build(); // POST Request form body -> type form url encoded
        Request request = new Request.Builder().header("Content-Type","application/x-www-form-urlencoded").url(STATIC_URL +"/login").post(requestBody).build(); // Request Object is prepared to send


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });




        return false;

    }



    private void paresJson(String s) throws JSONException {
        JSONObject root = new JSONObject(s);
        String status = root.getString("status");
        if (status.equals(STATUS_OK)) {
            USER_TOKEN = root.getString("token");
        }
    }



}
