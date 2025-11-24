WIP



Working on domain layer atm

NOTE TO SELF:
Complete dmoney submodule and incorporate JavaMoney Library into imoney submodule.
Test conversions in WeightUnitVO.java and WeightVO if it reflects real world math.


Key Insights for Handling Money and Currency Conversions

    Avoid Floating Point Numbers:
        Floating point arithmetic can introduce inaccuracies in monetary calculations. Use integers or fixed-decimal types instead.

    Use Integer Representation:
        Store values as integers representing the smallest currency units (e.g., cents) to ensure accuracy.

    Calculation Approach:
        When performing calculations:
            Convert to floating point for calculations.
            Round the result using a mathematical function (not just casting).
            Convert back to an integer.

    Store Currency Type with Value:
        Always store the currency code (e.g., USD, EUR) alongside the monetary value for proper identification.

    Additional Context Storage:
        Consider storing whether a value is before or after tax, and track original currencies for conversions.

    Know the Accuracy Bounds:
        Ensure that values don't exceed the smallest units of the currency (e.g., no values smaller than a penny, yen, etc.).

    Consider Conversion Precision:
        If managing multiple currencies, assess the precision needed for conversions. Use:
            Decimal(x, 2) for single currency systems.
            Decimal(x, 3) or higher for systems requiring frequent conversions, especially in volatile economic contexts.

    Dynamic Exchange Rates:
        Allow for frequent updates to exchange rates in your system to account for rapid changes, particularly in unstable economies.

    Fixed-Point Types:
        Use fixed-point types like DECIMAL to avoid inaccuracies associated with floating point types.

    Reporting Compliance:
        Ensure accurate conversions and calculations are crucial for financial reporting and compliance.

    Example Conversion Calculation:
        Highlight specific conversion scenarios, such as converting 100.15 VES to USD under current rates, demonstrating the need for precision.

    Precision for Hyperinflation:
        In instances of hyperinflation (like Venezuela), consider using higher decimal places (e.g., Decimal(x, 6)) to accommodate extreme conversion rates (e.g., 1 USD = 40 VES).
