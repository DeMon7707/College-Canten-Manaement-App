package com.example.lenovo.cafe_canteen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Model.User;
import com.google.android.gms.common.oob.SignUp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.BitSet;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp,btnSignIn;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = (Button)findViewById(R.id.btnSign_Up);
        btnSignIn = (Button)findViewById(R.id.btnSign_In);

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);

        //init paper
        Paper.init(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(MainActivity.this, com.example.lenovo.cafe_canteen.SignUp.class);
                startActivity(signUp);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = new Intent(MainActivity.this,SignIn.class);
                startActivity(signIn);
            }
        });

        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if(user != null && pwd != null) {
            if(!user.isEmpty() && !pwd.isEmpty()) {
                login(user,pwd);
            }
        }
    }

    private void login(final String phone, final String pwd) {
        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check if user doesn't exist in database
                        if (dataSnapshot.child(phone).exists()) {
                            //Get user information
                            mDialog.dismiss();
                            User user = dataSnapshot.child(phone).getValue(User.class);
                            user.setPhone(phone);  //set phone
                            if (user.getPassword().equals(pwd)) {
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                                //Toast.makeText(MainActivity.this, "Sign In Successful !!!", Toast.LENGTH_SHORT).show();
                            } else {
                                //edtPassword.setError("Wrong Password !");
                                Toast.makeText(MainActivity.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            //edtPhone.setError("This User doesn't exist !");
                            Toast.makeText(MainActivity.this, "User doesn't exist !!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
        else{
            Toast.makeText(MainActivity.this, "Please check your Internet Connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
