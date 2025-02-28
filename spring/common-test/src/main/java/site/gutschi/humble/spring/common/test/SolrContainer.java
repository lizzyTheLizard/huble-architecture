package site.gutschi.humble.spring.common.test;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SolrContainer extends GenericContainer<SolrContainer> {
    private static final String IMAGE_VERSION = "9.8.0";
    private static final String IMAGE_NAME = "solr";
    private static final int SOLR_PORT = 8983;
    private static final String COLLECTION_NAME = "tasks";
    private static final String CONFIG_DIR = "/config";

    private String configDir = null;

    public SolrContainer() {
        super(DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_VERSION));
    }

    public SolrContainer withConfigDir(String configDir) {
        this.configDir = configDir;
        return this;
    }

    @Override
    protected void configure() {
        super.configure();
        addExposedPort(SOLR_PORT);
        setWaitStrategy(new LogMessageWaitStrategy()
                .withRegEx(".*o\\.e\\.j\\.s\\.Server Started.*")
                .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)));
        setLogConsumers(List.of(outputFrame -> System.out.println(outputFrame.getUtf8String())));
        if (configDir != null) {
            setCommand("solr-precreate " + COLLECTION_NAME + " " + CONFIG_DIR);
            withClasspathResourceMapping(configDir, CONFIG_DIR + "/conf/", BindMode.READ_ONLY);
        }
    }

    public String getUrl() {
        return String.format("http://%s:%d/solr/%s", getHost(), getMappedPort(SolrContainer.SOLR_PORT), SolrContainer.COLLECTION_NAME);
    }

    public void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("site.gutschi.humble.spring.integration.solr.url", this::getUrl);
    }
}
