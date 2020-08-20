package com.naram.ewoman_project;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<ListProduct> searchList;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public SearchAdapter(List<ListProduct> searchList, Context context){
        this.searchList = searchList;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.listview_item,null);

            viewHolder = new ViewHolder();
            viewHolder.s_image = (ImageView) convertView.findViewById(R.id.iv_item_image);
            viewHolder.s_name = (TextView) convertView.findViewById(R.id.tv_item_name);
            viewHolder.s_price = (TextView) convertView.findViewById(R.id.tv_item_price);
            viewHolder.s_wishlist = (ToggleButton) convertView.findViewById(R.id.tb_wishlist_try);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder)convertView.getTag();

        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.s_image.setImageDrawable(searchList.get(position).getImage());
        viewHolder.s_name.setText(searchList.get(position).getName());
        viewHolder.s_price.setText(searchList.get(position).getPrice());
        viewHolder.s_wishlist.setText(searchList.get(position).getWishlist());

        return convertView;
    }

    class ViewHolder{
        private ImageView s_image;
        private TextView s_name;
        private TextView s_price;
        private ToggleButton s_wishlist;
    }

}
