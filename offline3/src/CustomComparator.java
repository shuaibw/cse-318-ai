import java.util.Comparator;

public class CustomComparator {
    public Comparator<Vertex> cmp;
    public boolean isDynamicHeuristic;

    public CustomComparator(Comparator<Vertex> cmp, boolean isDynamicHeuristic) {
        this.cmp = cmp;
        this.isDynamicHeuristic = isDynamicHeuristic;
    }
}
