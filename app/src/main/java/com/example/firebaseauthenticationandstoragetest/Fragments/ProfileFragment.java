package com.example.firebaseauthenticationandstoragetest.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firebaseauthenticationandstoragetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    EditText etDName, etEmail;
    Button btnEditSave;
    ImageView profileIV;
    ProgressBar progressBar;
    private boolean check = false;

    //Storage path
    String storagePath = "Users_Profile_Imgs/";

    //check for image
    String profileImage = "image";
    Uri uriProfileimage;

    private static final int CHOOSE_IMAGE = 101;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etDName = view.findViewById(R.id.etUsersDName);
        etEmail = view.findViewById(R.id.etUsersEmail);
        btnEditSave = view.findViewById(R.id.btnEditSave);
        profileIV = view.findViewById(R.id.usersprofileIV);
        progressBar = view.findViewById(R.id.progressBar3);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        view.findViewById(R.id.btnEditSave).setOnClickListener(this);
        view.findViewById(R.id.usersprofileIV).setOnClickListener(this);

        loadUserinfo();
        return view;
    }

    private void loadUserinfo() {
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data is found
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String image = ""+ ds.child("image").getValue();

                    //setting user data
                    etDName.setText(name);
                    etEmail.setText(email);

                    try {
                        Picasso.get().load(image).into(profileIV);
                    }
                    catch (Exception e)
                    {
                        //Toast.makeText(HomeActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
                        Log.d("Error: ",e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(HomeActivity.this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
                Log.d("onCancelled: ", databaseError.getMessage());
            }
        });
    }

    private void ImageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Profile Image"),CHOOSE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data.getData() != null)
        {
            uriProfileimage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uriProfileimage);
                profileIV.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri uri)
    {
        final FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        StorageReference mStorageRef;
        //creating image url path
        String filePathAndName = storagePath + "" + profileImage + "_"+ user.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mStorage2 = mStorageRef.child(filePathAndName);

        mStorage2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUrl = uriTask.getResult();

                        //check if image is uploaded or not
                        if(uriTask.isSuccessful()) {
                            //image uploaded
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profileImage, downloadUrl.toString());

                            ref.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //do nothing

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Image not uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            //not uploaded
                            Toast.makeText(getActivity(), "Error Uploading Profile Image", Toast.LENGTH_SHORT).show();
                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Profile Image Not Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnEditSave:
                if(!check)
                {
                    etDName.setEnabled(true);
                    etEmail.setEnabled(true);
                    profileIV.setEnabled(true);
                    btnEditSave.setText("Save");
                    check = true;
                    Toast.makeText(getActivity(), "Edit Profile", Toast.LENGTH_SHORT).show();
                    profileIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageChooser();
                        }
                    });
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    saveInfo();
                    etDName.setEnabled(false);
                    etEmail.setEnabled(false);
                    profileIV.setClickable(true);
                    btnEditSave.setText("Edit");
                    check = false;
                }
                break;

        }
    }

    private void saveInfo() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        String name = etDName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if(name.isEmpty())
        {
            etDName.setError("Display Name Cannot Be Empty");
            etDName.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            etEmail.setError("Email Cannot Be Email");
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etEmail.setError("Please Enter A Valid Email!");
            etEmail.requestFocus();
            return;
        }

        HashMap<String,Object> h = new HashMap<>();
        h.put("name",name);
        h.put("email",email);

        if(uriProfileimage != null) {
            //uploading and updating user profile image if one exist
            uploadImageToFirebaseStorage(uriProfileimage);
        }
        if(user != null)
        {
            ref.child(id).updateChildren(h).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
