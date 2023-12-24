public class LifeSimulation {
    public static void main(String[] args) {

        Planet planet = new Planet();

        // Assumption: Populate the planet with 5 organisms initially
        for (int i = 0; i < 3; i++) {
            double randomPower = Math.random() * 100;
            Organism organism = new Organism(randomPower);
            planet.addOrganism(organism);
        }

        // Simulation loop

        while (!planet.isEquilibrium()) {

            planet.simulateLife();

        }

        System.out.println("Equilibrium reached. Life on the planet is at peace and sustainable.");
    }
}
