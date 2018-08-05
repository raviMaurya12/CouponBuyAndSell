package com.example.lenovo.buyandsell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Account extends AppCompatActivity {

    private CircleImageView image;
    private Button changeImageButton,editProfileButton;
    private TextView displayName,course,contactNumber,paytmNumber,address;
    private DatabaseReference mDatabase;
    private String display_name,course_name,address_line,contact_number,paytm_number,uid;
    public static final int GALLERY_PICK =1 ;
    private StorageReference mStorageRef;
    private StorageReference thumb_storage_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        uid=current_user.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child( "users" ).child( uid );
        mStorageRef = FirebaseStorage.getInstance().getReference();

        image=(CircleImageView)findViewById( R.id.account_image );
        changeImageButton=(Button)findViewById( R.id.account_changeImageButton );
        editProfileButton=(Button)findViewById( R.id.account_editProfileButton );
        displayName=(TextView)findViewById( R.id.account_displayName );
        course=(TextView)findViewById( R.id.account_course );
        contactNumber=(TextView)findViewById( R.id.account_contactNumber );
        paytmNumber=(TextView)findViewById( R.id.account_paytmNumber );
        address=(TextView)findViewById(R.id.account_address );

        mDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name=dataSnapshot.child( "name" ).getValue().toString();
                if(!(dataSnapshot.child("mobile_number").getValue().toString()).equals("0")){

                    course_name=dataSnapshot.child( "course" ).getValue().toString()+","+dataSnapshot.child( "year" ).getValue().toString()+"st "+"Year";
                    contact_number= dataSnapshot.child( "mobile_number" ).getValue().toString();
                    paytm_number= dataSnapshot.child( "paytm_number" ).getValue().toString();
                    address_line=dataSnapshot.child( "hostel_name" ).getValue().toString()+","+dataSnapshot.child( "room_number" ).getValue().toString()+"("+
                            dataSnapshot.child( "room_section" ).getValue().toString()+")";
                    String img=dataSnapshot.child( "image" ).getValue().toString();
                    Picasso.get().load(img).placeholder( R.drawable.defaultpic ).into(image);

                    course.setText( course_name );
                    contactNumber.setText(contact_number);
                    paytmNumber.setText( paytm_number );
                    address.setText( address_line );
                }
                displayName.setText( display_name );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        editProfileButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent completeProfile_intent=new Intent( Account.this,CompleteProfile.class );
                startActivity( completeProfile_intent );
            }
        } );

        changeImageButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                        //Intent for Opening Gallery
                        Intent gallery_intent= new Intent();
                        gallery_intent.setType("image/*");
                        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(gallery_intent,GALLERY_PICK);
            }
        } );

    }

    //On successful gallery Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK){

            //Getting URI of the selected image
            Uri imageUri = data.getData();

            //Passing URI of selected image to open Crop Activity using an External Crop-Image-Library
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);


        }

        //Events after cropping the Image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                //Getting URI of the cropped Image.
                Uri resultUri = result.getUri();


                //getting Actual file from uri for compression.
                File thumb_file = new File(resultUri.getPath());


                final Bitmap thumb_image = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_file);


                //Storage reference for Creating an blank jpg image path with name=uid.jpg under profile_images folder
                StorageReference filePath=mStorageRef.child("profile_images").child(uid+".jpg");
                thumb_storage_reference= FirebaseStorage.getInstance().getReference().child("users").child("thumbs").child(uid+".jpg");

                Toast.makeText( this, "Just a moment.", Toast.LENGTH_SHORT ).show();
                //Putting the uri of the cropped image into the created blank image path add setting Oncomplete listener
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            //getting the download link for downloading the profile pic
                            final String download_link=task.getResult().getDownloadUrl().toString();


                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumb_image_byte = baos.toByteArray();
                            UploadTask uploadTask = thumb_storage_reference.putBytes(thumb_image_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        String thumb_download_link=task.getResult().getDownloadUrl().toString();

                                        //Changing the values in the table Users and adding OnComplete Listener
                                        Map update_Hashmap = new HashMap();
                                        update_Hashmap.put("image",download_link);
                                        update_Hashmap.put("thumb_image",thumb_download_link);
                                        mDatabase.updateChildren(update_Hashmap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(Account.this,"Uploading Successful!",
                                                            Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(Account.this,"Changing Firebase Database Failed!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });



                                    }else{
                                        Toast.makeText(Account.this,"Thumbnail Uploading Failed", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(Account.this,"Profile Image Uploading Failed.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
