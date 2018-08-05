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


public class LoginActivity extends AppCompatActivity {

    private EditText email_input,password_input;
    private Button loginButton;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        mProgress=new ProgressDialog( this );
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child( "users" );

        mToolbar=(Toolbar)findViewById( R.id.login_toolbar );
        email_input=(EditText)findViewById( R.id.login_email );
        password_input=(EditText)findViewById( R.id.login_password );
        loginButton=(Button)findViewById( R.id.login_signinButton );

        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Login" );
        mToolbar.setTitleTextColor( Color.WHITE );

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=email_input.getText().toString();
                String password=password_input.getText().toString();

                if(!TextUtils.isEmpty( email ) || !TextUtils.isEmpty( password )){

                    mProgress.setTitle("Logging In");
                    mProgress.setMessage("Please wait we Log you In");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    login(email, password);

                }else{
                    Toast.makeText( LoginActivity.this, "Please enter valid email and password.", Toast.LENGTH_SHORT ).show();
                }

            }
        } );

    }

    private void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            String uid=user.getUid();
                            mDatabase.child( uid ).child( "device_token" ).setValue( deviceToken );

                            Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        } else {
                            mProgress.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}
