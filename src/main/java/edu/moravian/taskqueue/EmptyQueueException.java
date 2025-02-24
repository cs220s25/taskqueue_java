package edu.moravian.taskqueue;

public class EmptyQueueException extends RuntimeException {
    public EmptyQueueException(String message) {
        super(message);
    }
}
