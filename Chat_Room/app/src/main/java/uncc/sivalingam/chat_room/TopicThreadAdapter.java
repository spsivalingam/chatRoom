package uncc.sivalingam.chat_room;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sivalingam on 11/7/2017.
 */

public class TopicThreadAdapter extends ArrayAdapter {
        Context context;
    MessageThreadsActivity messageThreadsActivity;
        List objects;
        ArrayList<ThreadMsg> topicList;
        long logedInUser_Id;

    public TopicThreadAdapter(Context context, List objects, long user_id) {
        super(context,R.layout.custom_listview_thread_topic,objects);
        this.context=context;
        messageThreadsActivity= (MessageThreadsActivity) context;
        this.objects=objects;
        this.topicList= (ArrayList<ThreadMsg>) objects;
        logedInUser_Id=user_id;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
        if(convertView==null){
           LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_thread_topic,parent,false);
            holder = new ViewHolder();
            holder.topic = (TextView) convertView.findViewById(R.id.topicTextView);
            holder.delete = (Button) convertView.findViewById(R.id.deleteTopicButton);
            convertView.setTag(holder);
        }

        holder= (ViewHolder) convertView.getTag();
        holder.topic.setText(topicList.get(position).getTopic());
        User user = topicList.get(position).getCreatedUser();
        /* Delete button should be displayed only for the Topics created by the logged in User

         */
        if (logedInUser_Id!=user.getUser_id()){
            holder.delete.setEnabled(false);
            holder.delete.setVisibility(convertView.GONE);
        }
        else{
            holder.delete.setVisibility(convertView.VISIBLE);
            holder.delete.setEnabled(true);
        }

/*
Listner to delete threads or topics from the list based on the creation
 */
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageThreadsActivity.deleteTopicThread(position);
            }
        });


        /*
        Listner to open chatRoom based on the topic clicked - Listner to listen for the event
         */

        holder.topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageThreadsActivity.callChatView(position);
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView topic;
        Button delete;
    }
}
