package edu.moravian.taskqueue;

import org.apache.commons.cli.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class TaskQueueCLI {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar taskqueue.jar add <task> | next");
            return;
        }

        String command = args[0]; // First argument is the command
        Jedis jedis = null;

        try {
            jedis = new Jedis("localhost", 6379);
            jedis.ping(); // Try to communicate with Redis

            TaskQueue queue = new TaskQueue(jedis);

            if ("add".equalsIgnoreCase(command)) {
                if (args.length < 2) {
                    System.out.println("Error: No task provided.");
                    return;
                }
                String task = args[1];
                try {
                    queue.pushTask(task);
                    System.out.println("Task added: " + task);
                } catch (JedisConnectionException e) {
                    System.out.println("Error: Unable to connect to Redis. Ensure the server is running.");
                }
            } else if ("next".equalsIgnoreCase(command)) {
                try {
                    String nextTask = queue.getNextTask();
                    System.out.println("Next task: " + nextTask);
                } catch (EmptyQueueException e) {
                    System.out.println("No tasks in the queue.");
                } catch (JedisConnectionException e) {
                    System.out.println("Error: Unable to connect to Redis. Ensure the server is running.");
                }
            } else {
                System.out.println("Invalid command. Use 'add <task>' or 'next'.");
            }
        } catch (JedisConnectionException e) {
            System.out.println("Error: Unable to connect to Redis. Ensure the Redis server is running on localhost:6379.");
        } finally {
            if (jedis != null) {
                jedis.close(); // Ensure Redis connection is closed
            }
        }
    }
}
