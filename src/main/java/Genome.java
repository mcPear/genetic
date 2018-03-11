
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.function.Supplier;

/**
 * Created by maciej on 01.03.18.
 */
public class Genome {
    private int n;
    private List<Integer> vector = new ArrayList<>();
    private Random random = new Random(System.currentTimeMillis());

    public Genome(int n) {
        this.n = n;
        for (int i = 1; i <= n; i++) {
            vector.add(i);
        }
        Collections.shuffle(vector);
    }

    public Genome(Genome other) {
        this.n = other.vector.size();
        other.vector.forEach(num -> vector.add(num));
    }

    public Genome(List<Integer> vector) {
        this.n = vector.size();
        this.vector = vector;
    }

    public void mutateGenes(Supplier<Boolean> possibilityFunction) {
        for (int i = 0; i < vector.size(); i++) {
            if (possibilityFunction.get()) {
                swapGene(i);
            }
        }
    }

    private void swapGene(int index) {
        int j;
        do {
            j = random.nextInt(n);
        }
        while (index == j);
        Collections.swap(vector, index, j);
    }

    public Pair<Genome, Genome> cross(Genome other) {
        if (vector.size() == 1 || vector.size() == 2) {
            return new Pair<>(this, other);
        }
        int half = n / 2;

//        System.out.println("Before crossover: "+this+" and "+other);

        List<Integer> child1 = new ArrayList<>(vector.subList(0, half));
        List<Integer> child2 = new ArrayList<>(other.vector.subList(0, half));
        child1.addAll(new ArrayList<>(other.vector.subList(half, other.vector.size())));
        child2.addAll(new ArrayList<>(vector.subList(half, vector.size())));
        Genome genome1 = new Genome(child1);
        Genome genome2 = new Genome(child2);
        fix(genome1);
        fix(genome2);
//        System.out.println("Crossover: "+this+" crossed with "+other+" gave "+genome1+" and "+genome2);
        return new Pair<>(genome1, genome2);
    }

    private void fix(Genome genome) {
        List<Integer> sortedCopyOfVector = copyAndSortVector(genome);
        List<Integer> duplicatedGenes = getDuplicatedGenes(sortedCopyOfVector);
        List<Integer> missingGenes = getMissingGenes(this.vector, genome.vector);
        replaceDuplicatedGenes(genome.vector, duplicatedGenes, missingGenes);
    }

    private List<Integer> copyAndSortVector(Genome genome) {
        List<Integer> result = new ArrayList<>(genome.getVector());
        Collections.sort(result);
        return result;
    }

    private List<Integer> getDuplicatedGenes(List<Integer> sortedVector) {
        List<Integer> duplicates = new ArrayList<>();
        for (int i = 0; i < sortedVector.size() - 1; i++) {
            if (Objects.equals(sortedVector.get(i), sortedVector.get(i + 1))) {
                duplicates.add(sortedVector.get(i));
            }
        }
        return duplicates;
    }

    private List<Integer> getMissingGenes(List<Integer> correctVector, List<Integer> brokenVector) {
        List<Integer> missingGenes = new ArrayList<>();
        correctVector.forEach(correctGene -> {
            if (!brokenVector.contains(correctGene)) {
                missingGenes.add(correctGene);
            }
        });
        return missingGenes;
    }

    private void replaceDuplicatedGenes(List<Integer> vector, List<Integer> duplicatedGenes, List<Integer> missingGenes) {
        final int FIRST_INDEX = 0;
        for (int i = 0; i < vector.size(); i++) {
            Integer currentGene = vector.get(i);
            if (duplicatedGenes.contains(currentGene)) { //FIXME połowa pierwszego genomu ulegnie zmianie, połowa drugiego się nie zmieni
                vector.set(i, missingGenes.get(FIRST_INDEX));
                duplicatedGenes.remove(currentGene);
                missingGenes.remove(FIRST_INDEX);
            }
        }
    }


    public List<Integer> getVector() {
        return vector;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Genome: \n\n");
        vector.forEach(num -> result.append(num + " "));
        return result.toString();
    }
}
