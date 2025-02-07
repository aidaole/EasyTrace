package com.aidaole.easytrace.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

abstract class TraceClassVisitorFactory : AsmClassVisitorFactory<TraceParameters> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val className = classContext.currentClassData.className
        print("tart processing class: $className")
        return TraceClassVisitor(nextClassVisitor, className)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        
        // 检查类名是否在配置的包名列表中
        val isInPackages = parameters.get().includePackages.get().any { packageName ->
            className.startsWith(packageName)
        }
        
        val isInstrumentable = isInPackages &&
                !className.endsWith(".R") &&
                !className.contains(".R\$") &&
                !className.endsWith(".BuildConfig") &&
                !className.contains("DataBinding")

        if (isInstrumentable) {
            print("Found instrumentable class: $className")
        }
        return isInstrumentable
    }
} 