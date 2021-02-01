package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<ListPost> listPostArrayList = new ArrayList<>(); // 커스텀 리스너 인터페이스
    private ArrayList<ListPost> displayPostArrayList = new ArrayList<>();

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(displayPostArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return displayPostArrayList.size();
    }

    public void addItem(ListPost listPost) {
        // 외부에서 item을 추가시킬 함수입니다.
        listPostArrayList.add(listPost);
        displayPostArrayList.add(listPost);
        notifyDataSetChanged();
    }

    public ListPost getItem(int position) {
        return displayPostArrayList.get(position) ;
    }

    public void clearAllItem() {

        listPostArrayList.clear();
        displayPostArrayList.clear();
        notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_category;
        private TextView tv_item_title;
        private TextView tv_item_userid;
        private TextView tv_item_like;
        private ImageView iv_item_image;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_userid = itemView.findViewById(R.id.tv_item_userid);
            tv_item_category = itemView.findViewById(R.id.tv_item_category);
            tv_item_like = itemView.findViewById(R.id.tv_item_like);
            iv_item_image = itemView.findViewById(R.id.iv_item_image);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(v, pos);
                    }

                }
            });

        }

        void onBind(ListPost listPost) {
            tv_item_category.setText(listPost.getCategory());
            tv_item_title.setText(listPost.getTitle());
            tv_item_userid.setText(listPost.getName());
            tv_item_like.setText(Integer.toString(listPost.getLike()));
        }

    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(View v, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.mLongListener = listener;
    }

    //
    public void TextAdapter(ArrayList<ListPost> list)
    {
        listPostArrayList = list;
    }

    public void filter(String search) {

        if(!search.isEmpty()) {

            displayPostArrayList.clear();

            for (int i = 0; i < listPostArrayList.size(); i++) {
                if (search.contains(listPostArrayList.get(i).getName())
                        || search.contains(listPostArrayList.get(i).getTitle())) {
                    displayPostArrayList.add(listPostArrayList.get(i));
                }
            }

        }

    }

}
