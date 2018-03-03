import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by maciej on 02.03.18.
 */
public class GeneticAlgorithmRun {
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final int FULL_CHANCE_PERCENT = 100;
    private final int maxGenerationsCount;
    private final int populationSize;
    private final int crossoverChancePercent;
    private final int mutationChancePercent;
    private final int tournamentSize;
    private final int minGenomeEvaluation;
    private final QapCase qapCase;

    private List<GenomeInCase> currentPopulation;
    private int counter = 0;
    private GAEvaluationResult currentEvaluationResult;
    private GAEvaluationResult bestEvaluationResult;
    private GARunResult runResult = new GARunResult();

    public GeneticAlgorithmRun(int maxGenerationsCount, int populationSize, int crossoverChancePercent,
                               int mutationChancePercent, int tournamentSize, int minGenomeEvaluation, QapCase qapCase) {
        this.maxGenerationsCount = maxGenerationsCount;
        this.populationSize = populationSize;
        this.crossoverChancePercent = crossoverChancePercent;
        this.mutationChancePercent = mutationChancePercent;
        this.tournamentSize = tournamentSize;
        this.minGenomeEvaluation = minGenomeEvaluation;
        this.qapCase = qapCase;
    }

    public GAEvaluationResult run() {
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
        return bestEvaluationResult;
//        begin
//        t:=0;
//        initialisePopulation( pop(t0) );
//        evaluate( pop(t0) );
//        while (not stop_condition) do 
//        begin
//          pop(t+1) := selection( pop(t) );
//          pop(t+1) := crossover( pop(t+1) );
//          pop(t+1) := mutation( pop(t+1) );
//          evaluate( pop(t+1) );
//          t:=t+1;
//        end 
//        return the_best_solution
//                end
    }

    private void initialisePopulation() {
        currentPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            currentPopulation.add(new GenomeInCase(new Genome(qapCase.getN()), qapCase));
        }
    }

    private void evaluate() {
        List<Integer> evaluationList = new ArrayList<>();
        for (int i = 0; i < currentPopulation.size(); i++) {
            evaluationList.add(currentPopulation.get(i).getEvaluation());
        }
        Collections.sort(evaluationList);
        currentEvaluationResult = new GAEvaluationResult(evaluationList.get(0), evaluationList.get(evaluationList.size() / 2), evaluationList.size() - 1);
        if (bestEvaluationResult == null || currentEvaluationResult.lowerThan(bestEvaluationResult)) {
            bestEvaluationResult = currentEvaluationResult;
        }
        runResult.add(currentEvaluationResult);
    }

    private void selection() {
        tournamentSelection();
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

    private void crossover() {
        for (int i = 0; i < currentPopulation.size(); i++) {
            if (occuredCrossover()) {
                currentPopulation.set(i, currentPopulation.get(i).cross(getRandomGenomeInCase()));
                if (!currentPopulation.get(i).valid()) throw new RuntimeException("Invalid genome !"); //todo remove
            }
        }
    }

    private GenomeInCase getRandomGenomeInCase() {
        return currentPopulation.get(RANDOM.nextInt(currentPopulation.size()));
    }

    private void mutation() {
        currentPopulation.forEach(gic -> {
            if (occuredMutation()) {
                gic.mutate();
            }
        });
    }

    private void resetCounter() {
        counter = 0;
    }

    private void incrementCounter() {
        counter++;
    }

    private boolean occuredMutation() {
        return RANDOM.nextInt(FULL_CHANCE_PERCENT) < mutationChancePercent;
    }

    private boolean occuredCrossover() {
        return RANDOM.nextInt(FULL_CHANCE_PERCENT) < crossoverChancePercent;
    }

    private boolean isStopConditionSatisfied() {
        return counter >= maxGenerationsCount || (bestEvaluationResult != null && bestEvaluationResult.best <= minGenomeEvaluation);
    }

}
