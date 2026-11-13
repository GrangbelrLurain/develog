package com.example.ticket;

import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 08장 지뢰 1: @RestController가 아닌 @Controller에서 객체를 반환하는 경우.
@Controller
public class PlainController {
    @GetMapping("/plain/ticket")
    public TicketResponse ticket() {
        return new TicketResponse(1L, "콘서트", BigDecimal.TEN);
    }
}
