package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.shared.valueobjects.*;
import com.github.calhanwynters.model.shared.valueobjects.MaterialVO.MaterialName;
import com.github.calhanwynters.model.ringattributes.RingSizeVO;
import com.github.calhanwynters.model.ringattributes.RingStyleVO;
import com.github.calhanwynters.model.shared.enums.VariantStatusEnums;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RingVariantTest {

    private final CurrencyUnit USD = Monetary.getCurrency("USD");
    private RingSizeVO defaultSize;
    private RingStyleVO defaultStyle;
    private CareInstructionVO defaultCare;
    private Set<MaterialCompositionVO> defaultMaterials;
    private RingVariant standardVariant;
    private GemstoneTypeVO diamondTypeVO; // Added field for the GemstoneTypeVO
    private WeightVO defaultWeight; // Field for WeightVO

    @BeforeEach
    public void setUp() {
        defaultSize = new RingSizeVO(new BigDecimal("19.8"));
        Set<String> styleAttributes = Collections.singleton("SOLITAIRE"); // Use a valid style
        defaultStyle = new RingStyleVO(styleAttributes); // Ensure VALID_STYLE is recognized
        defaultCare = new CareInstructionVO("Avoid harsh chemicals.");
        MaterialVO goldMaterial = MaterialVO.of(MaterialName.GOLD);
        defaultMaterials = Set.of(new MaterialCompositionVO(goldMaterial, "75% Au"));

        diamondTypeVO = GemstoneTypeVO.of("Diamond");
        defaultWeight = new WeightVO(new BigDecimal("1.0"), WeightVO.WeightUnit.GRAM);

        standardVariant = new RingVariant(
                VariantId.generate(), "SKU123", defaultSize, defaultStyle,
                Money.of(500, USD), Money.of(500, USD), defaultWeight,
                defaultMaterials,
                Set.of(), defaultCare, VariantStatusEnums.DRAFT
        );
    }


    @Test
    public void constructorThrowsExceptionWhenMaterialsIsEmpty() {
        Set<MaterialCompositionVO> emptyMaterials = Collections.emptySet();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                new RingVariant(
                        VariantId.generate(), "SKU123", defaultSize, defaultStyle,
                        Money.of(100, USD), Money.of(100, USD), defaultWeight, // Include defaultWeight
                        emptyMaterials,
                        Set.of(), defaultCare, VariantStatusEnums.ACTIVE
                )
        );
        assertTrue(thrown.getMessage().contains("must have at least one material composition"));
    }

    @Test
    public void constructorThrowsExceptionWhenPricesHaveDifferentCurrencies() {
        MonetaryAmount gbpPrice = Money.of(100, Monetary.getCurrency("GBP"));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                new RingVariant(
                        VariantId.generate(), "SKU123", defaultSize, defaultStyle,
                        Money.of(100, USD), gbpPrice, defaultWeight, // Include defaultWeight
                        defaultMaterials,
                        Set.of(), defaultCare, VariantStatusEnums.ACTIVE
                )
        );
        assertTrue(thrown.getMessage().contains("must be in the same currency"));
    }

    // --- Factory Method Tests ---

    @Test
    public void createFactoryProducesDraftStatusVariantWithGeneratedSku() {
        RingVariant variant = RingVariant.create(defaultSize, defaultStyle, Money.of(500, USD), defaultWeight, defaultMaterials, defaultCare); // Include defaultWeight

        assertNotNull(variant);
        assertEquals(VariantStatusEnums.DRAFT, variant.status());
        assertTrue(variant.sku().startsWith("RING-"));
        assertTrue(variant.gemstones().isEmpty());
    }

    // --- hasSameAttributes Logic Tests ---

    @Test
    public void hasSameAttributesReturnsTrueWhenAttributesMatch() {
        RingVariant variantB = new RingVariant(
                VariantId.generate(), "SKU-DIFF", defaultSize, defaultStyle,
                Money.of(999, USD), Money.of(999, USD), defaultWeight, // Include defaultWeight
                defaultMaterials,
                Set.of(), defaultCare, VariantStatusEnums.ACTIVE
        );

        assertTrue(standardVariant.hasSameAttributes(variantB), "Variants with identical physical attributes should match.");
    }

    @Test
    public void hasSameAttributesReturnsFalseWhenSizeDiffers() {
        RingSizeVO differentSize = new RingSizeVO(new BigDecimal("17.0"));
        RingVariant variantB = RingVariant.create(differentSize, defaultStyle, Money.of(500, USD), defaultWeight, defaultMaterials, defaultCare); // Include defaultWeight

        assertFalse(standardVariant.hasSameAttributes(variantB), "Variants should not match if size differs.");
    }

    @Test
    public void hasSameAttributesReturnsFalseWhenComparingDifferentVariantTypes() {
        Variant otherVariantType = mock(Variant.class);
        when(otherVariantType.materials()).thenReturn(Collections.emptySet());
        when(otherVariantType.gemstones()).thenReturn(Collections.emptySet());

        assertFalse(standardVariant.hasSameAttributes(otherVariantType), "Should return false when comparing different concrete types of Variant.");
    }

    // --- Behavior Method Tests (Change/Update methods return new instances as expected for records) ---

    @Test
    public void changeBasePriceCreatesNewInstanceWithUpdatedPrice() {
        MonetaryAmount newPrice = Money.of(600, USD);
        RingVariant updatedVariant = standardVariant.changeBasePrice(newPrice);

        assertNotSame(standardVariant, updatedVariant);
        assertEquals(newPrice, updatedVariant.basePrice());
        assertEquals(newPrice, updatedVariant.currentPrice());
        assertEquals(standardVariant.sku(), updatedVariant.sku());
        assertEquals(standardVariant.weight(), updatedVariant.weight()); // Ensure weight remains unchanged
    }

    @Test
    public void applyDiscountCalculatesNewCurrentPrice() {
        PercentageVO discount = new PercentageVO(new BigDecimal("0.10"));
        RingVariant discountedVariant = standardVariant.applyDiscount(discount);

        MonetaryAmount expectedPrice = standardVariant.basePrice().multiply(0.9);

        assertNotSame(standardVariant, discountedVariant);
        assertEquals(expectedPrice, discountedVariant.currentPrice());
        assertEquals(standardVariant.basePrice(), discountedVariant.basePrice());
        assertEquals(standardVariant.weight(), discountedVariant.weight()); // Ensure weight remains unchanged
    }

    @Test
    public void addGemstoneAddsToSetAndCreatesNewInstance() {
        GemstoneVO newGem = GemstoneVO.of(diamondTypeVO, "VVS1", new BigDecimal("0.5"));
        RingVariant updatedVariant = standardVariant.addGemstone(newGem);

        assertNotSame(standardVariant, updatedVariant);
        assertEquals(0, standardVariant.gemstones().size());
        assertEquals(1, updatedVariant.gemstones().size());
        assertTrue(updatedVariant.gemstones().contains(newGem));
        assertEquals(standardVariant.weight(), updatedVariant.weight()); // Ensure weight remains unchanged
    }

    @Test
    public void removeMaterialThrowsExceptionWhenRemovingLastMaterial() {
        MaterialCompositionVO soleMaterial = standardVariant.materials().iterator().next();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> standardVariant.removeMaterial(soleMaterial));

        assertTrue(thrown.getMessage().contains("cannot remove the last one"));
    }

    // --- Lifecycle/Status Behavior Tests ---

    @Test
    public void activateChangesStatusToActive() {
        RingVariant activatedVariant = standardVariant.activate();

        assertTrue(activatedVariant.isActive());
        assertEquals(VariantStatusEnums.ACTIVE, activatedVariant.status());
        assertEquals(standardVariant.weight(), activatedVariant.weight()); // Ensure weight remains unchanged
    }

    @Test
    public void activateThrowsExceptionWhenStatusIsDiscontinued() {
        RingVariant discontinuedVariant = standardVariant.markAsDiscontinued();

        assertThrows(IllegalStateException.class, discontinuedVariant::activate);
    }

    @Test
    public void markAsDiscontinuedChangesStatusCorrectly() {
        RingVariant discontinuedVariant = standardVariant.markAsDiscontinued();

        assertEquals(VariantStatusEnums.DISCONTINUED, discontinuedVariant.status());
        assertFalse(discontinuedVariant.isActive());
        assertEquals(standardVariant.weight(), discontinuedVariant.weight());
    }

    // Optionally, more test cases can be added for additional edge scenarios, particularly focusing on weight-related logic.
}
