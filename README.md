# VirtualApkTest

滴滴的VirtualApk插件化使用演示


## 快速集成指南

### 配置项目根目录

在项目根目录的 `build.gradle` 增加virtualapk的插件库:

```
buildscript {
    dependencies {
        ...
        classpath 'com.android.tools.build:gradle:3.1.0'
        //virtualapk插件库
        classpath 'com.didi.virtualapk:gradle:0.9.8.6'
    }
}

```

### 配置宿主工程

1.在宿主模块的 `build.gradle` 中增加宿主库：

```
apply plugin: 'com.didi.virtualapk.host'

dependencies {
    ...
    implementation 'com.didi.virtualapk:core:0.9.8'
}

```

2.在宿主模块的Application中初始化宿主库：

```
public class MyApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginManager.getInstance(base).init();
    }
}

```


### 配置插件工程

在插件模块的 `build.gradle` 中增加插件库：

```
apply plugin: 'com.didi.virtualapk.plugin'

virtualApk {
    packageId = 0x6f // the package id of Resources.
    targetHost = '../app' // the path of application module in host project.
    applyHostMapping = true //optional, default value: true.
}

```

## 如何使用

### 加载插件

使用PluginManager的`loadPlugin`方法即可完成插件的加载。

```
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
```

### 调用插件

使用PluginManager的`getLoadedPlugin`方法可判断插件是否加载完毕。

```
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
```


## 总结

* 优点：该插件化库集成和使用较为简单，易上手。

* 缺点：支持的功能不够完备，连最简单的插件内动态申请权限都不支持...

