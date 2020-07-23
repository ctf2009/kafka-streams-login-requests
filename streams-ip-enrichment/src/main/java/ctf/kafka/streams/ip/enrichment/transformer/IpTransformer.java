package ctf.kafka.streams.ip.enrichment.transformer;

import ctf.kafka.streams.common.model.EnrichedIpAddress;
import ctf.kafka.streams.common.model.LoginRequestRaw;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IpTransformer implements Transformer<String, LoginRequestRaw, KeyValue<String, EnrichedIpAddress>> {

    private static final Logger LOG = LoggerFactory.getLogger(IpTransformer.class);

    // TODO: Replace this with a State Store
    private Map<String, EnrichedIpAddress> cachedIpEnrichments = new HashMap<>();

    @Override
    public void init(ProcessorContext context) {
        // Not Currently Used
    }

    @Override
    public KeyValue<String, EnrichedIpAddress> transform(final String key, final LoginRequestRaw value) {
        final String reference = value.getHash();
        final EnrichedIpAddress cached = cachedIpEnrichments.get(key);

        if (cached != null) {
            LOG.debug("Cache Hit for IPAddress: " + key);
            return new KeyValue<>(reference, cached.toBuilder().reference(reference).build());
        } else {
            final EnrichedIpAddress lookup = getIpAddressDetails(key);

            if (!lookup.isErrored()) {
                cachedIpEnrichments.put(key, lookup);
            }
            return new KeyValue<>(reference, lookup.toBuilder().reference(reference).build());
        }
    }

    @Override
    public void close() {
        // Not Used
    }

    private static EnrichedIpAddress getIpAddressDetails(final String ipAddress) {

        // This is a mocked approach. We will take the last byte from the ipAddress and depending on its value will
        // use a specific city / country / region

        // Also we are assuming the input is IPV4 Only (Which is what the Driver sends)

        try {
            // Mimic a call to a IP lookup service
            Thread.sleep(1000);

            final int finalByteValue = Integer.parseInt(ipAddress.substring(ipAddress.lastIndexOf(".") + 1));

            // 10.0.0.200 = Canada  | British Columbia      | Vancouver
            // 10.0.0.201 = England | Greater Manchester    | Manchester
            // 10.0.0.202 = USA     | California            | Las Vegas

            if (ipAddress.equals("10.0.0.200")) {
                Thread.sleep(1500);
                return buildIpEnrichmentResult(ipAddress, "Canada", "British Columbia", "Vancouver");
            } else if (ipAddress.equals("10.0.0.201")) {
                Thread.sleep(1500);
                return buildIpEnrichmentResult(ipAddress, "England", "Greater Manchester", "Manchester");
            } else if (ipAddress.equals("10.0.0.202")) {
                Thread.sleep(1500);
                return buildIpEnrichmentResult(ipAddress, "USA", "California", "Las Vegas");
            } else if (finalByteValue < 40) {
                return buildIpEnrichmentResult(ipAddress, "Australia", "Queensland", "Brisbane");
            } else if (finalByteValue < 80) {
                return buildIpEnrichmentResult(ipAddress, "Australia", "Western Australia", "Perth");
            } else if (finalByteValue < 120) {
                return buildIpEnrichmentResult(ipAddress, "Australia", "New South Wales", "Sydney");
            } else if (finalByteValue < 160) {
                return buildIpEnrichmentResult(ipAddress, "Australia", "Northern Territory", "Darwin");
            } else if (finalByteValue < 200) {
                return buildIpEnrichmentResult(ipAddress, "Australia", "Victoria", "Melbourne");
            } else {
                return buildIpEnrichmentResult(ipAddress, "England", "Lancashire", "Liverpool");
            }
        } catch (Exception e) {
            return EnrichedIpAddress.builder()
                    .ipAddress(ipAddress)
                    .errored(true).build();
        }

    }

    private static EnrichedIpAddress buildIpEnrichmentResult(final String ipddress, final String country, final String region, final String city) {
        return EnrichedIpAddress.builder()
                .ipAddress(ipddress)
                .errored(false)
                .country(country)
                .region(region)
                .city(city)
                .type("ipv4")
                .build();
    }

}
