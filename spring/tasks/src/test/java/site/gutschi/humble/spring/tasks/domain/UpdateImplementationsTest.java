package site.gutschi.humble.spring.tasks.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import site.gutschi.humble.spring.tasks.domain.api.UpdateImplementationsRequest;
import site.gutschi.humble.spring.tasks.domain.api.UpdateImplementationsUseCase;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationCaller;
import site.gutschi.humble.spring.tasks.domain.ports.CheckImplementationsResponse;
import site.gutschi.humble.spring.tasks.domain.ports.TaskRepository;
import site.gutschi.humble.spring.tasks.model.Task;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class UpdateImplementationsTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private UpdateImplementationsUseCase target;

    @MockitoBean
    private TaskRepository taskRepository;

    @MockitoBean
    private CheckImplementationCaller checkImplementationCaller;

    @Test
    void updateImplementations() throws URISyntaxException, MalformedURLException {
        final var requestUrl = new URI("https://example.com").toURL();
        final var responseUrl = new URI("https://example.com/132").toURL();
        final var request = new UpdateImplementationsRequest(requestUrl);
        final var responseEntry = new CheckImplementationsResponse.CheckImplementationsResponseEntry("PRO-12", responseUrl, "Commit 132");
        final var response = new CheckImplementationsResponse(List.of(responseEntry));
        Mockito.when(checkImplementationCaller.checkForImplementations(request)).thenReturn(response);
        final var task = Mockito.mock(Task.class);
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.of(task));

        target.updateImplementations(request);

        Mockito.verify(task).addImplementation(responseUrl, "Commit 132");
        Mockito.verify(taskRepository).save(task);
    }

    @Test
    void continueIfError() throws MalformedURLException, URISyntaxException {
        final var requestUrl = new URI("https://example.com").toURL();
        final var responseUrl1 = new URI("https://example.com/11").toURL();
        final var responseUrl2 = new URI("https://example.com/132").toURL();
        final var request = new UpdateImplementationsRequest(requestUrl);
        final var responseEntry1 = new CheckImplementationsResponse.CheckImplementationsResponseEntry("PRO-17", responseUrl1, "Commit 11");
        final var responseEntry2 = new CheckImplementationsResponse.CheckImplementationsResponseEntry("PRO-12", responseUrl2, "Commit 132");
        final var response = new CheckImplementationsResponse(List.of(responseEntry1, responseEntry2));
        Mockito.when(checkImplementationCaller.checkForImplementations(request)).thenReturn(response);
        final var task = Mockito.mock(Task.class);
        Mockito.when(taskRepository.findByKey("PRO-17")).thenReturn(Optional.empty());
        Mockito.when(taskRepository.findByKey("PRO-12")).thenReturn(Optional.of(task));

        target.updateImplementations(request);

        Mockito.verify(task).addImplementation(responseUrl2, "Commit 132");
        Mockito.verify(taskRepository).save(task);
    }
}