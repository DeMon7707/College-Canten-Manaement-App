package com.example.lenovo.cafecanteenadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.cafecanteenadmin.Common.Common;
import com.example.lenovo.cafecanteenadmin.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSign_In;
    boolean val=true;

    FirebaseDatabase db;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSign_In = (Button) findViewById(R.id.btnSign_In);

        //Init Firebase

        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSign_In.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });

    }

    private void signInUser(String phone, String password) {

        if(Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            final String localPhone = phone;
            final String localPassword = password;

            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (edtPhone.getText().toString().isEmpty()) {
                        mDialog.dismiss();
                        val = false;
                        edtPhone.setError("Please Enter the Mobile No.");
                        //Toast.makeText(SignIn.this, "Please Enter the Mobile No. !!!", Toast.LENGTH_SHORT).show();
                    }
                    if (edtPassword.getText().toString().isEmpty()) {
                        mDialog.dismiss();
                        val = false;
                        edtPassword.setError("Please Enter the Password");
                        //Toast.makeText(SignIn.this, "Please Enter the password !!!", Toast.LENGTH_SHORT).show();
                    }
                    if (val == true) {
                        if (dataSnapshot.child(localPhone).exists()) {
                            mDialog.dismiss();
                            User user = dataSnapshot.child(localPhone).getValue(User.class);
                            user.setPhone(localPhone);
                            if (Boolean.parseBoolean(user.getIsStaff()))  //  If  IsStaff == true
                            {
                                if (user.getPassword().equals(localPassword)) {
                                    //login OK
                                    Intent login = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(login);
                                    finish();
                                    Toast.makeText(SignIn.this, "Sign in Successful !!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    edtPassword.setError("Wrong Password !");
                                    Toast.makeText(SignIn.this, "Wrong Password!!!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                edtPhone.setError("Not a Admin Account !");
                                Toast.makeText(SignIn.this, "Please login with Admin account", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            edtPhone.setError("This User doesn't Exist !");
                            Toast.makeText(SignIn.this, "User doesn't exit in database", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(SignIn.this, "Please check your Internet Connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
