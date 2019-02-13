package rabbitmq.java.tutorials;
//
// There are a few exchange types available: direct, topic,
// headers and fanout. We'll focus on the last one -- the fanout.
// Let's create an exchange of this type, and call it logs:
// The fanout exchange is very simple. As you can probably guess from the name,
// it just broadcasts all the messages it receives to all the queues it knows.
// And that's exactly what we need for our logger.

// to list exchanges in the server
// sudo rabbitmqctl list_exchanges

//In this list there will be some amq.* exchanges and the default
// (unnamed) exchange. These are created by default, but it is unlikely you'll need to use them at the moment.
// Now, we can publish to our named exchange instead:
//
//channel.basicPublish( "logs", "", null, message.getBytes());
// TEMPORARY QUEUES
// In the Java client, when we supply no parameters to queueDeclare() we create a non-durable, exclusive, autodelete queue with a generated name:
//
//String queueName = channel.queueDeclare().getQueue();
// At that point queueName contains a random queue name. For example it may look like amq.gen-JzTY20BRgKO-HjmUJj0wLg.

// BINDINGS
// You can list existing bindings using, you guessed it,
// A binding is a relationship between an exchange and a queue.
// This can be simply read as: the queue is interested in messages from this exchange.
//
//rabbitmqctl list_bindings
// This is how we could create a binding with a key:
//
//channel.queueBind(queueName, EXCHANGE_NAME, "black");
//
//The meaning of a binding key depends on the exchange type.
// The fanout exchanges, which we used previously, simply ignored its value.

// pub sub example
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogs {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = argv.length < 1 ? "info: Hello World!" :
                    String.join(" ", argv);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
