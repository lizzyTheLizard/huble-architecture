package site.gutschi.humble.spring.integration.solr;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SolrContainer extends GenericContainer<SolrContainer> {
    public static final String COLLECTION_NAME = "tasks";
    public static final int SOLR_PORT = 8983;
    private static final String CONFIG_DIR = "/config";
    private static final String SOLR_VERSION = "9.8.0";
    private static final String SOLR_IMAGE = "solr";

    public SolrContainer() {
        super(DockerImageName.parse(SOLR_IMAGE).withTag(SOLR_VERSION));
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
}
