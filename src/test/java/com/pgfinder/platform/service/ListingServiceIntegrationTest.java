package com.pgfinder.platform.service;

import com.pgfinder.platform.dto.IndiaCityDto;
import com.pgfinder.platform.dto.IndiaListingsResponseDto;
import com.pgfinder.platform.dto.ListingSearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:listing-service-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.h2.console.enabled=false"
})
class ListingServiceIntegrationTest {

    @Autowired
    private ListingService listingService;

    @Test
    void cityCatalogSearchReturnsRichCityData() {
        IndiaCityDto city = listingService.searchIndiaCities("Lucknow", 1).get(0);
        assertThat(city.code()).isNotBlank();
        assertThat(city.city()).isEqualToIgnoringCase("Lucknow");
        assertThat(city.state()).isNotBlank();
    }

    @Test
    void fallbackAppliesWhenRequestedCityHasNoListingsButNearbyCityExists() {
        ListingSearchCriteria criteria = baseCriteria();
        criteria.setCity("Zaidpur");

        IndiaListingsResponseDto response = listingService.searchIndiaListings(criteria);

        assertThat(response.fallbackApplied()).isTrue();
        assertThat(response.fallbackFromCity()).isEqualTo("Zaidpur");
        assertThat(response.fallbackToCity()).isEqualTo("Lucknow");
        assertThat(response.fallbackDistanceKm()).isNotNull();
        assertThat(response.total()).isGreaterThan(0);
    }

    @Test
    void fallbackDoesNotApplyWhenNoListingCityWithinRange() {
        ListingSearchCriteria criteria = baseCriteria();
        criteria.setCity("Port Blair");

        IndiaListingsResponseDto response = listingService.searchIndiaListings(criteria);

        assertThat(response.fallbackApplied()).isFalse();
        assertThat(response.fallbackFromCity()).isNull();
        assertThat(response.fallbackToCity()).isNull();
    }

    private ListingSearchCriteria baseCriteria() {
        ListingSearchCriteria criteria = new ListingSearchCriteria();
        criteria.setPage(0);
        criteria.setSize(9);
        criteria.setAmenities(Set.of());
        return criteria;
    }
}
