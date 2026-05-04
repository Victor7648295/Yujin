package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public String report(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "submitted", required = false) Boolean submitted,
            Model model) {

        int currentYear = LocalDate.now().getYear();
        if (from == null) {
            from = LocalDate.of(currentYear, 1, 1);
        }
        if (to == null) {
            to = LocalDate.of(currentYear, 12, 31);
        }
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        if (Boolean.TRUE.equals(submitted)) {
            try {
                model.addAttribute("report", reportService.generate(from, to));
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
        }
        return "report";
    }
}
