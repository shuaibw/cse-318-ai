import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class GraphGenerator {
    private final String dataset;
    private final String crsPath;
    private final String stuPath;
    private final String baseDir = "Toronto/";
    private final ArrayList<Vertex> graph;
    private int numStudents;
    private ArrayList<int[]> conflictCourses;

    public GraphGenerator(String dataset) {
        this.dataset = dataset;
        crsPath = baseDir + dataset + ".crs";
        stuPath = baseDir + dataset + ".stu";
        validatePaths();
        graph = new ArrayList<>();
        conflictCourses = new ArrayList<>();
        numStudents = 0;
    }

    private void validatePaths() {
        if (!Files.exists(Path.of(crsPath))) throw new RuntimeException("Course dataset not found: " + crsPath);
        if (!Files.exists(Path.of(stuPath))) throw new RuntimeException("Student dataset not found: " + stuPath);
    }

    private void createGraph() {
        // try with resources: open crsPath
        try (
                BufferedReader crs = new BufferedReader(new FileReader(crsPath));
                BufferedReader stu = new BufferedReader(new FileReader(stuPath));
        ) {
            addVertex(crs);
            addConflictEdges(stu);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addConflictEdges(BufferedReader stu) throws IOException {
        String line;
        while ((line = stu.readLine()) != null) {
            if (line.isBlank()) continue;
            numStudents++;
            int[] ids = Arrays.stream(line.strip().split(" ")).mapToInt(Integer::parseInt).toArray();
            if (ids.length == 1) continue;
            conflictCourses.add(ids);
            addEdgesBetweenCourses(ids);
        }
    }

    private void addEdgesBetweenCourses(int[] ids) {
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                Vertex vi = graph.get(ids[i] - 1);
                Vertex vj = graph.get(ids[j] - 1);
                if (!vi.neighbors.contains(vj)) vi.neighbors.add(vj);
                if (!vj.neighbors.contains(vi)) vj.neighbors.add(vi);
            }
        }
    }

    private void addVertex(BufferedReader crs) throws IOException {
        String line;
        while ((line = crs.readLine()) != null) {
            if (line.isBlank()) continue;
            String[] pieces = line.strip().split(" ");
            int courseId = Integer.parseInt(pieces[0]);
            int enrolled = Integer.parseInt(pieces[1]);
            graph.add(new Vertex(courseId, enrolled));
        }
    }

    public ArrayList<Vertex> getGraph() {
        if (graph.size() == 0) createGraph();
        return graph;
    }

    public ArrayList<int[]> getConflictCourses() {
        if (conflictCourses.size() == 0)
            throw new RuntimeException("Must run GraphGenerator::createGraph() before calling this method");
        return conflictCourses;
    }

    public int getNumStudents() {
        return numStudents;
    }
}
