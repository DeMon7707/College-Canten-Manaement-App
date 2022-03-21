package com.example.lenovo.cafe_canteen;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Database.Database;
import com.example.lenovo.cafe_canteen.Model.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Wallet extends AppCompatActivity {

    Button add_balance;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        add_balance = (Button)findViewById(R.id.add_balance);
        total = (TextView)findViewById(R.id.total);

        String bal =String.valueOf(Common.currentUser.getBalance());

        total.setText("$"+bal);

        add_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Wallet.this);

        alertDialog.setTitle("ADD MONEY");
        alertDialog.setMessage("Enter Amount : ");

        final EditText editbalance = new EditText(Wallet.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editbalance.setLayoutParams(lp);
        alertDialog.setView(editbalance);  //Add edit Text to alert dialog

        alertDialog.setIcon(R.drawable.ic_account_balance_wallet_black_24dp);

        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialogInterface.dismiss();
                double amount=0;
                //Get total price from txtTotalPrice

                amount = Double.valueOf(editbalance.getText().toString());

                //Update Balance
                double balance = Common.currentUser.getBalance()+amount;
                Map<String ,Object> update_balance = new HashMap<>();
                update_balance.put("balance",balance);

                FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(Common.currentUser.getPhone())
                        .updateChildren(update_balance)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                String balnc = String.valueOf(balance);
                total.setText("$"+balnc);

                Toast.makeText(Wallet.this, "Money Added !!!", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
