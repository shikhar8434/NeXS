package com.example.nexs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexs.models.User;
import com.example.nexs.models.UserResponse;
import com.example.nexs.utility.LoadingDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private Button googleSignIn, phoneSignIn;
    private Context context;
    private GoogleSignInClient mGoogleSignInClient;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createRequest();
        setReferences();
        setListeners();
    }

    private void setReferences() {
        context = this;
        googleSignIn = findViewById(R.id.google);
        phoneSignIn = findViewById(R.id.phone);
        dialog = new LoadingDialog(context);
    }

    private void setListeners() {
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        phoneSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NameActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            dialog.showDialog();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                User user = new User();
                assert account != null;
                String name = account.getDisplayName();
                assert name != null;
                user.setFirstname(name.substring(0, name.indexOf(' ')).trim());
                if (name.indexOf(' ') != name.lastIndexOf(' ')) {
                    user.setMiddlename(name.substring(name.indexOf(' ') + 1, name.lastIndexOf(' ') + 1).trim());
                }
                user.setLastname(name.substring(name.lastIndexOf(' ') + 1).trim());
                user.setEmail(account.getEmail());
                user.setCoins(0);
                firebaseAuthWithGoogle(account.getIdToken(), user);
            } catch (ApiException e) {
                dialog.stopDialog();
                Toast.makeText(context, "Something went Wrong!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, final User user) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.stopDialog();
                        if (task.isSuccessful()) {
                            final Intent intent = new Intent(context, MainActivity.class);
                            user.setUid(FirebaseAuth.getInstance().getUid());
                            MainActivity.api.userNewUser(user).enqueue(new Callback<UserResponse>() {
                                @Override
                                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                    if (response.body().getCode() == 200) {
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    } else {
                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserResponse> call, Throwable t) {

                                }
                            });
                        } else {
                            Toast.makeText(context, "Something went Wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
