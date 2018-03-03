import java.util.ArrayList;
import java.util.List;

/**
 * Created by maciej on 03.03.18.
 */
public class GARunResult {
    private List<GAEvaluationResult> allEvaluations = new ArrayList<>();

    public void add(GAEvaluationResult result) {
        allEvaluations.add(result);
    }

    public List<GAEvaluationResult> getAllEvaluations() {
        return allEvaluations;
    }

    public String toCsvString() {
        final String COMMA_DEL = ", ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < allEvaluations.size(); i++) {
            GAEvaluationResult e = allEvaluations.get(i);
            builder.append((i) + COMMA_DEL + e.best + COMMA_DEL + e.middle + COMMA_DEL + e.worst + "\n");
        }
        return builder.toString();
    }
}
