package com.example.jip.controller;

import com.example.jip.entity.Forum;
import com.example.jip.repository.ForumRepository;
import com.example.jip.services.ForumServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Service
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumServices forumServices;

    @GetMapping("/{id}")
    public ResponseEntity<Forum> readForum (@PathVariable int id) {
        Forum forum = forumServices.readForum(id);
        return new ResponseEntity<>(forum, HttpStatus.OK);
    }



}
