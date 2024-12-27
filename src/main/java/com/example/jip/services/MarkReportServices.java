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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
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
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            int rowNumber = 0;

            for (Row row : sheet) {
                rowNumber++;
                if (isFirstRow) {
                    isFirstRow = false; // Skip header
                    continue;
                }

                try {
                    String name = getStringCellValue(row.getCell(0), rowNumber, "Name", errors);
                    String email = getStringCellValue(row.getCell(1), rowNumber, "Email", errors);
                    BigDecimal softSkill = getNumericCellValue(row.getCell(2), rowNumber, "Soft Skill", errors);
                    BigDecimal avgExamMark = getNumericCellValue(row.getCell(3), rowNumber, "Avg Exam Mark", errors);
                    BigDecimal middleExam = getNumericCellValue(row.getCell(4), rowNumber, "Middle Exam", errors);
                    BigDecimal finalExam = getNumericCellValue(row.getCell(5), rowNumber, "Final Exam", errors);
                    String comment = getStringCellValue(row.getCell(6), rowNumber, "Comment", errors);

                    // Validate data
                    if (name.isEmpty() || email.isEmpty()) {
                        errors.add("Row " + rowNumber + ": Name and Email are required.");
                        continue;
                    }

                    Student studentExisted = studentRepository.findByFullnameAndEmail(name, email);
                    if (studentExisted == null) {
                        errors.add("Row " + rowNumber + ": Student not found.");
                        continue;
                    }

                    if (softSkill.compareTo(BigDecimal.ZERO) < 0 || softSkill.compareTo(BigDecimal.TEN) > 0) {
                        errors.add("Row " + rowNumber + ": Soft Skill must be between 0 and 10.");
                        continue;
                    }

                    if (avgExamMark.compareTo(BigDecimal.ZERO) < 0 || avgExamMark.compareTo(BigDecimal.TEN) > 0) {
                        errors.add("Row " + rowNumber + ": Avg Exam mark must be between 0 and 10.");
                        continue;
                    }

                    if (middleExam.compareTo(BigDecimal.ZERO) < 0 || middleExam.compareTo(BigDecimal.TEN) > 0) {
                        errors.add("Row " + rowNumber + ": Middle Exam mark must be between 0 and 10.");
                        continue;
                    }

                    if (finalExam.compareTo(BigDecimal.ZERO) < 0 || finalExam.compareTo(BigDecimal.TEN) > 0) {
                        errors.add("Row " + rowNumber + ": Final Exam mark must be between 0 and 10.");
                        continue;
                    }

                    if (comment.isEmpty()) {
                        errors.add("Row " + rowNumber + ": Comment can't be empty.");
                        continue;
                    }

                    markReports.add(new MarkReportImportRequest(name, email, softSkill, avgExamMark, middleExam, finalExam, comment));
                } catch (Exception e) {
                    errors.add("Row " + rowNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            throw new IOException("Validation errors: " + String.join(", ", errors));
        }

        return markReports;
    }

    private String getStringCellValue(Cell cell, int rowNumber, String columnName, List<String> errors) {
        if (cell == null) {
            errors.add("Row " + rowNumber + ": " + columnName + " is missing.");
            return "";
        }
        try {
            return cell.getStringCellValue().trim();
        } catch (Exception e) {
            errors.add("Row " + rowNumber + ": " + columnName + " contains invalid data.");
            return "";
        }
    }

    private BigDecimal getNumericCellValue(Cell cell, int rowNumber, String columnName, List<String> errors) {
        if (cell == null) {
            errors.add("Row " + rowNumber + ": " + columnName + " is missing.");
            return BigDecimal.ZERO;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return new BigDecimal(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
            } else if (cell.getCellType() == CellType.STRING) {
                return new BigDecimal(cell.getStringCellValue().trim()).setScale(2, RoundingMode.HALF_UP);
            } else {
                errors.add("Row " + rowNumber + ": " + columnName + " must be a numeric value.");
                return BigDecimal.ZERO;
            }
        } catch (NumberFormatException e) {
            errors.add("Row " + rowNumber + ": " + columnName + " contains invalid numeric value.");
            return BigDecimal.ZERO;
        }
    }

    @Transactional
    public void saveMarkReports(List<MarkReportImportRequest> markReports) {
        List<MarkReport> entities = markReports.stream()
                .map(request -> {
                    // Find the student by name and email
                    Student student = studentRepository.findByFullnameAndEmail(request.getName(), request.getEmail());

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

//                    // Calculate attitude
//                    int totalAssignments = assignmentStudentRepository.countByStudentId(student.getId());
//                    int submittedAssignments = studentAssignmentRepository.countByStudentId(student.getId());
//                    BigDecimal assignmentCompletion = new BigDecimal(submittedAssignments)
//                            .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);


//                    int totalSlots = attendantRepository.findTotalSlotByStudentId(student.getId());
//                    int attendedSlots = attendantRepository.countAttendedByStudentId(student);
//                    BigDecimal attendance = new BigDecimal(attendedSlots)
//                            .divide(new BigDecimal(totalSlots), 2, RoundingMode.HALF_UP);
//
//                    BigDecimal attitude = assignmentCompletion.add(attendance).divide(new BigDecimal(2), RoundingMode.HALF_UP);

                    // Calculate final mark
//                    BigDecimal softSkillWeight = request.getSoftskill().multiply(new BigDecimal("0.3"));
//                    BigDecimal skillWeight = skill.multiply(new BigDecimal("0.4"));
//                    BigDecimal attitudeWeight = attitude.multiply(new BigDecimal("0.3"));
//                    BigDecimal finalMark = softSkillWeight.add(skillWeight).add(attitudeWeight);

                    // Create the MarkReport
                    MarkReport markReport = new MarkReport();
                    markReport.setStudent(student);
                    markReport.setSoftskill(request.getSoftskill());
                    markReport.setAvg_exam_mark(request.getAvg_exam_mark());
                    markReport.setMiddle_exam(request.getMiddle_exam());
                    markReport.setFinal_exam(request.getFinal_exam());
                    markReport.setSkill(skill);
                    markReport.setAttitude(null);
                    markReport.setFinal_mark(null);
                    markReport.setComment(request.getComment());
                    return markReport;
                })
                .collect(Collectors.toList());

        markReportRepository.saveAll(entities);
    }



}
