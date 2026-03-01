package com.sirma.employees.service;

import com.sirma.employees.model.EmployeeAssignment;
import com.sirma.employees.model.EmployeePair;
import com.sirma.employees.model.PairProject;
import com.sirma.employees.util.DateUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeePairService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeePairService.class);

    private final DateUtils dateUtils;

    public EmployeePairService(DateUtils dateUtils) {
        this.dateUtils = dateUtils;
    }

    public List<EmployeePair> findEmployeePairs(MultipartFile file) throws IOException {
        List<EmployeeAssignment> assignments = parseCSVFile(file);

        if (assignments.isEmpty()) {
            return new ArrayList<>();
        }

        // Group assignments by project
        Map<Integer, List<EmployeeAssignment>> assignmentsByProject = new HashMap<>();
        for (EmployeeAssignment assignment : assignments) {
            assignmentsByProject
                    .computeIfAbsent(assignment.getProjectId(), k -> new ArrayList<>())
                    .add(assignment);
        }

        // Map to store pairs and their projects
        Map<String, EmployeePair> pairMap = new HashMap<>();

        // For each project, find overlapping periods between employees
        for (Map.Entry<Integer, List<EmployeeAssignment>> entry : assignmentsByProject.entrySet()) {
            int projectId = entry.getKey();
            List<EmployeeAssignment> projectAssignments = entry.getValue();

            // Compare each pair of employees on the same project
            for (int i = 0; i < projectAssignments.size(); i++) {
                for (int j = i + 1; j < projectAssignments.size(); j++) {
                    EmployeeAssignment emp1 = projectAssignments.get(i);
                    EmployeeAssignment emp2 = projectAssignments.get(j);

                    // Calculate overlapping days
                    long overlappingDays = calculateOverlappingDays(emp1, emp2);

                    if (overlappingDays > 0) {
                        String pairKey = getPairKey(emp1.getEmpId(), emp2.getEmpId());
                        EmployeePair pair = pairMap.computeIfAbsent(pairKey,
                                k -> new EmployeePair(emp1.getEmpId(), emp2.getEmpId()));

                        pair.addProject(new PairProject(projectId, overlappingDays));
                    }
                }
            }
        }

        // Sort pairs by total days worked (descending)
        List<EmployeePair> pairs = new ArrayList<>(pairMap.values());
        pairs.sort((p1, p2) -> Long.compare(p2.getTotalDaysWorked(), p1.getTotalDaysWorked()));

        return pairs;
    }

    private List<EmployeeAssignment> parseCSVFile(MultipartFile file) throws IOException {
        List<EmployeeAssignment> assignments = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] line;
            int lineNumber = 0;

            while ((line = csvReader.readNext()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.length == 0 || (line.length == 1 && line[0].trim().isEmpty())) {
                    continue;
                }

                try {
                    // Expected format: EmpID, ProjectID, DateFrom, DateTo
                    if (line.length >= 3) {
                        int empId = Integer.parseInt(line[0].trim());
                        int projectId = Integer.parseInt(line[1].trim());

                        LocalDate dateFrom = dateUtils.parseDate(line[2].trim());
                        LocalDate dateTo = null;

                        if (line.length >= 4 && line[3] != null && !line[3].trim().isEmpty()) {
                            dateTo = dateUtils.parseDate(line[3].trim());
                        }

                        if (dateFrom != null) {
                            assignments.add(new EmployeeAssignment(empId, projectId, dateFrom, dateTo));
                        } else {
                            logger.warn("Skipping line {}: Invalid date format", lineNumber);
                        }
                    } else {
                        logger.warn("Skipping line {}: Insufficient columns", lineNumber);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Skipping line {}: Invalid number format", lineNumber);
                } catch (Exception e) {
                    logger.warn("Skipping line {}: {}", lineNumber, e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error reading CSV file", e);
        }

        return assignments;
    }

    private long calculateOverlappingDays(EmployeeAssignment emp1, EmployeeAssignment emp2) {
        LocalDate start1 = emp1.getDateFrom();
        LocalDate end1 = emp1.getDateTo() != null ? emp1.getDateTo() : LocalDate.now();

        LocalDate start2 = emp2.getDateFrom();
        LocalDate end2 = emp2.getDateTo() != null ? emp2.getDateTo() : LocalDate.now();

        // Find overlap period
        LocalDate overlapStart = start1.isAfter(start2) ? start1 : start2;
        LocalDate overlapEnd = end1.isBefore(end2) ? end1 : end2;

        // If no overlap
        if (overlapStart.isAfter(overlapEnd)) {
            return 0;
        }

        return dateUtils.calculateWorkingDays(overlapStart, overlapEnd);
    }

    private String getPairKey(int empId1, int empId2) {
        return empId1 < empId2 ? empId1 + "-" + empId2 : empId2 + "-" + empId1;
    }
}