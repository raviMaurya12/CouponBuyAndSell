package com.example.lenovo.buyandsell;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InboxFragment extends Fragment {

    private View Fview;
    private RecyclerView inboxRecycler;
    private TextView emptyView;
    private static String uid;
    private DatabaseReference mDatabase;
    private DatabaseReference rootRef;
    private FirebaseRecyclerAdapter<Notifications,NotificationHolder> firebaseRecyclerAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fview=inflater.inflate( R.layout.fragment_inbox, container, false );

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        uid=current_user.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child( "notifications" ).child( uid );
        rootRef=FirebaseDatabase.getInstance().getReference();

        inboxRecycler=(RecyclerView)Fview.findViewById( R.id.inbox_recycler );
        inboxRecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );
        inboxRecycler.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        emptyView=(TextView)Fview.findViewById( R.id.inbox_empty_view );

        return Fview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Notifications> query=new FirebaseRecyclerOptions.Builder<Notifications>().setQuery( mDatabase,Notifications.class ).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Notifications, NotificationHolder>(query) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Notifications model) {
                String notID=getRef(position).getKey();
                final String couponID=model.getCoupon_id();
                String coupon_details=model.getCoupon_details();
                final String responderID=model.getResponder_id();
                final String state=model.getState();
                final String seller=model.getSeller();
                holder.setNotificationText(coupon_details,responderID,state,seller);
                holder.mView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!responderID.equals( uid ) && state.equals( "1" )){
//A buyer responded to coupon ad.
                            rootRef.child( "overall_ads" ).addValueEventListener( new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(couponID)){
                                        Intent myCoupon_intent=new Intent( getContext(),MyCoupon.class );
                                        myCoupon_intent.putExtra( "couponId",couponID );
                                        startActivity(myCoupon_intent);
                                    }else{
                                        sold();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            } );

                        }
                        if (responderID.equals( uid ) && state.equals( "3" )){
//Seller has declined the resoponse
                            rootRef.child( "overall_ads" ).addValueEventListener( new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild( couponID )){
                                        Intent viewCoupon_intent=new Intent( getContext(),ViewCoupon.class );
                                        viewCoupon_intent.putExtra( "coupon_id",couponID );
                                        viewCoupon_intent.putExtra( "seller_id",seller );
                                        startActivity( viewCoupon_intent );
                                    }
                                    else{
                                        sold();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            } );

                        }
                        if (responderID.equals( uid ) &&state.equals( "2" )){
//Seller has confirmed the coupon response
                            rootRef.child( "overall_ads" ).addValueEventListener( new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild( couponID )){
                                        sold();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            } );
                        }
                    }
                } );
            }

            @NonNull
            @Override
            public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notifications_single, parent, false);
                return new NotificationHolder(view);
            }
        };

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    inboxRecycler.setVisibility( View.GONE );
                    emptyView.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        firebaseRecyclerAdapter.startListening();
        inboxRecycler.setAdapter( firebaseRecyclerAdapter );

    }

    public void sold(){
        Intent sold_intent=new Intent( getContext(),SellSuccess.class );
        startActivity( sold_intent );
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView notText;
        private String string;
        private String seller_name,coupon_details,responder_name;
        private ImageButton notCallButton,notChatButton;

        public NotificationHolder(View itemView) {
            super( itemView );
            mView=itemView;
        }

        public void setNotificationText(final String coupon_details, final String responderID, final String state, final String seller) {
            notText=(TextView)mView.findViewById( R.id.not_single_text );
            notCallButton=(ImageButton) mView.findViewById( R.id.not_single_call_btn );
            notChatButton=(ImageButton) mView.findViewById( R.id.not_single_chat_btn );
            notCallButton.setOnClickListener( new View.OnClickListener() {
                public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE =9415 ;

                @Override
                public void onClick(View v) {
                    Toast.makeText( getContext(), "Just a moment.", Toast.LENGTH_SHORT ).show();
                    DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
                    if(!responderID.equals( uid ) && state.equals( "1" )) {
//A buyer responded to your ad
                        rootRef.child(responderID).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String contact_number=dataSnapshot.child("mobile_number").getValue().toString();
                                Intent i = new Intent( Intent.ACTION_CALL );
                                i.setData( Uri.parse( "tel:" + contact_number ) );
                                if (ActivityCompat.checkSelfPermission( mView.getContext(), android.Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {

                                    requestPermissions( new String[]{Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST_CALL_PHONE );

                                    return;
                                }
                                startActivity( i );
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        } )  ;
                    }
                    if (state.equals( "2" )||state.equals( "3" )){
                        rootRef.child( "users" ).child( seller ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String contact_number=dataSnapshot.child( "mobile_number" ).getValue().toString();
                                Intent i = new Intent( Intent.ACTION_CALL );
                                i.setData( Uri.parse( "tel:" + contact_number ) );
                                if (ActivityCompat.checkSelfPermission( mView.getContext(), android.Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {

                                    requestPermissions( new String[]{Manifest.permission.CALL_PHONE},
                                            MY_PERMISSIONS_REQUEST_CALL_PHONE );

                                    return;
                                }
                                startActivity( i );
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        } );
                    }
                }
            } );
            notChatButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference rootRefTwo=FirebaseDatabase.getInstance().getReference();
                    if(!responderID.equals( uid ) && state.equals( "1" )){
//A buyer responded to coupon ad
                        Intent chat_intent=new Intent( getContext(),ChatActivity.class );
                        chat_intent.putExtra( "CurrentUserID",seller );
                        chat_intent.putExtra( "friendID",responderID );
                        startActivity(chat_intent);
                    }
                    if (state.equals( "2" )||state.equals( "3" )){
                        Intent chat_intent=new Intent( getContext(),ChatActivity.class );
                        chat_intent.putExtra( "CurrentUserID",responderID );
                        chat_intent.putExtra( "friendID",seller );
                        startActivity(chat_intent);
                    }

                }
            } );

            if(responderID.equals( uid ) && state.equals( "3" )){

//Seller has Declined your response.

                final DatabaseReference database=FirebaseDatabase.getInstance().getReference();
                database.child( "users" ).child( seller ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            seller_name=dataSnapshot.child( "name" ).getValue().toString();
                            string=seller_name+" has declined your request for "+coupon_details+" coupon";
                            notText.setText(string);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                } );


            }
            if(responderID.equals( uid ) &&state.equals( "2" )){
//Seller has Confirmed your response.

                final DatabaseReference database=FirebaseDatabase.getInstance().getReference();
                database.child( "users" ).child( seller ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            seller_name=dataSnapshot.child( "name" ).getValue().toString();
                            string=seller_name+" sold "+coupon_details+"coupon to you!!";
                            notText.setText(string);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                } );

            }
            if(!responderID.equals( uid ) && state.equals( "1" )){
//A buyer responded to your Coupon Ad.
                final DatabaseReference database=FirebaseDatabase.getInstance().getReference();
                database.child( "users" ).child( responderID ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            responder_name=dataSnapshot.child( "name" ).getValue().toString();
                            string=responder_name+" has responded to your "+coupon_details+" "+"coupon";
                            notText.setText(string);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                } );

            }
        }
    }

}
