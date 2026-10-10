package ru.rvip.gateway.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalSearchBlockFilterTest {

    @Test
    void blocksSearchPathWithAndWithoutSlash() {
        assertTrue(InternalSearchBlockFilter.isBlocked("/api/contracts/search"));
        assertTrue(InternalSearchBlockFilter.isBlocked("/api/contracts/search/"));
    }

    @Test
    void keepsPublicContractRoutes() {
        assertFalse(InternalSearchBlockFilter.isBlocked("/api/contracts"));
        assertFalse(InternalSearchBlockFilter.isBlocked("/api/contracts/1"));
        assertFalse(InternalSearchBlockFilter.isBlocked("/api/contracts/report"));
        assertFalse(InternalSearchBlockFilter.isBlocked("/api/reports/contracts"));
    }
}
