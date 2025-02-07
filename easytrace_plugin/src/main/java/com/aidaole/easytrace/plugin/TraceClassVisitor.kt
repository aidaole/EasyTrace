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
        println("[EasyTrace] Visiting method: $className#$name$descriptor")
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return methodVisitor
        // TODO: 后续我们会在这里添加 TraceMethodVisitor 来实现方法跟踪
    }
} 