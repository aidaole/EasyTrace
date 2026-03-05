package com.aidaole.easytrace.plugin

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Pure try/finally instrumentation for coroutine invokeSuspend methods:
 *
 *   Trace.beginSection(label)
 *   try {
 *       invokeSuspend(...)
 *   } finally {
 *       Trace.endSection()
 *   }
 *
 * Extends MethodVisitor directly — no AdviceAdapter, no per-ATHROW hooks.
 *
 * CRITICAL: visitTryCatchBlock is called in visitMaxs (not visitCode) so that
 * our catch-all is the LAST entry in the exception table. The JVM scans handlers
 * top-to-bottom and takes the first match, so internal handlers (runCatching,
 * try-catch) registered during method body visitation come first and take priority.
 * Registering our handler first would make it intercept exceptions before internal
 * handlers could catch them.
 */
class CoroutineTraceMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    private val className: String,
    private val methodName: String,
    descriptor: String,
) : MethodVisitor(Opcodes.ASM9, methodVisitor) {

    private val tryStart = Label()
    private val tryEnd = Label()
    private val handler = Label()
    private val handlerLocals: Array<Any> = TraceMethodVisitor.buildLocals(
        className, descriptor, (access and Opcodes.ACC_STATIC) != 0
    )

    override fun visitCode() {
        super.visitCode()
        // beginSection + tryStart — do NOT call visitTryCatchBlock here, that would
        // put our catch-all first in the table, before internal handlers.
        mv.visitLdcInsn("$className#$methodName".takeLast(127))
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "beginSection", "(Ljava/lang/String;)V", false)
        mv.visitLabel(tryStart)
    }

    override fun visitInsn(opcode: Int) {
        // finally-normal path: endSection before every return
        if (opcode == Opcodes.RETURN || opcode == Opcodes.IRETURN || opcode == Opcodes.LRETURN ||
            opcode == Opcodes.FRETURN || opcode == Opcodes.DRETURN || opcode == Opcodes.ARETURN
        ) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "endSection", "()V", false)
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        // Register catch-all LAST — after all method body handlers — so internal
        // handlers (runCatching, try-catch) have higher priority in the JVM table.
        mv.visitTryCatchBlock(tryStart, tryEnd, handler, null)
        // finally-exceptional path
        mv.visitLabel(tryEnd)
        mv.visitLabel(handler)
        mv.visitFrame(
            Opcodes.F_NEW,
            handlerLocals.size,
            handlerLocals,
            1,
            arrayOf<Any>("java/lang/Throwable")
        )
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/os/Trace", "endSection", "()V", false)
        mv.visitInsn(Opcodes.ATHROW)
        super.visitMaxs(maxStack + 1, maxLocals) // +1 for the Throwable on the stack
    }
}
