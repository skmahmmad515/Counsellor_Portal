
package in.ashokit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import in.ashokit.constants.AppConstants;
import in.ashokit.dto.ViewEnqsFilterRequest;
import in.ashokit.entity.Enquiry;
import in.ashokit.service.EnquiryService;

@SpringBootTest
@AutoConfigureMockMvc
class EnquiryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EnquiryService enqService;

	@Test
	void testFilterEnquiries() throws Exception {
		ViewEnqsFilterRequest filterRequest = new ViewEnqsFilterRequest();
		Integer counsellorId = 1;
		List<Enquiry> enquiries = new ArrayList<>();

		when(enqService.getEnquiresWithFilter(any(ViewEnqsFilterRequest.class), eq(counsellorId)))
				.thenReturn(enquiries);

		mockMvc.perform(post("/filter-enqs").sessionAttr("counsellorId", counsellorId)).andExpect(status().isOk())
				.andExpect(view().name("viewEnqsPage")).andExpect(model().attribute("enquiries", enquiries));
	}

	@Test
	void testGetEnquiries() throws Exception {
		Integer counsellorId = 1;
		List<Enquiry> enquiries = new ArrayList<>();

		when(enqService.getAllEnquiries(counsellorId)).thenReturn(enquiries);

		mockMvc.perform(get("/view-enquiries").sessionAttr("counsellorId", counsellorId)).andExpect(status().isOk())
				.andExpect(view().name("viewEnqsPage")).andExpect(model().attribute("enquiries", enquiries))
				.andExpect(model().attributeExists("viewEnqsFilterRequest"));
	}

	@Test
	void testAddEnquiryPage() throws Exception {
		mockMvc.perform(get("/enquiry")).andExpect(status().isOk()).andExpect(view().name(AppConstants.ENQUIRY))
				.andExpect(model().attributeExists(AppConstants.ENQUIRY_ATTRIBUTE));
	}

	@Test
	 void testEditEnquiry() throws Exception {
		Integer enqId = 1;
		Enquiry enquiry = new Enquiry();

		when(enqService.getEnquiryById(enqId)).thenReturn(enquiry);

		mockMvc.perform(get("/editEnq").param("enqId", enqId.toString())).andExpect(status().isOk())
				.andExpect(view().name(AppConstants.ENQUIRY))
				.andExpect(model().attribute(AppConstants.ENQUIRY_ATTRIBUTE, enquiry));
	}

	@Test
	void testHandleAddEnquiry_Success() throws Exception {
		Integer counsellorId = 1;
		Enquiry enquiry = new Enquiry();

		when(enqService.addEnquiry(any(Enquiry.class), eq(counsellorId))).thenReturn(true);

		mockMvc.perform(post("/addEnq").sessionAttr("counsellorId", counsellorId).flashAttr("enquiry", enquiry)) // Pass
																													// enquiry
																													// object
																													// as
																													// flash
																													// attribute
				.andExpect(status().isOk()).andExpect(view().name(AppConstants.ENQUIRY))
				.andExpect(model().attribute("smsg", "Enquiry Added"))
				.andExpect(model().attributeExists(AppConstants.ENQUIRY_ATTRIBUTE));
	}

	@Test
	void testHandleAddEnquiry_Failure() throws Exception {
		Integer counsellorId = 1;
		Enquiry enquiry = new Enquiry();

		when(enqService.addEnquiry(enquiry, counsellorId)).thenReturn(false);

		mockMvc.perform(post("/addEnq").sessionAttr("counsellorId", counsellorId)).andExpect(status().isOk())
				.andExpect(view().name(AppConstants.ENQUIRY))
				.andExpect(model().attribute("emsg", "Failed To Add Enquiry"))
				.andExpect(model().attributeExists(AppConstants.ENQUIRY_ATTRIBUTE));
	}
}
