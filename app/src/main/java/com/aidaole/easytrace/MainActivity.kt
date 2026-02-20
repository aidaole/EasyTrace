package com.aidaole.easytrace

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fabnic(20)

        // Test Coroutine Tracing with custom scope
        CoroutineScope(Dispatchers.Main).launch {
            heavyWork()
            val caught = runCatching { testExceptionIsolation() }.exceptionOrNull()
            check(caught is IOException) { "Expected IOException, got $caught" }
            val suppressed = caught.suppressed
            check(suppressed.isEmpty()) {
                "IOException must have no suppressed exceptions, got: ${suppressed.toList()}"
            }
            println("[EasyTrace] PASS: IOException has no suppressed exceptions")
        }

        // Test Exclusion
        ExcludedClass().shouldNotBeTraced()
    }

    suspend fun heavyWork() {
        delay(100)
        println("Coroutine work done")
    }

    /**
     * Regression test for coroutine exception suppression bug.
     *
     * IllegalArgumentException is caught internally by runCatching — it must NOT
     * appear as a suppressed exception on the IOException that escapes.
     */
    suspend fun testExceptionIsolation() {
        // This exception is caught internally — never escapes invokeSuspend
        runCatching { throw IllegalArgumentException("swallowed") }

        delay(10)

        // This is the real escaped exception — its suppressed list must be empty
        throw IOException("real escaped exception")
    }

    fun fabnic(n: Int): Int {
        if (n == 1 || n == 2) {
            return 1
        }
        return fabnic(n - 1) + fabnic(n - 2)
    }
}