package com.itbank.mall.controller;

import com.itbank.mall.entity.Event;
import com.itbank.mall.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    // 이벤트 목록 (기획전 리스트)
    @GetMapping("/list")
    public String eventList(Model model) {
        List<Event> list = eventService.findAll();
        model.addAttribute("eventList", list);
        return "event/list"; // list.jsp
    }

    // 이벤트 상세 보기
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id);
