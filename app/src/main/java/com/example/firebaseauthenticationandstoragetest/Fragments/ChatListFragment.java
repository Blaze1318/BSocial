package com.example.firebaseauthenticationandstoragetest.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebaseauthenticationandstoragetest.Adapters.ChatListAdapter;
import com.example.firebaseauthenticationandstoragetest.LoginActivity;
import com.example.firebaseauthenticationandstoragetest.Models.ChatListModel;
import com.example.firebaseauthenticationandstoragetest.Models.ChatModel;
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
 */
public class ChatListFragment extends Fragment {

    //init variables
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ChatListModel> chatListModels;
    List<UsersModel> usersModels;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    ChatListAdapter chatListAdapter;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        chatListModels = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(currentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListModels.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChatListModel chatListModel = ds.getValue(ChatListModel.class);
                    chatListModels.add(chatListModel);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        recyclerView = view.findViewById(R.id.recycler_view_chat_list);

        return view;
    }
    //Loading chats
    private void loadChats() {
        usersModels = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersModels.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren())
                {
                    UsersModel usersModel = ds.getValue(UsersModel.class);
                    for (ChatListModel chatListModel : chatListModels)
                    {
                        if (usersModel.getUserid() != null && usersModel.getUserid().equals(chatListModel.getId())){
                            usersModels.add(usersModel);
                            break;
                        }
                    }

                    //adapter
                    chatListAdapter = new ChatListAdapter(getContext(),usersModels);
                    //set adapter data
                    recyclerView.setAdapter(chatListAdapter);
                    //setting last message
                    for(int i = 0; i < usersModels.size(); i++)
                    {
                        lastMessage(usersModels.get(i).getUserid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(final String userid) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastmessage = "default";
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChatModel chatModel = ds.getValue(ChatModel.class);
                    if (chatModel==null)
                    {
                        continue;
                    }
                    String sender = chatModel.getSender();
                    String receiver = chatModel.getReceiver();

                    if (sender == null || receiver == null)
                    {
                        continue;
                    }

                    if (chatModel.getReceiver().equals(currentUser.getUid()) && chatModel.getSender().equals(userid) ||
                            chatModel.getReceiver().equals(userid) && chatModel.getSender().equals(currentUser.getUid()))
                    {
                        theLastmessage = chatModel.getMessage();
                    }
                }

                chatListAdapter.setLastMessageMap(userid,theLastmessage);
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserstatus()
    {
        //checking if current user is logged on
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null)
        {
            String myId = user.getUid();
        }
        else
        {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }


}
