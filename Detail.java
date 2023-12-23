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
        return age >= 3;
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
        // interactions.put(otherId, interactions.getOrDefault(0,
        // interactions.get(otherId)) + 1);
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
        this.setInteractions(other.getId());
        other.setInteractions(this.getId());

        // second interation
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
    // private int ageOfPlanet;
    private int firstGen;

    public Planet(int size) {
        this.organisms = new ArrayList<>();
        this.firstGen = size;
    }

    public void addOrganism(Organism organism) {
        organisms.add(organism);
    }

    public void simulateLife() {

        // pandemic logic
        if (organisms.size() >= 20) {
            int counter = 0;
            // these more than 20 should be killed in pandemic
            while (counter < organisms.size() - 20) {
                Random random = new Random();
                Organism orgToKill = organisms.get(random.nextInt(organisms.size()));
                if (orgToKill.isAlive()) {
                    counter++;
                    organisms.remove(orgToKill);
                }
            }
        }

        // is eventual death
        for (Organism organism : organisms) {
            System.out.println("age of " + organism.getId() + " " + organism.getAge());
            organism.setAge();

            if (organism.isMaxAge()) {
                System.out.println("death of " + organism.getAge());
                organism.death();
            }

        }

        // interaction logic
        for (Organism organism : organisms) {
            if (organism.isAlive()) {
                Organism randomOrganism = getRandomOrganismExcept(organism);
                if (randomOrganism != null) {
                    organism.interactWith(randomOrganism);
                }
            }

        }

        // reprod logic
        int reprodCounter = 0;
        while (reprodCounter <= 4) {
            Random random = new Random();
            Organism parentOrg = organisms.get(random.nextInt(organisms.size()));

            if (parentOrg.isAlive() && !parentOrg.hasReproduced()) {
                Organism newOrganism = new Organism(parentOrg.getPower());
                organisms.add(newOrganism);
                parentOrg.reproduce();
                reprodCounter++;
            }
        }

    }

    private Organism getRandomOrganismExcept(Organism excludeOrganism) {
        List<Organism> validOrganisms = new ArrayList<>();
        for (Organism org : organisms) {
            if (org.isAlive()) {
                validOrganisms.add(org);
            }
        }

        validOrganisms.remove(excludeOrganism);

        if (validOrganisms.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return validOrganisms.get(random.nextInt(validOrganisms.size()));
    }

    public boolean isEquilibrium() {
        int livingOrganisms = 0;
        for (Organism organism : organisms) {
            if (organism.isAlive()) {
                livingOrganisms++;
            }
        }
        return livingOrganisms == organisms.size() / 2;
    }
}