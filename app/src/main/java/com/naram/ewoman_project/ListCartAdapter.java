package com.naram.ewoman_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListCartAdapter extends BaseAdapter {

    public static final String TAG = "ListCartAdapter";

    private ArrayList<ListCart> listViewItemList = new ArrayList<ListCart>();
    private ArrayList<ListCart> displayItemList = new ArrayList<ListCart>();


    public ListCartAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return displayItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cart_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iv_item_image = (ImageView) convertView.findViewById(R.id.iv_item_image);
        TextView tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
        TextView tv_item_price = (TextView) convertView.findViewById(R.id.tv_item_price);
        TextView tv_item_count = (TextView) convertView.findViewById(R.id.tv_item_count);
        TextView tv_item_date = (TextView) convertView.findViewById(R.id.tv_item_date);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListCart displayItem = getItem(position);

        // 아이템 내 각 위젯에 데이터 반영
//        iv_item_image.setImageDrawable(displayItem.getImage());
        tv_item_name.setText(displayItem.getName());
        tv_item_price.setText(displayItem.getPrice() + "\\");
        tv_item_count.setText(Integer.toString(displayItem.getCount()));
        tv_item_date.setText(displayItem.getDate());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public ListCart getItem(int position) {
        return displayItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ListCart listCart) {

        Log.d(TAG, "Item Add");
        listViewItemList.add(listCart);
        displayItemList.add(listCart);

        notifyDataSetChanged();

    }

    public void clearAllItems() {

        listViewItemList.clear();
        displayItemList.clear();

        notifyDataSetChanged();

    }

}
