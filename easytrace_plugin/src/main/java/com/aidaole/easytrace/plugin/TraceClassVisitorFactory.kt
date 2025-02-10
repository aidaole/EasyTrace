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
        print("Start processing class: $className")
        return TraceClassVisitor(nextClassVisitor, className)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        
        // 检查是否在包名列表中
        val isInPackages = parameters.get().includePackages.get().any { packageName ->
            className.startsWith(packageName)
        }
        
        // 检查是否在类名列表中
        val isInClasses = parameters.get().includeClasses.get().any { targetClass ->
            className == targetClass
        }
        
        // 包名或类名匹配其一即可
        val shouldInclude = isInPackages || isInClasses
        
        val isInstrumentable = shouldInclude &&
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