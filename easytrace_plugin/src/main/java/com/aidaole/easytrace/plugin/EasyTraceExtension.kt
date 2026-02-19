package com.aidaole.easytrace.plugin

open class EasyTraceExtension {
    var includePackages: List<String> = listOf()
    var includeClasses: List<String> = listOf()

    /**
     * Regex patterns for fully-qualified class names to exclude from instrumentation.
     */
    var excludePatterns: List<String> = listOf()

    /**
     * How to handle Kotlin coroutine state machine classes (identified by class names ending in $N).
     *
     * ENABLED (default): Use CoroutineTraceMethodVisitor with try-finally guarantee.
     *   Each resumption of a coroutine gets its own fully closed flat trace section.
     *
     * DISABLED: Do not instrument invokeSuspend on coroutine state machine classes at all.
     */
    var coroutineMode: CoroutineMode = CoroutineMode.ENABLED
}
