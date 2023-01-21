import java.util.ArrayList;
import java.util.Comparator;

public class Vertex implements Comparable<Vertex> {
    private static Comparator<Vertex> cmp;
    public int id;
    public int enrolled;
    public int satDegree;
    public ArrayList<Vertex> neighbors;

    public Vertex(int id, int enrolled) {
        neighbors = new ArrayList<>();
        this.id = id;
        this.enrolled = enrolled;
        satDegree = 0;
    }

    public static Comparator<Vertex> getCmp() {
        return cmp;
    }

    public static void setCmp(Comparator<Vertex> cmp) {
        Vertex.cmp = cmp;
    }

    @Override
    public int compareTo(Vertex vertex) {
        if (cmp == null) throw new RuntimeException("Comparator not set");
        return cmp.compare(this, vertex);
    }
}
