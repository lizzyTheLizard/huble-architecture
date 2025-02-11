package site.gutschi.humble.spring.integration.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IndexTests {
    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "user")
    public void language() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html lang=\"en\">")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "SYSTEM_ADMIN")
    public void systemUser() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Create new Task")))
                .andExpect(content().string(containsString("Create new Project")));
    }

    @Test
    @WithMockUser(username = "user")
    public void user() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Create new Task"))))
                .andExpect(content().string(not(containsString("Create new Project"))));
    }

    @Test
    @WithMockUser(username = "user")
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
