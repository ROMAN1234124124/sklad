package ru.altrimo.slad2025.fragment.scanner.processor

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean



class ScopedExecutor(private val executor: Executor) : Executor {

    private val shutdown = AtomicBoolean()

    override fun execute(command: Runnable) {
        if (shutdown.get()) {
            return
        }
        executor.execute {
            if (shutdown.get()) {
                @Suppress("LABEL_NAME_CLASH")
                return@execute
            }
            command.run()
        }
    }

    fun shutdown() {
        shutdown.set(true)
    }

}
