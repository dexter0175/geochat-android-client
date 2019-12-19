package co.siarhei.apps.android.geochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jakewharton.rxbinding3.view.RxView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import co.siarhei.apps.android.geochat.Model.Message;
import co.siarhei.apps.android.geochat.Model.PersonalMessage;
import co.siarhei.apps.android.geochat.Model.User;
import co.siarhei.apps.android.geochat.UI.Adapters.ChatMessageAdapter;
import co.siarhei.apps.android.geochat.Utils.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ChatActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    User user;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Intent intent;
    Toolbar toolbar;
    ImageView btn_send;
    EditText text_send;
    ChatMessageAdapter messageAdapter;
    List<PersonalMessage> messages;
    RecyclerView recyclerView;
    private String receiver_id;
    private CompositeDisposable cd = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intent = getIntent();
        receiver_id = intent.getStringExtra("receiver_id");

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> supportFinishAfterTransition());

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        messages = new ArrayList<>();

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        setUserRelated();
        setupRecyclerView();
        setupSendButton();

    }

    private void setupSendButton() {
        Disposable dp = RxView.clicks(btn_send)
                .flatMapSingle((aVoid)->{
                    CollectionReference ref = firestore.collection("Chats")
                            .document(Util.getChatName(mAuth.getUid(), receiver_id))
                            .collection("Messages");
                    PersonalMessage pm = new PersonalMessage();
                    pm.setFrom_id(mAuth.getUid());
                    pm.setTo_id(receiver_id);
                    pm.setContents(text_send.getText().toString());

                    text_send.setText("");

                    return  RxFirestore.addDocument(ref,pm);
                })
                .flatMapCompletable(res->{
                    DocumentReference doc_ref = firestore.collection("Chats").document(Util.getChatName(mAuth.getUid(), receiver_id));
                    Map<String, Date> status = new ArrayMap<>();
                        status.put("s", Calendar.getInstance().getTime());
                    return RxFirestore.setDocument(doc_ref,status);
                })
                .subscribe(()->{},throwable -> Log.d("ERR:",throwable.toString()));

        cd.add(dp);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_chat_messages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        messageAdapter = new ChatMessageAdapter(this, messages,"default");
        recyclerView.setAdapter(messageAdapter);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        recyclerView.setLayoutAnimation(animation);

        Query ref = firestore.collection("Chats")
                .document(Util.getChatName(mAuth.getUid(), receiver_id))
                .collection("Messages").orderBy("created_at");

        Disposable dp = RxFirestore.observeQueryRef(ref, PersonalMessage.class)
                .subscribe(messages -> {
                    messageAdapter.setMessages(messages);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                },throwable -> Log.d("ERR:", throwable.toString()));


        cd.add(dp);
    }

    private void setUserRelated() {
        DocumentReference user_ref = firestore.collection("Users").document(receiver_id);
        cd.add( RxFirestore.getDocument(user_ref,User.class)
                .subscribe(user->{
                    this.user = user;
                    username.setText(user.getFirst_name()+" "+user.getLast_name());
                    Picasso.get().load("https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png").into(profile_image);
                })
        );
    }
}
