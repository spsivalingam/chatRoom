package uncc.sivalingam.chat_room;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageThreadsActivity extends AppCompatActivity {
TextView fullName;
    EditText addThread;
    ListView listViewBody;
    Button logout,addThreadButton;
    static final String NEW_TITLE="title"; // Key to hold the topic text sent throu the form body in okHttp
    ArrayList<ThreadMsg> topicList = null;
    String userName;
    String data;
    final private OkHttpClient client = new OkHttpClient();
    private SharedPreferences.Editor sharedPrefEditor=null;
    private SharedPreferences sharedPref =null;
    ArrayAdapter adapter;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);
        setTitle("Message Threads");
        sharedPrefEditor = getSharedPreferences(MainActivity.SHARED_FILE,MODE_PRIVATE).edit();
        sharedPref = getSharedPreferences(MainActivity.SHARED_FILE,MODE_PRIVATE);
        logout = (Button) findViewById(R.id.logoutButton);
        addThreadButton = (Button) findViewById(R.id.addThreadButton);
        fullName = (TextView) findViewById(R.id.fullNameText);
        listViewBody = (ListView) findViewById(R.id.listViewThreads);
        addThread = (EditText)findViewById(R.id.addThreadText);
        userName = sharedPref.getString(MainActivity.USER_NAME,"");
        token = sharedPref.getString(MainActivity.USER_TOKEN,"");
        if (userName.length()> 0){
            fullName.setText(userName);
        }

/* Get all the topics using an API call to /thread

 */
        getAllThreads();



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefEditor.clear();
                sharedPrefEditor.commit();
                finish();
            }
        });


        addThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addThread!=null && addThread.getText().toString().length()!=0){
                    addnewThread(addThread.getText().toString());
                }

            }
        });

    }

    private void getAllThreads() {

        Request request = new Request.Builder().url(MainActivity.STATIC_URL+"/thread").header(MainActivity.AUTH_KEY,MainActivity.AUTH+" "+token).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                    data = response.body().string();
                MessageThreadsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        topicList=new ArrayList<ThreadMsg>();
                        Log.d("DATA",data);
                        parseJson(data);
                        String userJson = sharedPref.getString(MainActivity.USER,"");
                        if(userJson.length()> 0) {
                            Gson gson = new Gson();
                            User userLogedIn =gson.fromJson(userJson,User.class);
                            //After consuming the API and parsing the data now we can populate the Adapter and send it to the ListView
                             adapter = new TopicThreadAdapter(MessageThreadsActivity.this, topicList,userLogedIn.getUser_id());
                            listViewBody.setAdapter(adapter);
                        }
                    }
                });
            }
        });
    }

    private void parseJson(String s){

        try {
            JSONObject root = new JSONObject(s);
            JSONArray arrayThreads = root.getJSONArray("threads");
            JSONObject objthread;
            for (int i=0; i< arrayThreads.length();i++){
                objthread=arrayThreads.getJSONObject(i);

                User user = new User(objthread.getString("user_fname"),objthread.getString("user_lname"),Long.parseLong(objthread.getString("user_id")));
                //ThreadMsg(long thread_id, String topic, User createdUser, Date creationStamp)
                ThreadMsg threadMsg = new ThreadMsg(Long.parseLong(objthread.getString("id")),objthread.getString("title"),user,objthread.getString("created_at"));
                topicList.add(threadMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    private void addnewThread(String textTopic){
        Log.d("TAG","Request to Add");
        RequestBody body = new FormBody.Builder().add(NEW_TITLE,textTopic).build();
        Request request = new Request.Builder().url(MainActivity.STATIC_URL+"/thread/add").header(MainActivity.AUTH_KEY,MainActivity.AUTH+" "+token)
                .post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("RESPONSE",response.body().string());
                // On successfully adding the new Topic update the ListView
                MessageThreadsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        The method getAllThreads is re-invoked to pull the updated records and update the ListView
                         */
                        getAllThreads();
                    }
                });
            }
        });
    }
    public void deleteTopicThread(int position) {
        long thread_id;
        Request request;
        String auth= MainActivity.AUTH+" "+token;


        thread_id =topicList.get(position).getThread_id();  // got the thread_ID to be deleted

        request = new Request.Builder().header(MainActivity.AUTH_KEY,auth).url(MainActivity.STATIC_URL+"/thread/delete/"+String.valueOf(thread_id))
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                data = response.body().string();
                MessageThreadsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkResponse(data);
                    }
                });
            }
        });
    }


    private void checkResponse(String s){
        try {
            JSONObject root = new JSONObject(s);
            String status = root.getString("status");
            if (status.equals(MainActivity.STATUS_OK)){
                getAllThreads();
            }
            else{
                Toast.makeText(MessageThreadsActivity.this,"Delete Failed",Toast.LENGTH_LONG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
