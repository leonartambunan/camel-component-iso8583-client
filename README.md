# Camel Component - ISO8583 Client

ISO8583 Client as Camel Component

## Libraries
- j8583 (com.solab.iso8583)
- jReactive-8583 (https://github.com/kpavlov/jreactive-8583)
- slf4j

## How to Build

```$ mvn -DskipTests clean package```


## How to Use

### ISO8583 Specification

Put your iso8583client.xml file (containing j8583 message specification) in the root of Java class loader (usually as src/main/resources/iso8583client.xml)

### Code

```java


import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

.....

from("routexxx")
  .to("iso8583server:7001")
  .end();

```
