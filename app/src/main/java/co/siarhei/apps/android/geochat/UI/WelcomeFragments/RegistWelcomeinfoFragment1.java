
package co.siarhei.apps.android.geochat.UI.WelcomeFragments;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import co.siarhei.apps.android.geochat.R;
import co.siarhei.apps.android.geochat.RegistrationActivity;

public class RegistWelcomeinfoFragment1 extends Fragment {

    private OnFragmentInteractionListener mListener;

    public RegistWelcomeinfoFragment1() { }

    // TODO: Rename and change types and number of parameters
    public static RegistWelcomeinfoFragment1 newInstance() {

        return new RegistWelcomeinfoFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_regist_welcomeinfo_1, container, false);

        //
        Button vBottomsheetTriggerButton =  v.findViewById(R.id.regist_bottomsheet_triggerbutton);

        vBottomsheetTriggerButton.setOnClickListener(v1 -> ((RegistrationActivity) getActivity()).activateBottomSheet());


        AnimationDrawable animationDrawable = (AnimationDrawable) vBottomsheetTriggerButton.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
        return v;
    }





    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
