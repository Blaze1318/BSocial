package com.example.firebaseauthenticationandstoragetest.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaseauthenticationandstoragetest.Adapters.FriendRequestAdapter;
import com.example.firebaseauthenticationandstoragetest.Adapters.UsersAdapter;
import com.example.firebaseauthenticationandstoragetest.Models.FriendRequestModel;
import com.example.firebaseauthenticationandstoragetest.Models.UsersModel;
import com.example.firebaseauthenticationandstoragetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendRequestFragment#newInstance} factory method to
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

    public FriendRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendRequestFragment newInstance(String param1, String param2) {
        FriendRequestFragment fragment = new FriendRequestFragment();
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
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        recyclerView = view.findViewById(R.id.recyclerFriendRequest);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendRequestModelList = new ArrayList<>();

        getAllUsers();
        return view;
    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //get path to the database with the Users
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("FriendRequest");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendRequestModelList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    FriendRequestModel model = ds.getValue(FriendRequestModel.class);

                    //get all users except currently signed in one
                    if(!model.getRequestFrom().equals(user.getUid()))
                    {
                        friendRequestModelList.add(model);
                    }

                    //adapter
                    friendRequestAdapter = new FriendRequestAdapter(getActivity(),friendRequestModelList);

                    //set recyclerview
                    recyclerView.setAdapter(friendRequestAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
