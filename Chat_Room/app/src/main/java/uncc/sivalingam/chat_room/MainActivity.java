package uncc.sivalingam.chat_room;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

public class MainActivity extends AppCompatActivity {
    static String STATIC_URL = "http://ec2-54-164-74-55.compute-1.amazonaws.com/api";
    static String STATUS_OK = "ok";
    static String STATUS_ERR = "error";
    static String AUTH="BEARER";
    static String AUTH_KEY="Authorization";
    static String USER="USER";
    static final String SHARED_FILE="SHARED";
    static final String USER_TOKEN = "USER_TOKEN"; // key to store the token
    static final String USER_NAME="NAME";
    EditText emailLogin, pwdLogin;
    Button login, signUp;
    String emailUser, pwdUser;
    Response response = null;
    String data=null;
    private SharedPreferences.Editor sharedPrefEditor =null;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Chat Room");
        sharedPrefEditor = getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE).edit(); // Create a file if not exist or grab the
        // file from the system

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
                    validUser(emailUser, pwdUser); // check valid user or not




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

    private void validUser(String email, String pwd) {
// check user by an API call
        // Build Request for the POST Method
        Log.d("MainActivity",email);
        RequestBody requestBody = new FormBody.Builder().add("email", email).add("password", pwd).build(); // POST Request form body -> type form url encoded
        Request request = new Request.Builder().url(STATIC_URL +"/login").post(requestBody).build(); // Request Object is prepared to send


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                data= response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            paresJson(data);

                        } catch (JSONException e) {
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
        if (status.equals(STATUS_OK)) {

            String token = root.getString("token");
            String fname = root.getString("user_fname");
            String lname = root.getString("user_lname");

            User userLogedIn = new User(fname, lname, root.getString("user_email"), root.getInt("user_id"),token );
            /* Using a Gson object to convert the parsed and formated user object to json object, since shared preference can hold only string since
            it is a file (shared Preference stores data in a file)
             */
            Gson gson = new Gson();
            String user = gson.toJson(userLogedIn);
            sharedPrefEditor.putString(USER,user);
            sharedPrefEditor.putString(USER_TOKEN,token);
            sharedPrefEditor.putString(USER_NAME,fname+" "+lname);
            sharedPrefEditor.apply();

            // redirect to Message ThreadMsg
            Intent i = new Intent(MainActivity.this, MessageThreadsActivity.class);
            startActivity(i);
        }
        else{
            // invalid user ask him to signUp
            Toast.makeText(MainActivity.this, "Invaid user please SignUp", Toast.LENGTH_LONG).show();
        }
    }



}
