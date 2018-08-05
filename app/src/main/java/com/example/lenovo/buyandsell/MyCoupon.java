package com.example.lenovo.buyandsell;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MyCoupon extends AppCompatActivity {

    private Toolbar mToolbar;
    private  String couponId,uid,responderId;
    private DatabaseReference adsdatabase,rootRef;
    private TextView catererName,couponDate,couponMeal,couponPrice,couponNegotiable,couponPosteddate;
    private Button editButton,deleteButton;
    private RecyclerView responseRecycler;
    private FirebaseRecyclerAdapter<Responses,ResponseHolder> firebaseRecyclerAdapter;
    private DatabaseReference responseDatabase;
    private static DatabaseReference usersDatabase;
    private DatabaseReference deletedResponseDatabase;
    private String res_name;
    private String coupon_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_coupon );

        mToolbar=(Toolbar)findViewById( R.id.myCoupon_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle( "My Coupon" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        mToolbar.setTitleTextColor( Color.WHITE );

        couponId=getIntent().getStringExtra( "couponId" );

        catererName=(TextView)findViewById( R.id.myCoupon_caterer);
        couponDate=(TextView)findViewById( R.id.myCoupon_date);
        couponMeal=(TextView)findViewById( R.id.myCoupon_meal);
        couponPrice=(TextView)findViewById( R.id.myCoupon_price);
        couponNegotiable=(TextView)findViewById( R.id.myCoupon_negotiable);
        couponPosteddate=(TextView)findViewById( R.id.myCoupon_postedDate);
        editButton=(Button)findViewById( R.id.myCoupon_edit );
        deleteButton=(Button)findViewById( R.id.mycoupon_delete );
        responseRecycler=(RecyclerView)findViewById( R.id.myCoupon_recycler );

        responseRecycler.setLayoutManager(new LinearLayoutManager( this ));

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        uid=current_user.getUid();

        rootRef=FirebaseDatabase.getInstance().getReference();
        responseDatabase=FirebaseDatabase.getInstance().getReference().child( "responses" ).child(couponId);
        deletedResponseDatabase=FirebaseDatabase.getInstance().getReference().child("deleted");
        adsdatabase= FirebaseDatabase.getInstance().getReference().child("ads_from_user").child(uid).child(couponId);

        rootRef.child("ads_from_user").child(uid).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(couponId)){
                    adsdatabase.addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChildren()){
                                String caterer=dataSnapshot.child("caterer").getValue().toString();
                                String date=dataSnapshot.child("date").getValue().toString();
                                String meal=dataSnapshot.child("meal").getValue().toString();
                                String price="Rs."+dataSnapshot.child("price").getValue().toString();
                                String negotiable=dataSnapshot.child("negotiable").getValue().toString();
                                String posted_date=dataSnapshot.child("timestamp").getValue().toString();

                                catererName.setText( caterer );
                                couponDate.setText( date );
                                couponMeal.setText( meal );
                                couponPrice.setText( price );
                                couponNegotiable.setText( negotiable );
                                couponPosteddate.setText( posted_date );

                                coupon_details=caterer+" "+date+" "+meal;
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Responses> query=new FirebaseRecyclerOptions.Builder<Responses>().setQuery(responseDatabase,Responses.class).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Responses, ResponseHolder>(query) {
            @Override
            protected void onBindViewHolder(@NonNull ResponseHolder holder, int position, @NonNull Responses model) {
                responderId=getRef(position).getKey();
                holder.setResponder(responderId);
            }

            @NonNull
            @Override
            public ResponseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_coupon_single, parent, false);

                return new ResponseHolder( view );
            }
        };

        firebaseRecyclerAdapter.startListening();
        responseRecycler.setAdapter(firebaseRecyclerAdapter);
    }



    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public class ResponseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE =9415 ;
        public View mView;
        public Button confirmButton,declineButton;
        public ImageButton callButton,chatButton;
        public String responder_id,responder_contact;
        public TextView responderName,responderCourseAndYear,responderAddress;

        public ResponseHolder(View itemView) {
            super( itemView );
            mView=itemView;
            confirmButton=(Button)mView.findViewById( R.id.myCoupon_single_confirmButton );
            declineButton=(Button)mView.findViewById( R.id.myCoupon_single_declineButton );
            callButton=(ImageButton)mView.findViewById( R.id.myCoupon_single_callButton );
            chatButton=(ImageButton)mView.findViewById( R.id.myCoupon_single_chatButton );
            confirmButton.setOnClickListener( this );
            declineButton.setOnClickListener( this );
            callButton.setOnClickListener( this );
            chatButton.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==confirmButton.getId()){
//Confirm Button
                AlertDialog.Builder alert = new AlertDialog.Builder(MyCoupon.this);
                alert.setTitle( "Are you Sure:" );
                alert.setMessage("Do you want to sell the Coupon to "+res_name);
                alert.setCancelable(false);
                alert.setNegativeButton( "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } );
                alert.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        responseDatabase.removeValue();
                        deletedResponseDatabase.child(couponId).removeValue();
                        adsdatabase.removeValue();
                        rootRef.child("overall_ads").child(couponId).removeValue().addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent sold_intent=new Intent( MyCoupon.this,SellSuccess.class );
                                    startActivity( sold_intent );
                                    finish();

                                    Map acc_not=new HashMap();

                                    acc_not.put( "coupon_id",couponId );
                                    acc_not.put( "coupon_details",coupon_details );
                                    acc_not.put( "state","2" );
                                    acc_not.put( "responder_id",responder_id );
                                    acc_not.put( "time", ServerValue.TIMESTAMP );
                                    acc_not.put( "seller",uid );
                                    rootRef.child("notifications").child(responder_id).push().setValue( acc_not ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText( MyCoupon.this, "Notification sent to Responder.", Toast.LENGTH_SHORT ).show();
                                        }
                                    } );
                                }else{
                                    Toast.makeText( MyCoupon.this, "Transaction Failed.Please Try again later.", Toast.LENGTH_SHORT ).show();
                                }
                            }
                        } );
                    }
                } );
                AlertDialog dialog=alert.create();
                dialog.show();

            }else if (v.getId()==declineButton.getId()){
//Decline Button
                responseDatabase.child(responder_id).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map deletedResponseMap=new HashMap();
                        deletedResponseMap.put( "state","3" );
                        deletedResponseMap.put("seller",uid);
                        deletedResponseDatabase.child(couponId).child(responder_id).setValue(deletedResponseMap).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText( MyCoupon.this, "Request declined.", Toast.LENGTH_SHORT ).show();
                                }else{
                                    Toast.makeText( MyCoupon.this, "Failed to decline.Please try later.", Toast.LENGTH_SHORT ).show();
                                }
                            }
                        } );
                    }
                } );

                Map dec_not=new HashMap(  );
                dec_not.put( "coupon_id",couponId );
                dec_not.put( "coupon_details",coupon_details );
                dec_not.put( "state","3" );
                dec_not.put( "responder_id",responder_id );
                dec_not.put( "time", ServerValue.TIMESTAMP );
                dec_not.put( "seller",uid );
                rootRef.child( "notifications" ).child( responder_id ).push().setValue( dec_not ).addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( MyCoupon.this, "Notification sent to Responder.", Toast.LENGTH_SHORT ).show();
                    }
                } );

            }else if(v.getId()==callButton.getId()){
//Call Button
                Intent i = new Intent( Intent.ACTION_CALL );
                i.setData( Uri.parse( "tel:" + responder_contact ) );
                if (ActivityCompat.checkSelfPermission( mView.getContext(), android.Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    return;
                }
                startActivity( i );

            }else if(v.getId()==chatButton.getId()){
//Chat Button
                Intent chat_intent=new Intent( mView.getContext(),ChatActivity.class );
                chat_intent.putExtra( "CurrentUserID",uid );
                chat_intent.putExtra( "friendID",responder_id );
                startActivity(chat_intent);
                finish();

            }
        }

        public void setResponder(String responderId) {
            responder_id=responderId;
            usersDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(responderId);
            usersDatabase.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String responder_name=dataSnapshot.child("name").getValue().toString();
                    res_name=responder_name;
                    String responder_course_and_year=dataSnapshot.child("course").getValue().toString()+" "+
                            dataSnapshot.child("year").getValue().toString();
                    String responder_address=dataSnapshot.child( "hostel_name" ).getValue().toString()+","+
                            dataSnapshot.child( "room_number" ).getValue().toString()+"("+dataSnapshot.child("room_section").getValue().toString()+")";
                    responder_contact=dataSnapshot.child( "mobile_number" ).getValue().toString();

                    responderName=(TextView)mView.findViewById( R.id.myCoupon_single_name );
                    responderAddress=(TextView)mView.findViewById( R.id.myCoupon_single_address );
                    responderCourseAndYear=(TextView)mView.findViewById( R.id.myCoupon_single_courseAndYear );
                    responderName.setText( responder_name );
                    responderCourseAndYear.setText( responder_course_and_year );
                    responderAddress.setText( responder_address );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText( mView.getContext(), "Error Fetching Data.", Toast.LENGTH_SHORT ).show();
                }
            } );
        }
    }
}
