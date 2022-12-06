package com.selim_jnu.kothayacho.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.selim_jnu.kothayacho.R;
import com.selim_jnu.kothayacho.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    Activity context;
    List<com.selim_jnu.kothayacho.Address> addresses;

    public AddressAdapter(Activity context, List<com.selim_jnu.kothayacho.Address> t) {
        this.context = context;
        this.addresses = t;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressAdapter.ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AddressAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        com.selim_jnu.kothayacho.Address address = addresses.get(position);

        //@SuppressLint("SimpleDateFormat") SimpleDateFormat stepText = new SimpleDateFormat("hh:mm\na");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mainTime = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
        holder.nameEt.setText(address.getAddress());
        holder.address.setText(mainTime.format(address.getTimestamp()));
        holder.stepText.setText(String.valueOf(position+1));

        if (position == 0) {
            holder.up.setVisibility(View.INVISIBLE);
            holder.down.setVisibility(View.VISIBLE);
        }
        if (position == addresses.size() - 1 ) {
            holder.up.setVisibility(View.VISIBLE);
            holder.down.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameEt;
        TextView stepText, address;
        View up, down;
        public ViewHolder(View itemView) {
            super(itemView);
            nameEt = itemView.findViewById(R.id.Address_name);
            stepText = itemView.findViewById(R.id.stepTv);
            up = itemView.findViewById(R.id.iv_upper_line);
            down = itemView.findViewById(R.id.iv_lower_line);
            address = itemView.findViewById(R.id.address);
        }
    }
}
