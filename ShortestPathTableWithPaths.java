import java.io.*;
import java.util.*;

public class ShortestPathTableWithPaths {

    private Map<String, List<Edge>> graph = new HashMap<>();
    private Map<String, Map<String, Integer>> shortestPathTable = new HashMap<>();
    private Map<String, Map<String, String>> pathPredecessors = new HashMap<>();

    static class Edge {
        String destination;
        int weight;

        Edge(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    // Method to add an edge to the graph
    private void addEdge(String origin, String destination, int weight) {
        graph.putIfAbsent(origin, new ArrayList<>());
        graph.get(origin).add(new Edge(destination, weight));

        graph.putIfAbsent(destination, new ArrayList<>());
        graph.get(destination).add(new Edge(origin, weight));
    }

    // Dijkstra's algorithm to compute shortest paths from a given origin to all other nodes
    private Map<String, Integer> computeShortestPathsFrom(String origin) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }
        distances.put(origin, 0);
        queue.add(origin);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDist = distances.get(current);

            for (Edge edge : graph.getOrDefault(current, new ArrayList<>())) {
                int newDist = currentDist + edge.weight;
                if (newDist < distances.get(edge.destination)) {
                    distances.put(edge.destination, newDist);
                    predecessors.put(edge.destination, current);
                    queue.add(edge.destination);
                }
            }
        }

        pathPredecessors.put(origin, predecessors);  // Store the predecessor map for path reconstruction
        return distances;
    }

    // Builds the shortest path table for all nodes
    private void buildShortestPathTable() {
        for (String origin : graph.keySet()) {
            shortestPathTable.put(origin, computeShortestPathsFrom(origin));
        }
    }

    // Helper method to reconstruct the path from origin to destination
    private List<String> reconstructPath(String origin, String destination) {
        List<String> path = new ArrayList<>();
        String step = destination;

        while (step != null) {
            path.add(step);
            step = pathPredecessors.getOrDefault(origin, new HashMap<>()).get(step);
        }

        Collections.reverse(path);
        if (path.size() == 1 && !path.get(0).equals(origin)) {
            return new ArrayList<>(); // Return empty if no path exists
        }
        return path;
    }

    // Load the graph from a file
    public void loadGraphFromFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();  // Skip the first line
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String origin = parts[0];
                String destination = parts[1];
                int distance = Integer.parseInt(parts[2]);
                addEdge(origin, destination, distance);
            }
        }
    }

    // Display the shortest path table
    public void displayShortestPathTable() {
        System.out.println("Shortest Path Table:");
        System.out.printf("%-6s", "From\\To");
        for (String destination : graph.keySet()) {
            System.out.printf("%-6s", destination);
        }
        System.out.println();

        for (String origin : graph.keySet()) {
            System.out.printf("%-6s", origin);
            for (String destination : graph.keySet()) {
                int distance = shortestPathTable.get(origin).getOrDefault(destination, Integer.MAX_VALUE);
                System.out.printf("%-6s", distance == Integer.MAX_VALUE ? "INF" : distance);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        ShortestPathTableWithPaths spTable = new ShortestPathTableWithPaths();
        Scanner scanner = new Scanner(System.in);

        try {
            // Load graph from the provided file path
            System.out.print("Enter the file path for data.txt: ");
            String filePath = scanner.nextLine();
            spTable.loadGraphFromFile(filePath);

            // Build the shortest path table for all nodes
            spTable.buildShortestPathTable();

            // Display the shortest path table
            spTable.displayShortestPathTable();

            // Prompt user for origin and destination
            System.out.print("Enter the origin airport code: ");
            String origin = scanner.nextLine();
            System.out.print("Enter the destination airport code: ");
            String destination = scanner.nextLine();

            // Output the shortest path distance and path sequence
            int distance = spTable.shortestPathTable.get(origin).getOrDefault(destination, Integer.MAX_VALUE);
            if (distance == Integer.MAX_VALUE) {
                System.out.println("No path found between " + origin + " and " + destination);
            } else {
                System.out.println("Shortest path distance from " + origin + " to " + destination + " is: " + distance);
                List<String> path = spTable.reconstructPath(origin, destination);
                System.out.println("Path taken: " + String.join(" -> ", path));
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
