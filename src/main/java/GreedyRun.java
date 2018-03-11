import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GreedyRun {

    private final QapCase qapCase;

    private List<GenomeInCase> currentPopulation;
    private int counter = 0;
    private GAEvaluationResult currentEvaluationResult;
    private GAEvaluationResult bestEvaluationResult;
    private GARunResult runResult = new GARunResult();

    public GreedyRun(QapCase qapCase) {
        this.qapCase = qapCase;
    }

//    public GAEvaluationResult run() throws IOException {
//        List<Genome> greedyGenomes = findAllGreedyGenomes(qapCase.getN());
//        return bestEvaluationResult;
//    }
//
//    private Genome findTheBestGenome(List<Genome> genomes) {
//        //return genomes.stream().map(g -> new GenomeInCase(g, qapCase)).mapToInt(GenomeInCase::getEvaluation).min();
//    }
//
//    private List<Genome> findAllGreedyGenomes(int n) {
//        List<Genome> greedyGenomes = new ArrayList<>();
//        for (int i = 1; i <= n; i++) {
//            greedyGenomes.add(new Genome(greedyFulfillVector(new ArrayList<>(), n)));
//        }
//        return greedyGenomes;
//    }
//
//    private List<Integer> greedyFulfillVector(List<Integer> subVector, int n) {
//
//    }

    private List<Integer> getEvaluationList() {
        List<Integer> evaluationList = new ArrayList<>();
        for (int i = 0; i < currentPopulation.size(); i++) {
            evaluationList.add(currentPopulation.get(i).getEvaluation());
        }
        return evaluationList;
    }

}

