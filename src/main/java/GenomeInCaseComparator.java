import java.util.Comparator;

public class GenomeInCaseComparator implements Comparator<GenomeInCase> {

    @Override
    public int compare(GenomeInCase o1, GenomeInCase o2) {
        return o1.compareTo(o2);
    }
}
