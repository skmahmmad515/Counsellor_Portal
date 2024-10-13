package in.ashokit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import in.ashokit.dto.ViewEnqsFilterRequest;
import in.ashokit.entity.Counsellor;
import in.ashokit.entity.Enquiry;
import in.ashokit.exception.CounsellorException;
import in.ashokit.repos.CounsellorRepo;
import in.ashokit.repos.EnquiryRepo;
import io.micrometer.common.util.StringUtils;


@Service
public class EnquiryServiceImpl implements EnquiryService {

	@Autowired
	public EnquiryRepo enqRepo;
	
	@Autowired
	public CounsellorRepo counsellorRepo;
	
	public EnquiryServiceImpl(EnquiryRepo enqRepo,CounsellorRepo counsellorRepo)
	{
		this.enqRepo=enqRepo;
		this.counsellorRepo=counsellorRepo;
	}
	
	
	@Override
	public boolean addEnquiry(Enquiry enq, Integer counsellorId) throws CounsellorException {
		
		
		Counsellor counsellor = counsellorRepo.findById(counsellorId).orElse(null);
		if (counsellor == null) {
			throw new CounsellorException("No counsellor found");
		}
		
		
		enq.setCounsellor(counsellor);  	
		Enquiry save = enqRepo.save(enq); // save the given row of data to the enquiry
		return save.getEnqId() != null;
											
	
}

					
	@Override
	public List<Enquiry> getAllEnquiries(Integer counsellorId)throws CounsellorException {
		return enqRepo.getEnquriesByCounsellorId(counsellorId);         // get the record of the quiry based on the counsellorId
		 
	}

	
	@Override
	public Enquiry getEnquiryById(Integer enqId)  {
		return enqRepo.findById(enqId).orElse(null);
		
	}
	
	
	@Override  //implement the dynamic condition here ok because user may select or not select one of that ok they may select 1/2/3 filter 
	
	public List<Enquiry> getEnquiresWithFilter(ViewEnqsFilterRequest filterReq, Integer counsellorId)throws CounsellorException {

		// 	QBE implementation(Dynamic Query preparation)

	      Enquiry enq = new Enquiry();
	      
	      if(StringUtils.isNotEmpty(filterReq.getClassMode()))
	      {
	    	  enq.setClassMode(filterReq.getClassMode());
	      }
	      
	      if(StringUtils.isNotEmpty(filterReq.getCourseName()))
	      {
	    	  enq.setCourseName(filterReq.getCourseName());
	      }
	      
	      if(StringUtils.isNotEmpty(filterReq.getEnqStatus()))
	      {
	    	  enq.setEnqStatus(filterReq.getEnqStatus());
	      }
	      
	      Counsellor c=  counsellorRepo.findById(counsellorId).orElse(null);
	      
	      enq.setCounsellor(c);

	      Example<Enquiry> of = Example.of(enq);  //Example is used for dynamic query //The Example class is a part of Spring Data JPA and provides a way to perform searches by example
	      
	      return  enqRepo.findAll(of);
	      	 
	}

	

}