package com.sirma.employees.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class EmployeePair {
    private int empId1;
    private int empId2;
    private long totalDaysWorked;
    private List<PairProject> projects = new ArrayList<>();

    public EmployeePair(int empId1, int empId2) {
        this.empId1 = empId1;
        this.empId2 = empId2;
    }

    public void addProject(PairProject project) {
        projects.add(project);
        totalDaysWorked += project.getDaysWorked();
    }
}
