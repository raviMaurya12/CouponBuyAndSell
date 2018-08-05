package com.example.lenovo.buyandsell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class PostAd extends AppCompatActivity {

    private Toolbar mToolbar;
    private Spinner catererSpinner,dateSpinner,mealSpinner,negotiableSpinner;
    private Button  postButton;
    private EditText priceInput,remarkInput;
    private String caterer,date,meal,negotiable;
    private String price,remark;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseTwo;
    private ProgressDialog mProgress;
    private String pushID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_post_ad );

        mProgress=new ProgressDialog( this );

        mToolbar=(Toolbar)findViewById( R.id.postAd_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "Enter Coupon Details:" );
        mToolbar.setTitleTextColor( Color.WHITE );

        catererSpinner=(Spinner)findViewById( R.id.postAd_caterer_spinner );
        catererSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                caterer= parent.getItemAtPosition(pos).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dateSpinner=(Spinner)findViewById( R.id.postAd_date_spinner );
        dateSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date=parent.getItemAtPosition( position ).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        mealSpinner=(Spinner)findViewById( R.id.postAd_meal_spinner );
        mealSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meal=parent.getItemAtPosition( position ).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        negotiableSpinner=(Spinner)findViewById( R.id.postAd_negotiableSpinner );
        negotiableSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                negotiable=parent.getItemAtPosition( position ).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        priceInput=(EditText)findViewById( R.id.postAd_price );
        remarkInput=(EditText)findViewById( R.id.postAd_remark );

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        final String uid=current_user.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child( "overall_ads" );
        pushID=mDatabase.push().getKey();
        mDatabaseTwo=FirebaseDatabase.getInstance().getReference().child("ads_from_user").child(uid);

        postButton=(Button)findViewById( R.id.postAd_postButton );
        postButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Posting Coupon");
                mProgress.setMessage("Please wait while we post the Coupon for Sale");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                price=priceInput.getText().toString();
                remark=remarkInput.getText().toString();

                if(!TextUtils.isEmpty(  caterer)&&!TextUtils.isEmpty(  date )&& !TextUtils.isEmpty(  meal)
                    &&!TextUtils.isEmpty(price)&&!TextUtils.isEmpty(  negotiable )){

                    Map adMap=new HashMap<>();
                    adMap.put("seller",uid);
                    adMap.put("caterer",caterer);
                    adMap.put("date",date);
                    adMap.put("meal",meal);
                    adMap.put("price",price);
                    adMap.put("negotiable",negotiable);
                    adMap.put("remark",remark);
                    adMap.put("state","0");
                    adMap.put("timestamp", ServerValue.TIMESTAMP);

                    mDatabase.child(pushID).setValue(adMap).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Map adMapTwo=new HashMap<>();
                                adMapTwo.put("caterer",caterer);
                                adMapTwo.put("date",date);
                                adMapTwo.put("meal",meal);
                                adMapTwo.put("price",price);
                                adMapTwo.put("negotiable",negotiable);
                                adMapTwo.put("remark",remark);
                                adMapTwo.put("state","0");
                                adMapTwo.put("timestamp", ServerValue.TIMESTAMP);

                                mDatabaseTwo.child(pushID).setValue(adMapTwo).addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText( PostAd.this, "Posted successfully,now just wait for a buyer.", Toast.LENGTH_LONG ).show();
                                        Intent main_intent=new Intent( PostAd.this,MainActivity.class );
                                        startActivity( main_intent );
                                        finish();
                                        mProgress.dismiss();
                                    }
                                } );
                            }else{
                                mProgress.dismiss();
                                Toast.makeText( PostAd.this, "Error Occured.Please Try again.", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );
                }else{
                    Toast.makeText( PostAd.this, "All Feilds except Remark are manadatory.", Toast.LENGTH_SHORT ).show();
                }
            }
        } );

    }
}
