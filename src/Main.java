import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        QapCase qapCase = new QapCase();
        qapCase.loadFromFile(qapCaseFilePaths().get(4));
        GARun run1 = new GARun(200, 1000,
                80, 5, 5, 6922, true, qapCase);
        //100, 100, 50, 1, 5, 1 for 12 case finds max
        //1000, 1000, 50, 30, 2 for 12 case finds fast max
        //System.out.println("\n\nRun: " + run1.run().best);
        RandomRun randomRun = new RandomRun(200, 1000, 6922, qapCase);
        System.out.println("\n\nRandom run: " + randomRun.run().best);
    }

    public static void testCrossover() throws FileNotFoundException {
        QapCase qapCase = new QapCase();
        qapCase.loadFromFile(qapCaseFilePaths().get(0));
        new GenomeInCase(new Genome(qapCase.getN()), qapCase).cross(new GenomeInCase(new Genome(qapCase.getN()), qapCase));
        //new Genome(12).cross(new Genome(12));
    }

    public static void tests() throws IOException {
        //        for (int i = 0; i < 1000; i++) {
//            testCrossover();
//        }
//        List<String> qapCaseFilePaths = qapCaseFilePaths();
//        QapCase qapCase = new QapCase();
//        qapCase.loadFromFile(qapCaseFilePaths.get(0));
//        System.out.println(qapCase.toString());
//        Genome genome = new Genome(qapCase.getN());
//        System.out.println(genome);
//        Genome genome1 = new Genome(genome);
//        System.out.println(genome1);
//        genome1.mutateGenes();
//        System.out.println(genome1);
//        Genome genome2 = new Genome(qapCase.getN());
//        System.out.println(genome2);
//        //Genome crossed = genome.cross(genome2);
//        //System.out.println(crossed);
//        System.out.println("---------------");
//        List<Integer> bestSolution = new ArrayList<>(Arrays.asList(3, 10, 11, 2, 12, 5, 6, 7, 8, 1, 4, 9));
//        Genome genomeBest = new Genome(bestSolution);
//        GenomeInCase genomeInCase = new GenomeInCase(genomeBest, qapCase);
//        System.out.println("eval of best genome: " + genomeInCase.getEvaluation());
//        System.out.println("\nCross fix test: \n");
//        System.out.println(genome1 + "\n");
//        System.out.println(genome2 + "\n");
//        Genome crossed2 = genome1.cross(genome2);
//        System.out.println("\n" + crossed2);
    }

    public static List<String> qapCaseFilePaths() {
        List<String> paths = new ArrayList();
        paths.add("resources/had12");
        paths.add("resources/had14");
        paths.add("resources/had16");
        paths.add("resources/had18");
        paths.add("resources/had20");
        return paths;
    }
}
