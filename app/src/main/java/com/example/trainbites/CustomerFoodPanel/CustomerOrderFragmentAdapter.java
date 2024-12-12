package com.example.trainbites.CustomerFoodPanel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.trainbites.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CustomerOrderFragmentAdapter extends RecyclerView.Adapter<CustomerOrderFragmentAdapter.OrderViewHolder> {

    private List<Object> orders; // List of objects that can hold either model

    //private Context context;
    public CustomerOrderFragmentAdapter(List<Object> orders) {
        this.orders = orders;
        if (orders == null || orders.isEmpty()) {
            Log.e("CustomerOrderAdapter", "Order list is null or empty.");
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        if (orders == null || orders.isEmpty()) {
            Log.e("CustomerOrderAdapter", "Order list is empty at position " + position);
            return; // Prevent the crash if the orders list is empty
        }

        Object currentOrder = orders.get(position);

        if (currentOrder instanceof ConfirmOrderForSingleScreen_Model) {
            ConfirmOrderForSingleScreen_Model order = (ConfirmOrderForSingleScreen_Model) currentOrder;
            holder.date.setText(order.orderDate);
            holder.paid_or_unpaid.setText(order.paymentstatus);
            holder.order_no.setText(order.orderNumber);
            holder.seat_no.setText(order.Useat);
            holder.coach_no.setText(order.Ucoach);
            holder.whichItem.setText(order.SingleItemOrMultipleItem);
            holder.Amount.setText(order.UtotalAmount);
            holder.user_name.setText(order.Uname);

            // Set text color based on payment status
            if ("paid".equalsIgnoreCase(order.paymentstatus)) {
                holder.paid_or_unpaid.setTextColor(Color.parseColor("#028352"));
            } else if ("unpaid".equalsIgnoreCase(order.paymentstatus)) {
                holder.paid_or_unpaid.setTextColor(Color.RED);
            } else {
                holder.paid_or_unpaid.setTextColor(Color.BLACK);
            }

            // Delete order on button click
            holder.order_dlt_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //deleteOrderFromDatabase(order.orderNumber, position);
                    deleteOrderFromDatabase(holder.itemView.getContext(), order.orderNumber, position);

                }
            });

        } else if (currentOrder instanceof MultipleConfirmOrderModel) {
            MultipleConfirmOrderModel order = (MultipleConfirmOrderModel) currentOrder;
            holder.date.setText(order.orderDate);
            holder.paid_or_unpaid.setText(order.paymentstatus);
            holder.order_no.setText(order.orderNumber);
            holder.seat_no.setText(order.Useat);
            holder.coach_no.setText(order.Ucoach);
            holder.whichItem.setText(order.SingleItemOrMultipleItem);
            holder.Amount.setText(order.Udish);
            holder.user_name.setText(order.Uname);

            // Set text color based on payment status
            if ("paid".equalsIgnoreCase(order.paymentstatus)) {
                holder.paid_or_unpaid.setTextColor(Color.parseColor("#028352"));
            } else if ("unpaid".equalsIgnoreCase(order.paymentstatus)) {
                holder.paid_or_unpaid.setTextColor(Color.RED);
            } else {
                holder.paid_or_unpaid.setTextColor(Color.BLACK);
            }

            // Delete order on button click
            holder.order_dlt_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //deleteOrderFromDatabase(order.orderNumber, position);
                    deleteOrderFromDatabase(holder.itemView.getContext(), order.orderNumber, position);

                }
            });
        } else {
            Log.e("CustomerOrderAdapter", "Unexpected order type: " + currentOrder.getClass().getSimpleName());
        }
    }

   private void deleteOrderFromDatabase(Context context, String orderNumber, int position) {
       if (context == null) {
           Log.e("CustomerOrderAdapter", "Context is null, cannot show AlertDialog.");
           return;
       }

       if (orders == null || orders.isEmpty()) {
           Log.e("Firebase", "Order list is empty, cannot remove.");
           return;
       }

       if (position < 0 || position >= orders.size()) {
           Log.e("Firebase", "Invalid position: " + position);
           return;
       }

       // Show a confirmation dialog before deletion
       new AlertDialog.Builder(context)
               .setTitle("Delete Order")
               .setMessage("Are you sure you want to delete this order?")
               .setPositiveButton("Yes", (dialog, which) -> {
                   DatabaseReference singleOrderReference = FirebaseDatabase.getInstance().getReference("Single_Confirm_order");
                   DatabaseReference multipleOrderReference = FirebaseDatabase.getInstance().getReference("Multiple_Confirm_order");

                   Query singleOrderQuery = singleOrderReference.orderByChild("orderNumber").equalTo(orderNumber);
                   Query multipleOrderQuery = multipleOrderReference.orderByChild("orderNumber").equalTo(orderNumber);

                   singleOrderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()) {
                               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                   snapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                       if (position >= 0 && position < orders.size()) {
                                           orders.remove(position);
                                           notifyItemRemoved(position);
                                           Toast.makeText(context, "Order deleted successfully.", Toast.LENGTH_SHORT).show();
                                       } else {
                                           Log.e("CustomerOrderAdapter", "Invalid position during removal: " + position);
                                       }
                                   }).addOnFailureListener(e -> {
                                       Log.e("CustomerOrderAdapter", "Failed to remove order from Firebase: " + e.getMessage());
                                       Toast.makeText(context, "Failed to delete order.", Toast.LENGTH_SHORT).show();
                                   });
                               }
                           } else {
                               // Try checking in the Multiple_Confirm_order reference
                               multipleOrderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.exists()) {
                                           for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                               snapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                                   if (position >= 0 && position < orders.size()) {
                                                       orders.remove(position);
                                                       notifyItemRemoved(position);
                                                       Toast.makeText(context, "Order deleted successfully.", Toast.LENGTH_SHORT).show();
                                                   } else {
                                                       Log.e("CustomerOrderAdapter", "Invalid position during removal: " + position);
                                                   }
                                               }).addOnFailureListener(e -> {
                                                   Log.e("CustomerOrderAdapter", "Failed to remove order from Firebase: " + e.getMessage());
                                                   Toast.makeText(context, "Failed to delete order.", Toast.LENGTH_SHORT).show();
                                               });
                                           }
                                       } else {
                                           Log.e("CustomerOrderAdapter", "Order not found in Firebase.");
                                           Toast.makeText(context, "Order not found.", Toast.LENGTH_SHORT).show();
                                       }
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {
                                       Log.e("CustomerOrderAdapter", "Database error: " + databaseError.getMessage());
                                   }
                               });
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {
                           Log.e("CustomerOrderAdapter", "Database error: " + databaseError.getMessage());
                       }
                   });
               })
               .setNegativeButton("No", null)
               .show();

   }




    @Override
    public int getItemCount() {
        if (orders == null) {
            return 0;
        }
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView date, paid_or_unpaid, order_no, seat_no, coach_no, whichItem, Amount, user_name;

        ImageButton order_dlt_btn;
        @SuppressLint("WrongViewCast")
        public OrderViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.Dateid);
            paid_or_unpaid = itemView.findViewById(R.id.paid_OR_unpaid_id);
            order_no = itemView.findViewById(R.id.order_id);
            seat_no = itemView.findViewById(R.id.seat_id);
            coach_no = itemView.findViewById(R.id.coach_id);
            whichItem = itemView.findViewById(R.id.items_text);
            Amount = itemView.findViewById(R.id.Amouunt_id);
            user_name = itemView.findViewById(R.id.name_id);
            order_dlt_btn = itemView.findViewById(R.id.delete_order_btn);
        }
    }
}
