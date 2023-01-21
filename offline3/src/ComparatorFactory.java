import java.util.Comparator;
import java.util.Random;

public class ComparatorFactory {
    private static final Random random = new Random(1805010);

    public static CustomComparator getLargestDegreeCmp() {
        return new CustomComparator(
                (v0, v1) -> Integer.compare(v1.neighbors.size(), v0.neighbors.size()),
                false,
                "LargestDegree"
        );
    }

    public static CustomComparator getSaturationDegreeCmp() {
        return new CustomComparator(
                (v0, v1) -> {
                    int res = Integer.compare(v1.satDegree, v0.satDegree);
                    if (res != 0) return res;
                    return Integer.compare(v1.neighbors.size(), v0.neighbors.size());
                },
                true,
                "LargestSaturationDegree"
        );
    }

    public static CustomComparator getLargestEnrollCmp() {
        return new CustomComparator(
                (v0, v1) -> Integer.compare(v1.enrolled, v0.enrolled),
                false,
                "LargestEnroll"
        );
    }

    public static CustomComparator getRandomCmp() {
        return new CustomComparator(
                Comparator.comparingInt((Vertex o) -> random.nextBoolean() ? -1 : 1),
                false,
                "Random"
        );
    }

    public static CustomComparator[] getComparatorList(){
        return new CustomComparator[]{
                getLargestDegreeCmp(),
                getSaturationDegreeCmp(),
                getLargestEnrollCmp(),
                getRandomCmp()
        };
    }

}
