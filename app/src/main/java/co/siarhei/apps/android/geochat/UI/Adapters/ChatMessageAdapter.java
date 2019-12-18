package co.siarhei.apps.android.geochat.UI.Adapters;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.siarhei.apps.android.geochat.Model.Message;
import co.siarhei.apps.android.geochat.Model.PersonalMessage;
import co.siarhei.apps.android.geochat.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT= 1;
    private static final int MSG_TYPE_GEO = 2;
    private Context mContext;
    private List<PersonalMessage> mMessages;
    private String imageurl;


    public ChatMessageAdapter(Context mContext, List<PersonalMessage> mMessages, String imageurl) {
        this.mContext = mContext;
        this.mMessages = mMessages;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, viewGroup, false);
            return new ChatMessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, viewGroup, false);
            return new ChatMessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder viewHolder, int i) {
        PersonalMessage msg = mMessages.get(i);
        viewHolder.message_text.setText(msg.getContents());

        if(imageurl.equals("default") ){
            Picasso.get().load("https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909__340.png").into(viewHolder.profile_image);
        } else{
            Picasso.get().load(imageurl).into(viewHolder.profile_image);

        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message_text;
        CircleImageView profile_image;


        ViewHolder(View itemView){
            super(itemView);
            message_text = itemView.findViewById(R.id.message_text);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mMessages.get(position).getFrom_id().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public List<PersonalMessage> getMessages() {
        return mMessages;
    }

    public void setMessages(List<PersonalMessage> Messages) {
        this.mMessages = Messages;
        notifyDataSetChanged();
    }
    public void addMessage( PersonalMessage msg) {

        this.mMessages.add(msg);
        notifyDataSetChanged();

    }
    public void resetMessages() {
        this.mMessages.clear();
        notifyDataSetChanged();
    }
}
