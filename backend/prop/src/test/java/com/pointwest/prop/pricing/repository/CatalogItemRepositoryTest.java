package com.pointwest.prop.pricing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import com.pointwest.prop.common.entity.CatalogItem;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none"
})
class CatalogItemRepositoryTest {

    @Autowired
    private CatalogItemRepository repository;

    @Test
    void findsCatalogItemsIgnoringCategoryCase() {
        repository.save(new CatalogItem(null, "Keyboard", "Hardware", BigDecimal.TEN));
        repository.save(new CatalogItem(null, "Consulting", "Services", BigDecimal.ONE));

        var result = repository.findByCategoryIgnoreCase("hardware", PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("Keyboard", result.getContent().get(0).getName());
    }
}
