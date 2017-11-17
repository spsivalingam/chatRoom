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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatRoomActivity extends AppCompatActivity {
    Button send,home;
    TextView topicName;
    ListView listViewMsg;
    EditText newMsg;
    String data;
    String token;
    Long thread_id;
    Long userLoggedIn;
    ArrayList<Message> msgList;
    private SharedPreferences.Editor sharedPrefEditor=null;
    private SharedPreferences sharedPref =null;
    ArrayAdapter adapter;
    final private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        setTitle("Chatroom");


        sharedPref = getSharedPreferences(MainActivity.SHARED_FILE,MODE_PRIVATE);
        newMsg = (EditText) findViewById(R.id.addMsg);
        send = (Button) findViewById(R.id.msgsendButton);
        home = (Button) findViewById(R.id.homeButton);
        topicName = (TextView) findViewById(R.id.textViewTopic);
        listViewMsg = (ListView) findViewById(R.id.listviewMsg);
        token = sharedPref.getString(MainActivity.USER_TOKEN,"");

        Bundle b  = getIntent().getExtras().getBundle("MESSAGELIST");
        msgList = (ArrayList<Message>) b.getSerializable("MESSAGE");
        String topic = getIntent().getExtras().getString("MESSAGE_TOPIC");
     //   Log.d("TAG-ChatRoom",String.valueOf(msgList.size()));

        thread_id = getIntent().getExtras().getLong("THREAD_ID");
        userLoggedIn = getIntent().getExtras().getLong("USER_ID");
        topicName.setText(topic); // Set the topic of the page currently in based on the Message Topic grabbed
        if(msgList!=null && msgList.size()!=0){
            adapter = new ChartRoomAdapter(ChatRoomActivity.this, msgList,userLoggedIn);
            listViewMsg.setAdapter(adapter);
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newMsg!=null && newMsg.getText().length()!=0){
                    Log.d("TAG","Send msg called");
                    addNewMsg(newMsg.getText().toString());

                }

            }
        });

    }

    public void addNewMsg(String msg){
        RequestBody body = new FormBody.Builder().add("message",msg).add("thread_id",String.valueOf(thread_id)).build();

        Request request = new Request.Builder().url(MainActivity.STATIC_URL+"/message/add").header(MainActivity.AUTH_KEY,MainActivity.AUTH+" "+token)
                                .post(body).build();

        Log.d("TAG","MSG body buit and call is made");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                data= response.body().string();
                try {
                    JSONObject root = new JSONObject(data);
                    if(root.getString("status").equals(MainActivity.STATUS_OK)){
                        ChatRoomActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("TAG","Response is success and updated the view");
                                updateData();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void updateData(){
        Log.d("TAG","Msg are being collected using id");
    getMsg(thread_id);

    }

    public void getMsg(long thread_id){
        Request request = new Request.Builder().header(MainActivity.AUTH_KEY,MainActivity.AUTH+" "+token)
                .url(MainActivity.STATIC_URL+"/messages/"+thread_id).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                data = response.body().string();
                ChatRoomActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG","Parsing the new set of collected data");
                        Log.d("TAG-RESP",data);
                        parseAllMsg(data);
                      // adapter.setNotifyOnChange(true);
                    }
                });
            }
        });
    }
    private void parseAllMsg(String data){
        try {
            JSONObject root = new JSONObject(data);
            if (root.getString("status").equals(MainActivity.STATUS_OK)){
                JSONArray msgArray = root.getJSONArray("messages");
                msgList= new ArrayList<>();
                Message msg =null;
                for(int i=0; i< msgArray.length();i++){
                    JSONObject msgObj = (JSONObject) msgArray.get(i);
                    msg = new Message();
                    User user = new User();
                    user.setfName(msgObj.getString("user_fname"));
                    user.setlName(msgObj.getString("user_lname"));
                    user.setUser_id(Long.parseLong(msgObj.getString("user_id")));
                    msg.setMsg(msgObj.getString("message"));
                    msg.setCreated_At(msgObj.getString("created_at"));
                    msg.setThread_id(Long.parseLong(msgObj.getString("id")));
                    msg.setUser(user);
                    msgList.add(msg);
                }
                Log.d("TAG","successful parsing adapter is updated");
                adapter = new ChartRoomAdapter(ChatRoomActivity.this, msgList,userLoggedIn);
                listViewMsg.setAdapter(adapter);
            //adapter.setNotifyOnChange(true);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteMsg(long thread_Id) {
        deleteMsgAPI(thread_Id);
    }
    private void deleteMsgAPI(long thread_Id) {
        Long local_Id=thread_Id;
        Request request = new Request.Builder().url(MainActivity.STATIC_URL+"/message/delete/"+String.valueOf(local_Id))
                .header(MainActivity.AUTH_KEY,MainActivity.AUTH+" "+token).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ChatRoomActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateData();
                    }
                });

            }
        });
    }

}
