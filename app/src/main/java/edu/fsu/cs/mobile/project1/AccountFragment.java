package edu.fsu.cs.mobile.project1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {
    public static final String TAG = PostViewFragment.class.getCanonicalName();
    public static String TITLE = "Account";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user; // https://firebase.google.com/docs/auth/android/manage-users

    private Button btnSignOut;
    private Button btnDeleteAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get GoogleSignInClient instance
        mGoogleSignInClient = AuthHelper.getClient(getContext());

        // Get Firebase instance and current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        btnSignOut = view.findViewById(R.id.button_sign_out);
        btnDeleteAccount = view.findViewById(R.id.button_delete_account);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        // Set activity title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(TITLE);

        // Set sign out button event
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthHelper.signOut(getActivity(), mGoogleSignInClient);
            }
        });

        // Set delete account button event
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthHelper.deleteAccount(getActivity(), user);
            }
        });
    }
}
