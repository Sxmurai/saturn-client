/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.entity.player.inventory.task;

import cope.saturn.util.internal.Stopwatch;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles tasks easier
 */
public class TaskHandler {
    // A queue of tasks waiting to run
    private final Queue<Task> tasks = new ConcurrentLinkedQueue<>();
    private final Stopwatch stopwatch = new Stopwatch();

    /**
     * Adds a task to the queue
     * @param task The task instance
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Runs x amount of tasks
     * @param amount the amount of tasks to run
     */
    public void run(int amount) {
        for (int i = 0; i < amount; ++i) {
            Task task = tasks.poll();
            if (task == null) {
                break;
            }

            task.execute();
        }
    }

    /**
     * Checks if we have no more tasks
     * @return if the task queue is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Clears the task queue
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Gets the stopwatch instance
     * @return the stopwatch instance
     */
    public Stopwatch getStopwatch() {
        return stopwatch;
    }
}
