package com.example.newsettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import static android.content.ContentValues.TAG;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageView avatar;
    String userEmail;
    TextView _name,_email;
    Button mdelete,mreset,msave,mchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userEmail = fAuth.getCurrentUser().getEmail();
        FirebaseUser user = fAuth.getCurrentUser();

        _name = findViewById(R.id.profilename);
        _email = findViewById(R.id.profileemail);
        mdelete = findViewById(R.id.delete_schedule);
        mreset = findViewById(R.id.resetall);
        String get_email = user.getEmail();
        String get_name = user.getDisplayName();
        _email.setText(get_email);
        _name.setText(get_name);

        if(user.getPhotoUrl() != null){
            String phtoUrl = user.getPhotoUrl().toString();
            phtoUrl=phtoUrl+"?type=large";

            Picasso.get().load(phtoUrl).placeholder(R.drawable.image_circle)
                    .error(R.drawable.image_circle).transform(new CircleTransform()).into(avatar);
        }

        DocumentReference documentReference = fStore.collection("users").document(userEmail);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                _name.setText(documentSnapshot.getString("fName"));
                _email.setText(documentSnapshot.getString("email"));
            }
        });
//新增Dialog
        mdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("刪除")
                .setMessage("確認是否要刪除行程？")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WriteBatch batch = fStore.batch();

                        fStore.collection("users").document(userEmail).collection("test")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                batch.delete(document.getReference());
                                                Toast.makeText(MainActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                            Toast.makeText(MainActivity.this, "刪除失敗", Toast.LENGTH_SHORT).show();
                                        }
                                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.w(TAG, "Batch completed.", task.getException());
                                            }
                                        });
                                    }
                                });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

        });
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size);
            int y = (source.getHeight() - size);
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); //log out
        GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(view.getContext(),FirstPage.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
        startActivity(new Intent(getApplicationContext(),FirstPage.class));
        finish();
    }


    public void name(View view){
        Intent intent = new Intent(this,Name.class);
        startActivity(intent);
    }

    public void changepassword(View view){
        Intent intent = new Intent(this,ChangePassword.class);
        startActivity(intent);
    }
}