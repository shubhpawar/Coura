package com.coura.controllertest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.coura.app.SignupController;
import com.coura.model.Users;
import com.coura.service.UsersService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
 
@ContextConfiguration(locations = "classpath:Coura-servlet-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SignupControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
	@InjectMocks
	private SignupController signupController;
	
    @Mock
    private UsersService userService;
 
    @Before
    public void setUp() {
        
    	MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(signupController).build();
    }
    
    @Test
    public void testGetUsers() throws Exception {
    
    	Users user1 = new Users("user1@uncc.edu","Mark","Zuck","password1");
    	Users user2 = new Users("user2@uncc.edu","Sofia","Peters","password2");
    	
    	List<Users> list = new ArrayList<Users>();
    	list.add(user1);
    	list.add(user2);
    	
    	when(userService.listAllUsers()).thenReturn(list);
    	 
        mockMvc.perform(get("/signupservices/getusers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].emailId", is("user1@uncc.edu")))
        .andExpect(jsonPath("$[0].firstName", is("Mark")))
        .andExpect(jsonPath("$[0].lastName", is("Zuck")))
        .andExpect(jsonPath("$[0].password", is("password1")))
        .andExpect(jsonPath("$[1].emailId", is("user2@uncc.edu")))
        .andExpect(jsonPath("$[1].firstName", is("Sofia")))
        .andExpect(jsonPath("$[1].lastName", is("Peters")))
        .andExpect(jsonPath("$[1].password", is("password2")));
        
        verify(userService, times(1)).listAllUsers();
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testSaveUser() throws Exception {
    	
    	Users user = new Users("user1@uncc.edu","Mark","Zuck","password1");
    	
    	when(userService.saveUsers(user)).thenReturn(true);
    	
    	mockMvc.perform(post("/signupservices/saveuser").contentType(MediaType.APPLICATION_JSON).content(mapToJson(user)))
        .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
        
        verify(userService, times(1)).saveUsers(user);
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testDeleteUser() throws Exception {
        
    	doNothing().when(userService);
    	
        mockMvc.perform(get("/signupservices/deleteuser/{emailId}", "user@uncc.edu"))
        .andExpect(status().isOk());
        
        verify(userService, times(1)).deleteUser("user@uncc.edu");
        verifyNoMoreInteractions(userService);
    }
    
    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }
}