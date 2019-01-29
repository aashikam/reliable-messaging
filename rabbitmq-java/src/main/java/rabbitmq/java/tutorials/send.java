package rabbitmq.java.tutorials;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class send {

    private final static String QUEUE_NAME = "hello";

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
            Channel channel = connection.createChannel();

            // declaring queue is idempotent - only created if it doesn't exist
            // The message content is a byte array, so you can encode whatever you like there.
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
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