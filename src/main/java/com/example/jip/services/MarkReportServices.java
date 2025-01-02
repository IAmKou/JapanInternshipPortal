package com.example.jip.services;

import com.example.jip.dto.ClassDTO;
import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.request.markReport.MarkReportImportRequest;
import com.example.jip.dto.request.markReport.MarkReportUpdateRequest;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkReportServices {

    MarkReportRepository markReportRepository;
    StudentRepository studentRepository;
    ListRepository listRepository;


    public List<MarkReportResponse> getListMarkReport(int classId) {
        List<MarkReport> results =  markReportRepository.findAllByClassId(classId);
        return results.stream()
                .map(markReport -> {
                    MarkReportResponse response = new MarkReportResponse();
                    response.setId(markReport.getId());
                    response.setStudentName(markReport.getStudent().getFullname());
                    response.setStudentEmail(markReport.getStudent().getEmail());
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

    public MarkReportResponse getMarkReportById(int markRpId) {
        MarkReport markReport = markReportRepository.findById(markRpId);
        if (markReport != null) {
            String className = listRepository.getClassByStudentId(markReport.getStudent().getId());

            MarkReportResponse response = new MarkReportResponse();
            response.setId(markReport.getId());
            response.setStudentName(markReport.getStudent().getFullname());
            response.setStudentClass(className);
            response.setComment(markReport.getComment());
            response.setSoftskill(markReport.getSoftskill());
            response.setAvg_exam_mark(markReport.getAvg_exam_mark());
            response.setMiddle_exam(markReport.getMiddle_exam());
            response.setFinal_exam(markReport.getFinal_exam());
            response.setScriptPresentation(markReport.getScript_presentation()); // Thêm
            response.setPresentation(markReport.getPresentation()); // Thêm
            response.setSkill(markReport.getSkill());
            response.setAttitude(markReport.getAttitude());
            response.setFinal_mark(markReport.getFinal_mark());

            return response;
        } else {
            throw new NoSuchElementException("Mark report not found");
        }
    }

    public MarkReportResponse getMarkReportByStudentId(int studentId) {
        MarkReport markReport = markReportRepository.findByStudentId(studentId);
        if(markReport != null){
            String className = listRepository.getClassByStudentId(studentId);
            MarkReportResponse response = new MarkReportResponse();
            response.setId(markReport.getId());
            response.setStudentName(markReport.getStudent().getFullname());
            response.setComment(markReport.getComment());
            response.setStudentClass(className);
            response.setSoftskill(markReport.getSoftskill());
            response.setAvg_exam_mark(markReport.getAvg_exam_mark());
            response.setMiddle_exam(markReport.getMiddle_exam());
            response.setFinal_exam(markReport.getFinal_exam());
            response.setSkill(markReport.getSkill());
            response.setAttitude(markReport.getAttitude());
            response.setFinal_mark(markReport.getFinal_mark());
            return response;
        } else {
            throw new NoSuchElementException("Mark report not found");
        }
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

                    Student studentExisted = studentRepository.findByEmail(email)
                            .orElseThrow();
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
    public void importMarkReports(List<MarkReportImportRequest> markReports) {

        List<MarkReport> entities = markReports.stream()
                .map(request -> {
                    Student student = studentRepository.findByEmail(request.getEmail())
                            .orElseThrow();
//                    Skill Calculation:
//                    skill = (avg_exam_mark * 0.3) + (middle_exam * 0.4) + (final_exam * 0.3)
//
//                    Attitude Calculation:
//                    attitude = ((completed_assignments / total_assignments) + (attended_slots / total_slots)) / 2.
//
//                    Final Mark Calculation:
//                    final_mark = (soft_skill * 0.3) + (skill * 0.4) + (attitude * 0.3).

                    // Calculate skill
                    BigDecimal avgExamMark = request.getAvg_exam_mark().multiply(new BigDecimal("0.3"));
                    BigDecimal middleExam = request.getMiddle_exam().multiply(new BigDecimal("0.4"));
                    BigDecimal finalExam = request.getFinal_exam().multiply(new BigDecimal("0.3"));
                    BigDecimal skill = avgExamMark.add(middleExam).add(finalExam);

//                    // Calculate attitude
//                    int totalAssignments = assignmentStudentRepository.countByStudentId(student.getId());
//                    // Ensure there are assignments to avoid division by zero
//                    if (totalAssignments == 0) {
//                        throw new IllegalArgumentException("No assignments found for the student with ID: " + student.getId());
//                    }
//                    // Initialize the total marks of submitted assignments
//                    BigDecimal submittedAssignments = BigDecimal.ZERO;
//
//                    // Iterate through all assignments of the student
//                    for (StudentAssignment sa : studentAssignmentRepository.findAllByStudentId(student.getId())) {
//                        BigDecimal submissionMark = sa.getMark();
//
//                        // Check for null marks and handle gracefully
//                        if (submissionMark != null) {
//                            submittedAssignments = submittedAssignments.add(submissionMark);
//                        }
//                    }
//                    BigDecimal assignmentCompletion = submittedAssignments
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
                    MarkReport markReport = markReportRepository.findByEmail(request.getEmail());
                    if(markReport == null){
                        throw new IllegalStateException("Didn't find MarkReport with email: " + request.getEmail());
                    }
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
    public void updateMarkRp(int markRpId, MarkReportUpdateRequest request){
        MarkReport markReport = markReportRepository.findById(markRpId);

        if(markReport == null){
            throw new IllegalStateException("Didn't find MarkReport with id: " + markRpId);
        }

        Student student = studentRepository.findById(markReport.getStudent().getId())
                .orElseThrow();
        // Calculate skill
        BigDecimal avgExamMark = request.getAvg_exam_mark().multiply(new BigDecimal("0.3"));
        BigDecimal middleExam = request.getMiddle_exam().multiply(new BigDecimal("0.4"));
        BigDecimal finalExam = request.getFinal_exam().multiply(new BigDecimal("0.3"));
        BigDecimal skill = avgExamMark.add(middleExam).add(finalExam);

//        // Calculate attitude
//        int totalAssignments = assignmentStudentRepository.countByStudentId(student.getId());
//        // Ensure there are assignments to avoid division by zero
//        if (totalAssignments == 0) {
//             throw new IllegalArgumentException("No assignments found for the student with ID: " + student.getId());
//        }
//        // Initialize the total marks of submitted assignments
//        BigDecimal submittedAssignments = BigDecimal.ZERO;
//
//        // Iterate through all assignments of the student
//        for (StudentAssignment sa : studentAssignmentRepository.findAllByStudentId(student.getId())) {
//             BigDecimal submissionMark = sa.getMark();
//
//             // Check for null marks and handle gracefully
//             if (submissionMark != null) {
//                 submittedAssignments = submittedAssignments.add(submissionMark);
//                }
//        }
//
//        BigDecimal assignmentCompletion = submittedAssignments
//                            .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
//        int totalSlots = attendantRepository.findTotalSlotByStudentId(student.getId());
//        int attendedSlots = attendantRepository.countAttendedByStudentId(student);
//        BigDecimal attendance = new BigDecimal(attendedSlots)
//                            .divide(new BigDecimal(totalSlots), 2, RoundingMode.HALF_UP);
//
//        BigDecimal attitude = assignmentCompletion.add(attendance).divide(new BigDecimal(2), RoundingMode.HALF_UP);
//
////         Calculate final mark
//        BigDecimal softSkillWeight = request.getSoftskill().multiply(new BigDecimal("0.3"));
//        BigDecimal skillWeight = skill.multiply(new BigDecimal("0.4"));
//        BigDecimal attitudeWeight = attitude.multiply(new BigDecimal("0.3"));
//        BigDecimal finalMark = softSkillWeight.add(skillWeight).add(attitudeWeight);

        markReport.setSoftskill(request.getSoftskill());
        markReport.setAvg_exam_mark(request.getAvg_exam_mark());
        markReport.setMiddle_exam(request.getMiddle_exam());
        markReport.setFinal_exam(request.getFinal_exam());
        markReport.setSkill(skill);
        markReport.setAttitude(null);
        markReport.setFinal_mark(null);
        markReport.setComment(request.getComment());

        markReportRepository.save(markReport);
    }

    public void updateGrades(List<MarkReportDTO> gradeDTOs) {
        for (MarkReportDTO dto : gradeDTOs) {
            // We need to find the MarkReport for the student
            MarkReport markReport = markReportRepository.findByStudentId(dto.getStudentId());

            if (markReport != null) {
                // If the MarkReport exists, update the grade data
                markReportRepository.updateGrade(
                        dto.getStudentId(),
                        dto.getComment(),
                        dto.getAttitude(),
                        dto.getSoftskill(),
                        dto.getSkill(),
                        dto.getAvgExamMark(),
                        dto.getMiddleExam(),
                        dto.getFinalExam(),
                        dto.getFinalMark());
            } else {
                // Optionally, handle the case where the MarkReport doesn't exist
                // (for example, throw an exception or log a message)
                System.out.println("MarkReport not found for student ID: " + dto.getStudentId());
            }
        }
    }

}
