package com.example.lenovo.buyandsell;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.FileObserver;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewCoupon extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE =9415 ;
    private String couponId;
    private String sellerId;
    private DatabaseReference mDatabase;
    private TextView sellerName, sellerAddress, sellerContact, couponCaterer, couponDate, couponMeal, couponNegotiable, couponPrice, couponRemark;
    private Button callButton, chatButton, buyButton;
    private String seller_name, seller_address, contact_number,pic;
    private String couponState="0";
    private String coupon_details;
    private CircleImageView dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_coupon );

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        final String uid=current_user.getUid();

        couponId = getIntent().getStringExtra( "coupon_id" );
        sellerId = getIntent().getStringExtra( "seller_id" );

        sellerName = (TextView) findViewById( R.id.viewCoupon_name );
        sellerAddress = (TextView) findViewById( R.id.viewCoupon_address );
        sellerContact = (TextView) findViewById( R.id.viewCoupon_contactNumber );
        couponCaterer = (TextView) findViewById( R.id.viewCoupon_caterer );
        couponDate = (TextView) findViewById( R.id.viewCoupon_date );
        couponMeal = (TextView) findViewById( R.id.viewCoupon_meal );
        couponNegotiable = (TextView) findViewById( R.id.viewCoupon_negotiable );
        couponPrice = (TextView) findViewById( R.id.viewCoupon_price );
        couponRemark = (TextView) findViewById( R.id.viewCoupon_remark );
        callButton = (Button) findViewById( R.id.viewCoupon_callButton );
        chatButton = (Button) findViewById( R.id.viewCoupon_chatButton );
        buyButton = (Button) findViewById( R.id.viewCoupon_buyButton );
        dp=(CircleImageView)findViewById( R.id.viewCoupon_pic );

        mDatabase = FirebaseDatabase.getInstance().getReference();

//Fething Response Status

        mDatabase.child( "responses" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(couponId)){
                    mDatabase.child( "responses" ).child( couponId ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(uid)) {
                                couponState = dataSnapshot.child(uid).child( "state" ).getValue().toString();
                                if(couponState.equals( "1" )) {
                                    buyButton.setText( "Waiting for Seller Response" );
                                }
                            }else {

                                mDatabase.child( "deleted" ).addValueEventListener( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(couponId)){
                                            mDatabase.child( "deleted" ).child(couponId).addValueEventListener( new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(uid)){
                                                        couponState = dataSnapshot.child(uid).child("state").getValue().toString();
                                                        if(couponState.equals( "3" )){
                                                            buyButton.setText( "Request Declined by Buyer." );
                                                        }
                                                        if(couponState.equals("1") || couponState.equals("3")){
                                                            buyButton.setEnabled(false);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            } );
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                } );


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


//Changing BuyButton Functionality

        if(couponState.equals( "1" )){
            buyButton.setText("Waiting for Seller Response");
            buyButton.setEnabled(false);
        }else if(couponState.equals( "3" )){
            buyButton.setText( "Request Declined by Buyer." );
            buyButton.setEnabled(false);
        }

//Fething Seller Details

        mDatabase.child( "users" ).child( sellerId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seller_name = dataSnapshot.child( "name" ).getValue().toString();
                seller_address = dataSnapshot.child( "hostel_name" ).getValue().toString() + "," + dataSnapshot.child( "room_number" ).getValue().toString() + "(" +
                        dataSnapshot.child( "room_section" ).getValue().toString() + ")";
                contact_number = dataSnapshot.child( "mobile_number" ).getValue().toString();
                pic=dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(pic).placeholder( R.drawable.defaultpic ).into(dp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );

//Fething Coupon Details

        mDatabase.child("overall_ads").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(couponId)){
                    mDatabase.child( "overall_ads" ).child(couponId).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChildren()) {
                                String caterer_name = dataSnapshot.child( "caterer" ).getValue().toString();
                                String date_string = dataSnapshot.child( "date" ).getValue().toString() + "'s " + "coupon";
                                String meal_information = dataSnapshot.child( "meal" ).getValue().toString();
                                String negotiable_information = dataSnapshot.child( "negotiable" ).getValue().toString();
                                String price_information = "Rs." + dataSnapshot.child( "price" ).getValue().toString();
                                String remark_information = dataSnapshot.child( "remark" ).getValue().toString();

                                sellerName.setText( seller_name );
                                sellerAddress.setText( seller_address );
                                sellerContact.setText( contact_number );
                                couponCaterer.setText( caterer_name );
                                couponDate.setText( date_string );
                                couponMeal.setText( meal_information );
                                couponNegotiable.setText( negotiable_information );
                                couponPrice.setText( price_information );
                                couponRemark.setText( remark_information );

                                coupon_details=caterer_name+" "+date_string+" "+meal_information;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );

                }else{
                    Intent sold_intent=new Intent( ViewCoupon.this,SellSuccess.class );
                    startActivity(sold_intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        callButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Intent.ACTION_CALL );
                i.setData( Uri.parse( "tel:" + contact_number ) );
                if (ActivityCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    return;
                }
                startActivity(i);
            }
        } );

        chatButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat_intent=new Intent( ViewCoupon.this,ChatActivity.class );
                chat_intent.putExtra( "CurrentUserID",uid );
                chat_intent.putExtra( "friendID",sellerId );
                startActivity(chat_intent);
            }
        } );

        buyButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyButton.setEnabled( false );
                if(couponState.equals( "0" )) {
                    Map responseMap = new HashMap();
                    responseMap.put( "state", "1" );
                    responseMap.put( "seller", sellerId );
                    mDatabase.child( "responses" ).child( couponId ).child( uid ).setValue( responseMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            buyButton.setText( "Waiting for Seller Response" );
                            couponState="1";
                            buyButton.setEnabled(false);
                        }
                    } );
                }
                Map notification_map=new HashMap();
                notification_map.put( "coupon_id",couponId );
                notification_map.put( "coupon_details",coupon_details );
                notification_map.put( "state","1" );
                notification_map.put( "responder_id",uid );
                notification_map.put( "time", ServerValue.TIMESTAMP );
                notification_map.put( "seller",sellerId );
                mDatabase.child( "notifications" ).child(sellerId).push().setValue(notification_map).addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( ViewCoupon.this, "Notification sent to seller.", Toast.LENGTH_SHORT ).show();
                    }
                } );


            }
        } );

    }

}
