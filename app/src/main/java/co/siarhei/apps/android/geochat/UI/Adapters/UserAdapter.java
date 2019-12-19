package co.siarhei.apps.android.geochat.UI.Adapters;

import android.content.Context;;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.rxbinding3.view.RxView;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.siarhei.apps.android.geochat.ChatActivity;
import co.siarhei.apps.android.geochat.Model.User;
import co.siarhei.apps.android.geochat.R;
import co.siarhei.apps.android.geochat.Utils.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final SharedPreferences prefs;
    private Context mContext;
    private List<User> mUsers;
    private CompositeDisposable cd = new CompositeDisposable();


    public UserAdapter(Context mContext, List<User> mUsers ) {
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences("geochat", Context.MODE_PRIVATE);
        setUsers( mUsers);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = mUsers.get(i);
        viewHolder.username.setText(user.getFirst_name()+" "+user.getLast_name());
        if(Util.isOnline(user.getUpdated_at()))
            viewHolder.online_indicator.setVisibility(View.VISIBLE);
        else {
            viewHolder.online_indicator.setVisibility(View.GONE);
        }

        Location location = new Location("");

        location.setLatitude(user.getLocation().getLat());
        location.setLongitude(user.getLocation().getLng());

        Location myLocation = Util.getCurrentLocation(prefs);
        float distance = myLocation.distanceTo(location); //METERS
        viewHolder.location.setText("is "+distance+" meters from you");
        Picasso.get().load("https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909__340.png").into(viewHolder.profile_image);

        Disposable dp = RxView.clicks(viewHolder.itemView)
                .subscribe((aVoid) -> {

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("receiver_id", user.getFireID());
                    mContext.startActivity(intent);

                });
        cd.add(dp);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout online_indicator;
        TextView location;
        TextView username;
        CircleImageView profile_image;




        public ViewHolder (View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            location= itemView.findViewById(R.id.location);
            online_indicator = itemView.findViewById(R.id.online_indicator);
        }
    }

    public void setUsers(List<User> users ){
        users.removeIf(user -> user.getFireID().equals(FirebaseAuth.getInstance().getUid()));

        this.mUsers = users;
    }
    public void addUser(User user ){
       User found = this.mUsers
                .stream()
                .filter(usr -> usr.getFireID().equals(user.getFireID() ))
                .findFirst().orElse(null);


        if(found!=null) {
            mUsers.set(mUsers.indexOf(found), user);
            notifyDataSetChanged();
        }  else {
            this.mUsers.add(user);
        }


    }
    public void resetUsers( ){
        this.mUsers.clear();
    }
    public void updateUser(User user) {


    }

}
