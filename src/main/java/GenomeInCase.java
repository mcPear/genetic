import org.apache.commons.math3.util.Pair;

import java.util.function.Supplier;

/**
 * Created by maciej on 01.03.18.
 */
public class GenomeInCase implements Comparable<GenomeInCase> {
    private final Genome genome;
    private final QapCase qapCase;
    private Integer evaluation;
    private boolean requiresNewEvaluation = true;
    private double currentRouletteProbability = 0;

    public GenomeInCase(Genome genome, QapCase qapCase) {
        this.genome = genome;
        this.qapCase = qapCase;
    }

    public Integer getEvaluation() {
        if (evaluation == null || requiresNewEvaluation) {
            evaluation = evaluate();
            requiresNewEvaluation = false;
        }
        return evaluation;
    }

    public Double getFitness() {
        return 10_000d / getEvaluation();
    }

    private int evaluate() {
        return qapCase.evaluate(genome);
    }

    public void mutateGenes(Supplier<Boolean> possibilityFunction) {
        genome.mutateGenes(possibilityFunction);
        requiresNewEvaluation = true;
    }

    public Pair<GenomeInCase, GenomeInCase> cross(GenomeInCase other) {
        Pair<Genome, Genome> children = genome.cross(other.genome);
        return new Pair<>(
                new GenomeInCase(children.getFirst(), qapCase), new GenomeInCase(children.getSecond(), qapCase)
        );
    }

    public double getCurrentRouletteProbability() {
        return currentRouletteProbability;
    }

    public void setCurrentRouletteProbability(double currentRouletteProbability) {
        this.currentRouletteProbability = currentRouletteProbability;
    }

    public boolean valid() {
        return qapCase.getN() == genome.getVector().size();
    }

    @Override
    public int compareTo(GenomeInCase o) {
        return this.getEvaluation().compareTo(o.getEvaluation());
    }
}
