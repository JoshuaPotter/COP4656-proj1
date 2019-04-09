package edu.fsu.cs.mobile.project1;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PostViewFragment extends Fragment {
    public static final String TAG = PostViewFragment.class.getCanonicalName();

    private Post item;

    private TextView mTitle;
    private TextView mTimestamp;
    private TextView mMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(getArguments() != null) {
            item = bundle.getParcelable("post");
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get TextView objects
        mTitle = view.findViewById(R.id.textView_title);
        mTimestamp = view.findViewById(R.id.textView_timestamp);
        mMessage = view.findViewById(R.id.textView_message);

        // Set text for TextView objects for this post
        mTitle.setText(item.getTitle());
        mTimestamp.setText(item.getTimestamp().toString());
        mMessage.setText(item.getMessage());
    }
}
