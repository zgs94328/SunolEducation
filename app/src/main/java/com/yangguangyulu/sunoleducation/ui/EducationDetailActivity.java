package com.yangguangyulu.sunoleducation.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseMvpActivity;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.presenter.impl.VideoListPresenter;
import com.yangguangyulu.sunoleducation.ui.adapter.EducationListAdapter;
import com.yangguangyulu.sunoleducation.ui.face.FaceRecognizeActivity;
import com.yangguangyulu.sunoleducation.ui.interfaces.IVideoListView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtils;
import com.yangguangyulu.sunoleducation.util.DensityUtil;
import com.yangguangyulu.sunoleducation.util.SpUtils;
import com.yangguangyulu.sunoleducation.util.ToastUtil;
import com.yangguangyulu.sunoleducation.util.glide.GlideUtils;
import com.yangguangyulu.sunoleducation.widget.LandLayoutVideo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import example.FaceLivenessExpActivity;

/**
 * Modified:
 */
public class EducationDetailActivity extends BaseMvpActivity<VideoListPresenter> implements
        EducationListAdapter.OnItemClickListener, IVideoListView {
    @BindView(R.id.detail_player)
    LandLayoutVideo detailPlayer;
    @BindView(R.id.education_title)
    TextView educationTitle;
    @BindView(R.id.education_video_duration)
    TextView educationVideoDuration;
    @BindView(R.id.education_video_upload_time)
    TextView educationVideoUploadTime;
    @BindView(R.id.education_description)
    TextView educationDescription;
    @BindView(R.id.vertical_arrow_img)
    ImageView verticalArrowImg;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_layout)
    ConstraintLayout emptyLayout;

    private boolean isShowAll = false;  //描述信息是否显示完整内容
    private List<EducationInfo> educationInfos = new ArrayList<>(3);
    private EducationListAdapter adapter;
    private EducationInfo playingEducationInfo;

    private boolean isPlay;
    private boolean isPause;
    private boolean hasShowMiddleFace = false;
    private boolean hasShowStartFace = false;
    private boolean hasShowEndFace = false;

    private OrientationUtils orientationUtils;

    int middle = new Random().nextInt(70) + 15;

    @Override
    public VideoListPresenter initPresenter() {
        return new VideoListPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //Android全屏显示时，状态栏显示在最顶层, 不隐藏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN); //实现将window扩展至全屏，也就是全屏显示，并且不会覆盖状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); //为了避免在状态栏的显示状态发生变化时重新布局，从而避免界面卡顿。
        setContentView(R.layout.activity_education_detail);
        unbinder = ButterKnife.bind(this);

        Debuger.enable();
        initView();
        registerBroadCast();
    }

    @Override
    protected void setStatusBar() {
//        super.setStatusBar();
    }

    protected void initView() {
        educationTitle.setFocusableInTouchMode(true);
        educationTitle.requestFocus();

        //每次进入详情界面，则设置为没有授权在非WiFi情况下播放视频
        SpUtils.put(this, "canPlayVideoWithoutWifi", false);

        EducationInfo educationInfo = getIntent().getParcelableExtra("educationInfo");
        if (null != educationInfo) {
            playingEducationInfo = educationInfo;
            setEducationInfo(educationInfo);
            mPresenter.getRecommendEducation(educationInfo);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        adapter = new EducationListAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onGetVideoList(List<EducationInfo> educationInfos, int totalCount) {
        if (null != educationInfos && educationInfos.size() > 0) {
            for (EducationInfo educationInfo : educationInfos) {
                if (this.educationInfos.size() < 3 && educationInfo.isVideo()
                        && !educationInfo.getVideoUrl().equals(playingEducationInfo.getVideoUrl())) {
                    this.educationInfos.add(educationInfo);
                }
            }
            if (this.educationInfos.size() > 0) {
                adapter.updateList(recyclerView, this.educationInfos);
            } else {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE:
                        playingEducationInfo.setStatus(1);
                        break;
                }
            }
        }
    };


    @Override
    public void onItemClick(final int position) {
        if (null != playingEducationInfo) {
            if (playingEducationInfo.getStudyType() == 1 && playingEducationInfo.getStatus() != 1) {
                AlertDialogUtils.showDialog(this, "该课程尚未完成，确认退出将不累计学时！",
                        "继续学习", "确认退出", new AlertDialogUtils.OnAlertClickListener() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onConfirm() {
                                EducationInfo educationInfo = educationInfos.get(position);
                                setEducationInfo(educationInfo);
                            }
                        });
            } else {
                setEducationInfo(educationInfos.get(position));
            }
        }
    }



    private void setEducationInfo(EducationInfo educationInfo) {
        SpUtils.put(this, "haveShowToast", false);  //防止播放出错后，继续播放时重复弹出已学习的提示
        playingEducationInfo = educationInfo;
        educationTitle.setText(educationInfo.getVideoName());
        educationDescription.setText(educationInfo.getVideoDescription());
        educationVideoDuration.setText(educationInfo.getDurationTime());
        educationVideoUploadTime.setText(educationInfo.getUploadDate());

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideUtils.loadImage(this, educationInfo.getVideoImgUrl(), imageView);

        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        if (educationInfo.getVideoUrl().endsWith("avi")) {
            GSYVideoManager.instance().setVideoType(this, GSYVideoType.IJKPLAYER);
        } else if (educationInfo.getVideoUrl().endsWith("mp4")) {
            GSYVideoManager.instance().setVideoType(this, GSYVideoType.IJKEXOPLAYER2);
        }
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        //即非必修或则已完成的则可以快进
        boolean canChangeProgress = educationInfo.getStudyType() != 1 || educationInfo.getStatus() == 1;
//        boolean canChangeProgress = true;
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(canChangeProgress)  //是否支持非全屏滑动触摸有效
                .setIsTouchWigetFull(canChangeProgress)  //是否可以全屏滑动界面改变进度，声音等
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(educationInfo.getVideoUrl())
                .setCacheWithPlay(false)
                .setVideoTitle(educationInfo.getVideoName())
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                        showHaveLearnToast();
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        if (educationInfo.getStudyType() == 1 && educationInfo.getStatus() == 0) {

                            Intent intent = new Intent(EducationDetailActivity.this, QuestionActivity.class);
                            intent.putExtra("eduInfo", educationInfo);
                            startActivity(intent);

//                            Intent intent = new Intent(EducationDetailActivity.this, ThirdWebActivity.class);
//                            intent.putExtra("url", BASE_SOURCE_URL + "questionForApp/?educationId=" + educationInfo.getId());
//                            intent.putExtra("title", "答题");
//                            intent.putExtra("eduInfo", educationInfo);
//                            startActivity(intent);
                        }
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onPlayError(String url, int errorType, Object... objects) {
                        super.onPlayError(url, errorType, objects);
                        long position = detailPlayer.getGSYVideoManager().getCurrentPosition();
                        //不明白为什么，在播放一段时间回到桌面后，再回来继续播放，视频播放到某个点后中断。
                        //Stream ends prematurely at 19643340, should be 22939065感觉就是缓存没结束就提前中断了
                        //所以不会正常播放结束，导致不能执行onAutoComplete()方法
                        //因此增加如下处理:
                        if (errorType != VideoAllCallBack.ErrorTypeNet && position > 60 * 1000 && isPlay
                                && NetworkUtils.isAvailable(detailPlayer.getContext())) {
                            detailPlayer.setSeekOnStart(position);
                            detailPlayer.startPlayLogic();
                        }
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setStartAfterPrepared(true)
                .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {
                    if (progress == 3) {
                        if (isPlay && !hasShowStartFace) {
                            hasShowStartFace = true;
                            Intent intent = new Intent(EducationDetailActivity.this, FaceLivenessExpActivity.class);
                            startActivity(intent);
                        }
                    }
                    if (progress == middle) {
                        if (isPlay && !hasShowMiddleFace) {
                            hasShowMiddleFace = true;
                            Intent intent = new Intent(EducationDetailActivity.this, FaceLivenessExpActivity.class);
                            startActivity(intent);
                        }
                    }

                    if (progress == 96) {
                        if (isPlay && !hasShowEndFace) {
                            hasShowEndFace = true;
                            Intent intent = new Intent(EducationDetailActivity.this, FaceLivenessExpActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setLockClickListener((view, lock) -> {
                    if (orientationUtils != null) {
                        //配合下方的onConfigurationChanged
                        orientationUtils.setEnable(!lock);
                    }
                })
                .build(detailPlayer);

        // 全屏按钮
        detailPlayer.getFullscreenButton().setOnClickListener(v -> {
            if (orientationUtils.isEnable()) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusBar
                detailPlayer.startWindowFullscreen(EducationDetailActivity.this, true, true);
            }
        });

        if (NetworkUtils.isAvailable(this)) {
            boolean canPlayVideoWithoutWifi = SpUtils.getBoolean(this, "canPlayVideoWithoutWifi");
            if (NetworkUtils.isWifiConnected(this) || canPlayVideoWithoutWifi) {
                detailPlayer.clickStartIcon();
            } else {
                AlertDialogUtils.showWifiDialog(this, () -> {
                    SpUtils.put(EducationDetailActivity.this, "canPlayVideoWithoutWifi", true);
                    detailPlayer.clickStartIcon();
                });
            }
        } else {
            showToast("网络异常，请检查网络状态");
        }
    }

    /**
     * 判断是否已学习，若是，则提示不计入学时
     */
    private void showHaveLearnToast() {
        boolean haveShowToast = SpUtils.getBoolean(this, "haveShowToast");
        if (null != detailPlayer && !haveShowToast) {
            SpUtils.put(this, "haveShowToast", true);
            if (playingEducationInfo.getStatus() == 1 && playingEducationInfo.getStudyType() == 1) {
//                            String toast = "该课程已学，本次学习将不累计学时";
                String toast = "本次播放不计入学习时长";
                int yOffset = DensityUtil.getScreenHeight(this) / 2 - detailPlayer.getMeasuredHeight() / 2;
                ToastUtil.getInstance().showLongToast(EducationDetailActivity.this, toast, -yOffset);
            }
        }
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @OnClick({R.id.back_img, R.id.education_description, R.id.vertical_arrow_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            case R.id.education_description:
            case R.id.vertical_arrow_layout:
                if (isShowAll) {
                    isShowAll = false;
                    educationDescription.setMaxLines(1);
                    verticalArrowImg.setImageResource(R.drawable.icon_more_up);
                } else {
                    isShowAll = true;
                    educationDescription.setMaxLines(100);
                    verticalArrowImg.setImageResource(R.drawable.icon_more_down);
                }
                break;
        }
    }

    /**
     * 从Activity A跳转到B, A的onDestroy()方法比B的onCreate()方法后执行
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            GSYVideoManager.releaseAllVideos();
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    private GSYVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }


    @Override
    public void onCommitAnswer(boolean success) {
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        if (null != playingEducationInfo && playingEducationInfo.getStudyType() == 1 && playingEducationInfo.getStatus() != 1) {
            AlertDialogUtils.showDialog(this, "该课程尚未完成，确认退出将不累计学时！",
                    "继续学习", "确认退出", new AlertDialogUtils.OnAlertClickListener() {
                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onConfirm() {
                            finish();
                        }
                    });
            return;
        }
        super.onBackPressed();
    }
}