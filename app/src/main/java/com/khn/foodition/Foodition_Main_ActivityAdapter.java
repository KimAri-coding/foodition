package com.khn.foodition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Foodition_Main_ActivityAdapter extends RecyclerView.Adapter<Foodition_Main_ActivityAdapter.ViewHolder> {

    //아이템 어레이리스트 불러오기(가방개념)
    public ArrayList<Foodition_Itemlist> DataItem;

    public Foodition_Main_ActivityAdapter(ArrayList<Foodition_Itemlist> DataItem) {
        this.DataItem = DataItem;
    }

    @NonNull
    @Override
    public Foodition_Main_ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_foodition_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foodition_Itemlist item = DataItem.get(position);
        holder.FoodName.setText(item.getName());
        holder.tvDate.setText(item.getEnddate());
        holder.tvD_day.setText(item.getDday());
        holder.imgFood.setImageResource(item.getImg());

        //마이어플리케이션(알림창클래스).getInstance()를 통해 참조할 수 있다
        if (item.getDday().equals("D-Day"))
        {
            String foodName = item.getName();
            String message = "오늘은 "+ foodName + "의 D-Day입니다!";
            MyApplication.getInstance().showNoti(message, foodName);
        }
    }

    @Override
    public int getItemCount() {
        return DataItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView FoodName, tvDate, tvD_day;
        ImageView imgFood;
        Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FoodName = itemView.findViewById(R.id.FoodName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvD_day = itemView.findViewById(R.id.tvD_day);
            imgFood = itemView.findViewById(R.id.imgFood);

            context = itemView.getContext();

            //리사이클러뷰 아이템 클릭했을 때 이벤트를 위한 클릭리스너 달아주기
            itemView.setOnClickListener(this);
        }

        //클릭 이벤트를 처리하는 메서드 구현
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClicked(position);
                }
            }
        }
    }

    // Adapter 에서 데이터를 필터링하는 메소드 추가
    public List<Foodition_Itemlist> filterDataByCategory(String category) {
        List<Foodition_Itemlist> filteredDataList = new ArrayList<>();
        for (Foodition_Itemlist data : DataItem) {
            if (data.getName().equals(category)) {
                filteredDataList.add(data);
            }
        }
        return filteredDataList;
    }
    private MyRecyclerViewClickListener mListener;

    public void setOnClickListener(MyRecyclerViewClickListener listener) {
        this.mListener = listener;
    }

    public interface MyRecyclerViewClickListener {
        void onItemClicked(int position);

    }
}
