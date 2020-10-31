package com.yangguangyulu.sunoleducation.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.model.MyOnlineCourseItemBean;
import com.yangguangyulu.sunoleducation.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.yangguangyulu.sunoleducation.util.TimeUtils.DATE_FORMAT_DATE5;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/9.
 * Description:
 * Modified:
 */

public class MyCourseListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<MyOnlineCourseItemBean> courseInfoList = new ArrayList<>(5);
    private OnItemClickListener onItemClickListener;


    public MyCourseListAdapter(Context context, List<MyOnlineCourseItemBean> infos) {
        mContext = context;
        courseInfoList.addAll(infos);
    }

    public void updateAll(List<MyOnlineCourseItemBean> infos) {
        this.courseInfoList.clear();
        this.courseInfoList.addAll(infos);
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.my_cource_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(v -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClick((Integer) v.getTag());
            }
        });


        MyOnlineCourseItemBean courseInfo = courseInfoList.get(position);

//        switch (courseInfo.getEducation().getResourceType()) {
//            case EducationType.LEGAL_EDUCATION: //法制教育
//                viewHolder.courseType.setText("法制\n教育");
//                viewHolder.courseType.setTextColor(ContextCompat.getColor(mContext, R.color.education_type_color_one));
//                viewHolder.courseType.setBackgroundResource(R.drawable.education_type_img_bg_one);
//                break;
//            case EducationType.PSYCHOLOGICAL_COUNSELLING: //心理辅导
//                viewHolder.courseType.setText("心理\n辅导");
//                viewHolder.courseType.setTextColor(ContextCompat.getColor(mContext, R.color.education_type_color_two));
//                viewHolder.courseType.setBackgroundResource(R.drawable.education_type_img_bg_two);
//                break;
//            case EducationType.EMPLOYMENT_GUIDANCE: //就业指导
//                viewHolder.courseType.setText("就业\n指导");
//                viewHolder.courseType.setTextColor(ContextCompat.getColor(mContext, R.color.education_type_color_three));
//                viewHolder.courseType.setBackgroundResource(R.drawable.education_type_img_bg_three);
//                break;
//            case EducationType.CURRENT_AFFAIRS_POLICY:  //时事政策
//                viewHolder.courseType.setText("时事\n政策");
//                viewHolder.courseType.setTextColor(ContextCompat.getColor(mContext, R.color.education_type_color_four));
//                viewHolder.courseType.setBackgroundResource(R.drawable.education_type_img_bg_four);
//                break;
//            default:
//                viewHolder.courseType.setText("法制\n教育");
//                viewHolder.courseType.setTextColor(ContextCompat.getColor(mContext, R.color.education_type_color_one));
//                viewHolder.courseType.setBackgroundResource(R.drawable.education_type_img_bg_one);
//                break;
//        }
        String content = "此次视频总时长："
                + courseInfo.getAllTimeLong()
                + "分，获得的时长："
                + courseInfo.getTimeLong()
                + "分，\n总题目数："
                + courseInfo.getAllNum()
                + "，答对题目数："
                + courseInfo.getRightNum();


        viewHolder.courseTitle.setText(courseInfo.getEducationName());
        viewHolder.courseLearnDate.setText(TimeUtils.getTime(courseInfo.getInsertTime(), DATE_FORMAT_DATE5));
        String learnDuration = courseInfo.getTimeLong() + "分";
        viewHolder.courseLearnDuration.setText(learnDuration);
    }

    @Override
    public int getItemCount() {
        return courseInfoList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        //        private TextView courseType;  //法制教育等
        private TextView courseLearnDate;
        private TextView courseLearnDuration;
        private TextView courseTitle;

        private ViewHolder(View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.education_title);
//            courseType = itemView.findViewById(R.id.education_type);
            courseLearnDate = itemView.findViewById(R.id.education_learn_date);
            courseLearnDuration = itemView.findViewById(R.id.education_learn_duration);
        }
    }
}
