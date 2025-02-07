package com.aidaole.easytrace.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension

class EasyTracePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 创建扩展
        val extension = project.extensions.create("easyTrace", EasyTraceExtension::class.java)
        
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                TraceClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) { params ->
                // 使用扩展中的配置，如果没有配置则使用默认值
                val packages = if (extension.includePackages.isEmpty()) {
                    print("插桩的包名list: 为空")
                    listOf("")
                } else {
                    print("插桩的包名list: ${extension.includePackages}")
                    extension.includePackages
                }
                params.includePackages.set(packages)
            }
        }
    }
}