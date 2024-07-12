package com.sungkanngoding.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminTransactionConfirmationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminTransactionAdapter adapter;
    private List<Map<String, Object>> transactionList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_transaction_confirmation);

        recyclerView = findViewById(R.id.recyclerViewTransactions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        transactionsRef = FirebaseDatabase.getInstance().getReference().child("Transaksi");

        adapter = new AdminTransactionAdapter(this, transactionList);
        recyclerView.setAdapter(adapter);

        loadTransactions();
    }

    private void loadTransactions() {
        if (currentUser != null) {
            transactionsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    transactionList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> transactionData = (Map<String, Object>) snapshot.getValue();
                        if (transactionData != null) {
                            transactionData.put("transactionUid", snapshot.getKey());
                            transactionList.add(transactionData);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AdminTransactionConfirmationActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
