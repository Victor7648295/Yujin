package org.marketplace.marketplace.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ReportData {

    private LocalDate from;
    private LocalDate to;
    private int total;
    private Map<String, List<Product>> groups;
    private Map<String, String> descriptions;
}
