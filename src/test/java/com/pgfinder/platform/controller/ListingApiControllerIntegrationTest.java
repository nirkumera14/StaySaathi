package com.pgfinder.platform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:listing-api-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.h2.console.enabled=false"
})
class ListingApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCitiesKeepsLegacyStringResponse() throws Exception {
        mockMvc.perform(get("/api/cities").param("query", "Lu").param("limit", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").isString());
    }

    @Test
    void getIndiaCitiesReturnsRichObjects() throws Exception {
        mockMvc.perform(get("/api/cities/india").param("query", "Luck").param("limit", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].code").isNotEmpty())
            .andExpect(jsonPath("$[0].city").isNotEmpty())
            .andExpect(jsonPath("$[0].state").isNotEmpty())
            .andExpect(jsonPath("$[0].label").isNotEmpty())
            .andExpect(jsonPath("$[0].hasListings").isBoolean())
            .andExpect(jsonPath("$[0].listingCount").isNumber());
    }

    @Test
    void getCitySummaryIsSortedByListingCount() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cities/summary").param("limit", "8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.isArray()).isTrue();
        assertThat(json.size()).isGreaterThan(0);

        long previous = Long.MAX_VALUE;
        for (JsonNode node : json) {
            long current = node.path("listingCount").asLong();
            assertThat(current).isLessThanOrEqualTo(previous);
            previous = current;
        }
    }

    @Test
    void getIndiaListingsReturnsPaginationAndFallbackMetadata() throws Exception {
        mockMvc.perform(get("/api/listings/india")
                .param("city", "Lucknow")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.total").isNumber())
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.fallbackApplied").isBoolean());
    }
}
