package com.example.lenovo.cafecanteenadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lenovo.cafecanteenadmin.Common.Common;
import com.example.lenovo.cafecanteenadmin.Interface.ItemClickListner;
import com.example.lenovo.cafecanteenadmin.Model.Category;
import com.example.lenovo.cafecanteenadmin.Model.Request;
import com.example.lenovo.cafecanteenadmin.viewHolder.MenuViewHolder;
import com.example.lenovo.cafecanteenadmin.viewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.jaredrummler.materialspinner.MaterialSpinner;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    Spinner spinner;

    FirebaseDatabase db;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        //Init Service
        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();   //load all orders

    }

    private void loadOrders() {
        adapter =
                new FirebaseRecyclerAdapter<Request,OrderViewHolder>(


                        Request.class,
                        R.layout.order_layout,
                        OrderViewHolder.class,
                        requests

                ) {

                    @Override
                    protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                        viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                        viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                        viewHolder.txtOrderTime.setText(model.getTime());
                        viewHolder.txtOrderPhone.setText(model.getPhone());

                        //New Event Button
                        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showUpdateDialog(adapter.getRef(position).getKey(),adapter.getItem(position));
                            }
                        });

                        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteOrder(adapter.getRef(position).getKey());
                            }
                        });

                        viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(OrderStatus.this,OrderDetail.class);
                                Common.currentRequest=model;
                                i.putExtra("OrderId",adapter.getRef(position).getKey());
                                startActivity(i);
                            }
                        });
                        /*viewHolder.setItemClickListener(new ItemClickListner() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                //just implement it to fix crash

                            }
                        });*/

                    }
                };
        adapter.notifyDataSetChanged();    //Refresh data if data get changed
        recyclerView.setAdapter(adapter);

    }

    /*@Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if(item.getTitle().equals(Common.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }*/

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose Status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);

        spinner = (Spinner)view.findViewById(R.id.statusSpinner);
        //spinner.setItems("Order Placed","Order On the Way","Order Completed");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedItemPosition()));
                Toast.makeText(OrderStatus.this, "Status Updated !!!", Toast.LENGTH_SHORT).show();
                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

}
