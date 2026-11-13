package com.example.ticket;

import java.math.BigDecimal;

public record TicketResponse(Long ticketId, String title, BigDecimal price) {}
