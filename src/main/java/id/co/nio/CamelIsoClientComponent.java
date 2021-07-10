package id.co.nio;

import java.util.Map;

import org.apache.camel.Endpoint;

import org.apache.camel.support.DefaultComponent;

@org.apache.camel.spi.annotations.Component("iso8583client")
public class CamelIsoClientComponent extends DefaultComponent {
    
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new CamelIsoClientEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;

    }

}
