package me.kalmemarq.minicraft;

import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
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
			if (!this.runTask()) break;
		}
	}
}
