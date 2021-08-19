package com.example.newsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Id extends AppCompatActivity {

    TextView _name,_email;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id);

        _name = findViewById(R.id.profilename);
        _email = findViewById(R.id.profileemail);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                _name.setText(documentSnapshot.getString("fName"));
                _email.setText(documentSnapshot.getString("email"));
            }
        });
    }

    public void name(View view){
        Intent intent = new Intent(this,Name.class);
        startActivity(intent);
    }

    public void email(View view){
        Intent intent = new Intent(this,Email.class);
        startActivity(intent);
    }

    public void back(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
