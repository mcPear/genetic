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

    public GenomeInCase cross(GenomeInCase other) {
        return new GenomeInCase(genome.cross(other.genome), qapCase);
    }

    public boolean valid(){
        return qapCase.getN() == genome.getVector().size();
    }

}
