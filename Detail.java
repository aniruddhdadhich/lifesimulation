import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Organism {
    private static int nextId = 1;

    private int id;
    private double strength;
    private boolean alive;
    private boolean hasReproduced;

    public Organism(double strength) {
        this.id = nextId++;
        this.strength = strength;
        this.alive = true;
        this.hasReproduced = false;
    }

    public int getId() {
        return id;
    }

    public double getStrength() {
        return strength;
    }

    public boolean isAlive() {
        return alive;
    }

    public void eliminate() {
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
        if (this.strength > other.strength) {
            other.eliminate();
        } else {
            this.eliminate();
        }
    }
}

class Planet {
    private List<Organism> organisms;

    public Planet() {
        this.organisms = new ArrayList<>();
    }

    public void addOrganism(Organism organism) {
        organisms.add(organism);
    }

    public void simulateLife() {
        for (Organism organism : organisms) {
            if (organism.isAlive()) {
                Organism randomOrganism = getRandomOrganismExcept(organism);
                if (randomOrganism != null) {
                    organism.interactWith(randomOrganism);
                }
            }
        }

        for (Organism organism : new ArrayList<>(organisms)) {
            if (organism.isAlive() && !organism.hasReproduced()) {
                Organism newOrganism = new Organism(organism.getStrength());
                organisms.add(newOrganism);
                organism.reproduce();
            }
        }
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
        int livingOrganisms = 0;
        for (Organism organism : organisms) {
            if (organism.isAlive()) {
                livingOrganisms++;
            }
        }
        return livingOrganisms == organisms.size() / 2;
    }
}

