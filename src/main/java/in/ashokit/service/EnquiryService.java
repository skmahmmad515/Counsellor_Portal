package in.ashokit.service;

import java.util.List;

import in.ashokit.dto.ViewEnqsFilterRequest;
import in.ashokit.entity.Enquiry;
import in.ashokit.exception.CounsellorException;

public interface EnquiryService {

	public boolean addEnquiry(Enquiry enq, Integer counsellorId) throws CounsellorException;

	public List<Enquiry> getAllEnquiries(Integer counsellorId)throws CounsellorException;

	public List<Enquiry> getEnquiresWithFilter(ViewEnqsFilterRequest filterReq, Integer counsellorId)throws CounsellorException;

	public Enquiry getEnquiryById(Integer enqId);

}
