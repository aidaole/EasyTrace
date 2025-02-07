# EasyTrace Plugin

一个用于在Android项目中自动添加性能跟踪的Gradle插件。

## 更新日志

### 2024-02-06
- 初始化项目结构
- 添加基础插件配置
- 实现基于ASM的代码插桩框架
- 支持Gradle 8.0+的新API
- 重构ClassVisitor结构
- 添加包名过滤功能，只处理指定包名下的类
- 实现方法跟踪功能，在方法开始和结束时添加 Trace 标记
