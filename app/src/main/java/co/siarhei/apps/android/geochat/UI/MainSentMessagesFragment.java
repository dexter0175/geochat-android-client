

package co.siarhei.apps.android.geochat.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import androidx.fragment.app.Fragment;

import java.util.List;

import co.siarhei.apps.android.geochat.Model.Message;
import co.siarhei.apps.android.geochat.R;
import co.siarhei.apps.android.geochat.UI.Adapters.SentMessagesMessageListAdaptor;


public class MainSentMessagesFragment extends Fragment {



    View rootView;

    public SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_sent_messages, container, false);

        prefs = this.getActivity().getSharedPreferences("co.siarhei.apps.android.geochat", Context.MODE_PRIVATE);

        return rootView;
    }




    public void processMessagesIntoListView(List<Message> _messages) {

        ListView mMessageList = rootView.findViewById(R.id.main_sent_messages_messagelist);

        mMessageList.setAdapter(new SentMessagesMessageListAdaptor(getContext(), _messages));

    }



}
