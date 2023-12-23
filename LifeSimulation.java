public class LifeSimulation {
    public static void main(String[] args) {
        int firstGen = 7;
        Planet planet = new Planet(firstGen);

        // Assumption: Populate the planet with 10 organisms initially
        for (int i = 0; i < firstGen; i++) {
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
