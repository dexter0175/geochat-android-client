

package co.siarhei.apps.android.geochat.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import co.siarhei.apps.android.geochat.MainActivity;
import co.siarhei.apps.android.geochat.Model.Chat;
import co.siarhei.apps.android.geochat.Model.User;
import co.siarhei.apps.android.geochat.R;
import co.siarhei.apps.android.geochat.UI.Adapters.UserAdapter;
import durdinapps.rxfirebase2.RxFirestore;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class MainConnectionsFragment extends Fragment {

    View rootView;
    public SharedPreferences prefs;
    private RecyclerView rv_users;
    private UserAdapter usersAdapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CompositeDisposable cd = new CompositeDisposable();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_connections, container, false);
        prefs = this.getActivity().getSharedPreferences("geochat", Context.MODE_PRIVATE);

        rv_users = rootView.findViewById(R.id.rv_users);

        setupRecyclerView();
        setupReactiveGeoUpdates();



        return rootView;
    }

    private void setupReactiveGeoUpdates() {
        MainActivity mainActivity = (MainActivity) this.getActivity();
       Disposable dp =  mainActivity.locationObservable
               .subscribe(location -> usersAdapter.notifyDataSetChanged());
        cd.add(dp);
    }

    private void setupRecyclerView() {

        usersAdapter = new UserAdapter(getContext(), new ArrayList<>());
        rv_users.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_users.setAdapter(usersAdapter);

        CollectionReference ref = firestore.collection("Chats");

        Disposable dp = RxFirestore.observeQueryRef(ref)
                .flatMapIterable(query->query)
                .filter(chat-> chat.getId().contains(mAuth.getUid()))
                .map(chat->
                        chat.getId()
                                    .replace("chat", "")
                                    .replace(mAuth.getUid(), "")
                                    .replace("_", ""))
                .flatMap(id->{
                    Log.d("#ID",id);
                    DocumentReference docref = firestore.collection("Users").document(id);
                    return RxFirestore.observeDocumentRef(docref,User.class);
                })
                .subscribe(user -> {
                    Log.d("#ID2", user.toString());
                    usersAdapter.addUser(user);
                    usersAdapter.notifyDataSetChanged();
                },throwable -> Log.d("ERR:", throwable.toString()));

        cd.add(dp);
    }


}
