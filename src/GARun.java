import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by maciej on 02.03.18.
 */
public class GARun {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final int FULL_CHANCE_PERCENT = 100;
    private final int maxGenerationsCount;
    private final int populationSize;
    private final int crossoverChancePercent;
    private final int mutationChancePercent;
    private final int tournamentSize;
    private final int minGenomeEvaluation;
    private final boolean useTournament;
    private final QapCase qapCase;

    private List<GenomeInCase> currentPopulation;
    private int counter = 0;
    private GAEvaluationResult currentEvaluationResult;
    private GAEvaluationResult bestEvaluationResult;
    private GARunResult runResult = new GARunResult();

    public GARun(int maxGenerationsCount, int populationSize, int crossoverChancePercent, int mutationChancePercent,
                 int tournamentSize, int minGenomeEvaluation, boolean useTournament, QapCase qapCase) {
        this.maxGenerationsCount = maxGenerationsCount;
        this.populationSize = populationSize;
        this.crossoverChancePercent = crossoverChancePercent;
        this.mutationChancePercent = mutationChancePercent;
        this.tournamentSize = tournamentSize;
        this.minGenomeEvaluation = minGenomeEvaluation;
        this.useTournament = useTournament;
        this.qapCase = qapCase;
    }

    public GAEvaluationResult run() throws IOException {
        resetCounter();
        initialisePopulation();
        evaluate();

        while (!isStopConditionSatisfied()) {
            System.out.println("\nEvaluation no." + counter + " : " + currentEvaluationResult.best);
            selection();
            crossover();
            mutation();
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

    private void selection() {
        if (useTournament) {
            tournamentSelection();
        } else {
            rouletteSelection();
        }
    }

    private void tournamentSelection() {
        List<GenomeInCase> selectedPopulation = new ArrayList<>();
        while (selectedPopulation.size() < populationSize) {
            selectedPopulation.add(getTournamentWinner());
        }
        currentPopulation = selectedPopulation;
    }

    private GenomeInCase getTournamentWinner() {
        GenomeInCase winner = getRandomGenomeInCase();
        for (int i = 1; i < tournamentSize; i++) {
            GenomeInCase currentGenomeInCase = getRandomGenomeInCase();
            if (currentGenomeInCase.getEvaluation() < winner.getEvaluation()) {
                winner = currentGenomeInCase;
            }
        }
        return winner;
    }

    private void rouletteSelection() {
        double minFitness = currentPopulation.stream().mapToDouble(GenomeInCase::getFitness).min().getAsDouble();
        double maxFitness = currentPopulation.stream().mapToDouble(GenomeInCase::getFitness).max().getAsDouble();
        double sumOfScaledFinesses = 0;
        for (int i = 0; i < currentPopulation.size(); i++) {
            sumOfScaledFinesses += scaleFitness(currentPopulation.get(i).getFitness(), minFitness, maxFitness);
        }
        double sumOfPropabilities = 0;
        Collections.sort(currentPopulation, Comparator.comparing(GenomeInCase::getFitness));
        for (int i = 0; i < currentPopulation.size(); i++) {
            sumOfPropabilities += scaleFitness(currentPopulation.get(i).getFitness(), minFitness, maxFitness) / sumOfScaledFinesses;
            currentPopulation.get(i).setCurrentRoulettePropability(sumOfPropabilities);
        }
        List<GenomeInCase> selectedPopulation = new ArrayList<>();
        Collections.sort(currentPopulation, Comparator.comparing(GenomeInCase::getCurrentRoulettePropability));
        while (selectedPopulation.size() < currentPopulation.size()) {
            double draw = RANDOM.nextDouble();
            selectedPopulation.add(currentPopulation.stream().filter(gic -> gic.getCurrentRoulettePropability() > draw).findFirst().get());
        }
        currentPopulation = selectedPopulation;
    }

    private double scaleFitness(double fitness, double min, double max) {
//        return fitness;
        if (fitness == min) return fitness;
        double scale = 100d;
        int pow = 4;
        double result = Math.pow(fitness * scale * (fitness - min) / (max - min), pow);
        return result;
    }

    private void crossover() {
        List<GenomeInCase> crossedOverPopulation = new ArrayList<>();
        for (int i = 0; i < currentPopulation.size() / 2; i++) {
            GenomeInCase first = currentPopulation.get(i);
            GenomeInCase second = currentPopulation.get(i + currentPopulation.size() / 2);
            if (occuredCrossover()) {
                Pair<GenomeInCase, GenomeInCase> children = first.cross(second);
                crossedOverPopulation.add(children.getFirst());
                crossedOverPopulation.add(children.getSecond());
            } else {
                crossedOverPopulation.add(first);
                crossedOverPopulation.add(second);
            }
        }
        currentPopulation = crossedOverPopulation;
    }

    private GenomeInCase getRandomGenomeInCase() {
        return currentPopulation.get(RANDOM.nextInt(currentPopulation.size()));
    }

    private void mutation() {
        currentPopulation.forEach(gic ->
                gic.mutateGenes(this::occuredMutation)
        );
    }

    private void resetCounter() {
        counter = 0;
    }

    private void incrementCounter() {
        counter++;
    }

    private Boolean occuredMutation() {
        return RANDOM.nextInt(FULL_CHANCE_PERCENT) < mutationChancePercent;
    }

    private boolean occuredCrossover() {
        return RANDOM.nextInt(FULL_CHANCE_PERCENT) < crossoverChancePercent;
    }

    private boolean isStopConditionSatisfied() {
        return maxGenerationsCount <= counter || (bestEvaluationResult != null && bestEvaluationResult.best <= minGenomeEvaluation);
    }

    private void logResult() throws IOException {
        FileUtils.writeStringToFile(new File("./results/" + getFileName()), runResult.toCsvString());
    }

    private String getFileName() {
        final char DEL = '_';

        return "run" + DEL + getTime() + DEL + "gen" + maxGenerationsCount + DEL + "pop" + populationSize + DEL + "cross" +
                crossoverChancePercent + DEL + "mut" + mutationChancePercent + DEL + (useTournament ? "tour" + tournamentSize + DEL : "") +
                "min" + minGenomeEvaluation + DEL + "n" + qapCase.getN() + ".csv";
    }

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
