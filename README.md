WIP



Working on domain layer atm

NOTE TO SELF:
Test conversions in WeightUnitVO.java and WeightVO if it reflects real world math.
Add JavaMoney Library individually to each service module that needs it.
    > I decided not to create a common or shared module to avoid unintended transitive coupling of 
    my spring modules.
on dproduct.model.entities each of the polymorphed variant classes has duplicate lines on the method 
"hasSameAttributes". Could use some optimization.

I need to implement enums for ring sizes as there are floating point regional standards to use.
All other jewelry sizes can continue using BigDecimal.


