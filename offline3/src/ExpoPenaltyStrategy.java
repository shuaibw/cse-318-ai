public class ExpoPenaltyStrategy implements PenaltyStrategy {
    @Override
    public int calculatePenalty(int n) {
        if(n>5) return 0;
        return 1 << (5-n);
    }
}
