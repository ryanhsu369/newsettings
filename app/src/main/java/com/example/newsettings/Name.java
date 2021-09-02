package com.example.newsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class Name extends AppCompatActivity {

    EditText mName;
    Button mFinishButton;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userEmail;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);
        userEmail = fAuth.getCurrentUser().getEmail();

        final String key ="fName";
        mName = findViewById(R.id.Name);
        mFinishButton = findViewById(R.id.finishbutton);

        mFinishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name1 = mName.getText().toString();
                DocumentReference noteRef = db.collection("users").document(userEmail);
                noteRef.update(key,name1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        if(TextUtils.isEmpty(name1)){
                            mName.setError("不可為空");
                        }
                        else{
                            Toast.makeText(Name.this,"更新完成",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Name.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void back(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
