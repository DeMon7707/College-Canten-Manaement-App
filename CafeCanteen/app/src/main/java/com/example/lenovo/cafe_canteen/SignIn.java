package com.example.lenovo.cafe_canteen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSign_In;
    CheckBox ckbRemember;
    boolean value=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);

        btnSign_In = (Button)findViewById(R.id.btnSign_In);
        ckbRemember = (CheckBox)findViewById(R.id.ckbRemember);

        //init paper
        Paper.init(this);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSign_In.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    //save user and password
                    if(ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(edtPhone.getText().toString().isEmpty()){
                                mDialog.dismiss();
                                value=false;
                                edtPhone.setError("Please Enter the Mobile No.");
                                //Toast.makeText(SignIn.this, "Please Enter the Mobile No. !!!", Toast.LENGTH_SHORT).show();
                            }
                            if(edtPassword.getText().toString().isEmpty()){
                                mDialog.dismiss();
                                value=false;
                                edtPassword.setError("Please Enter the Password");
                                //Toast.makeText(SignIn.this, "Please Enter the password !!!", Toast.LENGTH_SHORT).show();
                            }
                            if(value == true){
                                //Check if user doesn't exist in database
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    //Get user information
                                    mDialog.dismiss();
                                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtPhone.getText().toString());  //set phone
                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        Intent homeIntent = new Intent(SignIn.this, Home.class);
                                        Common.currentUser = user;
                                        startActivity(homeIntent);
                                        finish();
                                        Toast.makeText(SignIn.this, "Sign In Successful !!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        edtPassword.setError("Wrong Password !");
                                        Toast.makeText(SignIn.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDialog.dismiss();
                                    edtPhone.setError("This User doesn't exist !");
                                    Toast.makeText(SignIn.this, "User doesn't exist !!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SignIn.this, "Please check your Internet Connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

    }
}
