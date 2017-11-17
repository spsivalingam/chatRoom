package uncc.sivalingam.chat_room;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sivalingam on 11/12/2017.
 */

public class ChartRoomAdapter extends ArrayAdapter {
ArrayList<Message> msgList;
    ChatRoomActivity chatRoomActivity;
Context context;
    Long userLoggedIn;
    public ChartRoomAdapter(Context context,  List objects,Long loggedin) {
        super(context, R.layout.custom_msgview_listview, objects);
        msgList = (ArrayList<Message>) objects;
        chatRoomActivity = (ChatRoomActivity) context;
        this.context=context;
        userLoggedIn=loggedin;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChartRoomAdapter.ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_msgview_listview,parent,false);
            holder = new ChartRoomAdapter.ViewHolder();
            holder.msg = (TextView) convertView.findViewById(R.id.textviewMessageData);
            holder.pettyTime = (TextView) convertView.findViewById(R.id.textviewPettyTime);
            holder.senderName = (TextView) convertView.findViewById(R.id.textviewMsgName);
            holder.delete = (Button) convertView.findViewById(R.id.buttonMsgDelete);
            convertView.setTag(holder);
        }
        Message msg = msgList.get(position);
        User u = msg.getUser();
        PrettyTime p = new PrettyTime();
        holder= (ViewHolder) convertView.getTag();
        holder.msg.setText(msg.getMsg());
        holder.senderName.setText(u.getfName()+" "+u.getlName());
        // sf -> pass the constrctor with expected input string format
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
            // using sf  to parse the string to date format
            Date d = sf.parse(msg.getCreated_At());
            Log.d("TAG",d.toString());
            Log.d("TAG-Petty",p.format(d));
            // pass the date as input to pettytime and the output is processed string format
            holder.pettyTime.setText(p.format(d));
            if (userLoggedIn!=u.getUser_id()){
                holder.delete.setEnabled(false);
                holder.delete.setVisibility(convertView.GONE);
            }
            else{
                holder.delete.setVisibility(convertView.VISIBLE);
                holder.delete.setEnabled(true);
            }

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatRoomActivity.deleteMsg(msgList.get(position).getThread_id());
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }






        return convertView;
    }


    static class ViewHolder{
        TextView pettyTime;
        TextView msg;
        TextView senderName;
        Button delete;
    }
}
