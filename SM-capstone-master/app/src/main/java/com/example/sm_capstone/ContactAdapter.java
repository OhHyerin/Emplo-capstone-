package com.example.sm_capstone;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<Contact> mList;
    private OnDeleteClickListener mListener = null;
    private OnOkClickListener mListener2 = null;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();


    public ContactAdapter(ArrayList<Contact> list){
        this.mList = list;
    }

    public interface OnDeleteClickListener{   void onDeleteClick(View v, int position);  }
    public interface OnOkClickListener{ void onOkClick(View v, int position);   }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.mListener = listener;
    }

    public void setOnOkClickListener(OnOkClickListener listener){
        this.mListener2 = listener;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        protected TextView phoneNum;
        protected Button remove_btn;
        protected Button ok_btn;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.contactNametv);
            this.phoneNum = itemView.findViewById(R.id.contactPhonetv);
            this.remove_btn = itemView.findViewById(R.id.remove_btn);
            this.ok_btn = itemView.findViewById(R.id.ok_btn);

            remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(mListener != null)
                            mListener.onDeleteClick(v, position);
                    }
                }
            });

            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(mListener2 != null)
                            mListener2.onOkClick(v, position);
                    }
                }
            });

        }
    }

    @NonNull
    @Override

    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_list,parent,false);
        ContactViewHolder viewHolder = new ContactViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        holder.name.setText(mList.get(position).getName());
        holder.phoneNum.setText(mList.get(position).getPhoneNum());
        if(mList.get(position).getAccept().equals("yes"))
        {
            holder.ok_btn.setVisibility(View.GONE);
            holder.remove_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public Contact getItem(int position){
        return mList.get(position);
    }
}
