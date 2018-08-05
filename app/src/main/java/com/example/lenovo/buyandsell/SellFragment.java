package com.example.lenovo.buyandsell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;


public class SellFragment extends Fragment {

    private FloatingActionButton fab;
    private View Fview;
    private DatabaseReference mDatabase;
    private String check="-1";
    private RecyclerView sellRecycler;
    private TextView sellEmptyTextView;
    private  DatabaseReference adsFromUserDatabase;
    FirebaseRecyclerAdapter<CouponsFromUser,CouponFromUserHolder> firebaseRecyclerAdapter;
    private String couponId;  //This must be member variable otherwise can throw null pointer Exception.

    public SellFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fview=inflater.inflate( R.layout.fragment_sell, container, false );
        // Inflate the layout for this fragment
        fab=(FloatingActionButton)Fview.findViewById( R.id.sell_fab );

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=current_user.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child( uid );

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mob_num=dataSnapshot.child("mobile_number").getValue().toString();
                if(mob_num.equals( "0" )){
                    check="0";
                }else{
                    check="1";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check.equals( "0" )) {
                    Intent completeProfile = new Intent( getContext(), CompleteProfile.class );
                    startActivity( completeProfile );
                }else if (check.equals( "1" )){
                    Intent postAd_intent=new Intent( getContext(),PostAd.class );
                    startActivity( postAd_intent );
                }
            }
        } );

        sellEmptyTextView=(TextView)Fview.findViewById( R.id.sell_empty_view );
        sellRecycler=(RecyclerView)Fview.findViewById( R.id.sell_recycler );
        sellRecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );

        adsFromUserDatabase=FirebaseDatabase.getInstance().getReference().child( "ads_from_user" ).child(uid);

        return Fview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CouponsFromUser> query=new FirebaseRecyclerOptions.Builder<CouponsFromUser>().setQuery(adsFromUserDatabase,CouponsFromUser.class).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<CouponsFromUser, CouponFromUserHolder>(query) {
            @Override
            protected void onBindViewHolder(@NonNull final CouponFromUserHolder holder, int position, @NonNull CouponsFromUser model) {
                couponId=getRef(position).getKey();
                adsFromUserDatabase.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(couponId)){
                            adsFromUserDatabase.child( couponId ).addValueEventListener( new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()) {
                                        String coupon_price = "Rs." + dataSnapshot.child( "price" ).getValue().toString();
                                        String caterer_name = dataSnapshot.child( "caterer" ).getValue().toString();
                                        String date_and_meal = dataSnapshot.child( "date" ).getValue().toString() + " " + dataSnapshot.child( "meal" ).getValue().toString();
                                        String time_ago = dataSnapshot.child( "timestamp" ).getValue().toString();
                                        holder.set( coupon_price, caterer_name, date_and_meal, time_ago );
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

                holder.mView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myCoupon_intent=new Intent( getContext(),MyCoupon.class );
                        myCoupon_intent.putExtra("couponId",couponId);
                        startActivity( myCoupon_intent );
                    }
                } );
            }

            @NonNull
            @Override
            public CouponFromUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sell_single_coupon, parent, false);
                return new CouponFromUserHolder(view);
            }
        };

        adsFromUserDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    sellRecycler.setVisibility( View.GONE );
                    sellEmptyTextView.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        firebaseRecyclerAdapter.startListening();
        sellRecycler.setAdapter( firebaseRecyclerAdapter );

    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class CouponFromUserHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView sellSinglePrice,sellSingleCaterer,sellSingleDateAndMeal,sellSingleTimeAgo;

        public CouponFromUserHolder(View itemView) {
            super( itemView );
            mView=itemView;
        }

        public void set(String coupon_price, String caterer_name, String date_and_meal, String time_ago) {

            sellSinglePrice=(TextView)mView.findViewById( R.id.sell_singlePrice );
            sellSingleCaterer=(TextView)mView.findViewById( R.id.sell_singleCatererName );
            sellSingleDateAndMeal=(TextView)mView.findViewById( R.id.sell_singleDateAndMeal );
            sellSingleTimeAgo=(TextView)mView.findViewById( R.id.sell_singleTimeAgo );

            sellSinglePrice.setText(coupon_price);
            sellSingleCaterer.setText( caterer_name );
            sellSingleDateAndMeal.setText( date_and_meal );
            sellSingleTimeAgo.setText(time_ago);

        }
    }
}
