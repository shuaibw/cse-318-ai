import java.util.Comparator;

public class CustomComparator {
    public Comparator<Vertex> cmp;
    public boolean isDynamicHeuristic;
    public String name;

    public CustomComparator(Comparator<Vertex> cmp, boolean isDynamicHeuristic, String name) {
        this.cmp = cmp;
        this.isDynamicHeuristic = isDynamicHeuristic;
        this.name=name;
    }
}
