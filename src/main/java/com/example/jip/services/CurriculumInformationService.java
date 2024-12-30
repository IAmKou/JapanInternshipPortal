package com.example.jip.services;

import com.example.jip.entity.Curriculum;
import com.example.jip.entity.CurriculumInformation;
import com.example.jip.repository.CurriculumInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurriculumInformationService {

    @Autowired
    private CurriculumInformationRepository curriculumInformationRepository;

    // Phương thức lưu CurriculumInformation
    public CurriculumInformation save(CurriculumInformation curriculumInformation) {
        return curriculumInformationRepository.save(curriculumInformation); // Lưu vào database
    }

    // Phương thức lấy CurriculumInformation theo id
    public Optional<CurriculumInformation> getCurriculumInformationById(int id) {
        return curriculumInformationRepository.findById(id); // Tìm đối tượng theo ID
    }

    // Phương thức để lấy thông tin CurriculumInformation theo Curriculum
    public CurriculumInformation getByCurriculum(Curriculum curriculum) {
        return curriculumInformationRepository.findByCurriculum(curriculum);
    }
}