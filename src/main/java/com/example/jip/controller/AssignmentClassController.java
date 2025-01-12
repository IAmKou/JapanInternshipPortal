package com.example.jip.controller;

import com.example.jip.dto.response.assignmentClass.AssignmentClassResponse;
import com.example.jip.repository.AssignmentClassRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/assignmentClass")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentClassController {
    AssignmentClassRepository assignmentClassRepository;
    @GetMapping("/getCByAid")
    public List<AssignmentClassResponse> getClassByAssignmentId(@RequestParam("assignmentId") int assignmentId) {

        return assignmentClassRepository.findAllByAssignmentId(assignmentId).stream()
                .map(assignmentClass -> {
                    AssignmentClassResponse response = new AssignmentClassResponse();
                    response.setAssignmentId(assignmentClass.getAssignment().getId());
                    response.setClassName(assignmentClass.getClas().getName());
                    response.setClassId(assignmentClass.getClas().getId());
                    log.info("Received response: " + response);
                    return response;
                })
                .collect(Collectors.toList());
    }
}
