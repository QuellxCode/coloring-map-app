package com.example.hp.database;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_up extends AppCompatActivity implements View.OnClickListener{
    //public class (class name) extends AppCompatActivity implements View.OnClickListener{
    //declaring variables
    EditText Name,Email,Password,Confirm_password;
    Button Sign_up;
    ProgressBar progressBar;
    String Current_user_id;

    //firebase varibales
    //firebase authorization variable
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    //firebase database variable
    private DatabaseReference databaserefrence;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //code for toolbar(which contains menu bars , arrow icons etc)
        //TODO:(in case of changing toolbar design/perfrom functionality)res>styles(DarkActionBar)>(noactionbar)
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_up);
//        toolbar.setTitle("SignUp");
//          setSupportActionBar(toolbar);
        //TODO: (in order to change toolbar title)
//        getSupportActionBar().setTitle("Sign Up");

        //attaching varibales with fields
        //TODO:write names of your fields under
        Name = findViewById(R.id.name);
        Email= findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Confirm_password= findViewById(R.id.confirm_password);
        progressBar= findViewById(R.id.progressBar);
        //name of button is (sign_up)
        //TODO:signin button name below
        findViewById(R.id.sign_in).setOnClickListener(this);
        // go to assistant(firebase from toolbox)>authentication >email and password authentication >scroll down
        // >copy fireaBaseAuth instance code > paste here
        mAuth = FirebaseAuth.getInstance();

    }
    //registration method
    private void register (){
        //resovling those variable for use
        String name = Name.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String conf_pass = Confirm_password.getText().toString().trim();



        //if email section is empty
        if(email.isEmpty()){
            Email.setError("Email is required");
            Email.requestFocus();
            return;
        }
        //if email patteren is not right
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Email is not valid");
            Email.requestFocus();
            return;
        }
        //if password is empty
        if (password.isEmpty()){
            Password.setError("password is required");
            Password.requestFocus();
            return;
        }
        //if password length is less then 6
        if (password.length()<6){
            Password.setError("password need to be more then 6 letters/characters");
            Password.requestFocus();
            return;
        }
        // if confirm password is emptly
        if (conf_pass.isEmpty()){
            Confirm_password.setError("password is required");
            Confirm_password.requestFocus();
            return;
        }
        // if confirm password is less then 6
        if (conf_pass.length()<6){
            Confirm_password.setError("password need to be more then 6 letters/characters");
            Confirm_password.requestFocus();
            return;
        }
        // if confirm password does not match with password
        if (!conf_pass.equals(password)){
            Confirm_password.setError("passwords does not match");
            Confirm_password.requestFocus();
            return;
        }
        //visibility of progressbar
        progressBar.setVisibility(View.VISIBLE);

        // storing authenticating data here
        mAuth.createUserWithEmailAndPassword(email,conf_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task){

                //storedata method
                Store_data();

                //visibility of progressbar
                progressBar.setVisibility(View.VISIBLE);

                if (task.isSuccessful()){
                    //preview tost message
                    Toast.makeText(getApplicationContext(), "Registration Done",Toast.LENGTH_SHORT).show();

                    //after successful signing up , focused returned to login page
                    Intent intent = new Intent(Sign_up.this,Login2.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.INVISIBLE);
                    //activity terminates
                    finish();
                }
                else{
                    //hiding progressbar
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "check your network", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void onClick(View v){
        switch (v.getId()){
            // TODO: write your singin button name here
            //if button tabbed (signup function execute)
            case R.id.sign_in:
                register();
                break;
        }
    }
    private void Store_data(){
        //TODO:(in case of adding more fields) connect your new variables of this class with viewdatabase class under here
        String iname = Name.getText().toString().trim();
        String iemail = Email.getText().toString().trim();

        // get specific id of authentication entries at variable (Current_user_id)
        Current_user_id = mAuth.getCurrentUser().getUid();
        //store data in firebase database under specific id of that user
        databaserefrence = FirebaseDatabase.getInstance().getReference().child(Current_user_id);

        // Write data on the database
        //TODO:make class having name (ViewDatabase)(if not exists) with variables (iname,iemail .......)
        //object of viewdatabase class
        ViewDatabase StoreData = new ViewDatabase(iname,iemail);

        databaserefrence.setValue(StoreData);
    }
    // go to assistant(firebase from toolbox)>authentication >email and password authentication
    // >connect to firebase > allow from browser > check your project name >select the country > connect to firebase
    // >add firebase authentication to your app >accept changes

    //go to your browser>your firebase account>develop>authentication>set up sign in method
    // >email and password >enable only top >save

    //go to your browser>your firebase account>Database>Rules
    //(pending)

    //go to left side > app > manifest
    //cut code from splash activity and paste in sign up activity

}

