package edu.fsu.cs.mobile.project1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.core.FirestoreClient;


public class PostCreateFragment extends Fragment {
    public static final String TAG = PostsListFragment.class.getCanonicalName();

    // TODO: Send values to FireStore database using FireBase

    private Button submit_button;
    private Button cancel_button;

    private EditText et_title;
    private EditText et_message;

    private String title;
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        et_title = getView().findViewById(R.id.editText_title);
        et_message = getView().findViewById(R.id.editText_message);

        submit_button = getView().findViewById(R.id.button_submit);
        cancel_button = getView().findViewById(R.id.button_cancel);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = et_title.getText().toString();
                message = et_message.getText().toString();

                if (title.matches("") || message.matches("")) {
                    Toast.makeText(getActivity(), "Missing title or message, could not create post.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // to be sent to firestore database
                    Post test_item = new Post(title, message, "0.0", "0.0", "Timestamp", "user id");
                }
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // May have to be done a different way
                getActivity().onBackPressed();
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_create, container, false);
    }

}
