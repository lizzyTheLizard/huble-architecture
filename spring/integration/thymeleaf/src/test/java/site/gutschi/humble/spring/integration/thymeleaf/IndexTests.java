package site.gutschi.humble.spring.integration.thymeleaf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import site.gutschi.humble.spring.common.api.CurrentUserApi;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IndexTests {
    @MockitoBean
    CurrentUserApi currentUserApi;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
    }

    @Test
    public void language() throws Exception {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(false);
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html lang=\"en\">")));
    }

    @Test
    public void systemUser() throws Exception {
        Mockito.when(currentUserApi.isSystemAdmin()).thenReturn(true);
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create new Task")))
                .andExpect(content().string(containsString("Create new Project")));
    }

    @Test
    public void user() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Create new Task"))))
                .andExpect(content().string(not(containsString("Create new Project"))));
    }

    @Test
    public void testCurrentUrl() throws Exception {
        mvc.perform(get("/tasks/create"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("class=\"nav-link active\" href=\"/projects\""))));
        mvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("class=\"nav-link active active\" href=\"/projects\""))));
        mvc.perform(get("/projects?test=1"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("class=\"nav-link active active\" href=\"/projects\""))));
    }
}
