public class LifeSimulation {
    public static void main(String[] args) {
        Planet planet = new Planet();

        // Assumption: Populate the planet with 10 organisms initially
        for (int i = 0; i < 10; i++) {
            double randomStrength = Math.random() * 100;
            Organism organism = new Organism(randomStrength);
            planet.addOrganism(organism);
        }

        // Simulation loop
        while (!planet.isEquilibrium()) {
            planet.simulateLife();
        }

        System.out.println("Equilibrium reached. Life on the planet is at peace and sustainable.");
    }
}
