package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatAdapter;
import com.example.chatapp.models.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabContacts;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    private List<Chat> chats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, PhoneActivity.class));
            finish();
            return;
        }
        fabContacts = findViewById(R.id.fab);
        fabContacts.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ContactsActivity.class));
            finish();
        });
        recyclerView = findViewById(R.id.recyclerViewChats);


        getChats();
        initList();
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ChatAdapter(this, chats);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycle_view_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("chat", chats.get(position));
            startActivity(intent);
        });
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

}