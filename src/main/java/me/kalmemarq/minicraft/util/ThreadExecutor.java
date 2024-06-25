/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.util;

import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

public abstract class ThreadExecutor implements Executor {
    private final Queue<Runnable> runnables = new ConcurrentLinkedQueue<>();

    public boolean isOnThread() {
        return this.getThread() == Thread.currentThread();
    }

    public abstract Thread getThread();

    @Override
    public void execute(@NotNull Runnable command) {
        if (!this.isOnThread()) {
            this.runnables.add(command);
        } else {
            command.run();
        }
    }

    public boolean runTask() {
        var command = this.runnables.peek();
        if (command == null) return false;
        command.run();
        this.runnables.remove();
        return true;
    }

    public void runAllTasks() {
        while (true) {
            if (!this.runTask()) break;
        }
    }

    public void runTasksWhile(Supplier<Boolean> stopCondition) {
        while (stopCondition.get()) {
            if (this.runTask()) continue;
            Thread.yield();
            LockSupport.parkNanos("waiting for tasks", 100000L);
        }
    }
}
