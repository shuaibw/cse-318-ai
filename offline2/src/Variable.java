import java.util.ArrayList;
import java.util.HashSet;

public class Variable {
    int r, c;
    public HashSet<Integer> domain;
    public int value;
    public int degree;
    public boolean[] removed = new boolean[100];

    public Variable(int r, int c, int value) {
        this.r = r;
        this.c = c;
        this.value = value;
        domain = new HashSet<>();
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]=%d, dom=%s, deg=%d\n", r, c, value, domain, degree);
    }
}
