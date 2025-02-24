package edu.moravian.taskqueue;

import redis.clients.jedis.Jedis;

public class TaskQueue {
    private final Jedis jedis;
    private static final String QUEUE_KEY = "taskQueue";

    public TaskQueue(Jedis jedis) {
        this.jedis = jedis;
    }

    public void pushTask(String task) {
        jedis.lpush(QUEUE_KEY, task);
    }

    public String getNextTask() {
        String task = jedis.rpop(QUEUE_KEY);
        if (task == null) {
            throw new EmptyQueueException("Task queue is empty.");
        }
        return task;
    }
}
