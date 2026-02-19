package com.aidaole.easytrace.plugin

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class TraceMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    private val className: String,
    private val methodName: String,
    descriptor: String
) : AdviceAdapter(Opcodes.ASM9, methodVisitor, access, methodName, descriptor) {

    private val tryStart = Label()
    private val tryEnd = Label()
    private val handler = Label()

    // Pre-compute the locals array for the F_NEW handler frame.
    private val handlerLocals: Array<Any> = buildLocals(
        className, descriptor, (access and Opcodes.ACC_STATIC) != 0
    )

    override fun onMethodEnter() {
        // Register the handler BEFORE placing tryStart so the ClassWriter
        // knows about it when writing the exception table.
        mv.visitTryCatchBlock(tryStart, tryEnd, handler, null) // null = catch all Throwables
        mv.visitLdcInsn("$className#$methodName".takeLast(127))
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/os/Trace",
            "beginSection",
            "(Ljava/lang/String;)V",
            false
        )
        mv.visitLabel(tryStart)
    }

    override fun onMethodExit(opcode: Int) {
        if (opcode != ATHROW) {
            mv.visitMethodInsn(
                INVOKESTATIC,
                "android/os/Trace",
                "endSection",
                "()V",
                false
            )
        }
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        mv.visitLabel(tryEnd)
        mv.visitLabel(handler)
        // Explicit F_NEW frame required because AGP's ClassWriter uses COMPUTE_MAXS
        mv.visitFrame(
            Opcodes.F_NEW,
            handlerLocals.size,
            handlerLocals,
            1,
            arrayOf<Any>("java/lang/Throwable")
        )
        // Stack: [Throwable] — endSection() has no args so it doesn't disturb the stack.
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/os/Trace",
            "endSection",
            "()V",
            false
        )
        mv.visitInsn(ATHROW) // re-throw
        super.visitMaxs(maxStack + 1, maxLocals) // +1 for the Throwable on the stack
    }

    companion object {

        /** Build the locals array for the F_FULL catch-handler frame from the method descriptor. */
        fun buildLocals(className: String, descriptor: String, isStatic: Boolean): Array<Any> {
            val locals = mutableListOf<Any>()
            if (!isStatic) locals.add(className.replace('.', '/'))
            for (t in Type.getArgumentTypes(descriptor)) {
                locals.add(
                    when (t.sort) {
                        Type.BOOLEAN, Type.BYTE, Type.CHAR, Type.SHORT, Type.INT -> Opcodes.INTEGER
                        Type.LONG -> Opcodes.LONG
                        Type.FLOAT -> Opcodes.FLOAT
                        Type.DOUBLE -> Opcodes.DOUBLE
                        Type.ARRAY -> t.descriptor
                        else -> t.internalName // OBJECT
                    }
                )
            }
            return locals.toTypedArray()
        }
    }
}
