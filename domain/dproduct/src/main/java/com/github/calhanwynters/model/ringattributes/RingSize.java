package com.github.calhanwynters.model.ringattributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/*** Represents standardized international ring sizes, mapping display strings and regions
 * to a consistent ISO internal diameter (in millimeters).
 * Note: ISO 8601 standard uses circumference in mm as the primary measurement,
 * while these values are based on internal diameter measurements common to various regional charts.*/
public enum RingSize {
    // Asian Sizes (Diameter in mm)
    ASIAN_SIZE_1("1", new BigDecimal("13.0"), "ASIAN"),
    ASIAN_SIZE_2("2", new BigDecimal("13.3"), "ASIAN"),
    ASIAN_SIZE_3("3", new BigDecimal("13.7"), "ASIAN"),
    ASIAN_SIZE_4("4", new BigDecimal("14.0"), "ASIAN"),
    ASIAN_SIZE_5("5", new BigDecimal("14.3"), "ASIAN"),
    ASIAN_SIZE_6("6", new BigDecimal("14.7"), "ASIAN"),
    ASIAN_SIZE_7("7", new BigDecimal("15.0"), "ASIAN"),
    ASIAN_SIZE_8("8", new BigDecimal("15.3"), "ASIAN"),
    ASIAN_SIZE_9("9", new BigDecimal("15.7"), "ASIAN"),
    ASIAN_SIZE_10("10", new BigDecimal("16.0"), "ASIAN"),
    ASIAN_SIZE_11("11", new BigDecimal("16.3"), "ASIAN"),
    ASIAN_SIZE_12("12", new BigDecimal("16.7"), "ASIAN"),
    ASIAN_SIZE_13("13", new BigDecimal("17.0"), "ASIAN"),
    ASIAN_SIZE_14("14", new BigDecimal("17.3"), "ASIAN"),
    ASIAN_SIZE_15("15", new BigDecimal("17.7"), "ASIAN"),
    ASIAN_SIZE_16("16", new BigDecimal("18.0"), "ASIAN"),
    ASIAN_SIZE_17("17", new BigDecimal("18.3"), "ASIAN"),
    ASIAN_SIZE_18("18", new BigDecimal("18.7"), "ASIAN"),
    ASIAN_SIZE_19("19", new BigDecimal("19.0"), "ASIAN"),
    ASIAN_SIZE_20("20", new BigDecimal("19.3"), "ASIAN"),
    ASIAN_SIZE_21("21", new BigDecimal("19.7"), "ASIAN"),
    ASIAN_SIZE_22("22", new BigDecimal("20.0"), "ASIAN"),
    ASIAN_SIZE_23("23", new BigDecimal("20.3"), "ASIAN"),
    ASIAN_SIZE_24("24", new BigDecimal("20.7"), "ASIAN"),
    ASIAN_SIZE_25("25", new BigDecimal("21.0"), "ASIAN"),
    ASIAN_SIZE_26("26", new BigDecimal("21.3"), "ASIAN"),

    // European Sizes (Note: These are diameter-based EUR sizes, not ISO 8601 circumference sizes)
    EUR_SIZE_47("47", new BigDecimal("14.96"), "EUR"),
    EUR_SIZE_48("48", new BigDecimal("15.28"), "EUR"),
    EUR_SIZE_49("49", new BigDecimal("15.60"), "EUR"),
    EUR_SIZE_50("50", new BigDecimal("15.92"), "EUR"),
    EUR_SIZE_51("51", new BigDecimal("16.23"), "EUR"),
    EUR_SIZE_52("52", new BigDecimal("16.55"), "EUR"),
    EUR_SIZE_53("53", new BigDecimal("16.87"), "EUR"),
    EUR_SIZE_54("54", new BigDecimal("17.19"), "EUR"),
    EUR_SIZE_55("55", new BigDecimal("17.51"), "EUR"),
    EUR_SIZE_56("56", new BigDecimal("17.82"), "EUR"),
    EUR_SIZE_57("57", new BigDecimal("18.14"), "EUR"),
    EUR_SIZE_58("58", new BigDecimal("18.46"), "EUR"),
    EUR_SIZE_59("59", new BigDecimal("18.78"), "EUR"),
    EUR_SIZE_60("60", new BigDecimal("19.10"), "EUR"),
    EUR_SIZE_61("61", new BigDecimal("19.41"), "EUR"),
    EUR_SIZE_62("62", new BigDecimal("19.73"), "EUR"),
    EUR_SIZE_63("63", new BigDecimal("20.05"), "EUR"),
    EUR_SIZE_64("64", new BigDecimal("20.37"), "EUR"),
    EUR_SIZE_65("65", new BigDecimal("20.69"), "EUR"),

    // North American Sizes
    NA_SIZE_4("4", new BigDecimal("14.9"), "NA"),
    NA_SIZE_4_5("4 1/2", new BigDecimal("15.3"), "NA"),
    NA_SIZE_5("5", new BigDecimal("15.7"), "NA"),
    NA_SIZE_5_5("5 1/2", new BigDecimal("16.1"), "NA"),
    NA_SIZE_6("6", new BigDecimal("16.5"), "NA"),
    NA_SIZE_6_5("6 1/2", new BigDecimal("16.9"), "NA"),
    NA_SIZE_7("7", new BigDecimal("17.3"), "NA"),
    NA_SIZE_7_5("7 1/2", new BigDecimal("17.7"), "NA"),
    NA_SIZE_8("8", new BigDecimal("18.2"), "NA"),
    NA_SIZE_8_5("8 1/2", new BigDecimal("18.6"), "NA"),
    NA_SIZE_9("9", new BigDecimal("19.0"), "NA"),
    NA_SIZE_9_5("9 1/2", new BigDecimal("19.4"), "NA"),
    NA_SIZE_10("10", new BigDecimal("19.8"), "NA"),
    NA_SIZE_10_5("10 1/2", new BigDecimal("20.2"), "NA"),
    NA_SIZE_11("11", new BigDecimal("20.6"), "NA"),
    NA_SIZE_11_5("11 1/2", new BigDecimal("21.0"), "NA"),
    NA_SIZE_12("12", new BigDecimal("21.4"), "NA"),
    NA_SIZE_12_5("12 1/2", new BigDecimal("21.8"), "NA"),
    NA_SIZE_13("13", new BigDecimal("22.2"), "NA"),

    // UK/Australian Sizes
    UK_AUS_SIZE_F("F", new BigDecimal("14.1"), "UK/AUS"),
    UK_AUS_SIZE_F_HALF("F 1/2", new BigDecimal("14.3"), "UK/AUS"),
    UK_AUS_SIZE_G("G", new BigDecimal("14.5"), "UK/AUS"),
    UK_AUS_SIZE_G_HALF("G 1/2", new BigDecimal("14.7"), "UK/AUS"),
    UK_AUS_SIZE_H("H", new BigDecimal("14.9"), "UK/AUS"),
    UK_AUS_SIZE_H_HALF("H 1/2", new BigDecimal("15.1"), "UK/AUS"),
    UK_AUS_SIZE_I("I", new BigDecimal("15.2"), "UK/AUS"),
    UK_AUS_SIZE_I_HALF("I 1/2", new BigDecimal("15.4"), "UK/AUS"),
    UK_AUS_SIZE_J("J", new BigDecimal("15.6"), "UK/AUS"),
    UK_AUS_SIZE_J_HALF("J 1/2", new BigDecimal("15.8"), "UK/AUS"),
    UK_AUS_SIZE_K("K", new BigDecimal("16.0"), "UK/AUS"),
    UK_AUS_SIZE_K_HALF("K 1/2", new BigDecimal("16.2"), "UK/AUS"),
    UK_AUS_SIZE_L("L", new BigDecimal("16.4"), "UK/AUS"),
    UK_AUS_SIZE_L_HALF("L 1/2", new BigDecimal("16.6"), "UK/AUS"),
    UK_AUS_SIZE_M("M", new BigDecimal("16.8"), "UK/AUS"),
    UK_AUS_SIZE_M_HALF("M 1/2", new BigDecimal("17.0"), "UK/AUS"),
    UK_AUS_SIZE_N("N", new BigDecimal("17.2"), "UK/AUS"),
    UK_AUS_SIZE_N_HALF("N 1/2", new BigDecimal("17.4"), "UK/AUS"),
    UK_AUS_SIZE_O("O", new BigDecimal("17.6"), "UK/AUS"),
    UK_AUS_SIZE_O_HALF("O 1/2", new BigDecimal("17.8"), "UK/AUS"),
    UK_AUS_SIZE_P("P", new BigDecimal("18.0"), "UK/AUS"),
    UK_AUS_SIZE_P_HALF("P 1/2", new BigDecimal("18.2"), "UK/AUS"),
    UK_AUS_SIZE_Q("Q", new BigDecimal("18.4"), "UK/AUS"),
    UK_AUS_SIZE_Q_HALF("Q 1/2", new BigDecimal("18.6"), "UK/AUS"),
    UK_AUS_SIZE_R("R", new BigDecimal("18.8"), "UK/AUS"),
    UK_AUS_SIZE_R_HALF("R 1/2", new BigDecimal("19.0"), "UK/AUS"),

    // German Sizes (Diameter is the size number)
    GERMAN_SIZE_15_0("15.0", new BigDecimal("15.0"), "GERMAN"),
    GERMAN_SIZE_15_5("15.5", new BigDecimal("15.5"), "GERMAN"),
    GERMAN_SIZE_16_0("16.0", new BigDecimal("16.0"), "GERMAN"),
    GERMAN_SIZE_16_5("16.5", new BigDecimal("16.5"), "GERMAN"),
    GERMAN_SIZE_17_0("17.0", new BigDecimal("17.0"), "GERMAN"),
    GERMAN_SIZE_17_5("17.5", new BigDecimal("17.5"), "GERMAN"),
    GERMAN_SIZE_18_0("18.0", new BigDecimal("18.0"), "GERMAN"),
    GERMAN_SIZE_18_5("18.5", new BigDecimal("18.5"), "GERMAN"),
    GERMAN_SIZE_19_0("19.0", new BigDecimal("19.0"), "GERMAN"),
    GERMAN_SIZE_19_5("19.5", new BigDecimal("19.5"), "GERMAN"),
    GERMAN_SIZE_20_0("20.0", new BigDecimal("20.0"), "GERMAN"),
    GERMAN_SIZE_20_5("20.5", new BigDecimal("20.5"), "GERMAN"),
    GERMAN_SIZE_21_0("21.0", new BigDecimal("21.0"), "GERMAN");

    private final String displayString;
    private final BigDecimal isoDiameterMm;
    private final String region;

    /** High-precision PI for accurate geometric calculations. */
    private static final BigDecimal PI = new BigDecimal("3.1415926535897932384626433832795028841971");
    /** Defines the standard scale for rounding public facing *display* measurements. */
    private static final int DISPLAY_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /** Composite-key reverse lookup: "region:displayString". */
    private static final Map<String, RingSize> LOOKUP_MAP;
    /** Optimized cache for regional size lists. */
    private static final Map<String, List<RingSize>> SIZES_BY_REGION;

    static {
        Map<String, RingSize> map = new HashMap<>();
        for (RingSize size : RingSize.values()) {
            String key = (size.region + ":" + size.displayString).toLowerCase();
            map.put(key, size);
        }
        LOOKUP_MAP = Collections.unmodifiableMap(map);

        // Optimization: Pre-calculate lists of sizes per region
        SIZES_BY_REGION = Collections.unmodifiableMap(
                Arrays.stream(RingSize.values())
                        .collect(Collectors.groupingBy(rs -> rs.region.toLowerCase()))
        );
    }

    RingSize(String displayString, BigDecimal isoDiameterMm, String region) {
        this.displayString = displayString;
        this.isoDiameterMm = isoDiameterMm;
        this.region = region;
    }

    /*** Gets the standard display string used in the enum's region (e.g., "7 1/2", "O", "54").
     * @return The size display string.*/
    public String getDisplayString() {
        return displayString;
    }

    /*** Gets the precise ISO standard internal diameter in millimeters.
     * @return The diameter as a BigDecimal.*/
    public BigDecimal getIsoDiameterMm() {
        // Return raw value for max internal precision, let consumer round for display
        return isoDiameterMm;
    }

    /*** Gets the region identifier (e.g., "NA", "EUR", "UK/AUS").
     * @return The region string.*/
    public String getRegion() {
        return region;
    }

    /**
     * Returns precise circumference in mm, calculated without premature rounding.
     * Consumer is responsible for rounding for display purposes if necessary.
     * @return The precise circumference as a BigDecimal.
     */
    public BigDecimal getCircumferenceMm() {
        // Return full precision result
        return isoDiameterMm.multiply(PI);
    }

    /*** Region-aware lookup using composite keys.
     * @param region The region identifier.
     * @param displayString The size display string within that region.
     * @return An Optional containing the matching RingSize, or empty if not found.*/
    public static Optional<RingSize> fromDisplayString(String region, String displayString) {
        if (region == null || displayString == null) return Optional.empty();
        String key = (region + ":" + displayString).toLowerCase();
        return Optional.ofNullable(LOOKUP_MAP.get(key));
    }

    /**
     * Converts the current size to the closest equivalent in another region
     * using minimum absolute difference in internal diameter (optimized using cache).
     * @param targetRegion The region to convert to (e.g., "NA").
     * @return An Optional containing the closest matching RingSize in the target region, or empty if no sizes exist in that region.
     */
    public Optional<RingSize> toRegion(String targetRegion) {
        if (targetRegion == null) return Optional.empty();

        // Use the pre-calculated, cached list of sizes
        List<RingSize> targetSizes = SIZES_BY_REGION.get(targetRegion.toLowerCase());

        if (targetSizes == null || targetSizes.isEmpty()) {
            return Optional.empty();
        }

        // Find the size with the minimum absolute difference in diameter
        return targetSizes.stream().min(Comparator.comparing(rs ->
                rs.isoDiameterMm.subtract(this.isoDiameterMm).abs()
        ));
    }

    @Override
    public String toString() {
        return String.format("%s (Diameter: %s mm, Region: %s)",
                displayString,
                isoDiameterMm.setScale(DISPLAY_SCALE, ROUNDING_MODE),
                region
        );
    }
}
