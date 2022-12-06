package com.selim_jnu.kothayacho.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.selim_jnu.kothayacho.IndividualOfficer;
import com.selim_jnu.kothayacho.R;
import com.selim_jnu.kothayacho.User;

import java.util.List;
import java.util.Locale;

public class OfficerAdapter extends RecyclerView.Adapter<OfficerAdapter.ViewHolder> {
    Activity context;
    List<User> UserList;

    public OfficerAdapter(Activity context, List<User> t) {
        this.context = context;
        this.UserList = t;
    }

    @NonNull
    @Override
    public OfficerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new OfficerAdapter.ViewHolder(view);
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
    public void onBindViewHolder(@NonNull final OfficerAdapter.ViewHolder holder, int position) {
        User user = UserList.get(position);
        holder.name.setText(user.getName());
        String text = TimeAgo.using(user.getLastSeen());
        holder.lastSeen.setText(text);
        holder.address.setText(getCompleteAddressString(user.getLat(), user.getLon()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, IndividualOfficer.class);
            intent.putExtra("user" , user);
            context.startActivity(intent);
        });

        holder.call.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                intent.setData(Uri.parse("tel: " + user.getNumber())); // Data with intent respective action on intent
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, lastSeen;
        ImageView call;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView5);
            address = itemView.findViewById(R.id.textView51);
            call = itemView.findViewById(R.id.imageView6);
            lastSeen = itemView.findViewById(R.id.textView2);
        }
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

}
