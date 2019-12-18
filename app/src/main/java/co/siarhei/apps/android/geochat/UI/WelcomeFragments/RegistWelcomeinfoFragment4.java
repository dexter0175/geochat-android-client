
package co.siarhei.apps.android.geochat.UI.WelcomeFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import co.siarhei.apps.android.geochat.R;

public class RegistWelcomeinfoFragment4 extends Fragment {



    public RegistWelcomeinfoFragment4() { }

    public static RegistWelcomeinfoFragment4 newInstance() {

        return new RegistWelcomeinfoFragment4();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_regist_welcomeinfo_4, container, false);
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
