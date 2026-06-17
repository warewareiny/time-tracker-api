package com.example.timetracker.dto;

import com.example.timetracker.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAndUpdateTaskRequest {

    @NotBlank
    @Size(min = 1, max = 64)
    private String title;

    @Size(max = 256)
    private String description;

    @NotNull
    private Status status;
}
