package com.example.jip.mapper;

import com.example.jip.dto.request.AssignmentCreationRequest;
import com.example.jip.dto.request.AssignmentUpdateRequest;
import com.example.jip.dto.response.AssignmentResponse;
import com.example.jip.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    Assignment toAssignment(AssignmentCreationRequest assignmentCreationRequest);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "end_date", source = "end_date")
    @Mapping(target = "created_date", source = "created_date")
    AssignmentResponse toAssignmentResponse(Assignment assignment);

    void updateAssigment(@MappingTarget Assignment assignment, AssignmentUpdateRequest assignmentUpdateRequest);
}
