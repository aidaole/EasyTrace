package com.aidaole.easytrace.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TraceClassVisitor(
    nextVisitor: ClassVisitor,
    private val className: String
) : ClassVisitor(Opcodes.ASM9, nextVisitor) {

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        
        // 跳过以下方法：
        // 1. 构造方法
        // 2. 静态初始化方法
        // 3. 合成方法（编译器生成的方法）
        if (name == "<init>" || 
            name == "<clinit>" || 
            access and Opcodes.ACC_SYNTHETIC != 0) {
            return methodVisitor
        }

        println("[EasyTrace] Adding trace to method: $className#$name$descriptor")
        return TraceMethodVisitor(
            methodVisitor,
            access,
            className,
            name,
            descriptor
        )
    }
} 