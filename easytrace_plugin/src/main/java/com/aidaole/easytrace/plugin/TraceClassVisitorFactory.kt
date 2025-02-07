package com.aidaole.easytrace.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class TraceClassVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val className = classContext.currentClassData.className
        println("[EasyTrace] Start processing class: $className")
        return TraceClassVisitor(nextClassVisitor, className)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val targetPackage = "com.aidaole.easytrace"
        val className = classData.className
        
        // 排除以下类型：
        // 1. 不是目标包名下的类
        // 2. R 类及其内部类
        // 3. BuildConfig 类
        // 4. DataBinding 相关类
        val isInstrumentable = className.startsWith(targetPackage) &&
                !className.endsWith(".R") &&
                !className.contains(".R\$") &&
                !className.endsWith(".BuildConfig") &&
                !className.contains("DataBinding")

        if (isInstrumentable) {
            println("[EasyTrace] Found instrumentable class: $className")
        }
        return isInstrumentable
    }
} 