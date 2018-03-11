import java.util.Collections;
import java.util.List;

/**
 * Created by maciej on 03.03.18.
 */
public class GAEvaluationResult {
    public final int best;
    public final int middle;
    public final int worst;

    public GAEvaluationResult(List<Integer> evaluationList) {
        Collections.sort(evaluationList);
        best = evaluationList.get(0);
        middle = evaluationList.get(evaluationList.size() / 2);
        worst = evaluationList.get(evaluationList.size() - 1);
    }

    public GAEvaluationResult(int best, int middle, int worst) {
        this.best = best;
        this.middle = middle;
        this.worst = worst;
    }

    public int getBest() {
        return best;
    }

    public int getMiddle() {
        return middle;
    }

    public int getWorst() {
        return worst;
    }

    public boolean lowerThan(GAEvaluationResult other) {
        return this.best < other.best;
    }
}
