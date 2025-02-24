package edu.moravian.taskqueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import com.github.fppt.jedismock.RedisServer;

import static org.junit.jupiter.api.Assertions.*;

public class TaskQueueTest {
    private RedisServer server;
    private Jedis jedis;
    private TaskQueue queue;

    @BeforeEach
    void setUp() throws Exception {
        server = RedisServer.newRedisServer().start(); // Start mock Redis server
        jedis = new Jedis(server.getHost(), server.getBindPort());
        queue = new TaskQueue(jedis);
    }

    @AfterEach
    void tearDown() throws Exception {
        jedis.close();
        server.stop();
    }

    @Test
    void testGetNextTaskThrowsExceptionOnEmptyQueue() {
        Exception exception = assertThrows(EmptyQueueException.class, queue::getNextTask);
        assertEquals("Task queue is empty.", exception.getMessage());
    }

    @Test
    void testPushAndRetrieveTask() {
        queue.pushTask("Test Task");
        assertEquals("Test Task", queue.getNextTask());
    }

    @Test
    void testTasksReturnedInOrder() {
        queue.pushTask("Task 1");
        queue.pushTask("Task 2");
        queue.pushTask("Task 3");

        assertEquals("Task 1", queue.getNextTask());
        assertEquals("Task 2", queue.getNextTask());
        assertEquals("Task 3", queue.getNextTask());
    }
}
