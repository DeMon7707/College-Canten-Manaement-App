package com.example.lenovo.cafe_canteen;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.lenovo.cafe_canteen.Common.Common;
import com.example.lenovo.cafe_canteen.Model.Rating;
import com.example.lenovo.cafe_canteen.viewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTbl;

    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder> adapter;

    String foodId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);

        //Firebase
        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");

        recyclerView = (RecyclerView)findViewById(R.id.recyclerComment);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe layout
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if(!foodId.isEmpty() && foodId != null) {
                    /*Query query = ratingTbl.orderByChild("foodId").equalTo(foodId);

                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();
                    */
                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(
                            Rating.class,
                            R.layout.show_comment_layout,
                            ShowCommentViewHolder.class,
                            ratingTbl.orderByChild("foodId").equalTo(foodId)
                    ) {
                        @Override
                        protected void populateViewHolder(ShowCommentViewHolder viewHolder, Rating model, int position) {
                            viewHolder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            viewHolder.txtComment.setText(model.getComment());
                            viewHolder.txtUserPhone.setText(model.getUserPhone());
                        }
                    };
                    loadComment(foodId);
                }
            }
        });
        //thread to load comment on first launch
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);

                if(getIntent() != null)
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                if(!foodId.isEmpty() && foodId != null) {
                    /*Query query = ratingTbl.orderByChild("foodId").equalTo(foodId);

                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                            .setQuery(query,Rating.class)
                            .build();
                    */
                    adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(
                            Rating.class,
                            R.layout.show_comment_layout,
                            ShowCommentViewHolder.class,
                            ratingTbl.orderByChild("foodId").equalTo(foodId)
                    ) {
                        @Override
                        protected void populateViewHolder(ShowCommentViewHolder viewHolder, Rating model, int position) {
                            viewHolder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                            viewHolder.txtComment.setText(model.getComment());
                            viewHolder.txtUserPhone.setText(model.getUserPhone());
                        }
                    };
                    loadComment(foodId);
                }
            }
        });
    }

    private void loadComment(String foodId) {
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
