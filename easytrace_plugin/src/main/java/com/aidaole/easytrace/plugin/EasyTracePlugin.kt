package com.aidaole.easytrace.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension

class EasyTracePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                TraceClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) { params ->
                // 配置参数如果需要
            }
        }
    }
}