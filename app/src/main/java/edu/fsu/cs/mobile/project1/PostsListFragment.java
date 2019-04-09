package edu.fsu.cs.mobile.project1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class PostsListFragment extends Fragment {
    public static final String TAG = PostsListFragment.class.getCanonicalName();

    private PostArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Define PostArrayAdapter and set ListView
        ListView list = view.findViewById(R.id.listView_posts);
        adapter = new PostArrayAdapter(getActivity(), R.layout.row_post);

        // dummy data
        for(int i = 1; i < 11; i++) {
            Post item = new Post("#" + i + ". Test Title", "Test Message", 0.0, 0.0, "Timestamp", "user id");
            adapter.add(item);
        }

        list.setAdapter(adapter);
    }
}
