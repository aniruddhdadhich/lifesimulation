import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Organism {
    private static int nextId = 1;

    private int id;
    private double power;
    private boolean alive;
    private int age;
    private boolean hasReproduced;
    private Map<Integer, Integer> interactions = new HashMap<>();

    public Organism(double power) {
        this.id = nextId++;
        this.power = power;
        this.alive = true;
        this.age = 0;
        this.hasReproduced = false;
    }

    public int getId() {
        return id;
    }

    public double getPower() {
        return power;
    }

    public int getAge() {
        return age;
    }

    public void setAge() {
        age++;
    }

    public boolean isMaxAge() {
        return age >= 15; // max age for an org can be 5 units - after this org dies
    }

    public int getInteractions(int otherId) {
        if (interactions.containsKey(otherId)) {
            return interactions.get(otherId);
        } else {
            return 0;
        }

    }

    public void setInteractions(int otherId) {
        if (interactions.containsKey(otherId)) {
            interactions.put(otherId, interactions.get(otherId) + 1);
        } else {
            interactions.put(otherId, 1);
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void death() {
        alive = false;

    }

    public boolean hasReproduced() {
        return hasReproduced;
    }

    public void reproduce() {
        if (!hasReproduced) {
            hasReproduced = true;
        }
    }

    public void interactWith(Organism other) {
        // count of interaction from both ends
        this.setInteractions(other.getId()); // 1 --> 3
        other.setInteractions(this.getId()); // 3 --> 1

        // second interation
        if (this.getInteractions(other.id) > 1) { // if second interaction then use elimination logic
            if (this.power > other.power) {
                other.death();
            } else {
                this.death();
            }
        }

    }
}

class Planet {
    private List<Organism> organisms;

    private int MAX_ORGANISMS ; // maximum organism that are supported on planet for equilibrium, more than that
                                      // will be killed planet  250 - 900

    // private int previousPopulationSize;
    private int consecutiveStableCount;
    private int cycle;

    public Planet(int pioneers) {
        this.organisms = new ArrayList<>();
        // this.previousPopulationSize = 0;
        this.consecutiveStableCount = 0;
        this.cycle = 0;
        this.MAX_ORGANISMS = pioneers*10;

    }

    public int getOrganisms() {
        if (organisms.isEmpty()) {
            System.out.println("No Organisms are present in planet");
            return 0;
        }
        // for (int i = 0; i < organisms.size(); i++) {
        //     System.out.print(organisms.get(i).getId() + " ");
        // }
        System.out.println();
        return organisms.size();
    }

    public void addOrganism(Organism organism) { // adding new born orgs to the list
        organisms.add(organism);
    }

    public void removeOrganism(Organism organism) { // removing dead ones from the list
        organisms.remove(organism);
    }

    public void simulateLife() {

        List<Organism> organismsToRemove = new ArrayList<>();
        List<Organism> organismsToAdd = new ArrayList<>();

        System.out.println("cycle no. = " + cycle);

        // Planet population limit logic
        if (organisms.size() >= MAX_ORGANISMS) {
            consecutiveStableCount = 0;
            System.out.println("Tipped the max");
            int excessOrganisms = organisms.size() - MAX_ORGANISMS;
            // these more than Max must be killed in pandemic
            for (int i = 0; i < excessOrganisms; i++) {
                Random random = new Random();
                Organism orgToKill = organisms.get(random.nextInt(organisms.size()));
                // System.out.println("extra guy " + orgToKill.getId());
                organismsToRemove.add(orgToKill);
            }
        }

        // Eventual death logic at max Age
        for (Organism organism : organisms) {
            organism.setAge(); // in each occurrence of simluteLife each org's age++
            if (organism.isMaxAge()) {
                organism.death();
                organismsToRemove.add(organism);
                // System.out.println("Death - " + organism.getId() + " at age " + organism.getAge());
            }

        }

        // remove organisms marked for removal
        organismsToRemove.forEach(this::removeOrganism);

        // Interaction Logic
        for (Organism organism : organisms) {
            if (organism.isAlive()) {
                double interactionProb = Math.random();
                if (interactionProb < 0.3) {
                    Organism randomOrganism = getRandomOrganismExcept(organism);
                    if (randomOrganism != null) {
                        organism.interactWith(randomOrganism);
                        // System.out.println(organism.getId() + " interact with " + randomOrganism.getId());
                        if (!organism.isAlive()) {
                            organismsToRemove.add(organism);
                            // System.out.println(organism.getId() + " dies in interaction");
                        }
                        if (!randomOrganism.isAlive()) {
                            organismsToRemove.add(randomOrganism);
                            // System.out.println(randomOrganism.getId() + " dies in interaction");
                        }
                    }
                }

            }

        }

        // remove organisms marked for removal
        organismsToRemove.forEach(this::removeOrganism);

        // reprod logic
        for (Organism parentOrg : organisms) {
            if (!parentOrg.hasReproduced()) {
                double reproductionProb = Math.random();
                // Introduce a probability-based reproduction rate
                if (reproductionProb < 0.45 && organisms.size() < MAX_ORGANISMS) {
                    double randomPower = Math.random() * 100;
                    Organism newOrganism = new Organism(randomPower);
                    organismsToAdd.add(newOrganism);
                    // System.out.println("New Org born " + newOrganism.getId());
                    parentOrg.reproduce(); // now this guy can't reprod again
                }
            }
        }

        // add organisms marked for addition
        organismsToAdd.forEach(this::addOrganism);

        // increase cycle
        cycle++;

    }

    private Organism getRandomOrganismExcept(Organism excludeOrganism) {
        List<Organism> validOrganisms = new ArrayList<>();

        // check if the organism we are selecting for interaction is alive and not the
        // same one as the first guy.
        for (Organism organism : organisms) {
            if (organism.isAlive() && organism != excludeOrganism) {
                validOrganisms.add(organism);
            }
        }

        if (validOrganisms.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return validOrganisms.get(random.nextInt(validOrganisms.size()));
    }

    public boolean isEquilibrium() {
        int minHealthyPopulation = (int) (MAX_ORGANISMS * 0.6) ;
        int maxHealthyPopulation = (int) (MAX_ORGANISMS * 0.95);
       
        // is population stable
        boolean isPopulationHealthy = (organisms.size() >= minHealthyPopulation)
                && (organisms.size() <= maxHealthyPopulation);
        if (!isPopulationHealthy) {
            consecutiveStableCount = 0; // Reset the counter if the population not stable
        } else {
            System.out.println("Population HEALTHY HAI MERE BHAI"+ " STABLE COUNT ---> "+ (consecutiveStableCount+1));
            consecutiveStableCount++;
        }
       
        

        // Assuming equilibrium is reached if the population is stable for at least 5
        // times
        return consecutiveStableCount >= 10;
    }
}