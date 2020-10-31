package com.yangguangyulu.sunoleducation.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.util.glide.GlideUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kun on 2016/12/14.
 * GitHub: https://github.com/AndroidKun
 * CSDN: http://blog.csdn.net/a1533588867
 * Description:模版
 */

public class EducationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<EducationInfo> dataList = new ArrayList<>(10);
    private OnItemClickListener onItemClickListener;

    public EducationListAdapter(Context context, List<EducationInfo> dataList) {
        mContext = context;
        this.dataList.addAll(dataList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void updateList(RecyclerView recyclerView, List<EducationInfo> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
//        notifyDataSetChanged();  // 为什么这样无效呢？ 为什么会认为数据源没有发生改变？  ???5/15号，又有效了??什么情况
        if (null != recyclerView.getAdapter()) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.education_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        EducationInfo educationInfo = dataList.get(position);
        viewHolder.educationTitle.setText(educationInfo.getVideoName());
        viewHolder.educationCreateTime.setText(educationInfo.getUploadDate());
        viewHolder.videoDurationTime.setText(educationInfo.getDurationTime());  //educationInfo.getDurationTime()

        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(v -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClick((Integer) v.getTag());
            }
        });

        String format = educationInfo.getEducationFormat();
        if (educationInfo.isVideo()) {
            viewHolder.videoDurationTime.setVisibility(View.VISIBLE);
            GlideUtils.loadImage(mContext, educationInfo.getVideoImgUrl(), R.color.text_black_c, viewHolder.educationResourceImg);
        }
        if (educationInfo.getStatus() == 1 && educationInfo.getStudyType() == 1) {
            //已完成 且 必修
            viewHolder.learnFinishedShadow.setVisibility(View.VISIBLE);
            viewHolder.learnFinished.setVisibility(View.VISIBLE);
            viewHolder.playImg.setVisibility(View.GONE);
        } else {
            viewHolder.learnFinishedShadow.setVisibility(View.GONE);
            viewHolder.learnFinished.setVisibility(View.GONE);
            viewHolder.playImg.setVisibility(isOfficeFile(format) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private boolean isOfficeFile(String format) {
        return "ppt".equals(format) || "pptx".equals(format) || "pdf".equals(format) || "doc".equals(format) || "docx".equals(format);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView educationTitle;
        private TextView videoDurationTime;
        private TextView educationCreateTime;
        private ImageView educationResourceImg;
        private TextView learnFinished;
        private View learnFinishedShadow;
        private ImageView playImg;

        public ViewHolder(View itemView) {
            super(itemView);
            educationTitle = itemView.findViewById(R.id.video_title);
            videoDurationTime = itemView.findViewById(R.id.video_duration_time);
            educationCreateTime = itemView.findViewById(R.id.video_create_time);
            educationResourceImg = itemView.findViewById(R.id.education_resource_img);
            learnFinished = itemView.findViewById(R.id.education_learn_finish);
            learnFinishedShadow = itemView.findViewById(R.id.learn_finish_shadow);
            playImg = itemView.findViewById(R.id.play_img);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
