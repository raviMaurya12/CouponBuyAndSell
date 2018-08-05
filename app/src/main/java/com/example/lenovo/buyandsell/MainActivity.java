package com.example.lenovo.buyandsell;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewpager;
    private CustomPagerAdapter mPagerAdapter;
    private TabLayout main_tabs;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mAuth = FirebaseAuth.getInstance();

        mToolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull( getSupportActionBar() ).setTitle("Coupon Buy and Sell");

        mViewpager=(ViewPager)findViewById( R.id.main_viewPager );
        mPagerAdapter=new CustomPagerAdapter( getSupportFragmentManager() );
        main_tabs=(TabLayout)findViewById( R.id.main_tabLayout );
        mViewpager.setAdapter( mPagerAdapter );
        main_tabs.setupWithViewPager( mViewpager );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            sendToStart();
        }else{
            FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
            String uid=current_user.getUid();
            mDatabase=FirebaseDatabase.getInstance().getReference().child( "users" ).child( uid );
            mDatabase.child("online").setValue(true);
            mDatabase.child( "last_seen" ).setValue( "online" );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            mDatabase.child( "online" ).setValue( false );
            mDatabase.child("last_seen").setValue( ServerValue.TIMESTAMP );
        }
    }

    private void sendToStart() {
        Intent userActivity = new Intent(MainActivity.this,User.class);
        startActivity(userActivity);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate( R.menu.main_menu,menu );
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected( item );

        if(item.getItemId()==R.id.main_menu_logout){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if(item.getItemId()==R.id.main_menu_account){
            Intent account_intent=new Intent( MainActivity.this,Account.class );
            startActivity( account_intent );
        }
        if(item.getItemId()==R.id.main_menu_history){
            Toast.makeText( this, "We are still working on this Page.", Toast.LENGTH_SHORT ).show();
        }
        if(item.getItemId()==R.id.main_menu_mune) {
            Toast.makeText( this, "Menu will soon be available.", Toast.LENGTH_SHORT ).show();
        }
        return true;
    }

}
