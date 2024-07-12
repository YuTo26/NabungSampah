package com.sungkanngoding.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class AdminTransactionAdapter extends RecyclerView.Adapter<AdminTransactionAdapter.TransactionViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mTransactionList;

    public AdminTransactionAdapter(Context context, List<Map<String, Object>> transactionList) {
        this.mContext = context;
        this.mTransactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Map<String, Object> transaction = mTransactionList.get(position);
        holder.bind(transaction);

        holder.btnConfirm.setOnClickListener(v -> {
            String transactionUid = (String) transaction.get("transactionUid");
            confirmTransaction(transactionUid);
        });
    }

    @Override
    public int getItemCount() {
        return mTransactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvFullName, tvProdukName, tvTotalItem, tvTotalPrice, tvBiayaLayanan, tvTotalRevenue;
        Button btnConfirm;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvProdukName = itemView.findViewById(R.id.tvProdukName);
            tvTotalItem = itemView.findViewById(R.id.tvTotalItem);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvBiayaLayanan = itemView.findViewById(R.id.tvBiayaLayanan);
            tvTotalRevenue = itemView.findViewById(R.id.tvTotalRevenue);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }

        public void bind(Map<String, Object> transaction) {
            String fullName = (String) transaction.get("full_name");
            String produkName = (String) transaction.get("produk_name");
            Long totalItem = (Long) transaction.get("total_item");
            Long totalPrice = (Long) transaction.get("total_price");
            Long biayaLayanan = (Long) transaction.get("service_fee");
            Long totalRevenue = (Long) transaction.get("total_revenue");

            tvFullName.setText(fullName);
            tvProdukName.setText(produkName);
            tvTotalItem.setText("Total: " + (totalItem != null ? totalItem + " Kg" : ""));
            tvTotalPrice.setText("Total Harga: Rp " + (totalPrice != null ? totalPrice : ""));
            tvBiayaLayanan.setText("Biaya Layanan: Rp " + (biayaLayanan != null ? biayaLayanan : ""));
            tvTotalRevenue.setText("Estimasi Pendapatan: Rp " + (totalRevenue != null ? totalRevenue : ""));
        }
    }

    private void confirmTransaction(String transactionUid) {
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("Transaksi").child(transactionUid);

        transactionRef.child("status").setValue("confirmed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateCustomerBalance(transactionUid);
                        updateAdminBalance(transactionUid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Gagal mengkonfirmasi transaksi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCustomerBalance(String transactionUid) {
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("Transaksi").child(transactionUid);
        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String customerUid = snapshot.child("uid").getValue(String.class);
                Long totalRevenue = snapshot.child("total_revenue").getValue(Long.class);

                if (customerUid != null && totalRevenue != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(customerUid);
                    userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long currentBalance = dataSnapshot.getValue(Long.class);
                            if (currentBalance == null) currentBalance = 0L;

                            long newBalance = currentBalance + totalRevenue;
                            userRef.child("balance").setValue(newBalance);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(mContext, "Gagal mengupdate saldo pengguna", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Gagal mendapatkan data transaksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdminBalance(String transactionUid) {
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference().child("Transaksi").child(transactionUid);
        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long serviceFee = snapshot.child("service_fee").getValue(Long.class);

                if (serviceFee != null) {
                    DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    adminRef.orderByChild("role").equalTo("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                                Long currentBalance = adminSnapshot.child("balance").getValue(Long.class);
                                if (currentBalance == null) currentBalance = 0L;

                                long newBalance = currentBalance + serviceFee;
                                adminSnapshot.getRef().child("balance").setValue(newBalance);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(mContext, "Gagal mengupdate saldo admin", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Gagal mendapatkan data transaksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
