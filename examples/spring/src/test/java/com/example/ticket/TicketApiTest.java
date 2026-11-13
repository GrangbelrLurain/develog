package com.example.ticket;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 05장, 08장의 예제 코드가 실제로 어떻게 응답하는지 고정해 두는 테스트. */
@SpringBootTest
@AutoConfigureMockMvc
class TicketApiTest {
    @Autowired MockMvc mvc;

    @Test
    void 정상_조회는_200() throws Exception {
        mvc.perform(get("/api/v1/tickets/101")).andExpect(status().isOk()).andExpect(jsonPath("$.ticketId").value(101));
    }

    @Test
    void BusinessException은_ErrorCode의_상태_코드로_변환된다() throws Exception {
        mvc.perform(get("/api/v1/tickets/999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("T001"));
    }

    @Test
    void record_DTO는_기본_생성자_없이_역직렬화되고_생성은_201() throws Exception {
        mvc.perform(post("/api/v1/tickets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"concert\",\"price\":50000,\"quantity\":2}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("concert"));
    }

    @Test
    void 삭제는_204() throws Exception {
        mvc.perform(delete("/api/v1/tickets/101")).andExpect(status().isNoContent());
    }

    // 아래 다섯 개는 05장 본문의 @ExceptionHandler(Exception.class)가 만드는 부작용이다.
    // 스프링이 원래 400/404/405로 처리하던 예외까지 전부 500이 된다.

    @Test
    void 본문의_핸들러에서는_Valid_검증_실패가_400이_아니라_500() throws Exception {
        mvc.perform(post("/api/v1/tickets").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"price\":10,\"quantity\":0}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 본문의_핸들러에서는_깨진_JSON이_400이_아니라_500() throws Exception {
        mvc.perform(post("/api/v1/tickets").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void 본문의_핸들러에서는_경로_변수_타입_불일치가_400이_아니라_500() throws Exception {
        mvc.perform(get("/api/v1/tickets/abc")).andExpect(status().isInternalServerError());
    }

    @Test
    void 본문의_핸들러에서는_없는_URL이_404가_아니라_500() throws Exception {
        mvc.perform(get("/nope")).andExpect(status().isInternalServerError());
    }

    @Test
    void 본문의_핸들러에서는_지원하지_않는_메서드가_405가_아니라_500() throws Exception {
        mvc.perform(put("/api/v1/tickets/101")).andExpect(status().isInternalServerError());
    }

    @Test
    void Controller에서_객체를_반환하면_요청_경로가_뷰_이름이_된다() throws Exception {
        mvc.perform(get("/plain/ticket")).andExpect(forwardedUrl("plain/ticket"));
    }
}
