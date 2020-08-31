package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatapp.adapters.ChatAdapter;
import com.example.chatapp.R;
import com.example.chatapp.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Chat> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, PhoneActivity.class));
        }

        recyclerView = findViewById(R.id.recyclerViewMain);
        initList();
        getChats();
    }

    private void getChats() {
        String myId = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("chats")
                .whereArrayContains("userIds", myId)
                .addSnapshotListener((snapshots, error) -> {
                    for (DocumentChange change : snapshots.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                Chat chat = change.getDocument().toObject(Chat.class);
                                chat.setId(change.getDocument().getId());
                                chats.add(chat);
                                break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        adapter = new ChatAdapter(this, chats);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("chat", chats.get(position));
            startActivity(intent);
        });
    }

    public void onClickContacts(View view) {
        startActivity(new Intent(this, ContactsActivity.class));
    }
}