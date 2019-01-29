package rabbitmq.java.tutorials;

// The extra DefaultConsumer is a class implementing the Consumer interface
// we'll use to buffer the messages pushed to us by the server.
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class receive {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Why don't we use a try-with-resource statement to automatically close the channel and the connection?
        // By doing so we would simply make the program move on, close everything,
        // and exit! This would be awkward because we want the process to stay alive while the consumer
        // is listening asynchronously for messages to arrive.
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Note that we declare the queue here, as well.
        // Because we might start the consumer before the publisher,
        // we want to make sure the queue exists before we try to consume messages from it
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // We're about to tell the server to deliver us the messages from the queue.
        // Since it will push us messages asynchronously, we provide a callback
        // in the form of an object that will buffer the messages until we're ready to use them.
        // That is what a DeliverCallback subclass does.
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}

    // Listing queues
    // You may wish to see what queues RabbitMQ has and how many messages are in them.
    // You can do it (as a privileged user) using the rabbitmqctl tool:

        // sudo rabbitmqctl list_queues
