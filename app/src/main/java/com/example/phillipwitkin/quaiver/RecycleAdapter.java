package com.example.phillipwitkin.quaiver;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by phillipwitkin on 12/20/17.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.Holder> {

    public interface OnItemClickListener{
        void onItemClick(DataModel item);
    }

    List<DataModel> dataModelArrayList;
    OnItemClickListener listener;

    public RecycleAdapter(List<DataModel> dataModelArrayList, OnItemClickListener listener){
        this.dataModelArrayList = dataModelArrayList;
        this.listener = listener;
    }
    class Holder extends RecyclerView.ViewHolder {
        TextView sequenceTitle;

        public Holder(View itemView) {
            super(itemView);
            sequenceTitle = (TextView) itemView.findViewById(R.id.sequenceNameRow);

        }

        public void bind(final DataModel item, final OnItemClickListener listener){
            sequenceTitle.setText(item.getSequenceName());
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v){
                    listener.onItemClick(item);
                }
            });

        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sequence_load_row, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position){
        DataModel dataModel = dataModelArrayList.get(position);
        holder.sequenceTitle.setText(dataModel.getSequenceName());
        holder.bind(dataModel, listener);
    }

    @Override
    public int getItemCount(){
        return dataModelArrayList.size();
    }


}
