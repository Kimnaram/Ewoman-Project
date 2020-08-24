package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<ListReview> listReviewArrayList = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listReviewArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listReviewArrayList.size();
    }

    void addItem(ListReview listReview) {
        // 외부에서 item을 추가시킬 함수입니다.
        listReviewArrayList.add(listReview);

        notifyDataSetChanged();
    }

    void clearAllItem() {

        listReviewArrayList.clear();

        notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_title;
        private TextView tv_item_userid;
        private ImageView iv_item_image;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_userid = itemView.findViewById(R.id.tv_item_userid);
            iv_item_image = itemView.findViewById(R.id.iv_item_image);

        }

        void onBind(ListReview listReview) {
            tv_item_title.setText(listReview.getTitle());
            tv_item_userid.setText(listReview.getName());
            iv_item_image.setImageDrawable(listReview.getImage());
        }
    }
}
