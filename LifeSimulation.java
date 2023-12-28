public class LifeSimulation {
    public static void main(String[] args) {

        int pioneers = 10;  // 
        Planet planet = new Planet(pioneers);

        // Assumption: Populate the planet with 5 organisms initially
        for (int i = 0; i < pioneers; i++) {
            double randomPower = Math.random() * 100;
            Organism organism = new Organism(randomPower); 
            planet.addOrganism(organism);
        }

        // Simulation loop
        boolean apocalypseFlag = false;
        while (!planet.isEquilibrium()) {
            planet.simulateLife();
            // if(planet.cycle>40){
            //     break;
            // }
            if(planet.getOrganisms()==0){
                apocalypseFlag  = true;
                break;
            }
        }
        if(apocalypseFlag){
            System.out.println("Mass Extinction Event");
        }
        else{
            System.out.println("Equilibrium reached. Life on the planet is at peace and sustainable.");
        }
        
        
    }
}
