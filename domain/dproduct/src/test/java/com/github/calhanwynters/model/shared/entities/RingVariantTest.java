package com.github.calhanwynters.model.shared.entities;

import com.github.calhanwynters.model.shared.valueobjects.*;
import com.github.calhanwynters.model.shared.valueobjects.MaterialVO.MaterialName;
import com.github.calhanwynters.model.ringattributes.RingSizeVO;
import com.github.calhanwynters.model.ringattributes.RingStyleVO;
import com.github.calhanwynters.model.ringattributes.RingSize; // Import added RingSize enum
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
    private GemstoneTypeVO diamondTypeVO;
    private WeightVO defaultWeight;
    private RingSize ringSize; // New field for RingSize enum

    @BeforeEach
    public void setUp() {
        defaultSize = new RingSizeVO(new BigDecimal("19.8"));
        ringSize = RingSize.NA_SIZE_10; // Example size from RingSize enum
        Set<String> styleAttributes = Collections.singleton("SOLITAIRE");
        defaultStyle = new RingStyleVO(styleAttributes);
        defaultCare = new CareInstructionVO("Avoid harsh chemicals.");
        MaterialVO goldMaterial = MaterialVO.of(MaterialName.GOLD);
        defaultMaterials = Set.of(new MaterialCompositionVO(goldMaterial, "75% Au"));

        diamondTypeVO = GemstoneTypeVO.of("Diamond");
        defaultWeight = new WeightVO(new BigDecimal("1.0"), WeightVO.WeightUnit.GRAM);

        standardVariant = new RingVariant(
                VariantId.generate(), "SKU123", defaultSize, ringSize, // Adjusted to include RingSize
                defaultStyle, Money.of(500, USD), Money.of(500, USD), defaultWeight,
                defaultMaterials, Set.of(), defaultCare, VariantStatusEnums.DRAFT
        );
    }

    @Test
    public void constructorThrowsExceptionWhenMaterialsIsEmpty() {
        Set<MaterialCompositionVO> emptyMaterials = Collections.emptySet();

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                new RingVariant(
                        VariantId.generate(), "SKU123", defaultSize, ringSize, // Include ringSize
                        defaultStyle, Money.of(100, USD), Money.of(100, USD), defaultWeight,
                        emptyMaterials, Set.of(), defaultCare, VariantStatusEnums.ACTIVE
                )
        );
        assertTrue(thrown.getMessage().contains("must have at least one material composition"));
    }

    @Test
    public void constructorThrowsExceptionWhenPricesHaveDifferentCurrencies() {
        MonetaryAmount gbpPrice = Money.of(100, Monetary.getCurrency("GBP"));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                new RingVariant(
                        VariantId.generate(), "SKU123", defaultSize, ringSize, // Include ringSize
                        defaultStyle, Money.of(100, USD), gbpPrice, defaultWeight,
                        defaultMaterials, Set.of(), defaultCare, VariantStatusEnums.ACTIVE
                )
        );
        assertTrue(thrown.getMessage().contains("must be in the same currency"));
    }

    @Test
    public void createFactoryProducesDraftStatusVariantWithGeneratedSku() {
        RingVariant variant = RingVariant.create(defaultSize, ringSize, defaultStyle, Money.of(500, USD), defaultWeight, defaultMaterials, defaultCare);

        assertNotNull(variant);
        assertEquals(VariantStatusEnums.DRAFT, variant.status());
        assertTrue(variant.sku().startsWith("RING-"));
        assertTrue(variant.gemstones().isEmpty());
        assertEquals(ringSize, variant.ringSize(), "The ring size should match the provided size.");
    }

    @Test
    public void hasSameAttributesReturnsTrueWhenAttributesMatch() {
        RingVariant variantB = new RingVariant(
                VariantId.generate(), "SKU-DIFF", defaultSize, ringSize, // Include ringSize
                defaultStyle, Money.of(999, USD), Money.of(999, USD), defaultWeight,
                defaultMaterials, Set.of(), defaultCare, VariantStatusEnums.ACTIVE
        );

        assertTrue(standardVariant.hasSameAttributes(variantB), "Variants with identical physical attributes should match.");
    }

    @Test
    public void hasSameAttributesReturnsFalseWhenSizeDiffers() {
        RingSizeVO differentSize = new RingSizeVO(new BigDecimal("17.0"));
        RingVariant variantB = RingVariant.create(differentSize, ringSize, defaultStyle, Money.of(500, USD), defaultWeight, defaultMaterials, defaultCare);

        assertFalse(standardVariant.hasSameAttributes(variantB), "Variants should not match if size differs.");
    }

    @Test
    public void hasSameAttributesReturnsFalseWhenRingSizeDiffers() {
        RingSize differentRingSize = RingSize.NA_SIZE_9; // Example different ring size
        RingVariant variantB = new RingVariant(
                VariantId.generate(), "SKU-DIFF", defaultSize, differentRingSize, // Use different ring size
                defaultStyle, Money.of(999, USD), Money.of(999, USD), defaultWeight,
                defaultMaterials, Set.of(), defaultCare, VariantStatusEnums.ACTIVE
        );

        assertFalse(standardVariant.hasSameAttributes(variantB), "Variants should not match if ring size differs.");
    }

    @Test
    public void hasSameAttributesReturnsFalseWhenComparingDifferentVariantTypes() {
        Variant otherVariantType = mock(Variant.class);
        when(otherVariantType.materials()).thenReturn(Collections.emptySet());
        when(otherVariantType.gemstones()).thenReturn(Collections.emptySet());

        assertFalse(standardVariant.hasSameAttributes(otherVariantType), "Should return false when comparing different concrete types of Variant.");
    }

    @Test
    public void changeBasePriceCreatesNewInstanceWithUpdatedPrice() {
        // Define the new base price
        MonetaryAmount newPrice = Money.of(600, USD);
        // Call the changeBasePrice method
        RingVariant updatedVariant = standardVariant.changeBasePrice(newPrice);

        // Print current base prices for comparison
        System.out.println("Standard Base Price: " + standardVariant.basePrice());
        System.out.println("Updated Base Price: " + updatedVariant.basePrice());

        // Ensure it's a new instance
        assertNotSame(standardVariant, updatedVariant);
        // Check the base price
        assertEquals(newPrice, updatedVariant.basePrice());
        // Ensure current price hasn't changed due to base price change
        assertEquals(standardVariant.currentPrice(), updatedVariant.currentPrice());
        // Check the SKU remains the same
        assertEquals(standardVariant.sku(), updatedVariant.sku());
        // Ensure weight remains unchanged
        assertEquals(standardVariant.weight(), updatedVariant.weight());
        // Ensure ring size remains unchanged
        assertEquals(standardVariant.ringSize(), updatedVariant.ringSize());
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
        assertEquals(standardVariant.ringSize(), discountedVariant.ringSize()); // Ensure ring size remains unchanged
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
        assertEquals(standardVariant.ringSize(), updatedVariant.ringSize()); // Ensure ring size remains unchanged
    }

    @Test
    public void removeMaterialThrowsExceptionWhenRemovingLastMaterial() {
        MaterialCompositionVO soleMaterial = standardVariant.materials().iterator().next();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                standardVariant.removeMaterial(soleMaterial)
        );

        assertTrue(thrown.getMessage().contains("cannot remove the last one"));
    }

    @Test
    public void activateChangesStatusToActive() {
        RingVariant activatedVariant = standardVariant.activate();

        assertTrue(activatedVariant.isActive());
        assertEquals(VariantStatusEnums.ACTIVE, activatedVariant.status());
        assertEquals(standardVariant.weight(), activatedVariant.weight()); // Ensure weight remains unchanged
        assertEquals(standardVariant.ringSize(), activatedVariant.ringSize()); // Ensure ring size remains unchanged
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
        assertEquals(standardVariant.weight(), discontinuedVariant.weight()); // Ensure weight remains unchanged
        assertEquals(standardVariant.ringSize(), discontinuedVariant.ringSize()); // Ensure ring size remains unchanged
    }

    @Test
    public void createFactoryProducesDraftStatusVariantWithGeneratedSkuAndPrintData() {
        RingVariant variant = RingVariant.create(defaultSize, ringSize, defaultStyle, Money.of(500, USD), defaultWeight, defaultMaterials, defaultCare);

        assertNotNull(variant);
        assertEquals(VariantStatusEnums.DRAFT, variant.status());
        assertTrue(variant.sku().startsWith("RING-"));
        assertTrue(variant.gemstones().isEmpty());

        // Print the resulting data for verification
        System.out.println("--- Ring Variant Data Printout (from Test) ---");
        System.out.println("ID: " + variant.id());
        System.out.println("SKU: " + variant.sku());
        System.out.println("Status: " + variant.status());
        System.out.println("Size (Diameter): " + variant.size().diameterMm() + " mm"); // Corrected accessor
        System.out.println("Weight: " + variant.weight().amount() + " " + variant.weight().unit());
        System.out.println("Base Price: " + variant.basePrice());
        System.out.println("Current Price: " + variant.currentPrice());
        System.out.println("Materials: " + variant.materials());
        System.out.println("Care Instructions: " + variant.careInstructions().instructions());
        System.out.println("Ring Size: " + variant.ringSize()); // Added print statement for ring size
        System.out.println("---------------------------------------------");
    }

}



