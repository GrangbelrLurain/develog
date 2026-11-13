package com.example.ticket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TicketCreateRequest(
        @NotBlank(message = "공연명은 필수입니다.") String title,
        @NotNull(message = "가격은 필수입니다.") @Min(value = 1000, message = "티켓 가격은 최소 1,000원 이상이어야 합니다.") BigDecimal price,
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.") int quantity) {}
