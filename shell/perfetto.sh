#!/bin/bash

# ./record_android_trace -o trace_file.perfetto-trace -t 10s -b 64mb sched freq idle am wm gfx view binder_driver hal dalvik camera input res memory atrace

# 获取第一个参数作为包名
packagename=$1

# 如果没有提供包名,显示使用说明并退出
if [ -z "$packagename" ]; then
    echo "使用方法: $0 <包名>"
    exit 1
fi

# 杀掉目标应用进程
adb shell am force-stop $packagename

# 在后台启动trace
./shell/record_android_trace \
  -o trace_file.perfetto-trace \
  -b 640mb \
  -t 100s \
  --app "$packagename" \
  sched/sched_switch \
  sched/sched_wakeup \
  sched/sched_process_exit \
  sched/sched_process_free \
  task/task_newtask \
  task/task_rename \
  power/suspend_resume \
  ftrace/print \
  gfx \
  view \
  webview \
  camera \
  dalvik \
  input \
  app \
  res \
  memory \
  sched 