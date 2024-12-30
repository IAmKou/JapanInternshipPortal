package com.example.jip.services;

import com.example.jip.dto.CurriculumRequest;
import com.example.jip.entity.Curriculum;
import com.example.jip.entity.CurriculumInformation;
import com.example.jip.repository.CurriculumInformationRepository;
import com.example.jip.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private CurriculumInformationRepository curriculumInformationRepository;

    // Phương thức save
    public Curriculum save(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    // Hoặc dùng phương thức đã có createCurriculumWithInformation (nếu bạn cần cả curriculum và information)
    public Curriculum createCurriculumWithInformation(CurriculumRequest request) {
        // 1. Tạo đối tượng Curriculum từ CurriculumRequest
        Curriculum curriculum = new Curriculum();
        curriculum.setSubject(request.getSubject());
        curriculum.setTotal_slot(request.getTotalSlot());
        curriculum.setTotal_time(request.getTotalTime());

        // 2. Lưu Curriculum vào cơ sở dữ liệu
        curriculum = curriculumRepository.save(curriculum);

        // 3. Tạo CurriculumInformation, liên kết với Curriculum mới
        CurriculumInformation curriculumInformation = new CurriculumInformation();
        curriculumInformation.setCurriculum(curriculum); // Liên kết curriculum
        curriculumInformation.setDescription(request.getDescription()); // Gán thông tin mô tả

        // 4. Lưu CurriculumInformation vào cơ sở dữ liệu
        curriculumInformationRepository.save(curriculumInformation);

        // 5. Trả về curriculum vừa lưu
        return curriculum;
    }

    public List<Map<String, Object>> getAllCurriculums() {
        List<Curriculum> curriculums = curriculumRepository.findAll(); // Lấy tất cả curriculum từ database
        List<Map<String, Object>> result = new ArrayList<>();

        // Duyệt qua từng curriculum và gắn thông tin description từ CurriculumInformation
        for (Curriculum curriculum : curriculums) {
            Map<String, Object> curriculumData = new HashMap<>();
            curriculumData.put("id", curriculum.getId());
            curriculumData.put("subject", curriculum.getSubject());
            curriculumData.put("total_slot", curriculum.getTotal_slot());
            curriculumData.put("total_time", curriculum.getTotal_time());

            // Lấy mô tả từ CurriculumInformation
            CurriculumInformation curriculumInfo = curriculumInformationRepository.findByCurriculum(curriculum);
            if (curriculumInfo != null) {
                curriculumData.put("description", curriculumInfo.getDescription());
            } else {
                curriculumData.put("description", null);
            }

            result.add(curriculumData);
        }

        return result;
    }

}