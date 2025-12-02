WIP



Working on domain layer atm

NOTE TO SELF:
Test conversions in WeightUnitVO.java and WeightVO if it reflects real world math.
Double Check International Standards for:
    > Ring Sizes
Add JavaMoney Library individually to each service module that needs it.
    > I decided not to create a common or shared module to avoid unintended transitive coupling of 
    my spring modules.
on dproduct.model.entities each of the polymorphed variant classes has duplicate lines on the method 
"hasSameAttributes". Could use some optimization.


