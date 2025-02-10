package com.aidaole.easytrace.plugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input

interface TraceParameters : InstrumentationParameters {
    @get:Input
    val includePackages: ListProperty<String>
    
    @get:Input
    val includeClasses: ListProperty<String>
} 