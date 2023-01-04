import java.util.HashSet;

public class Variable {
    int r, c;
    public HashSet<Integer> domain;
    public int value;
    public int degree;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (r != variable.r) return false;
        return c == variable.c;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + c;
        return result;
    }
}
