import org.apache.commons.math3.util.Pair;

/**
 * Created by maciej on 01.03.18.
 */
public class GenomeInCase {
    private final Genome genome;
    private final QapCase qapCase;
    private Integer evaluation;
    private boolean requiresNewEvaluation = true;

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

    private int evaluate() {
        return qapCase.evaluate(genome);
    }

    public void mutate() {
        genome.mutateBySingleSwap();
        requiresNewEvaluation = true;
    }

    public Pair<GenomeInCase, GenomeInCase> cross(GenomeInCase other) {
        Pair<Genome, Genome> children = genome.cross(other.genome);
        return new Pair<>(
                new GenomeInCase(children.getFirst(), qapCase), new GenomeInCase(children.getSecond(), qapCase)
        );
    }

    public boolean valid() {
        return qapCase.getN() == genome.getVector().size();
    }

}
