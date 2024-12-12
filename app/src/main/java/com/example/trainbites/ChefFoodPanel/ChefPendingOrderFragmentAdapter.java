package com.example.trainbites.ChefFoodPanel;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainbites.CustomerFoodPanel.MultipleConfirmOrderModel;
import com.example.trainbites.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChefPendingOrderFragmentAdapter extends RecyclerView.Adapter<ChefPendingOrderFragmentAdapter.PendingOrder> {


    private ArrayList<Object> list;
    Context context;

    public ChefPendingOrderFragmentAdapter(ArrayList<Object> list, Context context) {
        this.list = list;
        this.context = context;
    }
    //ConfirmOrderForSingleScreen_Model
    @NonNull
    @Override
    public PendingOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_order_layout, parent, false);
        return new PendingOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrder holder, int position) {
        Object CurrentOrder = list.get(position);
        if (CurrentOrder instanceof ChefPendingSingleOrderModel){
            ChefPendingSingleOrderModel model=(ChefPendingSingleOrderModel) CurrentOrder;

            holder.date.setText(model.getSorderDate());
            holder.paid_or_unpaid.setText(model.getSpaymentstatus());
            holder.order_no.setText(model.getSorderNumber());
            holder.seat_no.setText(model.getSseat());
            holder.coach_no.setText(model.getScoach());
           // holder.item.setText(model.getSdish());
            holder.itemTextView.setText(model.getSdish());
            holder.Amount.setText(model.getStotalAmount());
            holder.user_name.setText(model.getSname());
            holder.contact.setText(model.getSphone());

            // Set text color based on payment status
            if ("paid".equalsIgnoreCase(model.getSpaymentstatus())) {
                holder.paid_or_unpaid.setTextColor(Color.parseColor("#028352"));
            } else if ("unpaid".equalsIgnoreCase(model.getSpaymentstatus())) {
                holder.paid_or_unpaid.setTextColor(Color.RED);
            } else {
                holder.paid_or_unpaid.setTextColor(Color.BLACK);
            }

            //delete button implimentation
            holder.order_dlt_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show an AlertDialog for confirmation
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Order")
                            .setMessage("Are you sure you want to delete this order?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Proceed with deletion if user confirms
                                deleteDATAForSingleItem(model.getSorderNumber(), position);
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                // Dismiss the dialog if the user cancels
                                dialog.dismiss();
                            })
                            .show();
                }
            });
        } else if (CurrentOrder instanceof ChefPendingMultipleOrderModel) {
            ChefPendingMultipleOrderModel Ordermodel=(ChefPendingMultipleOrderModel) CurrentOrder;
            holder.date.setText(Ordermodel.getMorderDate());
            holder.paid_or_unpaid.setText(Ordermodel.getMpaymentstatus());
            holder.order_no.setText(Ordermodel.getMorderNumber());
            holder.seat_no.setText(Ordermodel.getMseat());
            holder.coach_no.setText(Ordermodel.getMcoach());
            //holder.item.setText(Ordermodel.getMdish());
            holder.itemTextView.setText(Ordermodel.getMdish());
            holder.Amount.setText(Ordermodel.getMtotalAmount());
            holder.user_name.setText(Ordermodel.getMname());
            holder.contact.setText(Ordermodel.getMphone());

            // Set text color based on payment status
            if ("paid".equalsIgnoreCase(Ordermodel.getMpaymentstatus())) {
                holder.paid_or_unpaid.setTextColor(Color.parseColor("#028352"));
            } else if ("unpaid".equalsIgnoreCase(Ordermodel.getMpaymentstatus())) {
                holder.paid_or_unpaid.setTextColor(Color.RED);
            } else {
                holder.paid_or_unpaid.setTextColor(Color.BLACK);
            }

            //delete button implimentation
            holder.order_dlt_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show an AlertDialog for confirmation
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Order")
                            .setMessage("Are you sure you want to delete this order?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Proceed with deletion if user confirms
                                deleteDATAForMultiItemItem(Ordermodel.getMorderNumber(), position);
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                // Dismiss the dialog if the user cancels
                                dialog.dismiss();
                            })
                            .show();
                }
            });




        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PendingOrder extends RecyclerView.ViewHolder {
        TextView date, paid_or_unpaid, order_no, seat_no, coach_no, Amount, user_name, contact,itemTextView;
        //EditText item;
        ScrollView scrollView;

        ImageButton order_dlt_btn;

        public PendingOrder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.Dateid);
            paid_or_unpaid = itemView.findViewById(R.id.paymentStatus_id);
            order_no = itemView.findViewById(R.id.Pendingorder_id);
            seat_no = itemView.findViewById(R.id.Orderseat_id);
            coach_no = itemView.findViewById(R.id.Ordercoach_id);
            //item = itemView.findViewById(R.id.Orderitem_id);
            itemTextView=itemView.findViewById(R.id.itemTextView);
            scrollView=itemView.findViewById(R.id.Orderitem_id);
            Amount = itemView.findViewById(R.id.Orderamount_id);
            user_name = itemView.findViewById(R.id.Ordername_id);
            contact = itemView.findViewById(R.id.Ordercontact_id);
            order_dlt_btn = itemView.findViewById(R.id.delete_PendingOrder_btn);

        }
    }

    //delete data from firebase of single item
    private void deleteDATAForSingleItem(String orderNumber, int position) {
        DatabaseReference singleOrderReference = FirebaseDatabase.getInstance().getReference("Pending_order_chef_db_singleOrder");
        Query singleOrderQuery = singleOrderReference.orderByChild("sorderNumber").equalTo(orderNumber);

        singleOrderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < list.size()) {  // Check if position is valid
                                list.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Order deleted successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ChefOrderAdapter", "Invalid position: " + position + ", List size: " + list.size());
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("ChefOrderAdapter", "Failed to remove order from Firebase: " + e.getMessage());
                            Toast.makeText(context, "Failed to delete order.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e("ChefOrderAdapter", "No matching order found.");
                    Toast.makeText(context, "Order not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChefOrderAdapter", "Database error: " + error.getMessage());
                Toast.makeText(context, "Failed to delete order. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //delete data from firebase of multi item

    private void deleteDATAForMultiItemItem(String orderNumber, int position) {
        DatabaseReference singleOrderReference = FirebaseDatabase.getInstance().getReference("Pending_order_chef_db_MultiOrder");
        Query singleOrderQuery = singleOrderReference.orderByChild("morderNumber").equalTo(orderNumber);

        singleOrderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < list.size()) {  // Check if position is valid
                                list.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Order deleted successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ChefOrderAdapter", "Invalid position: " + position + ", List size: " + list.size());
                            }
                        }).addOnFailureListener(e -> {
                            Log.e("ChefOrderAdapter", "Failed to remove order from Firebase: " + e.getMessage());
                            Toast.makeText(context, "Failed to delete order.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e("ChefOrderAdapter", "No matching order found.");
                    Toast.makeText(context, "Order not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChefOrderAdapter", "Database error: " + error.getMessage());
                Toast.makeText(context, "Failed to delete order. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
