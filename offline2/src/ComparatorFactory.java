import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class ComparatorFactory {
    private static final Random random = new Random(1805010);

    public static Comparator<Variable> minDomainComp() {
        return Comparator.comparingInt((Variable o) -> o.domain.size());
    }

    public static Comparator<Variable> maxDegreeComp() {
        return (a, b) -> -Integer.compare(a.degree, b.degree);
    }

    public static Comparator<Variable> preferDomComp() {
        return (a, b) -> {
            int c = Integer.compare(a.domain.size(), b.domain.size());
            return c != 0 ? c : -Integer.compare(a.degree, b.degree);
        };
    }

    public static Comparator<Variable> minDomByDegComp() {
        return Comparator.comparingDouble((Variable o) -> (double) o.domain.size() / o.degree);
    }

    public static Comparator<Variable> randomComp() {
        return Comparator.comparingInt((Variable o) -> random.nextBoolean() ? -1 : 1);
    }

    public static ArrayList<Comparator<Variable>> getCmpList() {
        ArrayList<Comparator<Variable>> comparators = new ArrayList<>();
        comparators.add(minDomainComp());
        comparators.add(maxDegreeComp());
        comparators.add(preferDomComp());
        comparators.add(minDomByDegComp());
        comparators.add(randomComp());
        return comparators;
    }
}
