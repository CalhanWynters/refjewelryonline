package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import com.github.calhanwynters.model.shared.valueobjects.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VariantInterfaceContractTest {

    @Mock
    private Variant mockVariant;

    @Test
    public void shouldDefineAllRequiredMethods() {
        assertNotNull(mockVariant, "The mock object should be initialized by MockitoExtension");
    }

    @Test
    public void methodsShouldReturnDefaultsUnlessStubbed() {
        assertNull(mockVariant.id());
        assertNull(mockVariant.sku());
        assertNull(mockVariant.basePrice());
        assertNull(mockVariant.currentPrice());
        assertNull(mockVariant.status());
        assertNull(mockVariant.careInstructions());
        assertNull(mockVariant.weight());

        assertNotNull(mockVariant.materials());
        assertTrue(mockVariant.materials().isEmpty(), "Materials collection should default to empty.");
        assertNotNull(mockVariant.gemstones());
        assertTrue(mockVariant.gemstones().isEmpty(), "Gemstones collection should default to empty.");
    }

    @Test
    public void methodsShouldReturnStubbedValuesWhenConfigured() {
        VariantId expectedId = VariantId.generate();
        String expectedSku = "TESTSKU";
        MonetaryAmount expectedBasePrice = mock(MonetaryAmount.class);
        MonetaryAmount expectedCurrentPrice = mock(MonetaryAmount.class);
        WeightVO expectedWeight = WeightVO.ofGrams(new BigDecimal("100.0")); // Example weight

        when(mockVariant.id()).thenReturn(expectedId);
        when(mockVariant.sku()).thenReturn(expectedSku);
        when(mockVariant.basePrice()).thenReturn(expectedBasePrice);
        when(mockVariant.currentPrice()).thenReturn(expectedCurrentPrice);
        when(mockVariant.weight()).thenReturn(expectedWeight);
        when(mockVariant.status()).thenReturn(VariantStatusEnums.ACTIVE);

        assertEquals(expectedId, mockVariant.id());
        assertEquals(expectedSku, mockVariant.sku());
        assertEquals(expectedBasePrice, mockVariant.basePrice());
        assertEquals(expectedCurrentPrice, mockVariant.currentPrice());
        assertEquals(expectedWeight, mockVariant.weight());
        assertEquals(VariantStatusEnums.ACTIVE, mockVariant.status());
    }

    @Test
    public void hasSameAttributesShouldBeCallableAsPartOfTheContract() {
        Variant otherMock = mock(Variant.class);

        // Define behavior for the abstract method if needed
        when(mockVariant.hasSameAttributes(otherMock)).thenReturn(true);

        assertTrue(mockVariant.hasSameAttributes(otherMock));
        verify(mockVariant).hasSameAttributes(otherMock);
    }
}
