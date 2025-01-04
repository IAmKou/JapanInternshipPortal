package com.example.jip.services;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.request.markReport.MarkReportImportRequest;
import com.example.jip.dto.request.markReport.MarkReportUpdateRequest;
import com.example.jip.dto.response.markReport.MarkReportResponse;
import com.example.jip.entity.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkReportServices {

    MarkReportRepository markReportRepository;
    StudentRepository studentRepository;
    ListRepository listRepository;
    AssignmentStudentRepository assignmentStudentRepository;
    StudentAssignmentRepository studentAssignmentRepository;
    AttendantRepository attendantRepository;
    MarkRpExamRepository markRpExamRepository;

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
                    response.setScriptPresentation(markReport.getScript_presentation());
                    response.setPresentation(markReport.getPresentation());
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
                    isFirstRow = false; // Skip header row
                    continue;
                }

                try {
                    // Parse the fixed columns
                    String name = getStringCellValue(row.getCell(0), rowNumber, "Name");
                    String email = getStringCellValue(row.getCell(1), rowNumber, "Email");
                    BigDecimal presentation = getNumericCellValue(row.getCell(2), rowNumber, "Presentation");
                    BigDecimal script = getNumericCellValue(row.getCell(3), rowNumber, "Script");
                    BigDecimal middleExam = getNumericCellValue(row.getCell(4), rowNumber, "Middle Exam");
                    BigDecimal finalExam = getNumericCellValue(row.getCell(5), rowNumber, "Final Exam");
                    String comment = getStringCellValue(row.getCell(6), rowNumber, "Comment");

                    // Validate required fields (if any)
                    if ((name == null || name.isEmpty()) || (email == null || email.isEmpty())) {
                        errors.add("Row " + rowNumber + ": Name and Email are required.");
                        continue;
                    }

                    // Check if student exists
                    Student studentExisted = studentRepository.findByEmail(email).orElse(null);
                    if (studentExisted == null) {
                        errors.add("Row " + rowNumber + ": Student not found.");
                        continue;
                    }

                    // Parse dynamic fields for Kanji, Bunpou, and Kotoba
                    List<MarkReportExam> scores = markRpExamRepository.findAllByStudentId(studentExisted.getId());
                    log.info("List Exam of student has id {} is:" + scores, studentExisted.getId());
                    int colIndex = 7; // Start after the fixed columns
                    for (int i = 0; i < 44; i++) {
                        BigDecimal kanji = getNumericCellValue(row.getCell(colIndex++), rowNumber, "Kanji " + i);
                        BigDecimal bunpou = getNumericCellValue(row.getCell(colIndex++), rowNumber, "Bunpou " + i);
                        BigDecimal kotoba = getNumericCellValue(row.getCell(colIndex++), rowNumber, "Kotoba " + i);

                        MarkReportExam exam = scores.get(i);
                        if (kanji != null){
                            exam.setKanji(kanji);
                        } else {
                            exam.setKanji(null);
                        }
                        if (bunpou != null){
                            exam.setBunpou(bunpou);
                        } else {
                            exam.setBunpou(null);
                        }
                        if (kotoba != null){
                            exam.setKotoba(kotoba);
                        } else {
                            exam.setKotoba(null);
                        }


                    }

                    // Create a new MarkReportImportRequest with all the parsed data
                    MarkReportImportRequest markReport = new MarkReportImportRequest(
                            name,
                            email,
                            presentation,
                            script,
                            middleExam,
                            finalExam,
                            comment
                    );
                    log.info("Mark Report {}", markReport);
                    markReports.add(markReport);
                } catch (Exception e) {
                    errors.add("Row " + rowNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        if (!errors.isEmpty()) {
            log.warn("Validation errors: " + String.join(", ", errors));
        }

        return markReports;
    }

    private String getStringCellValue(Cell cell, int rowNumber, String columnName) {
        if (cell == null) {
            return null; // Allow null value
        }
        try {
            return cell.getStringCellValue().trim();
        } catch (Exception e) {
            log.warn("Row " + rowNumber + ": " + columnName + " contains invalid data.");
            return null;
        }
    }

    private BigDecimal getNumericCellValue(Cell cell, int rowNumber, String columnName) {
        if (cell == null) {
            return null; // Allow null value
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return new BigDecimal(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) {
                    return null; // Allow empty string as null
                }
                return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
            } else {
                log.warn("Row " + rowNumber + ": " + columnName + " must be a numeric value.");
                return null;
            }
        } catch (NumberFormatException e) {
            log.warn("Row " + rowNumber + ": " + columnName + " contains invalid numeric value.");
            return null;
        }
    }

    @Transactional
    public void importMarkReports(List<MarkReportImportRequest> markReports) {

        List<MarkReport> entities = markReports.stream()
                .map(request -> {
                    Student student = studentRepository.findByEmail(request.getEmail())
                            .orElseThrow();
//                    Skill Calculation:
//                    exam = (kotoba + bunpou + kanji) /3
//                    avg_exam_mark = sum of all exam / total exam
//                    skill = (avg_exam_mark * 0.3) + (middle_exam * 0.4) + (final_exam * 0.3)
//
//                    Attitude Calculation:
//                    attitude = ((completed_assignments / total_assignments) + (attended_slots / total_slots)) / 2.
//
//                    soft_skill = (presentation + script) / 2
//
//                    Final Mark Calculation:
//                    final_mark = (soft_skill * 0.3) + (skill * 0.4) + (attitude * 0.3).



                    // Create the MarkReport
                    MarkReport markReport = markReportRepository.findByEmail(request.getEmail());
                    if(markReport == null){
                        throw new IllegalStateException("Didn't find MarkReport with email: " + request.getEmail());
                    }

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
//                        // Check for null marks and handle gracefully
//                        if (submissionMark != null) {
//                            submittedAssignments = submittedAssignments.add(submissionMark);
//                        }
//                    }
//                    BigDecimal assignmentCompletion = submittedAssignments
//                            .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
//
//                    int totalSlots = attendantRepository.findTotalSlotByStudentId(student.getId());
//                    int attendedSlots = attendantRepository.countAttendedByStudentId(student);
//                    BigDecimal attendance = new BigDecimal(attendedSlots)
//                            .divide(new BigDecimal(totalSlots), 2, RoundingMode.HALF_UP);
//
//                    BigDecimal attitude = assignmentCompletion.add(attendance).divide(new BigDecimal(2), RoundingMode.HALF_UP);

                    //Calculate soft skill
                    if (request.getPresentation() != null){
                        markReport.setPresentation(request.getPresentation());
                    } else {
                        markReport.setPresentation(null);
                    }
                    if (request.getScriptPresentation() != null){
                        markReport.setScript_presentation(request.getScriptPresentation());
                    } else {
                        markReport.setScript_presentation(null);
                    }

                    if (request.getPresentation() != null && request.getScriptPresentation() != null){
                        markReport.setSoftskill(request.getPresentation().multiply(new BigDecimal("0.5")).add(request.getScriptPresentation().multiply(new BigDecimal("0.5"))));
                    } else {
                        markReport.setSoftskill(null);
                    }

                    // Calculate Avg Exam Mark
                    List<MarkReportExam> exams = markRpExamRepository.findAllByStudentId(student.getId());
                    BigDecimal avgExamMark = markReport.getAvg_exam_mark();
                    BigDecimal totalExamMark = BigDecimal.ZERO;

                    for (MarkReportExam exam : exams) {
                        BigDecimal kanji = exam.getKanji() != null ? exam.getKanji() : null;
                        BigDecimal bunpou = exam.getBunpou() != null ? exam.getBunpou() : null;
                        BigDecimal kotoba = exam.getKotoba() != null ? exam.getKotoba() : null;

                        // Check for null marks and handle gracefully
                        if (kanji != null && bunpou != null && kotoba != null) {
                            BigDecimal examMark = kanji.add(bunpou).add(kotoba)
                                    .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
                            totalExamMark = totalExamMark.add(examMark);

                        } else {
                            markReport.setAvg_exam_mark(null);
                        }
                    }

                    boolean hasNullValues = false; // Flag to track if any null values exist

                    for (MarkReportExam exam : exams) {
                        if (exam.getBunpou() == null || exam.getKanji() == null || exam.getKotoba() == null) {
                            hasNullValues = true;
                            break; // Exit the loop as soon as a null value is found
                        }
                    }
                    if(hasNullValues == false) {
                        avgExamMark = totalExamMark.divide(new BigDecimal(exams.size()), 2, RoundingMode.HALF_UP);
                        markReport.setAvg_exam_mark(avgExamMark);
                    } else {
                        markReport.setAvg_exam_mark(null);
                    }

                    //Calculate Skill
                    if (request.getMiddle_exam() != null) {
                        markReport.setMiddle_exam(request.getMiddle_exam());
                    } else {
                        markReport.setMiddle_exam(null);
                    }

                    if (request.getFinal_exam() != null) {
                        markReport.setFinal_exam(request.getFinal_exam());
                    } else {
                        markReport.setFinal_exam(null);
                    }

                    if(markReport.getAvg_exam_mark() == null || request.getMiddle_exam() == null || request.getFinal_exam() == null) {
                        markReport.setSkill(null);
                    } else {
                        markReport.getAvg_exam_mark().multiply(new BigDecimal("0.3"));
                        BigDecimal middleExam = request.getMiddle_exam().multiply(new BigDecimal("0.4"));
                        BigDecimal finalExam = request.getFinal_exam().multiply(new BigDecimal("0.3"));
                        BigDecimal skill = avgExamMark.add(middleExam).add(finalExam);
                        markReport.setSkill(skill);
                    }

//                    // Calculate final mark
//                    BigDecimal softSkillWeight = markReport.getSoftskill().multiply(new BigDecimal("0.3"));
//                    BigDecimal skillWeight = markReport.getSkill().multiply(new BigDecimal("0.4"));
//                    BigDecimal attitudeWeight = attitude.multiply(new BigDecimal("0.3"));
//                    BigDecimal finalMark = softSkillWeight.add(skillWeight).add(attitudeWeight);
                    markReport.setAttitude(null);
                    markReport.setFinal_mark(null);
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
//                    Skill Calculation:
//                    exam = (kotoba + bunpou + kanji) /3
//                    avg_exam_mark = sum of all exam / total exam
//                    skill = (avg_exam_mark * 0.3) + (middle_exam * 0.4) + (final_exam * 0.3)
//
//                    Attitude Calculation:
//                    attitude = ((completed_assignments / total_assignments) + (attended_slots / total_slots)) / 2.
//
//                    soft_skill = (presentation + script) / 2
//
//                    Final Mark Calculation:
//                    final_mark = (soft_skill * 0.3) + (skill * 0.4) + (attitude * 0.3).



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
//                        // Check for null marks and handle gracefully
//                        if (submissionMark != null) {
//                            submittedAssignments = submittedAssignments.add(submissionMark);
//                        }
//                    }
//                    BigDecimal assignmentCompletion = submittedAssignments
//                            .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
//
//                    int totalSlots = attendantRepository.findTotalSlotByStudentId(student.getId());
//                    int attendedSlots = attendantRepository.countAttendedByStudentId(student);
//                    BigDecimal attendance = new BigDecimal(attendedSlots)
//                            .divide(new BigDecimal(totalSlots), 2, RoundingMode.HALF_UP);
//
//                    BigDecimal attitude = assignmentCompletion.add(attendance).divide(new BigDecimal(2), RoundingMode.HALF_UP);

        //Calculate soft skill
        if (request.getPresentation() != null){
            markReport.setPresentation(request.getPresentation());
        } else {
            markReport.setPresentation(null);
        }
        if (request.getScriptPresentation() != null){
            markReport.setScript_presentation(request.getScriptPresentation());
        } else {
            markReport.setScript_presentation(null);
        }

        if (request.getPresentation() != null && request.getScriptPresentation() != null){
            markReport.setSoftskill(request.getPresentation().multiply(new BigDecimal("0.5")).add(request.getScriptPresentation().multiply(new BigDecimal("0.5"))));
        } else {
            markReport.setSoftskill(null);
        }

        // Calculate Avg Exam Mark
        List<MarkReportExam> exams = markRpExamRepository.findAllByStudentId(student.getId());
        BigDecimal avgExamMark = markReport.getAvg_exam_mark();
        BigDecimal totalExamMark = BigDecimal.ZERO;

        for (MarkReportExam exam : exams) {
            BigDecimal kanji = exam.getKanji() != null ? exam.getKanji() : null;
            BigDecimal bunpou = exam.getBunpou() != null ? exam.getBunpou() : null;
            BigDecimal kotoba = exam.getKotoba() != null ? exam.getKotoba() : null;

            // Check for null marks and handle gracefully
            if (kanji != null && bunpou != null && kotoba != null) {
                BigDecimal examMark = kanji.add(bunpou).add(kotoba)
                        .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
                totalExamMark = totalExamMark.add(examMark);

            } else {
                markReport.setAvg_exam_mark(null);
            }
        }

        boolean hasNullValues = false; // Flag to track if any null values exist

        for (MarkReportExam exam : exams) {
            if (exam.getBunpou() == null || exam.getKanji() == null || exam.getKotoba() == null) {
                hasNullValues = true;
                break; // Exit the loop as soon as a null value is found
            }
        }
        if(hasNullValues == false) {
            avgExamMark = totalExamMark.divide(new BigDecimal(exams.size()), 2, RoundingMode.HALF_UP);
            markReport.setAvg_exam_mark(avgExamMark);
        } else {
            markReport.setAvg_exam_mark(null);
        }

        //Calculate Skill
        if (request.getMiddle_exam() != null) {
            markReport.setMiddle_exam(request.getMiddle_exam());
        } else {
            markReport.setMiddle_exam(null);
        }

        if (request.getFinal_exam() != null) {
            markReport.setFinal_exam(request.getFinal_exam());
        } else {
            markReport.setFinal_exam(null);
        }

        if(markReport.getAvg_exam_mark() == null || request.getMiddle_exam() == null || request.getFinal_exam() == null) {
            markReport.setSkill(null);
        } else {
            markReport.getAvg_exam_mark().multiply(new BigDecimal("0.3"));
            BigDecimal middleExam = request.getMiddle_exam().multiply(new BigDecimal("0.4"));
            BigDecimal finalExam = request.getFinal_exam().multiply(new BigDecimal("0.3"));
            BigDecimal skill = avgExamMark.add(middleExam).add(finalExam);
            markReport.setSkill(skill);
        }

//        // Calculate final mark
//                    BigDecimal softSkillWeight = markReport.getSoftskill().multiply(new BigDecimal("0.3"));
//                    BigDecimal skillWeight = markReport.getSkill().multiply(new BigDecimal("0.4"));
//                    BigDecimal attitudeWeight = attitude.multiply(new BigDecimal("0.3"));
//                    BigDecimal finalMark = softSkillWeight.add(skillWeight).add(attitudeWeight);


        markReport.setAttitude(null);
        markReport.setFinal_mark(null);
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
