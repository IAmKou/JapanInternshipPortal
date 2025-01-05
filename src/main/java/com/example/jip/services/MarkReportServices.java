package com.example.jip.services;

import com.example.jip.dto.MarkReportDTO;
import com.example.jip.dto.request.markReport.MarkReportImportRequest;
import com.example.jip.dto.request.markReport.MarkReportUpdateRequest;
import com.example.jip.dto.request.markReportExam.MarkReportExamUpdateRequest;
import com.example.jip.dto.response.markReport.MarkReportResponse;
import com.example.jip.dto.response.markReportExam.MarkReportExamResponse;
import com.example.jip.entity.*;
import com.example.jip.entity.Class;
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
    SemesterRepository semesterRepository;

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
                    response.setComment(markReport.getComment());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<MarkReportExamResponse> getListMarkReportExam(int classId){
        List<MarkReportExam> resultsExam = markRpExamRepository.findAllByClassId(classId);
        return resultsExam.stream()
                .map(markReport -> {
                    MarkReportExamResponse response = new MarkReportExamResponse();
                    response.setMarkRpId(markReport.getMarkReport().getId()); // Add `studentId` mapping
                    response.setExamName(markReport.getExam().getTitle());
                    response.setKotoba(markReport.getKotoba());
                    response.setBunpou(markReport.getBunpou());
                    response.setKanji(markReport.getKanji());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<MarkReportExamResponse> getMarkReportExamByMarkRpId(int markRpId){
        List<MarkReportExam> resultsExam = markRpExamRepository.findAllByMarkRpId(markRpId);
        return resultsExam.stream()
                .map(markReport -> {
                    MarkReportExamResponse response = new MarkReportExamResponse();
                    response.setMarkRpId(markReport.getMarkReport().getId()); // Add `studentId` mapping
                    response.setExamName(markReport.getExam().getTitle());
                    response.setKotoba(markReport.getKotoba());
                    response.setBunpou(markReport.getBunpou());
                    response.setKanji(markReport.getKanji());
                    return response;
                })
                .collect(Collectors.toList());

    }

    public List<MarkReportExamResponse> getMarkReportExamByStudentId(int studentId){
        List<MarkReportExam> resultsExam = markRpExamRepository.findAllByStudentId(studentId);
        return resultsExam.stream()
                .map(markReport -> {
                    MarkReportExamResponse response = new MarkReportExamResponse();
                    response.setMarkRpId(markReport.getMarkReport().getId()); // Add `studentId` mapping
                    response.setExamName(markReport.getExam().getTitle());
                    response.setKotoba(markReport.getKotoba());
                    response.setBunpou(markReport.getBunpou());
                    response.setKanji(markReport.getKanji());
                    return response;
                })
                .collect(Collectors.toList());

    }

    public MarkReportResponse getMarkReportById(int markRpId) {
        MarkReport markReport = markReportRepository.findById(markRpId);

        if (markReport != null) {
            Class clas = listRepository.getClassByStudentId(markReport.getStudent().getId());
            String className = clas.getName();
            String semester = semesterRepository.findSemesterByClassId(clas.getId()).getName();

            // Calculate attitude
            int totalAssignments = assignmentStudentRepository.countByStudentId(markReport.getStudent().getId());
            // Ensure there are assignments to avoid division by zero
            if (totalAssignments == 0) {
                throw new IllegalArgumentException("No assignments found for the student with ID: " + markReport.getStudent().getId());
            }
            // Initialize the total marks of submitted assignments
            BigDecimal submittedAssignments = BigDecimal.ZERO;

            // Iterate through all assignments of the student
            for (StudentAssignment sa : studentAssignmentRepository.findAllByStudentId(markReport.getStudent().getId())) {
                BigDecimal submissionMark = sa.getMark();
                // Check for null marks and handle gracefully
                if (submissionMark != null) {
                    submittedAssignments = submittedAssignments.add(submissionMark);
                }
            }
            BigDecimal assignmentCompletion = submittedAssignments
                    .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
            MarkReportResponse response = new MarkReportResponse();
            response.setSemester(semester);
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
            response.setAssignment(assignmentCompletion);
            return response;
        } else {
            throw new NoSuchElementException("Mark report not found");
        }
    }

    public MarkReportResponse getMarkReportByStudentId(int studentId) {
        MarkReport markReport = markReportRepository.findByStudentId(studentId);
        // Calculate attitude
        int totalAssignments = assignmentStudentRepository.countByStudentId(markReport.getStudent().getId());
        // Ensure there are assignments to avoid division by zero
        if (totalAssignments == 0) {
            throw new IllegalArgumentException("No assignments found for the student with ID: " + markReport.getStudent().getId());
        }
        // Initialize the total marks of submitted assignments
        BigDecimal submittedAssignments = BigDecimal.ZERO;

        // Iterate through all assignments of the student
        for (StudentAssignment sa : studentAssignmentRepository.findAllByStudentId(markReport.getStudent().getId())) {
            BigDecimal submissionMark = sa.getMark();
            // Check for null marks and handle gracefully
            if (submissionMark != null) {
                submittedAssignments = submittedAssignments.add(submissionMark);
            }
        }
        BigDecimal assignmentCompletion = submittedAssignments
                .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
        if(markReport != null){
            Class clas = listRepository.getClassByStudentId(markReport.getStudent().getId());
            String className = clas.getName();
            String semester = semesterRepository.findSemesterByClassId(clas.getId()).getName();
            MarkReportResponse response = new MarkReportResponse();
            response.setSemester(semester);
            response.setId(markReport.getId());
            response.setStudentName(markReport.getStudent().getFullname());
            response.setComment(markReport.getComment());
            response.setStudentClass(className);
            response.setPresentation(markReport.getPresentation());
            response.setScriptPresentation(markReport.getScript_presentation());
            response.setSoftskill(markReport.getSoftskill());
            response.setAvg_exam_mark(markReport.getAvg_exam_mark());
            response.setMiddle_exam(markReport.getMiddle_exam());
            response.setFinal_exam(markReport.getFinal_exam());
            response.setSkill(markReport.getSkill());
            response.setAttitude(markReport.getAttitude());
            response.setAssignment(assignmentCompletion);
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

                List<String> rowErrors = new ArrayList<>();

                try {
                    // Parse fixed columns
                    String name = getStringCellValue(row.getCell(0), rowNumber, "Name");
                    String email = getStringCellValue(row.getCell(1), rowNumber, "Email");
                    BigDecimal presentation = getNumericCellValue(row.getCell(2), rowNumber, "Presentation", rowErrors);
                    BigDecimal script = getNumericCellValue(row.getCell(3), rowNumber, "Script", rowErrors);
                    BigDecimal middleExam = getNumericCellValue(row.getCell(4), rowNumber, "Middle Exam", rowErrors);
                    BigDecimal finalExam = getNumericCellValue(row.getCell(5), rowNumber, "Final Exam", rowErrors);
                    String comment = getStringCellValue(row.getCell(6), rowNumber, "Comment");

                    // Validate required fields
                    if ((name == null || name.isEmpty()) || (email == null || email.isEmpty())) {
                        rowErrors.add("Row " + rowNumber + ": Name and Email are required.\n");
                    }

                    // Check if student exists
                    Student studentExisted = studentRepository.findByEmail(email).orElse(null);
                    if (studentExisted == null) {
                        rowErrors.add("Row " + rowNumber + ": Student not found. \n");
                    }

                    // Stop processing the row if there are errors
                    if (!rowErrors.isEmpty()) {
                        errors.addAll(rowErrors);
                        continue;
                    }


                    // Parse dynamic fields for Kanji, Bunpou, and Kotoba
                    int colIndex = 7; // Start after the fixed columns
                    for (int i = 0; i < 44; i++) {
                        BigDecimal kanji = getNumericCellValue(row.getCell(colIndex++), rowNumber, "Kanji " + (i + 1), rowErrors);
                        BigDecimal bunpou = getNumericCellValue(row.getCell(colIndex++), rowNumber, "Bunpou " + (i + 1), rowErrors);
                        BigDecimal kotoba = getNumericCellValue(row.getCell(colIndex++), rowNumber, "Kotoba " + (i + 1), rowErrors);

                        // Validate score range
                        validateScoreRange(kanji, rowNumber, "Kanji " + (i + 1), rowErrors);
                        validateScoreRange(bunpou, rowNumber, "Bunpou " + (i + 1), rowErrors);
                        validateScoreRange(kotoba, rowNumber, "Kotoba " + (i + 1), rowErrors);

                        if (rowErrors.isEmpty()) {
                            List<MarkReportExam> scores = markRpExamRepository.findAllByStudentId(studentExisted.getId());
                            log.info("List Exam of student with id {} is: {}", studentExisted.getId(), scores);
                            MarkReportExam exam = scores.get(i);
                            exam.setKanji(kanji);
                            exam.setBunpou(bunpou);
                            exam.setKotoba(kotoba);
                        }
                    }

                    if (!rowErrors.isEmpty()) {
                        errors.addAll(rowErrors);
                        continue;
                    } else {
                        markReports.add(new MarkReportImportRequest(
                            name, email, presentation, script, middleExam, finalExam, comment));
                    }

                } catch (Exception e) {
                    rowErrors.add("Row " + rowNumber + ": " + e.getMessage());
                    errors.addAll(rowErrors);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        // Check for validation errors and throw exception if present
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors + "\n"));
        }

        return markReports;
    }

    private void validateScoreRange(BigDecimal score, int rowNumber, String columnName, List<String> errors) {
        if (score != null && (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0)) {
            errors.add("Row " + rowNumber + ": " + columnName + " must be between 0 and 10. Found: " + score + "\n");
        }
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

    private BigDecimal getNumericCellValue(Cell cell, int rowNumber, String columnName, List<String> errors) {
        if (cell == null) {
            return null; // Allow null value
        }
        try {
            BigDecimal value;
            if (cell.getCellType() == CellType.NUMERIC) {
                value = new BigDecimal(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
            } else if (cell.getCellType() == CellType.STRING) {
                String strValue = cell.getStringCellValue().trim();
                if (strValue.isEmpty()) {
                    return null; // Allow empty string as null
                }
                value = new BigDecimal(strValue).setScale(2, RoundingMode.HALF_UP);
            } else {
                errors.add("Row " + rowNumber + ": " + columnName + " must be a numeric value." + "\n");
                return null;
            }

            // Reject values outside the range 0-10
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.TEN) > 0) {
                errors.add("Row " + rowNumber + ": " + columnName + " must be between 0 and 10. Found: " + value + "\n");
                return null; // Discard invalid value
            }

            return value;
        } catch (NumberFormatException e) {
            errors.add("Row " + rowNumber + ": " + columnName + " contains an invalid numeric value: "  + "\n");
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
//                    markReportExam = (kotoba + bunpou + kanji) /3
//                    avg_exam_mark = sum of all markReportExam / total markReportExam
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

                    // Calculate attitude
                    int totalAssignments = assignmentStudentRepository.countByStudentId(student.getId());
                    // Ensure there are assignments to avoid division by zero
                    if (totalAssignments == 0) {
                        throw new IllegalArgumentException("No assignments found for the student with ID: " + student.getId());
                    }
                    // Initialize the total marks of submitted assignments
                    BigDecimal submittedAssignments = BigDecimal.ZERO;

                    // Iterate through all assignments of the student
                    for (StudentAssignment sa : studentAssignmentRepository.findAllByStudentId(student.getId())) {
                        BigDecimal submissionMark = sa.getMark();
                        // Check for null marks and handle gracefully
                        if (submissionMark != null) {
                            submittedAssignments = submittedAssignments.add(submissionMark);
                        }
                    }
                    BigDecimal assignmentCompletion = submittedAssignments
                            .divide(new BigDecimal(totalAssignments), 2, RoundingMode.HALF_UP);
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
                        BigDecimal avgExam = avgExamMark.multiply(new BigDecimal("0.3"));
                        BigDecimal middleExam = markReport.getMiddle_exam().multiply(new BigDecimal("0.4"));
                        BigDecimal finalExam = markReport.getFinal_exam().multiply(new BigDecimal("0.3"));
                        BigDecimal skill = avgExam.add(middleExam).add(finalExam);
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

        // Update related exams
        for (MarkReportExamUpdateRequest examUpdate : request.getExams()) {
            MarkReportExam exam = markRpExamRepository.findByMarkRpIdAndExamName(examUpdate.getMarkRpId(), examUpdate.getExamName());
            exam.setKanji(examUpdate.getKanji());
            exam.setBunpou(examUpdate.getBunpou());
            exam.setKotoba(examUpdate.getKotoba());
            markRpExamRepository.save(exam);
        }

        if(markReport == null){
            throw new IllegalStateException("Didn't find MarkReport with id: " + markRpId);
        }

        Student student = studentRepository.findById(markReport.getStudent().getId())
                .orElseThrow();
//                    Skill Calculation:
//                    markReportExam = (kotoba + bunpou + kanji) /3
//                    avg_exam_mark = sum of all markReportExam / total markReportExam
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
            BigDecimal avgExam = avgExamMark.multiply(new BigDecimal("0.3"));
            BigDecimal middleExam = markReport.getMiddle_exam().multiply(new BigDecimal("0.4"));
            BigDecimal finalExam = markReport.getFinal_exam().multiply(new BigDecimal("0.3"));
            BigDecimal skill = avgExam.add(middleExam).add(finalExam);
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
