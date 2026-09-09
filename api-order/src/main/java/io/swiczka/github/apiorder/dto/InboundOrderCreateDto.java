package io.swiczka.github.apiorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record InboundOrderCreateDto(

        @NotNull(message = "Company ID has to be provided")
        Long companyId, //company that owns packages

        @NotNull(message = "Layout ID has to be provided")
        Long layoutId,

        @Size(min = 1, max = 15, message = "There must be 1-15 packages provided")
        List<@NotBlank String> packageNames //list size equals order size
) { }
