package com.example.hp.database;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.READ_CONTACTS;

/*
 * A login screen that offers login via email/password.
 */
 //recommended by template activity (implements LoaderCallbacks<Cursor>)
public class Login2 extends AppCompatActivity implements View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /*  private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };*/
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;
    //google sign in
    int RC_SIGN_IN=1;
    GoogleSignInClient SignInClient;
    String TAG = "Login2";
    // UI references/variables
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;
    private View mLoginFormView;
    //firebase authorication variable
    private FirebaseAuth mAuthTask;
      //-------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        //code for toolbar(which contains menu bars , arrow icons etc)
        //TODO:(in case of changing toolbar design)res>styles(DarkActionBar)>(noactionbar)
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_up);
//        toolbar.setTitle("SignUp");
//          setSupportActionBar(toolbar);
        //TODO: in order to change toolbar title
//        getSupportActionBar().setTitle("Sign In");

        // Set up the login form.
        //connecting variable with field
        //TODO: write your fileds and buttons name/id of your layout
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        //populateAutoComplete();
        //required in case of using button clicklistner method
//        Button LoginButton = (Button) findViewById(R.id.login_button);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.sign_up_btn).setOnClickListener(this);
//        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        // go to assistant(firebase from toolbox)>authentication >email and password authentication >scroll down
        // >copy fireaBaseAuth instance code > paste here
        mAuthTask = FirebaseAuth.getInstance();
        //---------//

        //google sign in
        findViewById(R.id.sign_in_button).setOnClickListener(this);
//        google_signin.setSize(SignInButton.SIZE_STANDARD);
// Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

// Build a GoogleSignInClient with the options specified by gso.(gradle latest version required)
        SignInClient = GoogleSignIn.getClient(this, gso);
        //------------------//

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }



/*    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }*/
        //google sign in
        private void signIn() {
            Intent signInIntent = SignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Show a progressbar and perform the user login attempt.
                mProgressView.setVisibility(View.VISIBLE);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuthTask.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuthTask.getCurrentUser();
                            updateUI(user);
                            //after successful login
                            Intent intent = new Intent(Login2.this, Login_page.class);
                            startActivity(intent);

                            Log.d(TAG, "onComplete: " + mAuthTask.getUid());
                            // Show a progressbar and perform the user login attempt.
                            mProgressView.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login2.this, "Login successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login2.this, "Network Error", Toast.LENGTH_SHORT).show();
                            mProgressView.setVisibility(View.INVISIBLE);
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        //accessing data from account into fields
        /*        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }*/

    }

             //-----------------//

    private void attemptLogin() {
        if (mAuthTask == null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
           //----------//

        // Store values in variables at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

         //-----------//

        //if email section is empty
        if(email.isEmpty()){
            mEmailView.setError("Email is required");
            mEmailView.requestFocus();
            return;
        }
        //if email patteren is not right
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailView.setError("Email is not valid");
            mEmailView.requestFocus();
            return;
        }
        //if password is empty
        if (password.isEmpty()){
            mPasswordView.setError("password is required");
            mPasswordView.requestFocus();
            return;
        }
        //if password length is less then 6
        if (password.length()<6){
            mPasswordView.setError("password need to be more then 6 letters/characters");
            mPasswordView.requestFocus();
            return;
        }

            // Show a progressbar and perform the user login attempt.
            mProgressView.setVisibility(View.VISIBLE);
            mAuthTask.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login Sucecssfully", Toast.LENGTH_SHORT).show();
                        //after successful login
                        Intent intent = new Intent(Login2.this, Login_page.class);
                        //setting text manual and intenting to login page , so that thread will
                        // enter in condition (login page 124) to assign id to current user id
                        intent.putExtra("loginMode", "Manual");
                        startActivity(intent);
                        //hide progressbar
                        mProgressView.setVisibility(View.INVISIBLE);
                        // empty those fields after login
                        mEmailView.setText("");
                        mPasswordView.setText("");
                        //activity terminates
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Check your Email/Password", Toast.LENGTH_SHORT).show();
                        //hide progressbar
                        mProgressView.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }
    //checking email and password functions
/*    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }*/

/*    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }*/
     //------------//
    /*    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/
//implements LoaderCallbacks<Cursor>(methods)

   /*    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }*/

    /*
     *//**
     * Callback received when a permissions request has been completed.
     *//*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/

    /*    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Login2.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }*/
         //-----------------//
    //implements View.OnClickListener(method)
    @Override
    public void onClick(View view) {
//        Toast.makeText(getApplicationContext(), String.valueOf(getTaskId()), Toast.LENGTH_SHORT).show();
     switch (view.getId()){
         //TODO: write name of your login button
         case R.id.login_button:
             attemptLogin();
             break;
             //in case google signin button tabbed
         case R.id.sign_in_button:
             signIn();
             break;
         //TODO: write name of your signup text
             //if text (sign up) tabbed (sign up window appears)
         case R.id.sign_up_btn:
             Intent intent = new Intent(Login2.this, Sign_up.class);
             startActivity(intent);
             break;
     }
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;

    }

    /* public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }
    }*/
//        @Override
      /*  protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;//changed to false
        }*/

        /*@Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                //provide code here intent to switch window/layout
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }*/

}

