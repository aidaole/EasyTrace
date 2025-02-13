# EasyTrace

[![](https://jitpack.io/v/aidaole/EasyTrace.svg)](https://jitpack.io/#aidaole/EasyTrace)

## 项目简介
这个项目主要用于在Android平台上使用插桩技术给应用添加SystemTrace, 方便在Perfetto中查看应用的性能数据。

可以很方便的发现应用中的耗时方法, 对于优化启动速度, 卡顿问题很有帮助。功能类似于与头条的 `Btrace`框架

但是为什么要使用EasyTrace呢? 现在也有不少框架可以插桩, 但是由于gradle8.0的升级, android.registerTransform 被废弃了, 导致如果你的项目比较新是没办法使用的, 除非你的gradle还在 7.x 版本.

## 本项目特点:

1. 支持gradle 8.0 及以上版本
2. 支持根据包名插桩
3. 支持根据类名插桩
4. 自动忽略R文件,kotlin自动生成内部类等多余方法的插桩
5. shell脚本目录下包含了perfetto使用的基本工具, 简单易用

## 使用方法

### 配置插件

**在项目根目录的 `build.gradle` 添加插件**

``` 
buildscript {
    dependencies {
        classpath("com.github.aidaole:EasyTrace:1.0.0")
    }
}
```

**在app的 `build.gradle` 文件中添加插件配置**

```
plugins {
    // ...
    id 'com.aidaole.easytrace'
}

easyTrace {
    // 指定包名, 可以指定多个以,分割. 包名下的所有方法都会被插桩, 插桩的方法会比较多
    // includePackages = ['com.aidaole.easytrace'] 

    // 指定类名, 可以指定多个以,分割. 类名下的所有方法都会被插桩
    includeClasses = ['com.aidaole.easytrace.App', 'com.aidaole.easytrace.MainActivity'] 
}
```

### 编译apk

编译的时候可以看到插桩了哪些方法

```
EasyTrace git:(main) ✗ ./gradlew :app:installDebug

> Task :app:transformDebugClassesWithAsm
[EasyTrace] Found instrumentable class: com.aidaole.easytrace.App
[EasyTrace] Start processing class: com.aidaole.easytrace.App
[EasyTrace] Adding trace to method: com.aidaole.easytrace.App#attachBaseContext(Landroid/content/Context;)V
[EasyTrace] Adding trace to method: com.aidaole.easytrace.App#onCreate()V
[EasyTrace] Adding trace to method: com.aidaole.easytrace.App#fabnic2(I)I
```

### 使用Perfetto查看Trace

可以直接将shell目录下的脚本都copy到你自己的项目中

然后使用 `perfetto.sh 包名` 抓取trace日志, 

注意: 需要手动启动一下应用, 抓取完trace后自己在终端 `ctrl+c` 结束抓取即可

```
sh shell/perfetto.sh com.aidaole.easytrace
```

## 查看效果

在运行脚本的目录下会生成 `trace_file.perfetto-trace` 文件, 使用 `perfetto_ui` 打开即可

perfetto ui 地址: [https://ui.perfetto.dev](https://ui.perfetto.dev)

![](images/README/2025-02-11-11-14-39.png)

这里可以看到启动过程中 App 内内插桩的方法, 这里写了一个斐波那契数列的递归方法, 可以看到这个方法的耗时, 以及调用栈