package in.ashokit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.ashokit.dto.DashboardResponse;
import in.ashokit.entity.Counsellor;
import in.ashokit.entity.Enquiry;
import in.ashokit.repos.CounsellorRepo;
import in.ashokit.repos.EnquiryRepo;


@Service
public class CounsellorServiceImpl implements CounsellorService {

	
	@Autowired
	private CounsellorRepo counsellorRepo;
	
	@Autowired
	private EnquiryRepo enqRepo;
	
	
	@Override
	public boolean register(Counsellor counsellor) {
		Counsellor savedCounsellor = counsellorRepo.save(counsellor);
		return null != savedCounsellor.getCounsellorId();
		
	}

	@Override
	public Counsellor login(String email, String pwd) {

		return counsellorRepo.findByEmailAndPwd(email, pwd);

	}

	
	
	@Override
	public DashboardResponse getDashboardInfo(Integer counsellorId) {
		
		// here important that we are only one time we accessing the database and process the data using java 8 stream api
		DashboardResponse response = new DashboardResponse();
		List<Enquiry> enqList = enqRepo.getEnquriesByCounsellorId(counsellorId);
		int totalEnq = enqList.size();

		int enrolledEnqs = enqList.stream().filter(e -> e.getEnqStatus().equals("Enrolled"))
				.toList().size();
		int lostEnqs = enqList.stream().filter(e -> e.getEnqStatus().equals("Lost")).toList()
				.size();
		int openEnqs = enqList.stream().filter(e -> e.getEnqStatus().equals("Open")).toList()
				.size();

		response.setTotalEnqs(totalEnq);
		response.setEnrolledEnqs(enrolledEnqs);
		response.setLostEnqs(lostEnqs);
		response.setOpenEnqs(openEnqs);
		
		return response;
	}

	@Override
	public Counsellor findByEmail(String email) 
	{		
		return counsellorRepo.findByEmail(email);
	}

}
