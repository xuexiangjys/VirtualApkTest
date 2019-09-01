package com.xuexiang.virtualapktest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;
import com.didi.virtualapk.internal.LoadedPlugin;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xutil.tip.ToastUtils;

import java.io.File;

import static com.xuexiang.xaop.consts.PermissionConsts.CAMERA;
import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;

/**
 *
 *
 * @author xuexiang
 * @since 2019-09-01 23:11
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @SingleClick
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_init:
                loadPlugin();
                break;
            case R.id.btn_do:
                callPlugin();
                break;
            default:
                break;
        }
    }

    /**
     * 加载插件
     */
    @Permission(STORAGE)
    private void loadPlugin() {
        PluginManager pluginManager = PluginManager.getInstance(this);
        pluginManager.addCallback(new PluginManager.Callback() {
            @Override
            public void onAddedLoadedPlugin(LoadedPlugin plugin) {
                ToastUtils.toast("插件：[" + plugin.getPackageName() + "]加载完毕！");
            }
        });
        File apk = new File(Environment.getExternalStorageDirectory(), "xqrcode_plugin.apk");
        if (apk.exists()) {
            try {
                pluginManager.loadPlugin(apk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.toast("插件不存在，请将插件push到sdcard上！");
        }
    }

    /**
     * 调用插件
     */
    @Permission(CAMERA)
    private void callPlugin() {
        if (PluginManager.getInstance(this).getLoadedPlugin("com.xuexiang.xqrcodeplugin") == null) {
            ToastUtils.toast("请先加载插件！");
            return;
        }

        Intent intent = new Intent();
        intent.setClassName(this, "com.xuexiang.xqrcode.ui.CaptureActivity");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理二维码扫描结果
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data);
        }
    }

    /**
     * 处理二维码扫描结果
     * @param data
     */
    private void handleScanResult(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getInt("result_type") == 1) {
                    String result = bundle.getString("result_data");
                    ToastUtils.toast("解析结果:" + result, Toast.LENGTH_LONG);
                } else if (bundle.getInt("result_type") == 2) {
                    ToastUtils.toast("解析二维码失败", Toast.LENGTH_LONG);
                }
            }
        }
    }
}
