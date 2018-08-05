package com.example.lenovo.buyandsell;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText displayName,hostelName,roomNumber,roomSection,mobileNumber,paytmNumber,course,year;
    private Button saveChanges;
    private DatabaseReference userDatabase;
    private String setname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_complete_profile );

        mToolbar=(Toolbar)findViewById( R.id.completeProfile_toolbar ) ;
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Complete your Profile" );
        mToolbar.setTitleTextColor( Color.WHITE );

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=current_user.getUid();
        userDatabase= FirebaseDatabase.getInstance().getReference().child( "users" ).child( uid );


        userDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setname=dataSnapshot.child( "name" ).getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        displayName=(EditText)findViewById( R.id.cf_displayNameInput);
        displayName.setText( setname );
        hostelName=(EditText)findViewById( R.id.cf_hostelNameInput );
        roomNumber=(EditText)findViewById( R.id.cf_roomNumberInput);
        roomSection=(EditText)findViewById( R.id.cf_roomSectionInput);
        mobileNumber=(EditText)findViewById( R.id.cf_mobileNumberInput);
        paytmNumber=(EditText)findViewById( R.id.cf_paytmNumberInput);
        course=(EditText)findViewById( R.id.cf_courseInput);
        year=(EditText)findViewById( R.id.cf_yearInput);
        saveChanges=(Button)findViewById( R.id.cf_saveButton );


        saveChanges.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=displayName.getText().toString();
                String hostel_name=hostelName.getText().toString();
                long room_number=Long.valueOf( roomNumber.getText().toString() );
                String room_section=roomSection.getText().toString();
                long mobile_number=Long.valueOf( mobileNumber.getText().toString() );
                long paytm_number=Long.valueOf( paytmNumber.getText().toString() );
                String course_of_study=course.getText().toString();
                String year_of_study=year.getText().toString();

                if(!TextUtils.isEmpty( name )||!TextUtils.isEmpty(hostel_name)||!(room_number==0)||
                        !TextUtils.isEmpty(room_section)||!(mobile_number==0)||!(paytm_number==0)||!TextUtils.isEmpty(course_of_study)||
                        !TextUtils.isEmpty(year_of_study)){

                    Map update_hashmap=new HashMap();
                    update_hashmap.put( "name",name);
                    update_hashmap.put("hostel_name",hostel_name);
                    update_hashmap.put("room_number",room_number);
                    update_hashmap.put("room_section",room_section);
                    update_hashmap.put( "mobile_number",mobile_number );
                    update_hashmap.put("paytm_number",paytm_number);
                    update_hashmap.put("course",course_of_study);
                    update_hashmap.put("year",year_of_study);

                    userDatabase.updateChildren( update_hashmap ).addOnCompleteListener( new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()) {
                                Toast.makeText( CompleteProfile.this, "Changes saved successfully.", Toast.LENGTH_SHORT ).show();
                                Intent main_intent=new Intent( CompleteProfile.this,MainActivity.class );
                                startActivity( main_intent );
                                finish();
                            }else{
                                Toast.makeText( CompleteProfile.this, "Error saving data.Please try again", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );

                }else{
                    Toast.makeText( CompleteProfile.this, "All fields are manadatory.Please try again.", Toast.LENGTH_SHORT ).show();
                }

            }
        } );

    }
}
