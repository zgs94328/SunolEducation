package com.yangguangyulu.sunoleducation.operator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.util.FormatUtil;
import com.yangguangyulu.sunoleducation.util.PhonePackageUtils;
import com.yangguangyulu.sunoleducation.util.SpUtils;
import com.yangguangyulu.sunoleducation.util.StringUtils;
import com.yangguangyulu.sunoleducation.util.ToastUtil;
import com.yangguangyulu.sunoleducation.widget.roundcornprogressbar.RoundCornerProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


/**
 * 下载服务
 *
 * @author tangjian
 * @date 2016年8月17日
 */
public class UpdateService extends Service {
    private static final int Time_Out = 30000;
    private static final int MAX_REDIRECT_COUNT = 5;
    private int progress;
    private String downloadUrl;
    private Context context;
    private boolean isForceToUpdate = false; //是否强制更新
    private String saveFilePath = Constants.Config.DEFAULT_SAVE_FILE_PATH;
    private UpdateBinder binder;
    private long totalSize = 0; //已下载的大小
    private long updateTotalSize = 0; //apk总大小
    private Thread downLoadThread;
    private boolean isStop;// 是否中断下载

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    dlg.cancel();
                    // 下载完毕
                    Log.d("yinqm","download finish");
                    LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(new Intent(Constants.BroadCastReceiver.DOWNLOAD_APK_COMPLETE));
                    SpUtils.put(getApplicationContext(), Constants.HAS_DOWN_NEW_VERSION, true);
                    SpUtils.put(getApplicationContext(), Constants.IS_FORCE_TO_UPDATE, isForceToUpdate);
                    downLoadThread = null;
                    break;
                case 1: //更新进度条
                    if (progress < 100 && null != progress_tv && null != progress_pb) {
                        String apkSize = FormatUtil.getOneString((float) updateTotalSize / 1024 / 1024);
                        apk_size_tv.setText(apkSize + "MB");
                        progress_tv.setText(progress + "%");
                        progress_pb.setProgress(progress > 3 ? progress : 3);
                    } else {
                        stopSelf();// 下载完毕, 停掉服务自身
                    }
                    break;
                case 2:// 中断
                    dlg.cancel();
                    downLoadThread = null;
                    isStop = false;
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(4, MyNotificationManager.getNotification1(getApplicationContext()));
        }
        binder = new UpdateBinder();

    }

    @Override
    public IBinder onBind(Intent intent) {
        UpdateService.this.context = AppManager.getAppManager().currentActivity();
        UpdateService.this.downloadUrl = intent.getStringExtra("downLoadUrl");
        UpdateService.this.isForceToUpdate = intent.getBooleanExtra("isForceToUpdate", false);
        return binder;
    }

    public class UpdateBinder extends Binder {
        public void start() {
            String saveFileName = saveFilePath + File.separator + Constants.Config.APK_NAME;
            File file = new File(saveFileName);
            if (file.exists()) {
                file.delete();
            }

            if (FormatUtil.validateUrl(downloadUrl)) {
                totalSize = 0;
                progress = 0;
                setUpNotification();
                // 下载
                startDownload();
            }
        }
    }

    private void startDownload() {
        downLoadThread = new Thread(mDownApkRunnable);
        downLoadThread.start();
    }

    private TextView progress_tv; //显示进度百分比
    private RoundCornerProgressBar progress_pb; //进度条
    private TextView apk_size_tv; //安装包大小
    private AlertDialog dlg = null;

    /**
     * 显示下载对话框
     */
    private void setUpNotification() {
        View view = View.inflate(context, R.layout.update_alert, null);
        dlg = new AlertDialog.Builder(context).create();
        dlg.setView(view);
        dlg.show();
        Window window = dlg.getWindow();
        if (null != window) {
            window.setContentView(R.layout.update_alert);
            //AlertDialog自定义界面圆角有背景问题    有白色背景，加这句代码
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress_tv = window.findViewById(R.id.progress_tv);
            apk_size_tv = window.findViewById(R.id.apk_size_tv);
            progress_pb = window.findViewById(R.id.progress_pb);
        }
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
    }

    private Runnable mDownApkRunnable = ()-> {
            File file = new File(saveFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String saveFileName = saveFilePath + File.separator + Constants.Config.APK_NAME;
            File saveFile = new File(saveFileName);
            downloadUpdateFile(saveFile);
    };

    private long lastTimeMillis = 0; //上一秒

    public long downloadUpdateFile(File saveFile) {
        if (downloadUrl.contains("https://")) {
            return downloadByHttps(downloadUrl, saveFile);
        } else {
            return downloadByHttp(downloadUrl, saveFile);
        }
    }

    private HttpURLConnection createConnection(String downloadUrl) {
        HttpURLConnection httpConnection = null;
        URL url;
        try {
            url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (updateTotalSize == -1) {
                updateTotalSize = (long) (27 * 1024 * 1024);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpConnection;
    }

    private long downloadByHttp(String downloadUrl, File saveFile) {
        HttpURLConnection httpConnection = createConnection(downloadUrl);
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            int redirectCount = 0;
            while (httpConnection.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
                httpConnection = createConnection(httpConnection.getHeaderField("Location"));
                redirectCount++;
            }
            // 设置超时时间
            httpConnection.setConnectTimeout(Time_Out);
            httpConnection.setReadTimeout(Time_Out);
            int connCode = httpConnection.getResponseCode();
            if (200 == connCode) {
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(saveFile, false);
                byte buffer[] = new byte[4096];
                int readSize;
                while ((readSize = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, readSize);
                    totalSize += readSize;
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastTimeMillis > 1000) {
                        progress = (int) (totalSize * 100 / updateTotalSize);
                        if (progress > 100) {
                            Log.e(this.getClass().getSimpleName(), "progress = " + progress);
                            progress = 100;
                        }
                        // 通知更新进度
                        mHandler.sendEmptyMessage(1);
                    }

                    if (isStop) {
                        mHandler.sendEmptyMessage(2);
                        break;
                    }
                }

                if (!isStop) {
                    // 下载没被中断  下载完成通知安装
                    mHandler.sendEmptyMessage(0);
                }

            } else {
                errorHandle("下载失败！1");
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                errorHandle("网络超时，下载失败！");
            } else {
                errorHandle("下载失败！2");
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return totalSize;
    }

    private void errorHandle(String tip) {
        Looper.prepare();
        if (!StringUtils.isEmpty(tip)) {
            ToastUtil.getInstance().show(context, tip);
        }
        dlg.cancel();
        Looper.loop();
        stopSelf();
    }

    private long downloadByHttps(String downloadUrl, File saveFile) {
        HttpsURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpsURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            httpConnection.setConnectTimeout(Time_Out);
            httpConnection.setReadTimeout(Time_Out);
            updateTotalSize = httpConnection.getContentLength();
            if (updateTotalSize == -1) {
                updateTotalSize = (long) (27 * 1024 * 1024);
            }
            int connCode = httpConnection.getResponseCode();
            if (200 == connCode) {
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(saveFile, false);
                byte buffer[] = new byte[1024];
                int readSize;
                while ((readSize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readSize);
                    totalSize += readSize;
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastTimeMillis > 1000) {
                        progress = (int) (totalSize * 100 / updateTotalSize);
                        if (progress > 100) {
                            progress = 100;
                        }
                        // 通知更新进度
                        mHandler.sendEmptyMessage(1);
                    }
                }

                // 下载完成通知安装
                mHandler.sendEmptyMessage(0);
            } else {
                ToastUtil.getInstance().show(context, "网络异常");
                dlg.cancel();
                stopSelf();
            }
        } catch (Exception e) {
            errorHandle("下载失败！3");
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return totalSize;
    }
}
