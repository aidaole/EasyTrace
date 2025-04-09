package com.aidaole.easytrace.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class TraceMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    private val className: String,
    private val methodName: String,
    descriptor: String
) : AdviceAdapter(Opcodes.ASM9, methodVisitor, access, methodName, descriptor) {

    override fun onMethodEnter() {
        // 插入 Trace.beginSection(String)
        mv.visitLdcInsn("$className#$methodName".takeLast(127))
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/os/Trace",
            "beginSection",
            "(Ljava/lang/String;)V",
            false
        )
    }

    override fun onMethodExit(opcode: Int) {
        // 插入 Trace.endSection()
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/os/Trace",
            "endSection",
            "()V",
            false
        )
    }
} 
