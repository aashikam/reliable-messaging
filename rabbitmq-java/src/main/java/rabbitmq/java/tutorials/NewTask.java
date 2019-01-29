package rabbitmq.java.tutorials;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

// work queues: to avoid doing a resource-intensive task
// immediately and having to wait for it to complete.
// Instead we schedule the task to be done later.
// We encapsulate a task as a message and send it to a queue.
// A worker process running in the background
// will pop the tasks and eventually execute the job.
// When you run many workers the tasks will be shared between them.
// This concept is especially useful in web applications where it's
// impossible to handle a complex task during a short HTTP request window.
public class NewTask {
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            // For example, a fake task described by Hello... will take three seconds.
            String msg = "Message 5";
            String message = String.join(" ", msg);

            channel.basicPublish("", TASK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
