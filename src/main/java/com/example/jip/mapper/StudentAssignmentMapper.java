package com.example.jip.mapper;

import com.example.jip.dto.response.AssignmentResponse;
import com.example.jip.dto.response.StudentAssignmentResponse;
import com.example.jip.entity.Assignment;
import com.example.jip.entity.StudentAssignment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentAssignmentMapper {
    StudentAssignmentResponse toStudentAssignmentResponse(StudentAssignment studentAssignment);
}
