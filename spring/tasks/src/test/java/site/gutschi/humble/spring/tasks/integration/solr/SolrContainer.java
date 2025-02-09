package site.gutschi.humble.spring.tasks.integration.solr;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SolrContainer extends GenericContainer<SolrContainer> {
    private static final String CONFIG_DIR = "/config";
    private static final String COLLECTION_NAME = "tasks";
    private static final int SOLR_PORT = 8983;

    public SolrContainer() {
        super(DockerImageName.parse("solr:9.3.0"));
        addExposedPort(SOLR_PORT);
        setCommand("solr-precreate " + COLLECTION_NAME);
        setWaitStrategy(new LogMessageWaitStrategy()
                .withRegEx(".*o\\.e\\.j\\.s\\.Server Started.*")
                .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
        setLogConsumers(List.of(outputFrame -> System.out.println(outputFrame.getUtf8String())));
    }

    public SolrContainer(String configDir) {
        this();
        setCommand("solr-precreate " + COLLECTION_NAME + " " + CONFIG_DIR);
        withClasspathResourceMapping(configDir, CONFIG_DIR + "/conf/", BindMode.READ_ONLY);
    }

    public SolrConfiguration getConfiguration() {
        @SuppressWarnings("HttpUrlsUsage")
        final var url = String.format("http://%s:%d/solr/%s",
                getHost(),
                getMappedPort(SOLR_PORT),
                COLLECTION_NAME
        );
        final var result = new SolrConfiguration();
        result.setUrl(url);
        return result;
    }
}
