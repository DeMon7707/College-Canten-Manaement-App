package com.example.lenovo.cafe_canteen;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Database.Database;
import com.example.lenovo.cafe_canteen.Model.Order;
import com.example.lenovo.cafe_canteen.Model.Request;
import com.example.lenovo.cafe_canteen.Model.User;
import com.example.lenovo.cafe_canteen.viewHolder.CartAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart =  new ArrayList<>();

    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ///firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init

        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (Button) findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.size() > 0)
                    showAlertDialog();
                else
                {
                    Toast.makeText(Cart.this, "Your Cart is Empty !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();

    }

    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);

        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your Time: ");

        /*final EditText editTime = new EditText(Cart.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editTime.setLayoutParams(lp);
        alertDialog.setView(editTime);  //Add edit Text to alert dialog*/

        LayoutInflater inflater = this.getLayoutInflater();
        View order_payment_mode = inflater.inflate(R.layout.order_payment_mode,null);

        final MaterialEditText edtTime = (MaterialEditText)order_payment_mode.findViewById(R.id.edtTime);

        final RadioButton rdiCash = (RadioButton)order_payment_mode.findViewById(R.id.rdiCash);
        final RadioButton rdiEatItBalance = (RadioButton)order_payment_mode.findViewById(R.id.rdiEatItBalance);

        alertDialog.setView(order_payment_mode);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String time = edtTime.getText().toString();
                if(time.isEmpty() || time.compareTo("09:00:00") <0 || time.compareTo("19:00:00") >0 ) {
                    edtTime.setError("Enter valid Time !");
                    Toast.makeText(Cart.this, "Please enter valid time !!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!rdiCash.isChecked() && !rdiEatItBalance.isChecked()) {
                        Toast.makeText(Cart.this, "Please Select the Payment Method !!!", Toast.LENGTH_SHORT).show();
                    }
                    else if(rdiCash.isChecked()) {
                        Request request = new Request(

                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                edtTime.getText().toString(),
                                txtTotalPrice.getText().toString(),
                                "Cash Payment",
                                "Unpaid",
                                cart
                        );

                        //Submit to Firebase
                        //We are using CurrentMilli to key
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);

                        //Delete cart
                        new Database(getBaseContext()).cleanCart();

                        Toast.makeText(Cart.this, "Thank You, Your Order has been Placed !!!", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else if(rdiEatItBalance.isChecked()){
                        double amount = 0;
                        //Get total price from txtTotalPrice
                        try {
                            amount = Common.formatCurrency(txtTotalPrice.getText().toString(),Locale.US).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //Compare with user balance
                        if (Common.currentUser.getBalance() >= amount){
                            Request request = new Request(

                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getName(),
                                    edtTime.getText().toString(),
                                    txtTotalPrice.getText().toString(),
                                    "Eat It's Wallet Balance",
                                    "Paid",
                                    cart
                            );

                            //Submit to Firebase
                            //We are using CurrentMilli to key
                            requests.child(String.valueOf(System.currentTimeMillis()))
                                    .setValue(request);

                            //Delete cart
                            new Database(getBaseContext()).cleanCart();

                            //Update Balance
                            double balance = Common.currentUser.getBalance()-amount;
                            Map<String ,Object> update_balance = new HashMap<>();
                            update_balance.put("balance",balance);

                            FirebaseDatabase.getInstance()
                                    .getReference("User")
                                    .child(Common.currentUser.getPhone())
                                    .updateChildren(update_balance)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            /*if(task.isSuccessful())
                                            {
                                                //Refresh user
                                                FirebaseDatabase.getInstance()
                                                        .getReference("User")
                                                        .child(Common.currentUser.getPhone())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                Common.currentUser = dataSnapshot.getValue(User.class);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }*/
                                        }
                                    });

                            Toast.makeText(Cart.this, "Thank You, Your Order has been Placed !!!", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                        else {
                            Toast.makeText(Cart.this, "You don't have enough Balance ! Please Add money to your Wallet / Choose Cash Payment mode", Toast.LENGTH_LONG).show();
                        }
                        /*Request request = new Request(

                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                edtTime.getText().toString(),
                                txtTotalPrice.getText().toString(),
                                "Eat It's Wallet Balance",
                                "Paid",
                                cart
                        );

                        //Submit to Firebase
                        //We are using CurrentMilli to key
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);

                        //Delete cart
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, "Thank You, Your Order has been Placed !!!", Toast.LENGTH_SHORT).show();
                        finish();*/
                    }
                }

                /*Request request = new Request(

                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtTime.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "Eat It's Wallet Balance",
                        cart
                );

                //Submit to Firebase
                //We are using CurrentMilli to key
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                //Delete cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thank You, Your Order has been Placed !!!", Toast.LENGTH_SHORT).show();
                finish();*/
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


    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate total price

        int total=0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale =new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //we will remove item at List<Order> by position
        cart.remove(position);
        //After that we will delete all data from SQLite
        new Database(this).cleanCart();
        //And finally we will update new data from List<Order> to SQLite
        for(Order item:cart)
            new Database(this).addToCart(item);
        //Refresh
        loadListFood();
    }
}
