package com.sirma.employees.controller;

import com.sirma.employees.model.EmployeePair;
import com.sirma.employees.service.EmployeePairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class EmployeePairController {

    private final EmployeePairService employeePairService;

    public EmployeePairController(EmployeePairService employeePairService) {
        this.employeePairService = employeePairService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }

        try {
            List<EmployeePair> pairs = employeePairService.findEmployeePairs(file);
            model.addAttribute("pairs", pairs);
            model.addAttribute("filename", file.getOriginalFilename());

            if (pairs.isEmpty()) {
                model.addAttribute("message", "No employee pairs found with overlapping work periods");
            } else {
                model.addAttribute("message", "Found " + pairs.size() + " employee pairs");
                // Get the top pair (longest working together)
                if (!pairs.isEmpty()) {
                    EmployeePair topPair = pairs.get(0);
                    model.addAttribute("topPair", topPair);
                }
            }

            return "result";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error processing file: " + e.getMessage());
            return "redirect:/";
        }
    }
}