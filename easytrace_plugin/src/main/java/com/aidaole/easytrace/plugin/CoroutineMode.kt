package com.aidaole.easytrace.plugin

enum class CoroutineMode {
    /**
     * Use TraceMethodVisitor with try-finally guarantee.
     * Each resumption of a coroutine gets its own fully closed flat trace section.
     */
    ENABLED,

    /**
     * Do not instrument invokeSuspend on coroutine state machine classes at all.
     */
    DISABLED
}
