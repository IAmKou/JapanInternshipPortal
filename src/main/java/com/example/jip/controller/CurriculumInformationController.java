    package com.example.jip.controller;

    import com.example.jip.entity.CurriculumInformation;
    import com.example.jip.services.CurriculumInformationService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.Optional;

    @RestController
    public class CurriculumInformationController {

        @Autowired
        private CurriculumInformationService curriculumInformationService;

        // Lấy Curriculum Information theo ID
        @GetMapping("/curriculum-information/{id}")
        public ResponseEntity<Object> getCurriculumInformation(@PathVariable int id) {
            Optional<CurriculumInformation> curriculumInformation = curriculumInformationService.getCurriculumInformationById(id);

            // Nếu tìm thấy thông tin CurriculumInformation
            if (curriculumInformation.isPresent()) {
                return ResponseEntity.ok(curriculumInformation.get());
            } else {
                // Nếu không tìm thấy thông tin
                return ResponseEntity.status(404).body("Curriculum Information not found");
            }
        }
    }