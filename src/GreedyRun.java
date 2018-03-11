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

    public GAEvaluationResult run() throws IOException {
        List<Genome> greedygenomes =
        return bestEvaluationResult;
    }

    private List<Integer> greedyFulfillGenome(){

    }

    private List<Integer> getEvaluationList() {
        List<Integer> evaluationList = new ArrayList<>();
        for (int i = 0; i < currentPopulation.size(); i++) {
            evaluationList.add(currentPopulation.get(i).getEvaluation());
        }
        return evaluationList;
    }

}

