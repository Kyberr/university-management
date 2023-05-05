package ua.com.foxminded.university.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import ua.com.foxminded.university.entity.GroupEntity;
import ua.com.foxminded.university.entitymother.GroupEntityMother;
import ua.com.foxminded.university.model.Authority;
import ua.com.foxminded.university.repository.GroupRepository;

class GroupControllerIntegrationTest extends DefaultControllerTest {
    
    public static final String GROUP_NAME = "kt-156";
    
    @Autowired
    private GroupRepository groupRepository;
    
    private GroupEntity groupEntity;
    
    @BeforeTransaction
    void setUp() {
        groupEntity = GroupEntityMother.complete().build();
        groupRepository.saveAndFlush(groupEntity);
    }
    
    @AfterTransaction
    void tearDown() {
        groupRepository.deleteAll();
    }
    
    @Test
    @WithUserDetails(AUTHORIZED_EMAIL)
    void delete_ShouldAuthenticateCredantialsAndRedirect() throws Exception {
        mockMvc.perform(post("/groups/{groupId}/delete", groupEntity.getId()).with(csrf()))
               .andExpect(authenticated().withRoles(Authority.ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(AUTHORIZED_EMAIL)
    void create_ShouldAuthenticateCredantialsAndRedirect() throws Exception {
        mockMvc.perform(post("/groups/create").param("name", GROUP_NAME)
                                              .with(csrf()))
               .andExpect(authenticated().withRoles(Authority.ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(AUTHORIZED_EMAIL)
    void update() throws Exception {
        mockMvc.perform(post("/groups/{groupId}/update", groupEntity.getId())
                    .param("name", GROUP_NAME)
                    .with(csrf()))
        .andExpect(authenticated().withRoles(Authority.ADMIN.toString()))
        .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(AUTHORIZED_EMAIL)
    void getById_ShouldAuthoriseCredentialsAndReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/groups/{groupId}", groupEntity.getId()))
               .andExpect(authenticated().withRoles(Authority.ADMIN.toString()))
               .andExpect(status().isOk());
    }
    
    @Test
    @WithUserDetails(AUTHORIZED_EMAIL)
    void getAllGroups_ShouldAuthenticateCredentialsAndReternStatusIsOk() throws Exception {
        mockMvc.perform(get("/groups/list"))
               .andExpect(authenticated().withRoles(Authority.ADMIN.toString()))
               .andExpect(status().isOk());
    }
}