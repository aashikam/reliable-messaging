package rabbitmq.java.tutorials;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {

    private final static String QUEUE_NAME_1 = "queue1";
    private final static String QUEUE_NAME_2 = "queue2";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        // broker on the local machine - hence the localhost.
        // If we wanted to connect to a broker on a different
        // machine we'd simply specify its name or IP address here.

        factory.setHost("localhost");

        try {
            // Connection abstracts the socket connection
            // can use try with resources cause both Connection and Channel implement java.closeable
            Connection connection = factory.newConnection();
            Channel channel1 = connection.createChannel();
            Channel channel2 = connection.createChannel();


            // declaring queue is idempotent - only created if it doesn't exist
            // The message content is a byte array, can encode whatever you like there

            // Using same channel to publish messages to different queues-----------------------------------------------
            // Sending messages to queue1 using channel1
            // Creates aa non-durable, non-exclusive, non-auto-delete queue with a well-known name
            channel1.queueDeclare(QUEUE_NAME_1, false, false, false, null);
            String message = "Hello World!-1 (queue1 - channel1)";
            // all queues are bound to the default exchange ("") with the queue name as its routing key
            channel1.basicPublish("", QUEUE_NAME_1, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");

            // Sending messages to queue2 using channel1
            channel1.queueDeclare(QUEUE_NAME_2, false, false, false, null);
            String message2 = "Hello World!-2 (queue2 - channel1)";
            channel1.basicPublish("", QUEUE_NAME_2, null, message2.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message2 + "'");
            // ---------------------------------------------------------------------------------------------------------

            // Creating different channels using the same connection----------------------------------------------------
            // Sending messages to queue2 using channel2
            channel2.queueDeclare(QUEUE_NAME_2, false, false, false, null);
            String message3 = "Hello World!-3 (queue2 - channel2)";
            channel2.basicPublish("", QUEUE_NAME_2, null, message3.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message3 + "'");
            //----------------------------------------------------------------------------------------------------------

        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}

// If the program doesn't work:
// Maybe the broker was started without enough free disk space
// (by default it needs at least 200 MB free) and is therefore
// refusing to accept messages.
// Check the broker logfile to confirm and reduce the limit if necessary.
// The configuration file documentation will show you how to set disk_free_limit.