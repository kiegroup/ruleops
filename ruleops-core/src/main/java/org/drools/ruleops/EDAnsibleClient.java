package org.drools.ruleops;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EDAnsibleClient {
    private static final Logger LOG = LoggerFactory.getLogger(EDAnsibleClient.class);
    private static long lastTsSent = 0;

    private EDAnsibleClient() {
        // only static utils.
    }

    public static EDAnsibleClientBuilder with(String key, Object value) {
        return new EDAnsibleClientBuilder(key, value);
    }

    public static class EDAnsibleClientBuilder {
        private URI edansibleURI;
        private Map<String, Object> params = new HashMap<>();

        EDAnsibleClientBuilder(String key, Object value) {
            var endpoint = ConfigProvider.getConfig().getOptionalValue("ruleops.edansible.endpoint", String.class);
            if (endpoint.isPresent()) {
                edansibleURI = URI.create(endpoint.get());
            }
            params.put(key, value);
        }

        public EDAnsibleClientBuilder with(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public void sendEvent() throws Exception {
            if (edansibleURI == null) {
                LOG.warn("ruleops.edansible.endpoint is not setup, won't notify EDAnsible.");
                return;
            }
            long curTs = System.currentTimeMillis();
            if (curTs - lastTsSent < 10_000) {
                LOG.warn("TODO currently ruleops.edansible.endpoint supports sending max 1 event, every 10sec.");
                return;
            }
            lastTsSent = curTs;
            String requestBody = new ObjectMapper().writeValueAsString(params);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .version(Version.HTTP_1_1)
                    .uri(edansibleURI)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            LOG.info("{}", response);
        }
    }
}
