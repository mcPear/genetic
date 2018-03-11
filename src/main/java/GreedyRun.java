import java.util.ArrayList;
import java.util.List;

public class GreedyRun {

    private final QapCase qapCase;

    public GreedyRun(QapCase qapCase) {
        this.qapCase = qapCase;
    }

    public Integer run() {
        List<Genome> greedyGenomes = findAllGreedyGenomes(qapCase.getN());
        return findTheBestGenomeInCase(greedyGenomes).getEvaluation();
    }

    private GenomeInCase findTheBestGenomeInCase(List<Genome> genomes) {
        return genomes.stream().map(g -> new GenomeInCase(g, qapCase)).min(new GenomeInCaseComparator()).get();
    }

    private List<Genome> findAllGreedyGenomes(int n) {
        List<Genome> greedyGenomes = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            List<Integer> vectorToFulfill = new ArrayList<>();
            vectorToFulfill.add(i);
            greedyGenomes.add(new Genome(greedyFulfillVector(vectorToFulfill, n)));
        }
        return greedyGenomes;
    }

    private List<Integer> greedyFulfillVector(List<Integer> subVector, int n) {
        if (subVector.size() == n) {
            return subVector;
        } else {
            greedyAddNextGene(subVector, n);
            return greedyFulfillVector(subVector, n);
        }
    }

    private void greedyAddNextGene(List<Integer> subVector, int n) {
        List<Integer> missingGenes = getMissingGenes(subVector, n);
        int theBestNextGene = missingGenes.get(0);
        for (Integer gene : missingGenes) {
            if (subEvaluate(subVector, gene) < subEvaluate(subVector, theBestNextGene)) {
                theBestNextGene = gene;
            }
        }
        subVector.add(theBestNextGene);
    }

    private Integer subEvaluate(List<Integer> subVector, int gene) {
        List<Integer> greaterSubVector = new ArrayList<>(subVector);
        greaterSubVector.add(gene);
        return qapCase.evaluateSubVector(greaterSubVector);
    }

    private List<Integer> getMissingGenes(List<Integer> subVector, int n) {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            result.add(i);
        }
        result.removeAll(subVector);
        return result;
    }

}

