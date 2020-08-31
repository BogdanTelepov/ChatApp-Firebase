package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private User user;
    RecyclerView recyclerView;
    MessageAdapter adapter;
    List<Message> messageList = new ArrayList<>();
    EditText editTextEnterMessage;
    ImageView imageViewSend;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user = (User) getIntent().getSerializableExtra("user");
        chat = (Chat) getIntent().getSerializableExtra("chat");
        editTextEnterMessage = findViewById(R.id.editTxtMessage);
        recyclerView = findViewById(R.id.recyclerViewChat);
        imageViewSend = findViewById(R.id.imageSend);
        if (chat == null) {
            chat = new Chat();
            List<String> userIds = new ArrayList<>();
            userIds.add(user.getId());
            userIds.add(FirebaseAuth.getInstance().getUid());
            chat.setUserIds(userIds);
        }else {
            initList();
            getMessages();
        }
    }

    private void getMessages() {
        FirebaseFirestore.getInstance().collection("chats")
                .document(chat.getId())
                .collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            switch (change.getType()) {
                                case ADDED:
                                    messageList.add(change.getDocument().toObject(Message.class));
                                    break;

                                case MODIFIED:

                                    break;

                                case REMOVED:

                                    break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new MessageAdapter(this, messageList);
        recyclerView.setAdapter(adapter);

    }


    public void onClickSend(View view) {
        String text = editTextEnterMessage.getText().toString().trim();
        if (chat.getId() != null) {
            sendMessage(text);
        } else {
            createChat(text);
        }
    }

    private void createChat(String text) {
        FirebaseFirestore.getInstance()
                .collection("chats")
                .add(chat)
                .addOnSuccessListener(documentReference -> {
                    chat.setId(documentReference.getId());
                    sendMessage(text);
                });
    }

    private void sendMessage(String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        FirebaseFirestore.getInstance().collection("chats")
                .document(chat.getId())
                .collection("messages")
                .add(map);
    }
}