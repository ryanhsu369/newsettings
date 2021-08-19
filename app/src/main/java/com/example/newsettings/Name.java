package com.example.newsettings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Name extends AppCompatActivity {

    EditText mName;
    FirebaseAuth fAuth;
    Button mFinishButton;

    FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);

        mName = findViewById(R.id.Name);
        fAuth = FirebaseAuth.getInstance();
        mFinishButton = findViewById(R.id.finishbutton);

        mFinishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    mName.setError("需填入姓名.");
                    return;
                }
            }
        });
    }



    public void back(View view){
        Intent intent = new Intent(this,Id.class);
        startActivity(intent);
    }
}
