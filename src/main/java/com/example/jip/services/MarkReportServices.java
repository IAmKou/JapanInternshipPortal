package com.example.jip.services;

import com.example.jip.dto.request.markReport.MarkReportImportRequest;
import com.example.jip.dto.response.markReport.MarkReportResponse;
import com.example.jip.entity.MarkReport;
import com.example.jip.entity.Student;
import com.example.jip.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkReportServices {

    MarkReportRepository markReportRepository;
    StudentRepository studentRepository;
    AssignmentStudentRepository assignmentStudentRepository;
    StudentAssignmentRepository studentAssignmentRepository;
    AttendantRepository attendantRepository;

    public List<MarkReportResponse> getListMarkReport(int classId) {
        List<MarkReport> results =  markReportRepository.findAllByClassId(classId);
        return results.stream()
                .map(markReport -> {
                    MarkReportResponse response = new MarkReportResponse();
                    response.setId(markReport.getId());
                    response.setStudentName(markReport.getStudent().getFullname());
                    response.setSoftskill(markReport.getSoftskill());
                    response.setAvg_exam_mark(markReport.getAvg_exam_mark());
                    response.setMiddle_exam(markReport.getMiddle_exam());
                    response.setFinal_exam(markReport.getFinal_exam());
                    response.setSkill(markReport.getSkill());
                    response.setAttitude(markReport.getAttitude());
                    response.setFinal_mark(markReport.getFinal_mark());
                    return response;
                })
                .collect(Collectors.toList());
    }


    public List<MarkReportImportRequest> parseExcel(MultipartFile file) throws IOException {
        List<MarkReportImportRequest> markReports = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false; // Skip header
                    continue;
                }
                String name = row.getCell(0).getStringCellValue().trim();
                String email = row.getCell(1).getStringCellValue().trim();
                BigDecimal softSkill = new BigDecimal(row.getCell(2).getNumericCellValue());
                BigDecimal avgExamMark = new BigDecimal(row.getCell(3).getNumericCellValue());
                BigDecimal middleExam = new BigDecimal(row.getCell(4).getNumericCellValue());
                BigDecimal finalExam = new BigDecimal(row.getCell(5).getNumericCellValue());
                String comment = row.getCell(6).getStringCellValue().trim();

                markReports.add(new MarkReportImportRequest(name, email, softSkill, avgExamMark, middleExam, finalExam, comment));
            }
        }
        return markReports;
    }

    @Transactional
    public void saveMarkReports(List<MarkReportImportRequest> markReports) {
        List<MarkReport> entities = markReports.stream()
                .map(request -> {
                    // Find the student by name and email
                    Student student = studentRepository.findByFullnameAndEmail(request.getName(), request.getEmail())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Student not found for name: " + request.getName() + ", email: " + request.getEmail()));

//                    Skill Calculation:
//                    skill = (avg_exam_mark + middle_exam + final_exam) / 3.
//
//                    Attitude Calculation:
//                    attitude = ((completed_assignments / total_assignments) + (attended_slots / total_slots)) / 2.
//
//                    Final Mark Calculation:
//                    final_mark = (soft_skill * 0.3) + (skill * 0.4) + (attitude * 0.3).

                    // Calculate skill
                    BigDecimal avgExamMark = request.getAvg_exam_mark();
                    BigDecimal middleExam = request.getMiddle_exam();
                    BigDecimal finalExam = request.getFinal_exam();
                    BigDecimal skill = avgExamMark.add(middleExam).add(finalExam).divide(new BigDecimal(3), RoundingMode.HALF_UP);

                    // Calculate attitude
                    int totalAssignments = assignmentStudentRepository.countByStudentId(student.getId());
                    int submittedAssignments = studentAssignmentRepository.countByStudentId(student.getId());
                    BigDecimal assignmentCompletion = new BigDecimal(submittedAssignments)
                            .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);


                    int totalSlots = attendantRepository.findTotalSlotByStudentId(student.getId());
                    int attendedSlots = attendantRepository.countAttendedByStudentId(student);
                    BigDecimal attendance = new BigDecimal(attendedSlots)
                            .divide(new BigDecimal(totalSlots), 2, RoundingMode.HALF_UP);

                    BigDecimal attitude = assignmentCompletion.add(attendance).divide(new BigDecimal(2), RoundingMode.HALF_UP);

                    // Calculate final mark
                    BigDecimal softSkillWeight = request.getSoftskill().multiply(new BigDecimal("0.3"));
                    BigDecimal skillWeight = skill.multiply(new BigDecimal("0.4"));
                    BigDecimal attitudeWeight = attitude.multiply(new BigDecimal("0.3"));
                    BigDecimal finalMark = softSkillWeight.add(skillWeight).add(attitudeWeight);

                    // Create the MarkReport
                    MarkReport markReport = new MarkReport();
                    markReport.setStudent(student);
                    markReport.setSoftskill(request.getSoftskill());
                    markReport.setAvg_exam_mark(request.getAvg_exam_mark());
                    markReport.setMiddle_exam(request.getMiddle_exam());
                    markReport.setFinal_exam(request.getFinal_exam());
                    markReport.setSkill(skill);
                    markReport.setAttitude(attitude);
                    markReport.setFinal_mark(finalMark);
                    markReport.setComment(request.getComment());
                    return markReport;
                })
                .collect(Collectors.toList());

        markReportRepository.saveAll(entities);
    }



}
