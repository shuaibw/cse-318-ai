import java.util.*;

public class Solver {
    private ArrayList<Vertex> graph;
    private IndexMinPQ<Vertex> pq;
    private ArrayList<int[]> conflictCourses;
    private int[] colors;
    private final CustomComparator customComparator;
    private int numStudents;
    private PenaltyStrategy penaltyStrategy;
    private Random random;

    public Solver(String dataset, CustomComparator customComparator) {
        this.customComparator = customComparator;
        Vertex.setCmp(customComparator.cmp);
        GraphGenerator graphGen = new GraphGenerator(dataset);
        initFields(graphGen);
    }

    public void setPenaltyStrategy(PenaltyStrategy penaltyStrategy) {
        this.penaltyStrategy = penaltyStrategy;
    }

    private void initFields(GraphGenerator graphGen) {
        random = new Random(0xDEADBEEF);
        graph = graphGen.getGraph();
        conflictCourses = graphGen.getConflictCourses();
        numStudents = graphGen.getNumStudents();
        pq = new IndexMinPQ<>(graph.size());
        for (int i = 0; i < graph.size(); i++) pq.insert(i, graph.get(i));
        colors = new int[graph.size()];
        Arrays.fill(colors, -1);
    }

    // https://www.cs.cornell.edu/courses/JavaAndDS/files/graphColoring.pdf
    public long solve() {
        while (!pq.isEmpty()) {
            int i = pq.delMin();
            colors[i] = findColorForVertex(graph.get(i));
            if (customComparator.isDynamicHeuristic) updatePq(graph.get(i));
        }
        return Arrays.stream(colors).distinct().count();
    }

    private void updatePq(Vertex v) {
        for (Vertex n : v.neighbors) {
            n.satDegree++;
            if (pq.contains(n.id - 1)) pq.changeKey(n.id - 1, n);
        }
    }

    private int findColorForVertex(Vertex v) {
        int numNeighbors = v.neighbors.size();
        boolean[] usedColors = new boolean[numNeighbors];
        for (Vertex n : v.neighbors) {
            // skip if not colored or color greater that numNeighbors
            if (colors[n.id - 1] == -1 || colors[n.id - 1] >= numNeighbors) continue;
            usedColors[colors[n.id - 1]] = true;
        }
        return findMinColor(usedColors);
    }

    private int findMinColor(boolean[] usedColors) {
        int minColor = usedColors.length + 1;
        for (int i = 0; i < usedColors.length; i++) {
            if (usedColors[i]) continue;
            minColor = i;
            break;
        }
        return minColor;
    }

    public double calculatePenalty() {
        double penalty = 0;
        for (int[] ara : conflictCourses) {
            ara = sortCoursesByColor(ara);
            for (int i = 0; i < ara.length; i++) {
                for (int j = i + 1; j < ara.length; j++) {
                    penalty += penaltyStrategy.calculatePenalty(colors[ara[j] - 1] - colors[ara[i] - 1]);
                }
            }
        }
        return penalty / numStudents;
    }

    private int[] sortCoursesByColor(int[] ara) {
        ara = Arrays.stream(ara).boxed().sorted(
                Comparator.comparingInt(a -> colors[a - 1])
        ).mapToInt(i -> i).toArray();
        return ara;
    }

    private KempeChain getRandomKempeChain() {
        Vertex v = getRandomVertex();
        ArrayList<Vertex> chain = new ArrayList<>();
        LinkedList<Vertex> queue = new LinkedList<>();
        queue.add(v);
        chain.add(v);
        int color1 = colors[v.id - 1];
        int maxColor = Arrays.stream(colors).max().getAsInt();
        int color2 = random.nextInt() % (maxColor + 1);
        int curColor = color2;
        boolean[] visited = new boolean[graph.size()];
        while (!queue.isEmpty()) {
            Vertex front = queue.removeFirst();
            visited[front.id - 1] = true;
            for (Vertex n : front.neighbors) {
                if (colors[n.id - 1] != curColor || visited[n.id - 1]) continue;
                queue.add(n);
                chain.add(n);
                curColor = (curColor == color1) ? color2 : color1;
            }
        }
        return new KempeChain(chain, color1, color2);
    }

    public void runKempeInterchange(int iterations) {
        double penalty = calculatePenalty();
        for (int i = 0; i < iterations; i++) {
            KempeChain kempeChain = getRandomKempeChain();
            swapColors(kempeChain);
            double newPenalty = calculatePenalty();
            if (newPenalty > penalty) swapColors(kempeChain);
        }
    }

    private void swapColors(KempeChain kempeChain) {
        int color1 = kempeChain.color1;
        int color2 = kempeChain.color2;
        ArrayList<Vertex> chain = kempeChain.chain;
        int curColor = colors[chain.get(0).id - 1] == color1 ? color2 : color1;
        for (Vertex v : chain) {
            colors[v.id - 1] = curColor;
            curColor = (curColor == color1) ? color2 : color1;
        }
    }

    private Vertex getRandomVertex() {
        Vertex v;
        do {
            int randIdx = Math.abs(random.nextInt() % graph.size());
            v = graph.get(randIdx);
        } while (v.neighbors.size() == 0);
        return v;
    }

    public static String[] getDataList() {
        return new String[]{
                "car-f-92",
                "car-s-91",
                "kfu-s-93",
                "tre-s-92",
                "yor-f-83"
        };
    }

    public static void main(String[] args) {
        for (String d : Solver.getDataList()) {
            System.out.println("Dataset: " + d);
            int i = 1;
            for (CustomComparator c : ComparatorFactory.getComparatorList()) {
                System.out.print("Using heuristic " + i + ": ");
                Solver solver = new Solver(d, c);
                solver.setPenaltyStrategy(new ExpoPenaltyStrategy());
                long timeSlots = solver.solve();
                double initPenalty = solver.calculatePenalty();
                solver.runKempeInterchange(1000);
                double newPenalty = solver.calculatePenalty();
                System.out.printf("[Timeslots, initial penalty, after kempe interchange]=[%d, %.2f, %.2f]\n", timeSlots, initPenalty, newPenalty);
                i++;
            }
        }
    }
}
