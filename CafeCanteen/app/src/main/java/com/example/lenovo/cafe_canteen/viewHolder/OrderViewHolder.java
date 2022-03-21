package com.example.lenovo.cafe_canteen.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.cafe_canteen.Interface.ItemClickListner;
import com.example.lenovo.cafe_canteen.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderTime;

    private ItemClickListner itemClickListener;

    public ImageView btn_delete;


    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderTime = (TextView)itemView.findViewById(R.id.order_time);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        btn_delete = (ImageView)itemView.findViewById(R.id.btn_delete);

        //itemView.setOnClickListener(this);

    }

    /*public void setItemClickListener(ItemClickListner itemClickListener) {
        this.itemClickListener = itemClickListener;
    }*/


    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
