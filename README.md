# Life Simulation on a New Planet

## Overview
This life simulation program depicts the evolution of organisms on a new planet. Organisms interact, reproduce, and age over iterations, leading to a dynamic ecosystem.

## Assumptions
1. **Pioneer Organisms:** The simulation begins with 10 pioneer organisms on the planet.
2. **Stability Check:** After 50 iterations, a stability check occurs. If the population remains stable (more than 60% of the maximum organism limit) for 10 consecutive iterations, the planet is considered at equilibrium.
3. **Time Unit:** Each execution of `simulateLife()` represents the passage of one time unit, aging each organism by one unit.
4. **Default Rates:**
    - **Reproduction Rate (40%):** Each organism has a 40% chance of reproducing in a given iteration.
    - **Death Rate (80%):** Organisms crossing the age limit of 20 units have an 80% chance of dying.
    - **Interaction Rate (80%):** Each organism has an 80% chance of interacting with another random organism.
5. **Pandemic Situation:** Rates are adjusted during a pandemic: increased death and interaction rates, and decreased reproduction rates.

## Laws
1. **Survival of the Fittest:** Stronger organisms eliminate weaker ones in the second interaction.
2. **Life Cycle:** All living organisms eventually die.
3. **Population Limit:** The planet can sustain 10,000 organisms. If the population exceeds this limit, a pandemic occurs, resetting the population to 70% of the maximum.
4. **Reproduction Limit:** An organism can reproduce only twice in its lifetime.

## Simulation Logic
- The simulation comprises a `Planet` class and an `Organism` class.
- Organisms interact, reproduce, and age over time.
- The simulation continues until stability is achieved or a mass extinction event occurs.
