package com.example.firebaseauthenticationandstoragetest.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaseauthenticationandstoragetest.Adapters.FriendsAdapter;
import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.Models.FriendsModel;
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
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;




    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    RecyclerView recyclerView;
    FriendsAdapter friendsAdapter;
    List<FriendsModel> friendList;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    List<UsersModel> usersModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);



        friendList = new ArrayList<>();
       // usersModels = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        Query friendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUid()).orderByChild("friends").equalTo("YES");
        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    FriendsModel friendsModel = ds.getValue(FriendsModel.class);
                    friendList.add(friendsModel);
                }
                loadAllFriends();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView = view.findViewById(R.id.recyclerFriends);



        return view;
    }

    private void loadAllFriends() {
        usersModels = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersModels.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren())
                {
                    UsersModel usersModel = ds.getValue(UsersModel.class);
                    for (FriendsModel friendModel : friendList)
                    {
                        if (usersModel.getUserid() != null && usersModel.getUserid().equals(friendModel.getId())){
                            usersModels.add(usersModel);
                            break;
                        }
                    }

                    //adapter
                    friendsAdapter = new FriendsAdapter(getContext(),usersModels);
                    //set adapter data
                    recyclerView.setAdapter(friendsAdapter);
                    friendsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
