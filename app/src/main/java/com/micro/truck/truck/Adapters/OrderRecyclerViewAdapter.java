package com.micro.truck.truck.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.micro.truck.truck.Models.Order;
import com.micro.truck.truck.R;

import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {

    private List<Order> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context ctx;

    // data is passed into the constructor
    public OrderRecyclerViewAdapter(Context context, List<Order> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.ctx = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_item_order, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = mData.get(position);
        int order_status = order.getStatus();

        String driver_name = order.getCurrent_driver();
//        Double distance = order.getDistances();
        int price = order.getPrice_km();
        String image = order.getImage();
        String from = order.getLocation_from();
        String to = order.getLocation_to();

        holder.driver_name.setText(driver_name);
//        if (distance != null)
//        holder.distance.setText(String.valueOf(distance) + " Km");
        holder.price.setText(String.valueOf(price) + " per Km");
        holder.location_from.setText(from);
        holder.location_to.setText(to);

        Glide.with(ctx).load( image).apply(new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)).into(holder.image);

        switch (order_status){
            case 0:
                holder.order_button.setText(R.string.cancel);
                break;
            case 1:
                holder.order_button.setText(R.string.view_current_location);
                break;
            case 3:
                if (order.getRating() > 0){
                    holder.order_button.setEnabled(false);
                    holder.order_button.setText( "You Rated This Order  " +String.valueOf(order.getRating()));
                }
                else
                    holder.order_button.setText(R.string.rate_this_ride);
                break;
            case -2:
                holder.order_button.setText(R.string.reorder);
                holder.order_button.setVisibility(View.INVISIBLE);
                break;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView driver_name,price,distance,location_from,location_to;
        ImageView image;
        Button order_button;

        ViewHolder(View itemView) {
            super(itemView);
            driver_name = itemView.findViewById(R.id.driver_name);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            distance = itemView.findViewById(R.id.estimated_distance);
            order_button = itemView.findViewById(R.id.order_button);
            location_from = itemView.findViewById(R.id.from_value);
            location_to = itemView.findViewById(R.id.to_value);
//            itemView.setOnClickListener(this);
            order_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Order getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(OrderRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
