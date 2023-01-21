public class LinearPenaltyStrategy implements PenaltyStrategy {
    @Override
    public int calculatePenalty(int n) {
        if(n>5) return 0;
        return 2 *(5-n);
    }
}
