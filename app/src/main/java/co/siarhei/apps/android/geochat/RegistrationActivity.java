

package co.siarhei.apps.android.geochat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;


import co.siarhei.apps.android.geochat.Model.User;
import co.siarhei.apps.android.geochat.UI.WelcomeFragments.RegistWelcomeinfoFragment1;
import co.siarhei.apps.android.geochat.UI.WelcomeFragments.RegistWelcomeinfoFragment2;
import co.siarhei.apps.android.geochat.UI.WelcomeFragments.RegistWelcomeinfoFragment3;
import co.siarhei.apps.android.geochat.UI.WelcomeFragments.RegistWelcomeinfoFragment4;
import durdinapps.rxfirebase2.RxFirebaseAuth;
import durdinapps.rxfirebase2.RxFirestore;

public class RegistrationActivity extends AppCompatActivity implements
        RegistWelcomeinfoFragment1.OnFragmentInteractionListener,
        RegistWelcomeinfoFragment2.OnFragmentInteractionListener,
        RegistWelcomeinfoFragment3.OnFragmentInteractionListener,
        RegistWelcomeinfoFragment4.OnFragmentInteractionListener{



    private static final int NUM_PAGES = 4;

    private ViewPager mPager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private PagerAdapter mPagerAdapter;


    BottomSheetBehavior bottomSheetBehavior;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        prefs = this.getSharedPreferences("geochat", Context.MODE_PRIVATE);

        networkCheck();

        setupRegistrationBottomSheet(this);


        mPager =  findViewById(R.id.regist_welcomeinfo_container);
        mPagerAdapter = new WelcomeinfoPagerAdaptor(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }





    public void setupRegistrationBottomSheet(AppCompatActivity context){


        LinearLayout vBottomsheet = (LinearLayout) findViewById(R.id.regist_bottomsheet_root);


        bottomSheetBehavior = BottomSheetBehavior.from(vBottomsheet);


        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //register
        findViewById(R.id.regist_bottomsheet_form_createaccount_button).setOnClickListener(
                v -> createUserAccountAndProceed()
        );

        // signIn
        findViewById(R.id.regist_bottomsheet_form_signin_button).setOnClickListener(
                v -> showSigninDialog()
        );


    }







    public void activateBottomSheet(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }




    @SuppressLint("CheckResult")
    public void createUserAccountAndProceed(){

        MaterialEditText _first_name = findViewById(R.id.regist_bottomsheet_form_firstname);
        MaterialEditText _last_name = findViewById(R.id.regist_bottomsheet_form_lastname);
        MaterialEditText _email_address = findViewById(R.id.regist_bottomsheet_form_email);
        final MaterialEditText _password = findViewById(R.id.regist_bottomsheet_form_password);


        final User _user = new User();
        _user.first_name = _first_name.getText().toString();
        _user.last_name = _last_name.getText().toString();
        _user.email_address = _email_address.getText().toString();
        _user.password = _password.getText().toString();


        RxFirebaseAuth.createUserWithEmailAndPassword(mAuth, _user.email_address, _user.password)
                .flatMapCompletable(authResult -> {
                   DocumentReference document = firestore.collection("Users").document(authResult.getUser().getUid());
                   return RxFirestore.setDocument(document,_user);
                })
                .subscribe(() -> {
                    startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                    finish();
                },throwable -> {
                    Toast.makeText(this, throwable.toString(),Toast.LENGTH_SHORT).show();
                });
    }






    public void showSigninDialog(){

        // Show Sign-in dialog
        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.dialog_signin, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(prompt);
        final EditText _email = prompt.findViewById(R.id.signin_email) ;
        final EditText _pass =  prompt.findViewById(R.id.signin_password);
        alertDialogBuilder.setTitle("Sign in with existing account...");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> signinUserAccount(_email.getText().toString(), _pass.getText().toString()));

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();

            }
        });

        alertDialogBuilder.show();

    }






    @SuppressLint("CheckResult")
    public void signinUserAccount(String _email, String _pass){
        RxFirebaseAuth.signInWithEmailAndPassword(mAuth, _email, _pass)
                .map(authResult -> authResult.getUser() != null)
                .subscribe(logged -> {
                    Log.d("Rxfirebase2", "User logged " + logged);
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();
                }, throwable -> Log.d("RxFirebase", throwable.toString()));


    }







    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {

            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {

                super.onBackPressed();
            }
        } else {

            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        networkCheck();

    }





    @Override
    public void onFragmentInteraction(Uri uri) {  }



    private class WelcomeinfoPagerAdaptor extends FragmentStatePagerAdapter {


        public WelcomeinfoPagerAdaptor(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            switch (position){
                case 0:
                    return new RegistWelcomeinfoFragment1();
                case 1:
                    return new RegistWelcomeinfoFragment2();
                case 2:
                    return new RegistWelcomeinfoFragment3();
                case 3:
                    return new RegistWelcomeinfoFragment4();
                default:
                    return new Fragment();


            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }



    private boolean networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_network_connection)
                    .setCancelable(false)
                    .setPositiveButton("Exit...", (dialog, id) -> {

                        finish();
                        System.exit(0);

                    });
            AlertDialog alert = builder.create();
            alert.show();

            return false;

        } else {

            return true;

        }
    }




}
