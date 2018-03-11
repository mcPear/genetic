import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RandomRun {

    private final int maxGenerationsCount;
    private final int populationSize;
    private final int minGenomeEvaluation;
    private final QapCase qapCase;

    private List<GenomeInCase> currentPopulation;
    private int counter = 0;
    private GAEvaluationResult currentEvaluationResult;
    private GAEvaluationResult bestEvaluationResult;
    private GARunResult runResult = new GARunResult();

    public RandomRun(int maxGenerationsCount, int populationSize, int minGenomeEvaluation, QapCase qapCase) {
        this.maxGenerationsCount = maxGenerationsCount;
        this.populationSize = populationSize;
        this.minGenomeEvaluation = minGenomeEvaluation;
        this.qapCase = qapCase;
    }

    public GAEvaluationResult run() throws IOException {
        resetCounter();
        initialisePopulation();
        evaluate();

        while (!isStopConditionSatisfied()) {
            System.out.println("\nEvaluation no." + counter + " : " + currentEvaluationResult.best);
            initialisePopulation();
            incrementCounter();
            evaluate();
        }
        logResult();
        return bestEvaluationResult;
    }

    private void initialisePopulation() {
        currentPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            currentPopulation.add(new GenomeInCase(new Genome(qapCase.getN()), qapCase));
        }
    }

    private void evaluate() {
        currentEvaluationResult = new GAEvaluationResult(getEvaluationList());
        if (bestEvaluationResult == null || currentEvaluationResult.lowerThan(bestEvaluationResult)) {
            bestEvaluationResult = currentEvaluationResult;
        }
        runResult.add(currentEvaluationResult);
    }

    private List<Integer> getEvaluationList() {
        List<Integer> evaluationList = new ArrayList<>();
        for (int i = 0; i < currentPopulation.size(); i++) {
            evaluationList.add(currentPopulation.get(i).getEvaluation());
        }
        return evaluationList;
    }

    private void resetCounter() {
        counter = 0;
    }

    private void incrementCounter() {
        counter++;
    }

    private boolean isStopConditionSatisfied() {
        return maxGenerationsCount <= counter || (bestEvaluationResult != null && bestEvaluationResult.best <= minGenomeEvaluation);
    }

    private void logResult() throws IOException {
        FileUtils.writeStringToFile(new File("./results/" + getFileName()), runResult.toCsvString());
    }

    private String getFileName() {
        final char DEL = '_';

        return "random-run" + DEL + getTime() + DEL + "gen" + maxGenerationsCount + DEL + "pop" + populationSize + DEL + "cross" +
                "min" + minGenomeEvaluation + DEL + "n" + qapCase.getN() + ".csv";
    }

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}

