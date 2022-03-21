package com.example.lenovo.cafecanteenadmin.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.cafecanteenadmin.Interface.ItemClickListner;
import com.example.lenovo.cafecanteenadmin.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderTime;

    public Button btnEdit,btnRemove,btnDetail;




    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderTime = (TextView)itemView.findViewById(R.id.order_time);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);

    }


}
