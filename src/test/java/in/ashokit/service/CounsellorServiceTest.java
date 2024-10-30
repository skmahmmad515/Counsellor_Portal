
package in.ashokit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import in.ashokit.dto.DashboardResponse;
import in.ashokit.entity.Counsellor;
import in.ashokit.entity.Enquiry;
import in.ashokit.repos.CounsellorRepo;
import in.ashokit.repos.EnquiryRepo;

	 class CounsellorServiceTest {

	    @Mock
	    private CounsellorRepo counsellorRepo;

	    @Mock
	    private EnquiryRepo enqRepo;

	    @InjectMocks
	    private CounsellorServiceImpl counsellorService;

	    private Counsellor mockCounsellor;
	    private List<Enquiry> mockEnquiryList;

	    @BeforeEach
	     void setUp() {
	        MockitoAnnotations.openMocks(this);

	        // Set up a mock Counsellor object
	        mockCounsellor = new Counsellor();
	        mockCounsellor.setCounsellorId(1);
	        mockCounsellor.setEmail("test@example.com");
	       // mockCounsellor.setPassword("password123");

	        // Set up a mock Enquiry list
	        Enquiry enquiry1 = new Enquiry();
	        enquiry1.setEnqStatus("Enrolled");

	        Enquiry enquiry2 = new Enquiry();
	        enquiry2.setEnqStatus("Lost");

	        Enquiry enquiry3 = new Enquiry();
	        enquiry3.setEnqStatus("Open");

	        mockEnquiryList = List.of(enquiry1, enquiry2, enquiry3);
	    }

	    @Test
	     void testRegister_Success() {
	        // Arrange
	        Counsellor counsellor = new Counsellor();
	        counsellor.setCounsellorId(1); // Simulate an ID being assigned upon save
	        when(counsellorRepo.save(counsellor)).thenReturn(counsellor); // Mock the repository save

	        // Act
	        boolean result = counsellorService.register(counsellor);

	        // Assert
	        assertTrue(result, "Counsellor should be registered successfully");
	    }
	    
	    
	    @Test
	    void testRegister_Failure() {
	        // Arrange
	        Counsellor counsellor = new Counsellor();
	        // No ID assigned, simulating failure in saving
	        when(counsellorRepo.save(counsellor)).thenReturn(new Counsellor()); // Mock the repository to return a new Counsellor without ID

	        // Act
	        boolean result = counsellorService.register(counsellor);

	        // Assert
	        assertFalse(result, "Counsellor registration should fail");
	    }

	    @Test
	    void testLogin_Successful() {
	        when(counsellorRepo.findByEmailAndPwd("test@example.com", "password123")).thenReturn(mockCounsellor);

	        Counsellor result = counsellorService.login("test@example.com", "password123");

	        assertNotNull(result);
	        assertEquals("test@example.com", result.getEmail());
	        verify(counsellorRepo, times(1)).findByEmailAndPwd("test@example.com", "password123");
	    }

	    @Test
	     void testLogin_InvalidCredentials() {
	        when(counsellorRepo.findByEmailAndPwd("test@example.com", "wrongPassword")).thenReturn(null);

	        Counsellor result = counsellorService.login("test@example.com", "wrongPassword");

	        assertNull(result);
	        verify(counsellorRepo, times(1)).findByEmailAndPwd("test@example.com", "wrongPassword");
	    }

	    @Test
	     void testGetDashboardInfo() {
	        when(enqRepo.getEnquriesByCounsellorId(1)).thenReturn(mockEnquiryList);

	        DashboardResponse response = counsellorService.getDashboardInfo(1);

	        assertNotNull(response);
	        assertEquals(3, response.getTotalEnqs());
	        assertEquals(1, response.getEnrolledEnqs());
	        assertEquals(1, response.getLostEnqs());
	        assertEquals(1, response.getOpenEnqs());

	        verify(enqRepo, times(1)).getEnquriesByCounsellorId(1);
	    }

	    @Test
	    void testFindByEmail() {
	        when(counsellorRepo.findByEmail("test@example.com")).thenReturn(mockCounsellor);

	        Counsellor result = counsellorService.findByEmail("test@example.com");

	        assertNotNull(result);
	        assertEquals("test@example.com", result.getEmail());
	        verify(counsellorRepo, times(1)).findByEmail("test@example.com");
	    }
	}


