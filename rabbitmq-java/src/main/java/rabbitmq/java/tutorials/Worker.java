package rabbitmq.java.tutorials;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println(" [x] Received '" + message + "'");
            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        // Acknowledgement must be sent on the same
        // channel the delivery it is for was received on.
        // Attempts to acknowledge using a different channel will result in a channel-level protocol exception
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

// Round Robin dispatching
// parallelise work
// if two Worker instances are run they will both get the messages from the queue
// java -cp $CP Worker
//# => [*] Waiting for messages. To exit press CTRL+C
//# => [x] Received 'First message.'
//# => [x] Received 'Third message...'
//# => [x] Received 'Fifth message.....'
//
//java -cp $CP Worker
//# => [*] Waiting for messages. To exit press CTRL+C
//# => [x] Received 'Second message..'
//# => [x] Received 'Fourth message....'

// By default, RabbitMQ will send each message to the next consumer,
// in sequence. On average every consumer will get the same number of messages.
// This way of distributing messages is called round-robin. Try this out with three or more workers.

// Message acknowledgements
//  With our current code, once RabbitMQ delivers a message to the customer it immediately marks it for deletion.
// In this case, if you kill a worker we will lose the message it was just processing.
// We'll also lose all the messages that were dispatched to this particular worker but were not yet handled.
// Inorder to make sure the messages aren't lost - message acknowledgements
// An ack is sent back by the consumer when it is safe to delete it - done processing
// if the connection/channel was closed before processing a message, rabbitmq will re-queue it.
// if another consumer is online at the time it will be delivered to that consumer
// manual message acknowledgements are turned on by default - turned off by "autoAck = true" flag


// Forgotten acknowledgement
// missing the basicAck
// to print the unacked messages use
// sudo rabbitmqctl list_queues name messages_ready messages_unacknowledged

// Message durability
// tasks wont be lost of the connection is lost, but if the server stops, it will be lost
// to avoid - make queues and messages durable
// to never lose the queue declare it durable
// cant declare the same queue with different parameters - cant make an existing queue durable
// Now we need to mark our messages as persistent -
// by setting MessageProperties (which implements BasicProperties) to the value PERSISTENT_TEXT_PLAIN.
// saves the messages to the disk
//