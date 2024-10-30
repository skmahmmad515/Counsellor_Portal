

package in.ashokit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import in.ashokit.dto.ViewEnqsFilterRequest;
import in.ashokit.entity.Counsellor;
import in.ashokit.entity.Enquiry;
import in.ashokit.exception.CounsellorException;
import in.ashokit.repos.CounsellorRepo;
import in.ashokit.repos.EnquiryRepo;

@ExtendWith(MockitoExtension.class)
	 class EnquiryServiceTest {
		
		private Counsellor counsellor; // Test instance of Counsellor
	    private Enquiry enquiry;
	    @InjectMocks
	    private EnquiryServiceImpl enquiryService; // The service to test

	    @Mock
	    private EnquiryRepo enqRepo; // The mock enquiry repository

	    @Mock
	    private CounsellorRepo counsellorRepo; // The mock counsellor repository

	    @BeforeEach
	     void setUp() {
	        // Initialize a Counsellor and an Enquiry before each test
	        counsellor = new Counsellor();
	        counsellor.setCounsellorId(1); // Assuming an ID of 1 for the counsellor

	        enquiry = new Enquiry();
	        enquiry.setEnqId(1); // Assuming an ID of 1 for the enquiry
	    }


	    @Test
	    void testAddEnquiry_Success() throws CounsellorException {
	        // Arrange
	        Integer counsellorId = 1;
	        when(counsellorRepo.findById(counsellorId)).thenReturn(Optional.of(counsellor)); // Mock found counsellor
	        when(enqRepo.save(any(Enquiry.class))).thenReturn(enquiry); // Mock save return value

	        // Act
	        boolean result = enquiryService.addEnquiry(enquiry, counsellorId); // Call the method under test

	        // Assert
	        assertTrue(result, "Enquiry should be added successfully");
	        verify(counsellorRepo).findById(counsellorId); // Verify that the repository method was called
	        verify(enqRepo).save(enquiry); // Verify that the save method was called
	    }

	    @Test
	   void testAddEnquiry_CounsellorNotFound() {
	        // Arrange
	        Enquiry enquiry = new Enquiry();
	        Integer counsellorId = 1;

	        // Mock the behaviour of the repository
	        when(counsellorRepo.findById(counsellorId)).thenReturn(Optional.empty());

	        // Act & Assert
	        assertThrows(CounsellorException.class, () -> {
	            enquiryService.addEnquiry(enquiry, counsellorId);
	        }, "Should throw CounsellorException when counsellor not found");
	    }

	    @Test
	    void testGetAllEnquiries() throws CounsellorException {
	        // Arrange
	        Integer counsellorId = 1;
	        List<Enquiry> mockEnquiries = new ArrayList<>();
	        mockEnquiries.add(new Enquiry());

	        // Mock the repository behaviour
	        when(enqRepo.getEnquriesByCounsellorId(counsellorId)).thenReturn(mockEnquiries);

	        // Act
	        List<Enquiry> enquiries = enquiryService.getAllEnquiries(counsellorId);

	        // Assert
	        assertNotNull(enquiries);
	        assertEquals(1, enquiries.size(), "Should return a list of enquiries");
	    }

	    @Test
	     void testGetEnquiryById() {
	        // Arrange
	        Integer enqId = 1;
	        Enquiry mockEnquiry = new Enquiry();
	        mockEnquiry.setEnqId(enqId);

	        // Mock the repository behaviour
	        when(enqRepo.findById(enqId)).thenReturn(Optional.of(mockEnquiry));

	        // Act
	        Enquiry enquiry = enquiryService.getEnquiryById(enqId);

	        // Assert
	        assertNotNull(enquiry);
	        assertEquals(enqId, enquiry.getEnqId(), "Should return the enquiry with the specified ID");
	    }

	    @Test
	     void testGetEnquiryById_NotFound() {
	        // Arrange
	        Integer enqId = 2; // Non-existent enquiry ID
	        when(enqRepo.findById(enqId)).thenReturn(Optional.empty()); // Mock not found

	        // Act
	        Enquiry result = enquiryService.getEnquiryById(enqId);

	        // Assert
	        assertNull(result, "Enquiry should be null if not found");
	        verify(enqRepo).findById(enqId); // Verify that the repository method was called
	    }
	    
	    @SuppressWarnings("unchecked")
		@Test
	     void testGetEnquiresWithFilter_Success() throws CounsellorException {
	        // Arrange
	        Integer counsellorId = 1;
	        ViewEnqsFilterRequest filterReq = new ViewEnqsFilterRequest();
	        filterReq.setClassMode("Online");
	        filterReq.setCourseName("Math");
	        filterReq.setEnqStatus("New");

	        // Create a mock enquiry that should match the filter
	        Enquiry mockEnquiry = new Enquiry();
	        mockEnquiry.setClassMode("Online");
	        mockEnquiry.setCourseName("Math");
	        mockEnquiry.setEnqStatus("New");
	        mockEnquiry.setCounsellor(counsellor);  // Set the counsellor

	        List<Enquiry> mockEnquiries = new ArrayList<>();
	        mockEnquiries.add(mockEnquiry);

	        // Mock counsellorRepo to return a counsellor for the given ID
	        when(counsellorRepo.findById(counsellorId)).thenReturn(Optional.of(counsellor));

	        // Mock enqRepo to return the mockEnquiries list when called with any Example<Enquiry>
	        when(enqRepo.findAll(any(Example.class))).thenReturn(mockEnquiries);

	        // Act
	        List<Enquiry> enquiries = enquiryService.getEnquiresWithFilter(filterReq, counsellorId);

	        // Assert
	        assertNotNull(enquiries, "Enquiries list should not be null");
	        assertEquals(1, enquiries.size(), "Enquiries list should contain one enquiry");
	        assertEquals("Online", enquiries.get(0).getClassMode(), "Class mode should match the filter");
	        assertEquals("Math", enquiries.get(0).getCourseName(), "Course name should match the filter");
	        assertEquals("New", enquiries.get(0).getEnqStatus(), "Status should match the filter");

	        // Verify that the expected interactions occurred
	        verify(counsellorRepo).findById(counsellorId);
	        verify(enqRepo).findAll(any(Example.class));
	    }
	    @SuppressWarnings("unchecked")
        @ParameterizedTest
        @CsvSource({
            "Online",
            "Math",
            "New"
        })
        void testGetEnquiresWithFilter_NoEnquiriesFound(String classMode) throws CounsellorException {
            // Arrange
            Integer counsellorId = 1;
            ViewEnqsFilterRequest filterReq = new ViewEnqsFilterRequest();
            filterReq.setClassMode(classMode);

            when(counsellorRepo.findById(counsellorId)).thenReturn(Optional.of(counsellor));
            when(enqRepo.findAll(any(Example.class))).thenReturn(new ArrayList<>());

            // Act
            List<Enquiry> enquiries = enquiryService.getEnquiresWithFilter(filterReq, counsellorId);

            // Assert
            assertNotNull(enquiries, "Enquiries list should not be null");
            assertTrue(enquiries.isEmpty(), "Enquiries list should be empty");
            verify(counsellorRepo).findById(counsellorId);
            verify(enqRepo).findAll(any(Example.class));
        }
    }
	   
	    
	    
	    

