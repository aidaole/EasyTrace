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
                params.includePackages.set(extension.includePackages)
                params.includeClasses.set(extension.includeClasses)
            }
        }
    }
}