package com.example.lenovo.buyandsell;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyFragment extends Fragment {

    private View Fview;
    private RecyclerView buyRecycler;
    private TextView emptyView;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Coupons,CouponHolder> firebaseRecyclerAdapter;
    private ProgressDialog mProgress;
    private  DatabaseReference usersDatabase;

    public BuyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Fview=inflater.inflate( R.layout.fragment_buy, container, false );

        buyRecycler=(RecyclerView)Fview.findViewById( R.id.buy_recycler );
        buyRecycler.setLayoutManager( new LinearLayoutManager(getContext()) );
        emptyView=(TextView)Fview.findViewById( R.id.empty_view );

        mDatabase= FirebaseDatabase.getInstance().getReference().child( "overall_ads" );

        mProgress=new ProgressDialog(getContext());

        return Fview;
    }

    @Override
    public void onStart() {
        super.onStart();

        mProgress.setTitle( "Loading" );
        mProgress.setMessage( "Please wait while we get things ready for you." );
        mProgress.setCanceledOnTouchOutside( false );
        mProgress.show();

        FirebaseRecyclerOptions<Coupons> query=new FirebaseRecyclerOptions.Builder<Coupons>().setQuery(mDatabase,Coupons.class).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Coupons, CouponHolder>(query) {
            @Override
            protected void onBindViewHolder(@NonNull final CouponHolder holder, int position, @NonNull final Coupons model) {
                final String adContentRoot=getRef(position).getKey();
                mDatabase.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(adContentRoot)){
                            mDatabase.child(adContentRoot).addValueEventListener( new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild( "caterer" )) {
                                        final String caterer = dataSnapshot.child( "caterer" ).getValue().toString();
                                        final String dateAndMeal = dataSnapshot.child( "date" ).getValue().toString() + " " + dataSnapshot.child( "meal" ).getValue().toString();
                                        final String sellerId = dataSnapshot.child( "seller" ).getValue().toString();
                                        final String price = "Rs." + dataSnapshot.child( "price" ).getValue().toString();


                                        usersDatabase = FirebaseDatabase.getInstance().getReference().child( "users" ).child( sellerId );
                                        usersDatabase.addValueEventListener( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String sellerName = dataSnapshot.child( "name" ).getValue().toString();
                                                final String thumbImg=dataSnapshot.child( "thumb_image" ).getValue().toString();
                                                final String mobile_num=dataSnapshot.child( "mobile_number" ).getValue().toString();

                                                holder.mView.post( new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(!mobile_num.equals( "0" )) {
                                                            holder.setCaterer( caterer );
                                                            holder.setDateAndMeal( dateAndMeal );
                                                            holder.setSellerName( sellerName );
                                                            holder.setPrice( price );
                                                            holder.setImage( thumbImg );
                                                            mProgress.dismiss();
                                                        }else{
                                                            Intent completeProfile = new Intent( getContext(), CompleteProfile.class );
                                                            startActivity( completeProfile );
                                                        }
                                                    }
                                                } );
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        } );


                                        holder.mView.setOnClickListener( new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent viewCoupon_intent = new Intent( getContext(), ViewCoupon.class );
                                                viewCoupon_intent.putExtra( "coupon_id", adContentRoot );
                                                viewCoupon_intent.putExtra( "seller_id", sellerId );
                                                startActivity( viewCoupon_intent );
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

            @NonNull
            @Override
            public CouponHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.buy_single_coupon, parent, false);
                return new CouponHolder(view);
            }
        };

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    buyRecycler.setVisibility( View.GONE );
                    emptyView.setVisibility( View.VISIBLE );
                    mProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        firebaseRecyclerAdapter.startListening();
        buyRecycler.setAdapter( firebaseRecyclerAdapter );
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class CouponHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView single_caterer,single_dateAndMeal,single_sellerName,single_price;
        private CircleImageView imageView;

        public CouponHolder(View itemView) {
            super( itemView );
            mView=itemView;
        }

        public void setCaterer(String caterer) {
            single_caterer=(TextView)mView.findViewById( R.id.sell_singleCatererName );
            single_caterer.setText( caterer );
        }

        public void setDateAndMeal(String dateAndMeal) {
            single_dateAndMeal=(TextView)mView.findViewById( R.id.sell_singleDateAndMeal );
            single_dateAndMeal.setText( dateAndMeal );
        }

        public void setSellerName(String sellerName) {
            single_sellerName=(TextView)mView.findViewById( R.id.buy_singleSellerName );
            single_sellerName.setText( sellerName );
        }

        public void setPrice(String price) {
            single_price=(TextView)mView.findViewById( R.id.sell_singlePrice );
            single_price.setText( price );
        }

        public void setImage(String thumbImg) {
           imageView=(CircleImageView)mView.findViewById( R.id.buy_singleImage ) ;
            Picasso.get().load( thumbImg ).placeholder( R.drawable.defaultpic ).into(imageView);
        }

    }

}
