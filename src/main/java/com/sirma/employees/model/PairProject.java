package com.sirma.employees.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PairProject {
    private int projectId;
    private long daysWorked;

    public PairProject(int projectId, long daysWorked) {
        this.projectId = projectId;
        this.daysWorked = daysWorked;
    }
}
