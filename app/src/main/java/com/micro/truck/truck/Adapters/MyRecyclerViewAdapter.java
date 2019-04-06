package com.micro.truck.truck.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.micro.truck.truck.Api.SendGetJsonApi;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.Models.Truck;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.MyAppGlideModule;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Truck> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context ctx;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Truck> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.ctx = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_item_const_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Truck truck_obj = mData.get(position);
        String truck = mData.get(position).getDriver_name();
        Double distance = mData.get(position).getDistances();
        String s_distance = Double.toString(distance) + " Km";
        String desc = mData.get(position).getDesc();
        String model = mData.get(position).getModel();
        int price_per_km = mData.get(position).getPrice_km();
//        int rating = mData.get(position).getRating();
        String image = mData.get(position).getImage();
        boolean is_expanded = truck_obj.isExpanded();


        holder.driver_name.setText(truck);
        holder.desc.setText(desc);
        holder.distance.setText(s_distance);
        holder.model.setText(model);
        holder.price_per_km.setText(String.valueOf(price_per_km));
        holder.subItem.setVisibility(is_expanded ? View.VISIBLE : View.GONE);
//        holder.rating.setRating(rating);

        Glide.with(ctx).load( image).apply(new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)).into(holder.image);

        holder.itemView.setOnClickListener(v ->{
           boolean expanded = truck_obj.isExpanded();
           truck_obj.setExpanded(!expanded);
           notifyItemChanged(position);

        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView driver_name,distance,desc,model,price_per_km;
        RatingBar rating;
        ImageView image;
        View subItem;
        Button show_data,direct_request;


        ViewHolder(View itemView) {
            super(itemView);
            driver_name = itemView.findViewById(R.id.driver_name);
            distance = itemView.findViewById(R.id.distance);
            distance.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(ctx,R.drawable.ic_map_marker_alt_solid),null,null,null);
            desc = itemView.findViewById(R.id.truck_desc);
            model = itemView.findViewById(R.id.truck_model);
            price_per_km = itemView.findViewById(R.id.price_per_d);
            price_per_km.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(ctx,R.drawable.ic_dollar_coin_money),null,null,null);
//            rating = itemView.findViewById(R.id.rating);
            image = itemView.findViewById(R.id.truck_image);
            subItem = itemView.findViewById(R.id.sub_data);
            show_data = itemView.findViewById(R.id.truck_data);
            direct_request = itemView.findViewById(R.id.direct_request);
            direct_request.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(ctx,R.drawable.ic_hand_point_right_solid),null,null,null);
            show_data.setOnClickListener(this);
            direct_request.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Truck getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}