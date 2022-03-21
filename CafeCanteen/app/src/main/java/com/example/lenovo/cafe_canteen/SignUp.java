package com.example.lenovo.cafe_canteen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {
    MaterialEditText edtname,edtphone,edtpassword,edtreenterpassword;
    Button btnSign_Up;
    boolean val=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtname = (MaterialEditText)findViewById(R.id.edtName);
        edtphone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtpassword = (MaterialEditText)findViewById(R.id.edtPassword);
        edtreenterpassword = (MaterialEditText)findViewById(R.id.edtReEnterPassword);
        btnSign_Up = (Button)findViewById(R.id.btnSign_Up);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(edtname.getText().toString().isEmpty()) {
                                mDialog.dismiss();
                                val=false;
                                edtname.setError("Please Enter the Name");
                                //Toast.makeText(SignUp.this, "Please Enter the Name !!!", Toast.LENGTH_SHORT).show();
                            }
                            if(edtphone.getText().toString().isEmpty() || edtphone.getText().toString().length()!=10){
                                mDialog.dismiss();
                                val=false;
                                edtphone.setError("Please Enter a Valid Mobile No. !");
                                //Toast.makeText(SignUp.this, "Please Enter the Mobile No. to Register !!!", Toast.LENGTH_SHORT).show();
                            }
                            if(edtpassword.getText().toString().isEmpty()){
                                mDialog.dismiss();
                                val=false;
                                edtpassword.setError("Please Enter the Password");
                                //Toast.makeText(SignUp.this, "Please Enter the Password !!!", Toast.LENGTH_SHORT).show();
                            }
                            if(edtreenterpassword.getText().toString().isEmpty() || !(edtreenterpassword.getText().toString().equals(edtpassword.getText().toString()))){
                                mDialog.dismiss();
                                val=false;
                                edtreenterpassword.setError("Password do not Match!");
                                //Toast.makeText(SignUp.this, "Password do not Match !!!", Toast.LENGTH_SHORT).show();
                            }
                            if(val == true){
                                //check if phone no already exist
                                if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                    //Get user information
                                    mDialog.dismiss();
                                    edtphone.setError("Phone no. Already Registered !");
                                    //Toast.makeText(SignUp.this, "Phone no. already registered !!!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mDialog.dismiss();
                                    User user = new User(edtname.getText().toString(), edtpassword.getText().toString());
                                    table_user.child(edtphone.getText().toString()).setValue(user);
                                    Intent signinIntent = new Intent(SignUp.this, SignIn.class);
                                    startActivity(signinIntent);
                                    finish();
                                    Toast.makeText(SignUp.this, "Account created successfully !!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                } else {
                    Toast.makeText(SignUp.this, "Please check your Internet Connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
