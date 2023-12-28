import java.util.ArrayList;
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
    private int reprodCount;
    private Map<Integer, Integer> interactions = new HashMap<>();

    public Organism(double power) {
        this.id = nextId++;
        this.power = power;
        this.alive = true;
        this.age = 0;
        this.reprodCount = 0;
    }

    public int getId() {
        return id;
    }

    public double getPower() {
        return power;
    }

    public boolean canReproduce() {
        return reprodCount < 2;
    }

    public void setReprodCount() {
        reprodCount++;
    }

    public int getAge() {
        return age;
    }

    public void setAge() {
        age++;
    }

    public boolean isMaxAge() {
        return age >= 20;
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

    public void reproduce() {
        setReprodCount();
    }

    public void interactWith(Organism other) {
        // count of interaction from both ends
        this.setInteractions(other.getId()); // e.g. if 1 --> 3
        other.setInteractions(this.getId()); // then 3 --> 1 also

        // second interation - elimination based on power gradient
        if (this.getInteractions(other.id) > 1) {
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
    private int MAX_ORGANISMS;
    private int consecutiveStableCount;
    public int cycle;
    public double reprodRate;
    public double deathRate;
    public double interactionRate;

    public Planet(int pioneers) {
        this.organisms = new ArrayList<>();
        this.consecutiveStableCount = 0;
        this.cycle = 0;
        this.MAX_ORGANISMS = pioneers * 1000;
        this.reprodRate = 0.5;
        this.deathRate = 0.8;
        this.interactionRate = 0.8;

    }

    public int getOrganisms() {
        if (organisms.isEmpty()) {
            System.out.println("No Organisms are present in planet");
            return 0;
        }
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
        System.out.println("Population count :" + organisms.size());

        // // dynamic rates?
        // if (organisms.size() < 0.4 * MAX_ORGANISMS) {
        // reprodRate = 0.9;
        // deathRate = 0.2;
        // interactionRate = 0.1;
        // } else if (organisms.size() >= 0.4 * MAX_ORGANISMS && organisms.size() < 0.6
        // * MAX_ORGANISMS) {
        // reprodRate = 0.7;
        // deathRate = 0.4;
        // interactionRate = 0.4;
        // }
        // // else{
        // // reprodRate = 0.2;
        // // deathRate = 1;
        // // interactionRate = 1;
        // // }

        // Planet population limit logic => A Pandemic situation
        if (organisms.size() >= 0.95 * MAX_ORGANISMS) {
            System.out.println("Tipped the max");
            int excessOrganisms = organisms.size() - MAX_ORGANISMS;
            double populationToWipe = excessOrganisms + 0.3 * MAX_ORGANISMS;
            // these more than Max must be killed in pandemic
            while (organismsToRemove.size() < populationToWipe) {
                Random random = new Random();
                Organism orgToKill = organisms.get(random.nextInt(organisms.size()));
                // System.out.println("extra guy " + orgToKill.getId());
                organismsToRemove.add(orgToKill);
            }
            reprodRate = 0.2;
            deathRate = 1;
            interactionRate = 1;

        }

        // Eventual death logic at max Age
        for (Organism organism : organisms) {
            organism.setAge(); // in each occurrence of simluteLife each org's age++
            if (organism.isMaxAge()) {
                double deathProbability = Math.random();
                if (deathProbability < deathRate) {
                    organism.death();
                    organismsToRemove.add(organism);
                    // System.out.println("Death - " + organism.getId() + " at age " +
                    // organism.getAge());
                }

            }

        }

        // remove organisms marked for removal
        organismsToRemove.forEach(this::removeOrganism);

        // Interaction Logic
        for (Organism organism : organisms) {
            if (organism.isAlive()) {
                double interactionProb = Math.random();
                if (interactionProb < interactionRate) {
                    Organism randomOrganism = getRandomOrganismExcept(organism);
                    if (randomOrganism != null) {
                        organism.interactWith(randomOrganism);
                        // System.out.println(organism.getId() + " interact with " +
                        // randomOrganism.getId());
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
            if (parentOrg.canReproduce()) {
                double reproductionProb = Math.random();
                // if less then 0.3 => no reproduction
                if (reproductionProb < reprodRate) {
                    double randomPower = Math.random() * 100;
                    Organism newOrganism = new Organism(randomPower);
                    organismsToAdd.add(newOrganism);
                    // System.out.println("New Org born " + newOrganism.getId());
                    parentOrg.reproduce();
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
        int minHealthyPopulation = (int) (MAX_ORGANISMS * 0.6);

        // Have the planet atleast existed for 50 cycles
        if (cycle < 50) {
            return false;
        } else {

            // if yes, then is population healthy & stable
            boolean isPopulationHealthy = (organisms.size() >= minHealthyPopulation);
            if (!isPopulationHealthy) {
                consecutiveStableCount = 0; // Reset the counter if the population not stable
            } else {
                System.out.println("Healthy Population " + " STABLE COUNT ---> " + (consecutiveStableCount + 1));
                consecutiveStableCount++;
            }
            // Assuming equilibrium is reached if the population is stable for at least 10 cycles
            return consecutiveStableCount >= 10;
        }

    }
}