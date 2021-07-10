package id.co.nio;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.ClientConfiguration;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import io.netty.channel.ChannelHandlerContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;

public class CamelIsoClientProducer extends DefaultProducer implements IsoMessageListener<IsoMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(CamelIsoClientProducer.class);
    private CamelIsoClientEndpoint endpoint;
    private Iso8583Client<IsoMessage> client=null;

    private static Logger logger = LoggerFactory.getLogger(CamelIsoClientProducer.class);

    public CamelIsoClientProducer(CamelIsoClientEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

        String uri = endpoint.getEndpointUri();

        String host = "localhost";
        int port = 7001;


        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .addLoggingHandler(true)
                .logSensitiveData(true)
                .workerThreadsCount(4)
                .replyOnError(true)
                .workerThreadsCount(12)
                .idleTimeout(0)
                .build();


        URL specUrl = getClass().getClassLoader().getResource("iso8583client.xml");

        if (specUrl==null) {
            logger.warn("***************************************************************************");
            logger.warn("* iso8583client.xml file is not found. Endpoint will use the default spec *");
            logger.warn("***************************************************************************");
        } else {
            logger.info("Specification file : {}",specUrl.toString());
        }

        try {

            MessageFactory mf = new MessageFactory();

            if (specUrl == null) {
                ConfigParser.configureFromClasspathConfig(mf, "default_iso.xml");
            } else {
                ConfigParser.configureFromUrl(mf, specUrl);
            }


            J8583MessageFactory messageFactory = new J8583MessageFactory<>(mf, ISO8583Version.V1987);// [1]

            SocketAddress socketAddress = new InetSocketAddress("localhost", 7001);

            client = new Iso8583Client<IsoMessage>(socketAddress, clientConfiguration, messageFactory);

            client.addMessageListener(this);

            client.init();

            try {
                client.connect(host, port);// [6]
            } catch (InterruptedException e) {
                logger.error("e",e);
            }

        } catch (Exception e) {
            logger.error("e",e);
        }

    }

    public void process(Exchange exchange) throws Exception {
        logger.info("process");
        IsoMessage isoMessage = (IsoMessage) (exchange.getIn().getBody());
        if (client.isConnected()) {
            logger.info("sending iso message");
            client.send(isoMessage);
        } else {
            logger.error("Hey, the client is not yet connected");
        }
    }

    @Override
    public boolean applies(@NotNull IsoMessage isoMessage) {
        logger.info("applies");
        return true;
    }

    @Override
    public boolean onMessage(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull IsoMessage isoMessage) {
        logger.info("onMessage");
        return false;
    }
}
