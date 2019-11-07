package com.example.firebaseauthenticationandstoragetest.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaseauthenticationandstoragetest.Adapters.ChatListAdapter;
import com.example.firebaseauthenticationandstoragetest.Adapters.FriendRequestAdapter;
import com.example.firebaseauthenticationandstoragetest.Adapters.UsersAdapter;
import com.example.firebaseauthenticationandstoragetest.Models.ChatListModel;
import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FriendRequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    RecyclerView recyclerView;
    FriendRequestAdapter friendRequestAdapter;
    List<FriendRequestModel> friendRequestModelList;


    FirebaseAuth firebaseAuth;

    List<UsersModel> usersModels;
    DatabaseReference databaseReference;
    DatabaseReference friendRequestReference;
    FirebaseUser currentUser;



    public FriendRequestFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        friendRequestModelList = new ArrayList<>();

        Query friendRequestReference = FirebaseDatabase.getInstance().getReference("FriendsRequest").child(currentUser.getUid()).orderByChild("request_type").equalTo("received");
        friendRequestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendRequestModelList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    FriendRequestModel friendRequestModel = ds.getValue(FriendRequestModel.class);
                    friendRequestModelList.add(friendRequestModel);
                }
                getAllRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView = view.findViewById(R.id.recyclerFriendRequest);




        //populates friend request screen with all request to the user

        return view;
    }

    private void getAllRequest() {
        usersModels = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersModels.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren())
                {
                    UsersModel usersModel = ds.getValue(UsersModel.class);
                    for (FriendRequestModel friendRequestModel : friendRequestModelList)
                    {
                        if (usersModel.getUserid() != null && usersModel.getUserid().equals(friendRequestModel.getId())){
                            usersModels.add(usersModel);
                            break;
                        }
                    }

                    //adapter
                    friendRequestAdapter = new FriendRequestAdapter(getContext(),usersModels);
                    //set adapter data
                    recyclerView.setAdapter(friendRequestAdapter);
                    friendRequestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
