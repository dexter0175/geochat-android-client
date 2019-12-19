
package co.siarhei.apps.android.geochat.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jakewharton.rxbinding3.view.RxView;
import com.patloew.rxlocation.RxLocation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import co.siarhei.apps.android.geochat.MainActivity;
import co.siarhei.apps.android.geochat.MapActivity;
import co.siarhei.apps.android.geochat.Model.Message;
import co.siarhei.apps.android.geochat.R;
import co.siarhei.apps.android.geochat.UI.Adapters.RvMessageAdapter;
import co.siarhei.apps.android.geochat.Utils.Util;
import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainThisAreaFragment extends Fragment {


    private Location currentLocation;
    private SharedPreferences prefs;

    private MainActivity mainActivity;

    private OkHttpClient OkHTTP_Client = new OkHttpClient();

    private String googleAPIKey = "";

    private View rootView;
    private LinearLayout mMapButton;
    private TextView mMapButtonTitle;
    private TextView mMapButtonSubtitle;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private CompositeDisposable cd = new CompositeDisposable();
    private ImageButton mSendButton;
    private EditText mTextSend;
    private RvMessageAdapter messageAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_this_area, container, false);
        prefs = this.getActivity().getSharedPreferences("geochat", Context.MODE_PRIVATE);

        mSendButton = rootView.findViewById(R.id.btn_send);
        mTextSend = rootView.findViewById(R.id.text_send);
        mMapButton = rootView.findViewById(R.id.main_this_area_mapbutton);
        mMapButtonTitle = rootView.findViewById(R.id.main_this_area_mapbutton_title);
        mMapButtonSubtitle = rootView.findViewById(R.id.main_this_area_mapbutton_subtitle);
        mainActivity = (MainActivity) getActivity();

        currentLocation = Util.getCurrentLocation(prefs);

        getGoogleAPIKey();

        setupMapButton();
        setupSendButton();
        setupMapButtonGeocoding();
        setupMessageListView();

        return rootView;
    }



    public void setupMessageListView(){

        Query query = firestore.collection("Messages").orderBy("created_at");
        RecyclerView mMessageList = rootView.findViewById(R.id.main_this_area_messagelist);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        mMessageList.setLayoutManager(llm);
        messageAdapter = new RvMessageAdapter(getContext(), new ArrayList<>());
        mMessageList.setAdapter(messageAdapter);

        cd.add(RxFirestore.observeQueryRef(query,Message.class)
                .take(100)
                .subscribe((messages)->{
                    messageAdapter.setMessages(messages);
                    mMessageList.smoothScrollToPosition(messageAdapter.getItemCount());
                    }, throwable -> Log.d("ERR",throwable.toString()))
        );


    }





    public void setCurrentLocation(double _lat, double _long){
        prefs.edit().putLong("locationLat", Double.doubleToRawLongBits(_lat)).apply();
        prefs.edit().putLong("locationLong", Double.doubleToRawLongBits(_long)).apply();
    }

    public void setCurrentLocation(Location _location){
        prefs.edit().putLong("locationLat", Double.doubleToRawLongBits(_location.getLatitude())).apply();
        prefs.edit().putLong("locationLong", Double.doubleToRawLongBits(_location.getLongitude())).apply();
    }

    public void setCurrentRadius(double _radius){
        prefs.edit().putLong("locationRadius", Double.doubleToRawLongBits(_radius)).apply();
    }



    public double getCurrentRadius() {
        return Double.longBitsToDouble(prefs.getLong("locationRadius", 0));
    }



    void getGoogleAPIKey () {

        try {
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            this.googleAPIKey = bundle.getString("com.google.android.geo.API_KEY");
            Log.i("GETTINGAPIKEY", "Found Google Maps API Key: "+this.googleAPIKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("GETTINGAPIKEY", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("GETTINGAPIKEY", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }



    private void setupMapButton() {

        mMapButton.post(() -> {

            int _mapButton_width = mMapButton.getWidth();
            int _mapButton_height = mMapButton.getHeight();

            // Construct Google Map Static image request URL
            String mapButtonStaticImageURL ="https://maps.googleapis.com/maps/api/staticmap" +
                    "?center=" + Util.getCurrentLocation(prefs).getLatitude() + "," + Util.getCurrentLocation(prefs).getLongitude() +
                    "&format=png" +
                    "&zoom=16" +
                    "&size=600x600" +
                    "&maptype=road" +
                    "&key="+googleAPIKey +
                    "&style=feature:poi|visibility:off" +
                    "&style=feature:administrative|visibility:off" +
                    "&style=feature:road|visibility:simplified" +
                    "&style=feature:transit|visibility:off";

            Picasso
                    .get()
                    .load(mapButtonStaticImageURL)
                    .resize(_mapButton_width, _mapButton_height)
                    .centerCrop()
                    .into(new Target(){
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mMapButton.setBackground(new BitmapDrawable(getActivity().getResources(), bitmap));
                            mMapButton.getBackground().setAlpha(160);
                        }

                        @Override
                        public void onBitmapFailed(Exception e,final Drawable errorDrawable) {
                            Log.d("PICASSO", "Failed!");
                        }

                        @Override
                        public void onPrepareLoad(final Drawable placeHolderDrawable) {
                            Log.d("PICASSO", "Prepare Load");
                        }
                    });
        });

        cd.add(
                RxView
                        .clicks(mMapButton)
                        .subscribe((aVoid)->{
                            openMapActivity();
                        })
        );



    }

    private void openMapActivity() {
        Intent intent = new Intent(mainActivity.getApplicationContext(), MapActivity.class);
        intent.putExtra("CURRENT_LOCATION_LAT",currentLocation.getLatitude());
        intent.putExtra("CURRENT_LOCATION_LONG",currentLocation.getLongitude());

        startActivityForResult(intent,);
    }


    public void setupMapButtonGeocoding() {
        RxLocation rxLocation = new RxLocation(getContext());
        mainActivity.cd.add( mainActivity.locationObservable
                .flatMap(location -> rxLocation.geocoding().fromLocation(location).toObservable())
                .subscribe(address -> {
                   mMapButtonTitle.setText(address.getAddressLine(0));
                   mMapButtonSubtitle.setText(address.getLatitude()+" "+address.getLongitude());
                   setupMapButton();
                },throwable -> {})
        );
    }
    private void setupSendButton() {
        cd.add(RxView.clicks(mSendButton)
                .filter((aVoid) -> (mTextSend.getText().toString().length()>=1))
                .flatMapSingle((aVoid)->{
                    Message msg = new Message(mTextSend.getText().toString());
                    msg.setOrigin_lat(Util.getCurrentLocation(prefs).getLatitude());
                    msg.setOrigin_long(Util.getCurrentLocation(prefs).getLongitude());

                    if (!prefs.getBoolean("isAnonymous", false)){
                        msg.setSender( mainActivity.user.first_name);
                        msg.setUser_id(FirebaseAuth.getInstance().getUid());
                        msg.setIs_anonymaus(false);
                    } else {
                        msg.setUser_id("<anonym>");
                        msg.setIs_anonymaus(true);
                        msg.setSender("Anonym");
                    }
                    mTextSend.setText("");
                    return RxFirestore.addDocument(firestore.collection("Messages"),msg);
                })
                .flatMapMaybe(ref->RxFirestore.getDocument(ref,Message.class))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((msg) -> {
                    //messageAdapter.addMessage(msg);
                })
        );
        }



}
