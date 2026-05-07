package com.pgfinder.platform.controller;

import com.pgfinder.platform.domain.Amenity;
import com.pgfinder.platform.domain.GenderType;
import com.pgfinder.platform.domain.RoomType;
import com.pgfinder.platform.domain.SortBy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("amenities", Amenity.values());
        model.addAttribute("genderTypes", GenderType.values());
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("sortOptions", SortBy.values());
        return "index";
    }

    @GetMapping("/listings/{slug}")
    public String listingDetail(@PathVariable String slug, Model model) {
        model.addAttribute("slug", slug);
        return "listing-detail";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin";
    }
}