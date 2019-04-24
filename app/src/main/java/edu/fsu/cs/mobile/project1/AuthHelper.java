package edu.fsu.cs.mobile.project1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// This class provides helper functions for Google Sign In API
public final class AuthHelper {
    public static GoogleSignInClient getClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, gso);
    }

    public static void signIn(Activity activity, GoogleSignInClient mGoogleSignInClient, int resultCode) {
        // Launch the Intent for GoogleSignInClient.getSignInIntent(...);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, resultCode);
        // onActivityResult(...) is handled in each activity file
    }

    public static void signOut(Activity activity, GoogleSignInClient mGoogleSignInClient) {
        final Activity mActivity = activity;

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out & trigger UI update
        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Upon sign out, send to MainActivity and finish current activity so user
                        //  cannot go back
                        Intent toSignIn = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(toSignIn);
                        mActivity.finish();
                    }
                });
    }

    public static void deleteAccount(final Activity activity, final FirebaseUser user){
        final String userId = user.getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all user's posts
        db.collection(FirestoreHelper.POSTS_COLLECTION)
            .whereEqualTo(FirestoreHelper.USERID, userId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<Post> userPosts = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                             Log.d("DocumentSnapshot", document.getId() + " => " + document.getData());

                            // Add post to array list
                            Map<String, Object> data = new HashMap<>(document.getData());
                            data.put(FirestoreHelper.ID, document.getId());
                            userPosts.add(new Post(data));
                        }

                        // Delete all user's posts
                        for(Post post : userPosts) {
                            FirestoreHelper.deleteFromDB(db, post.getPostid());
                        }

                        // Delete user
                        user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent toSignIn = new Intent(activity, MainActivity.class);
                                        activity.startActivity(toSignIn);
                                        activity.finish();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                    } else {
                        Log.w("Firestore error: ", "Error getting documents.", task.getException());
                    }
                }
            });
    }

    /*
    public static void reAuthenticateAccount(FirebaseUser user){
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
    }
    */
}
