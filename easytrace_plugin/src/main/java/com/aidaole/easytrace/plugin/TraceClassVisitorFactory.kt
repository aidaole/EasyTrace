package com.aidaole.easytrace.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

abstract class TraceClassVisitorFactory : AsmClassVisitorFactory<TraceParameters> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        val className = classContext.currentClassData.className
        val coroutineMode = parameters.get().coroutineMode.get()
        print("Start processing class: $className")
        return TraceClassVisitor(nextClassVisitor, className, coroutineMode)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className

        val isInPackages = parameters.get().includePackages.get().any { className.startsWith(it) }
        val isInClasses  = parameters.get().includeClasses.get().any  { className == it }
        val isExcluded   = parameters.get().excludePatterns.get().any { className.matches(Regex(it)) }

        val isInstrumentable = (isInPackages || isInClasses) &&
                !isExcluded &&
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
