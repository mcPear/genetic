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
}
