package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class eTodayListAdapter extends RecyclerView.Adapter<eTodayListAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<ListPost> listeTodayArrayList = new ArrayList<>(); // 커스텀 리스너 인터페이스

    private Context context;

    // 리스너 객체 참조를 저장하는 변수
//    private OnItemClickListener mListener = null;
//    private OnItemLongClickListener mLongListener = null;

    public eTodayListAdapter(){

    }

    public eTodayListAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.etoday_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listeTodayArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listeTodayArrayList.size();
    }

    public void addItem(ListPost listPost) {
        // 외부에서 item을 추가시킬 함수입니다.
        listeTodayArrayList.add(listPost);
//        notifyDataSetChanged();
    }

    public ListPost getItem(int position) {
        return listeTodayArrayList.get(position) ;
    }

    public void clearAllItem() {

        listeTodayArrayList.clear();
        notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_title;
        private TextView tv_item_content;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_content = itemView.findViewById(R.id.tv_item_content);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ListPost listetoday = listeTodayArrayList.get(position);

                    Intent intent = new Intent(context, eTodayDetailActivity.class);
                    intent.putExtra("title", listetoday.getTitle());
                    intent.putExtra("detail_url", listetoday.getDetailurl());
                    context.startActivity(intent);

                }
            });

        }

        void onBind(ListPost listPost) {
            tv_item_title.setText(listPost.getTitle());
            tv_item_content.setText(listPost.getContent());
        }

    }

//    public interface OnItemClickListener
//    {
//        void onItemClick(View v, int pos);
//    }
//
//    public interface OnItemLongClickListener
//    {
//        void onItemLongClick(View v, int pos);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener)
//    {
//        this.mListener = listener;
//    }
//
//    public void setOnItemLongClickListener(OnItemLongClickListener listener)
//    {
//        this.mLongListener = listener;
//    }

    public void TextAdapter(ArrayList<ListPost> list)
    {
        listeTodayArrayList = list;
    }

}