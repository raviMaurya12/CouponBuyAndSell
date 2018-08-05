package com.example.lenovo.buyandsell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class User extends AppCompatActivity {

    private Button loginButton;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user );

        loginButton=(Button)findViewById( R.id.user_loginButton );
        signUpButton=(Button)findViewById( R.id.user_SignUpButton );

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_Intent=new Intent( User.this,LoginActivity.class ) ;
                startActivity( login_Intent );
            }
        } );

        signUpButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_intent=new Intent( User.this,SignupActivity.class ) ;
                startActivity( signup_intent );
            }
        } );

    }
}
