/*
 * RvMessageAdapter
 * D:/course/geochat-client-master/app/src/main/java/co/siarhei/apps/android/geochat/UI/Adapters/RvMessageAdapter.java
 *
 * Project: geochat-client-master
 * Module: app
 * Last Modified: 17.12.19 19:17 <dexte>
 * Last Compilation: 17.12.19 19:17
 *
 * Copyright (c) 2019. Martin David Shaw. All rights reserved.
 */

package co.siarhei.apps.android.geochat.UI.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;


import java.util.List;

import co.siarhei.apps.android.geochat.ChatActivity;
import co.siarhei.apps.android.geochat.Model.Message;
import co.siarhei.apps.android.geochat.R;

public class RvMessageAdapter extends RecyclerView.Adapter<RvMessageAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> Messages;
    private String googleAPIKey;

    public RvMessageAdapter(Context mContext, List<Message> mMessages ) {
        this.mContext = mContext;
        this.Messages = mMessages;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.component_messagelist_item, viewGroup, false);
        getGoogleAPIKey();
        return new RvMessageAdapter.ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Message message = Messages.get(i);

       // viewHolder.firstLine.setText(message.origin_lat+" "+message.origin_long);
        viewHolder.icon_cont.setText( (message.getSender().charAt(0)+"").toUpperCase());
        viewHolder.secondLine.setText(message.contents);
        viewHolder.author.setText("from "+message.sender);
        viewHolder.isAnon = message.is_anonymaus;
        if(message.is_anonymaus){
            viewHolder.marker.setVisibility(View.GONE);
        } else {
            viewHolder.marker.setVisibility(View.VISIBLE);
        }


            RxView.clicks(viewHolder.marker).subscribe((aVoid)-> Toast.makeText(mContext,"Got to map",Toast.LENGTH_SHORT).show() );
            RxView.clicks(viewHolder.itemView)
                    .subscribe((aVoid)->{
                        if(viewHolder.isAnon){
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("This message is Anonymous, you can't connect this user")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", (dialog, id) -> {

                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.putExtra("receiver_id",message.getUser_id());
                            mContext.startActivity(intent);
                        }

                    });




    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView icon_cont;
        TextView firstLine;
        TextView secondLine;
        TextView author;
        ImageView marker;
        boolean isAnon;


        public ViewHolder (View itemView){
            super(itemView);
            icon_cont = itemView.findViewById(R.id.icon_cont);
            //firstLine = itemView.findViewById(R.id.firstLine);
            secondLine= itemView.findViewById(R.id.secondLine);
            author = itemView.findViewById(R.id.author);
            marker = itemView.findViewById(R.id.location_marker);


        }
    }



    public List<Message> getMessages() {
        return Messages;
    }

    public void setMessages(List<Message> Messages) {
        this.Messages = Messages;
        notifyDataSetChanged();
    }
    public void addMessage( Message msg) {

        this.Messages.add(msg);
        notifyDataSetChanged();

    }
    public void resetMessages() {
        this.Messages.clear();
        notifyDataSetChanged();
    }


    public void getGoogleAPIKey(){
        try {
            ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo( mContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            this.googleAPIKey = bundle.getString("com.google.android.geo.API_KEY");
            Log.i("GETTINGAPIKEY", "Found Google Maps API Key: "+this.googleAPIKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("GETTINGAPIKEY", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("GETTINGAPIKEY", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }
}
