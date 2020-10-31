package example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ui.FaceLivenessActivity;
import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.operator.FaceServer;
import com.yangguangyulu.sunoleducation.ui.EducationActivity;
import com.yangguangyulu.sunoleducation.ui.MainActivity;
import com.yangguangyulu.sunoleducation.ui.face.FaceRecognizeActivity;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtils;
import com.yangguangyulu.sunoleducation.util.ConfigUtil;

import java.util.HashMap;

import example.widget.DefaultDialog;

public class FaceLivenessExpActivity extends FaceLivenessActivity {

    private DefaultDialog mDefaultDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLivenessCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap) {
        super.onLivenessCompletion(status, message, base64ImageMap);
        if (status == FaceStatusEnum.OK && mIsCompletion) {
            showMessageDialog("活体检测", "检测成功");
        } else if (status == FaceStatusEnum.Error_DetectTimeout ||
                status == FaceStatusEnum.Error_LivenessTimeout ||
                status == FaceStatusEnum.Error_Timeout) {
            showMessageDialog("活体检测", "未在规定时间内完成人脸识别操作，本次学习已自动停止");
        }
    }

    /***
     * 打开另外一个Activity
     */
    public void startActivity(Class clazz) {
        startActivity(clazz, -1);
    }

    /***
     * 打开另外一个Activity
     */
    public void startActivity(Class clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    private void showMessageDialog(String title, String message) {
        if (mDefaultDialog == null) {
            DefaultDialog.Builder builder = new DefaultDialog.Builder(this);
            builder.setTitle(title).
                    setMessage(message).
                    setNegativeButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (message.contains("停止")) {
                                        mDefaultDialog.dismiss();
                                        startActivity(EducationActivity.class);
                                        finish();
                                    } else {
                                        mDefaultDialog.dismiss();
                                        finish();
                                    }

                                }
                            });
            mDefaultDialog = builder.create();
            mDefaultDialog.setCancelable(true);
        }
        mDefaultDialog.dismiss();
        mDefaultDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();

    }

    private void showExitDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(FaceLivenessExpActivity.this);
        normalDialog.setTitle("活体检测");
        normalDialog.setMessage("该课程尚未完成，确认退出将不累计学时！");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(EducationActivity.class);
                        finish();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }
}
