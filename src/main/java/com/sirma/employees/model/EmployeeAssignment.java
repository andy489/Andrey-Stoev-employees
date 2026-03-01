package com.sirma.employees.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAssignment {
    private int empId;
    private int projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public boolean isActiveOnDate(LocalDate date) {
        return !date.isBefore(dateFrom) && (dateTo == null || !date.isAfter(dateTo));
    }
}