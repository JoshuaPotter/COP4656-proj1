package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class PostArrayAdapter extends ArrayAdapter<Post> {
    private Context mContext;
    private ArrayList<Post> postList;
    private Singleton var = Singleton.getInstance();
    private FirebaseFirestore db;

    public PostArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        mContext = context;
        postList = new ArrayList<>();
    }

    private static class PostHolder {
        TextView title;
        TextView message;
        TextView timestamp;
        TextView upvotes;
        ImageButton favorite_btn;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Post item = getItem(position); // Post at this position

        // Populate ListView with each post entry
        final PostHolder viewHolder;
        if(convertView == null) {
            // Inflate layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_post, parent, false);

            viewHolder = new PostHolder();
            viewHolder.title = convertView.findViewById(R.id.row_textView_title);
            viewHolder.message = convertView.findViewById(R.id.row_textView_message);
            viewHolder.timestamp = convertView.findViewById(R.id.row_textView_timestamp);
            viewHolder.upvotes = convertView.findViewById(R.id.row_textView_upvotes);
            viewHolder.favorite_btn = convertView.findViewById(R.id.favorite);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PostHolder) convertView.getTag();
        }

        // Remove newlines and only show first 140 characters
        String sanitizedMessage = item.getMessage().replace("\n", " ")
                .substring(0, Math.min(item.getMessage().length(), 139));

        viewHolder.title.setText(item.getTitle());
        viewHolder.message.setText(sanitizedMessage); // get first 144 characters
        viewHolder.timestamp.setText(item.getFormattedTimestamp());
        viewHolder.upvotes.setText(item.getUpvotes());

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
                transaction.replace(R.id.frameLayout_posts, fragment);
                transaction.commit();
            }
        });

        // OnClickListener for starring a post
        viewHolder.favorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!var.favoritedPostList.contains(item.getPostid())) {
                    //int x = Integer.parseInt(item.getUpvotes()) + 1;
                    //String y = Integer.toString(x);
                    //viewHolder.upvotes.setText(y);
                    upvotePost(position);

                    viewHolder.upvotes.setText(item.getUpvotes());
                    var.favoritedPostList.add(item.getPostid());
                }
                else {
                    var.favoritedPostList.remove(item.getPostid());
                    //int x = Integer.parseInt(item.getUpvotes()) - 1;
                    //String y = Integer.toString(x);
                    //viewHolder.upvotes.setText(y);
                    downvotePost(position);
                    viewHolder.upvotes.setText(item.getUpvotes());
                }
            }
        });

        return convertView;
    }

    /****************************************
     *                                      *
     *  Helper Functions for Array Adapter  *
     *                                      *
     ****************************************/

    @Override
    public int getCount() {
        return postList.size();
    }

    @Nullable
    @Override
    public Post getItem(int position) {
        return postList.get(position);
    }

    @Override
    public int getPosition(@Nullable Post item) {
        for(int i = 0; i < postList.size(); i++) {
            if( TextUtils.equals(item.getTitle(), postList.get(i).getTitle())
                    && TextUtils.equals(item.getMessage(), postList.get(i).getMessage())
                    && TextUtils.equals(item.getTimestamp().toString(), postList.get(i).getTimestamp().toString()) ) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void add(@Nullable Post item) {
        // Add item or set existing item
        int idx = getPosition(item);
        if(idx >= 0) {
            postList.set(idx, item);
        } else {
            postList.add(item);
        }
    }

    @Override
    public void remove(@Nullable Post item) {
        // Remove item at specific index
        int idx = getPosition(item);
        if(idx >= 0 && idx < postList.size()) {
            postList.remove(idx);
        }
    }

    @Override
    public void clear() {
        // Clears all item from ArrayList
        postList.clear();
        notifyDataSetChanged();
    }

    public void sortByTimestamp() {
        // Sorts using Comparable in Posts class by timestamp date
        Collections.sort(postList, Collections.reverseOrder());
    }

    public void upvotePost(int position){
        // Upvotes a post at specific index
        db = FirebaseFirestore.getInstance();
        final Post item = getItem(position);
        int upvoteCountPlusOne = Integer.parseInt(item.getUpvotes()) + 1;
        String new_upvote = Integer.toString(upvoteCountPlusOne);
        FirestoreHelper.updateUpvoteDB(db, item.getPostid(), new_upvote);
    }

    public void downvotePost(int position) {
        // Downvotes a post at a specific index
        db = FirebaseFirestore.getInstance();
        final Post item = getItem(position);
        int upvoteCountPlusOne = Integer.parseInt(item.getUpvotes()) - 1;
        String new_upvote = Integer.toString(upvoteCountPlusOne);
        FirestoreHelper.updateUpvoteDB(db, item.getPostid(), new_upvote);
    }

}
