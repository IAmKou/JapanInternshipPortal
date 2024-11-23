package com.example.jip.controller;
import com.example.jip.configuration.SecurityConfig;
import com.example.jip.dto.TeacherDTO;
import com.example.jip.dto.ThreadDTO;
import com.example.jip.entity.Thread;
import com.example.jip.services.ThreadServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    @Autowired
    ThreadServices threadService;

    @GetMapping()
    public Page<ThreadDTO> showAllThread(@RequestParam int page, @RequestParam int size){
        Pageable pageable = PageRequest.of(page - 1, size);
        return threadService.getAllThread(pageable).map(x-> new ThreadDTO(
                x.getId(), x.getTopicName(),x.getDateCreated(),x.getDescription(),x.getCreatorId(),x.getImage(),threadService.getCreatorName(x.getCreatorId())
        ));
    }
    @GetMapping("/{id}")
    public ThreadDTO findThreadById(@PathVariable("id") int id){
        Thread thread = threadService.getThreadById(id);
        ThreadDTO threadDTO = new ThreadDTO(thread.getId(),thread.getTopicName(),thread.getDateCreated(),thread.getDescription(),thread.getCreatorId(),thread.getImage(),threadService.getCreatorName(thread.getCreatorId()));
        return threadDTO;
    }

    @GetMapping("/add-thread")
    public String showAddThreadForm() {
        return "create-forum"; // File HTML để hiển thị form
    }

    @PostMapping("/add-thread")
    public RedirectView addThread(
            @RequestParam("topicName") String topicName,
            @RequestParam("description") String description,
            @RequestParam("creatorId") int creatorId,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) {

        try {
            threadService.addThread(topicName, description, creatorId, imageFile);
            model.addAttribute("message", "Thread added successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error saving thread: " + e.getMessage());
        }

        return new RedirectView("/forum.html");
    }

    @DeleteMapping("/{id}")
    public String deleteThread(@PathVariable("id") int id) {
        try {
            threadService.deleteThread(id); // Gọi service để xóa thread
            return "Thread deleted successfully!";
        } catch (Exception e) {
            return "Error deleting thread: " + e.getMessage();
        }
    }
}
