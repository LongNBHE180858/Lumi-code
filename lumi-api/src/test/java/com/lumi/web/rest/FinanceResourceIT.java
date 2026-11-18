package com.lumi.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = "spring.autoconfigure.exclude=org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiEmbeddingConnectionAutoConfiguration,org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiTextEmbeddingAutoConfiguration"
)
class FinanceResourceIT {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser
    void generatedFinancialCrudIsBlocked() throws Exception {
        mvc.perform(get("/api/instructor-wallets")).andExpect(status().isForbidden());
        mvc.perform(get("/api/wallet-transactions")).andExpect(status().isForbidden());
        mvc.perform(get("/api/payout-requests")).andExpect(status().isForbidden());
        mvc.perform(get("/api/refunds")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PARENT")
    void parentCannotReadAdminFinance() throws Exception {
        mvc.perform(get("/api/finance/admin/refunds")).andExpect(status().isForbidden());
        mvc.perform(get("/api/finance/admin/payouts")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void instructorCannotReadParentRefunds() throws Exception {
        mvc.perform(get("/api/finance/refunds/my")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReadFinancialQueues() throws Exception {
        mvc.perform(get("/api/finance/admin/refunds")).andExpect(status().isOk());
        mvc.perform(get("/api/finance/admin/payouts")).andExpect(status().isOk());
    }
}
