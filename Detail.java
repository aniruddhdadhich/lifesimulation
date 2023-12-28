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
    public double REPRODUCTION_RATE;
    public double DEATH_RATE;
    public double INTERACTION_RATE;

    public Planet(int pioneers) {
        this.organisms = new ArrayList<>();
        this.consecutiveStableCount = 0;
        this.cycle = 0;
        this.MAX_ORGANISMS = pioneers * 1000;
        this.REPRODUCTION_RATE = 0.4;
        this.DEATH_RATE = 0.8;
        this.INTERACTION_RATE = 0.8;
    }

    public int getOrganisms() { // Returns no. of alive organisms on the planet
        if (organisms.isEmpty()) {
            System.out.println("No Organisms are present in planet");
            return 0;
        }
        System.out.println();
        return organisms.size();
    }

    public void addOrganism(Organism organism) {
        organisms.add(organism);
    }

    public void removeOrganism(Organism organism) {
        organisms.remove(organism);
    }

    public void simulateLife() {
        List<Organism> organismsToRemove = new ArrayList<>();
        List<Organism> organismsToAdd = new ArrayList<>();

        System.out.println("cycle no. = " + cycle);
        System.out.println("Population count :" + organisms.size());

        // Population Limit: If population grows more than the limit - reset to 70% of
        // Max.
        if (organisms.size() >= MAX_ORGANISMS) {
            int excessOrganisms = organisms.size() - MAX_ORGANISMS;
            double populationToWipe = excessOrganisms + 0.3 * MAX_ORGANISMS;

            // randomly eliminate these many organisms
            while (organismsToRemove.size() < populationToWipe) {
                Random random = new Random();
                Organism orgToKill = organisms.get(random.nextInt(organisms.size()));
                organismsToRemove.add(orgToKill);
            }

            // decrease reproduction rate + increase elimination rates
            REPRODUCTION_RATE = 0.25;
            DEATH_RATE = 1;
            INTERACTION_RATE = 1;

        }

        // Eventual Death - if more than max age then 80% chances that organism will
        // die.
        for (Organism organism : organisms) {
            organism.setAge(); 
            if (organism.isMaxAge()) {
                double deathProbability = Math.random();
                if (deathProbability < DEATH_RATE) {
                    organism.death();
                    organismsToRemove.add(organism);
                    // System.out.println("Death - " + organism.getId() )
                }
            }
        }

        // remove organisms marked for removal
        organismsToRemove.forEach(this::removeOrganism);

        // Interaction Logic
        for (Organism organism : organisms) {
            double interactionProb = Math.random();
            if (interactionProb < INTERACTION_RATE) {
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

        // remove organisms marked for removal
        organismsToRemove.forEach(this::removeOrganism);

        // reprod logic
        for (Organism parentOrg : organisms) {
            if (parentOrg.canReproduce()) {
                double reproductionProb = Math.random();
                // 40% chances to reproduce
                if (reproductionProb < REPRODUCTION_RATE) {
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
        List<Organism> validOrganisms = new ArrayList<>(organisms);
        validOrganisms.remove(excludeOrganism);

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
            // Assuming equilibrium is reached if the population is stable for at least 10
            // cycles
            return consecutiveStableCount >= 10;
        }

    }
}