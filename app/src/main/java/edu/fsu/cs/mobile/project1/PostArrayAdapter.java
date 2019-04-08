package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PostArrayAdapter extends ArrayAdapter<Post> {
    private Context mContext;
    private ArrayList<Post> postList;

    public PostArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        mContext = context;
        postList = new ArrayList<>();
    }

    private static class PostHolder {
        TextView title;
        TextView message;
        TextView timestamp;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Post item = getItem(position);

        // Populate ListView with each post entry
        PostHolder viewHolder;
        if(convertView == null) {
            // Inflate layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_post, parent, false);

            viewHolder = new PostHolder();
            viewHolder.title = convertView.findViewById(R.id.row_textView_title);
            viewHolder.title = convertView.findViewById(R.id.row_textView_message);
            viewHolder.title = convertView.findViewById(R.id.row_textView_timestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PostHolder) convertView.getTag();
        }

        viewHolder.title.setText(item.getTitle());
        viewHolder.message.setText(item.getMessage());
        viewHolder.timestamp.setText(item.getTimestamp());

        // OnClickListener for each post in adapter
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display PostViewFragment
                FragmentManager manager = ((FragmentActivity) mContext).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                // Create new fragment and set arguments
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", item);
                PostViewFragment fragment = new PostViewFragment();
                fragment.setArguments(bundle);

                // Hide PostsListFragment and show PostViewFragment
                transaction.addToBackStack(PostsListFragment.TAG);
                transaction.hide(manager.findFragmentByTag(PostsListFragment.TAG));
                transaction.add(R.id.frameLayout_posts, fragment, PostViewFragment.TAG);
                transaction.commit();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public int getPosition(@Nullable Post item) {
        for(int i = 0; i < postList.size(); i++) {
            if( TextUtils.equals(item.getTitle(), postList.get(i).getTitle()) && TextUtils.equals(item.getMessage(), postList.get(i).getMessage()) ) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void add(@Nullable Post item) {
        int idx = getPosition(item);
        if(idx >= 0) {
            postList.set(idx, item);
        } else {
            postList.add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public void remove(@Nullable Post item) {
        int idx = getPosition(item);
        if(idx >= 0 && idx < postList.size()) {
            postList.remove(idx);
        }
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }
}
