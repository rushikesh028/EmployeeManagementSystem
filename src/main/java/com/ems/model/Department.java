package com.ems.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;


 // Department Entity – maps to `departments` table.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    private Long id;

    @NotBlank(message = "Department name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 150)
    private String location;

    private LocalDateTime createdAt;

    // Populated for reporting
    private int employeeCount;
}
