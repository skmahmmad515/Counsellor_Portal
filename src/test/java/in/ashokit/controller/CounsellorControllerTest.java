
package in.ashokit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import in.ashokit.constants.AppConstants;
import in.ashokit.dto.DashboardResponse;
import in.ashokit.entity.Counsellor;
import in.ashokit.service.CounsellorService;

@SpringBootTest
@AutoConfigureMockMvc
class CounsellorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CounsellorService counsellorService;

	@Test
	void testIndex() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attributeExists("counsellor"));
	}

	@Test
	void testHandleLoginBtn_ValidCredentials() throws Exception {
		Counsellor counsellor = new Counsellor();
		counsellor.setEmail("test@example.com");
		counsellor.setPwd("password");
		counsellor.setCounsellorId(1);

		when(counsellorService.login("test@example.com", "password")).thenReturn(counsellor);

		DashboardResponse dashboardResponse = new DashboardResponse();
		when(counsellorService.getDashboardInfo(1)).thenReturn(dashboardResponse);

		mockMvc.perform(post("/login").param("email", "test@example.com").param("pwd", "password"))
				.andExpect(status().isOk()).andExpect(view().name("dashboard"))
				.andExpect(model().attributeExists("dashboardInfo"));
	}

	@Test
	void testHandleLoginBtn_InvalidCredentials() throws Exception {
		when(counsellorService.login("wrong@example.com", "wrongpassword")).thenReturn(null);

		mockMvc.perform(post("/login").param("email", "wrong@example.com").param("pwd", "wrongpassword"))
				.andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attribute("emsg", "Invalid Credentials"));
	}

	@Test
	void testDisplayDashboard() throws Exception {
		Counsellor counsellor = new Counsellor();
		counsellor.setCounsellorId(1);

		DashboardResponse dashboardResponse = new DashboardResponse();
		when(counsellorService.getDashboardInfo(1)).thenReturn(dashboardResponse);

		mockMvc.perform(get("/dashboard").sessionAttr("counsellorId", 1)).andExpect(status().isOk())
				.andExpect(view().name("dashboard")).andExpect(model().attributeExists("dashboardInfo"));
	}

	@Test
	void testRegisterPage() throws Exception {
		mockMvc.perform(get("/register")).andExpect(status().isOk()).andExpect(view().name(AppConstants.REGISTER))
				.andExpect(model().attributeExists("counsellor"));
	}

	@Test
	void testHandleRegistration_DuplicateEmail() throws Exception {
		Counsellor counsellor = new Counsellor();
		counsellor.setEmail("duplicate@example.com");

		when(counsellorService.findByEmail("duplicate@example.com")).thenReturn(counsellor);

		mockMvc.perform(post("/register").param("email", "duplicate@example.com")).andExpect(status().isOk())
				.andExpect(view().name("register")).andExpect(model().attribute("emsg", "Duplicate Email"));
	}

	@Test
	void testHandleRegistration_Success() throws Exception {
		// Mock a unique counsellor email
		Counsellor counsellor = new Counsellor();
		counsellor.setEmail("unique@example.com");

		when(counsellorService.findByEmail(counsellor.getEmail())).thenReturn(null);
		when(counsellorService.register(any(Counsellor.class))).thenReturn(true);

		mockMvc.perform(post("/register").flashAttr("counsellor", counsellor)).andExpect(status().isOk())
				.andExpect(view().name("register")).andExpect(model().attribute("smsg", "Registration Success..!!"));
	}

	@Test
	void testHandleRegistration_Failure() throws Exception {
		// Arrange
		Counsellor counsellor = new Counsellor();
		counsellor.setEmail("newuser@example.com");

		when(counsellorService.findByEmail(counsellor.getEmail())).thenReturn(null); // No duplicate
		when(counsellorService.register(any(Counsellor.class))).thenReturn(false); // Registration failed

		// Act & Assert
		mockMvc.perform(post("/register").flashAttr("counsellor", counsellor)).andExpect(status().isOk())
				.andExpect(view().name("register")).andExpect(model().attribute("emsg", "Registration Failed"));
	}

	@Test
	void testLogout() throws Exception {
		mockMvc.perform(get("/logout").sessionAttr("counsellorId", 1)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}
}
