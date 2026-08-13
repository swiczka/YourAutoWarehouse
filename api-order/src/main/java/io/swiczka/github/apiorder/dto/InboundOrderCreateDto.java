package io.swiczka.github.apiorder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record InboundOrderCreateDto(

        @NotNull(message = "User ID has to be provided")
        UUID operatorId, //related to browser userId

        @NotNull(message = "Company ID has to be provided")
        Long companyId, //company that owns packages

        @Size(min = 1, max = 15, message = "There must be 1-15 packages provided")
        List<String> packageNames //list size equals order size
) { }
