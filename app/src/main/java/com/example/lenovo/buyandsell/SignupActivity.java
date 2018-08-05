package com.example.lenovo.buyandsell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG ="Signup" ;
    private EditText displayNameInput,emailInput,passwordInput;
    private Button signupButton;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup );

        mAuth = FirebaseAuth.getInstance();
        mProgress=new ProgressDialog(this);

        displayNameInput=(EditText)findViewById( R.id.signup_displayName );
        emailInput=(EditText)findViewById( R.id.signup_emailId );
        passwordInput=(EditText)findViewById( R.id.signup_password );
        signupButton=(Button)findViewById( R.id.signup_registerButton );
        mToolbar=(Toolbar)findViewById( R.id.signup_toolbar );

        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Sign Up" );
        mToolbar.setTitleTextColor( Color.WHITE );

        signupButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName=displayNameInput.getText().toString();
                String email=emailInput.getText().toString();
                String password=passwordInput.getText().toString();

                if(!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mProgress.setTitle("Registering User");
                    mProgress.setMessage("Please wait while we create the account");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    register( email, password,displayName );
                }else{
                    Toast.makeText( SignupActivity.this, "All feilds are Mandatory.Please try again.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

    }

    private void register(String email, String password, final String displayName) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=current_user.getUid();

                            String deviceToken= FirebaseInstanceId.getInstance().getToken();

                            usersDatabase= FirebaseDatabase.getInstance().getReference().child( "users" ).child(uid);
                            HashMap<String,String> usermap=new HashMap<>();
                            usermap.put( "device_token",deviceToken );
                            usermap.put( "name",displayName);
                            usermap.put("hostel_name","default");
                            usermap.put("room_number","default");
                            usermap.put("room_section","default");
                            usermap.put( "mobile_number","0" );
                            usermap.put("paytm_number","0");
                            usermap.put("course","default");
                            usermap.put("year","default");
                            usermap.put("image","default");
                            usermap.put( "thumb_image","default" );

                            usersDatabase.setValue(usermap).addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgress.dismiss();
                                        Toast.makeText( SignupActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT ).show();
                                        Intent mainActivity=new Intent(SignupActivity.this,MainActivity.class);
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainActivity);
                                        finish();
                                    }
                                }
                            } );



                        } else {
                            mProgress.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
