import java.io.*;
import java.util.*;

public class MSTWithPrims {

    private Map<String, List<Edge>> graph = new HashMap<>();
    private Map<String, Integer> minEdge = new HashMap<>();
    private Map<String, String> pathPredecessors = new HashMap<>();
    private Set<String> visited = new HashSet<>();

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

    // Prim's algorithm to compute Minimum Spanning Tree (MST)
    private void computeMST(String start) {
        // Initialize all nodes with infinity weight and no predecessor
        for (String node : graph.keySet()) {
            minEdge.put(node, Integer.MAX_VALUE);
            pathPredecessors.put(node, null);
        }

        // Start from the given node
        minEdge.put(start, 0);
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(minEdge::get));
        pq.add(start);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            visited.add(current);

            // Explore the neighbors of the current node
            for (Edge edge : graph.getOrDefault(current, new ArrayList<>())) {
                String neighbor = edge.destination;
                if (!visited.contains(neighbor) && edge.weight < minEdge.get(neighbor)) {
                    // If the edge weight is less than the current min, update the MST
                    minEdge.put(neighbor, edge.weight);
                    pathPredecessors.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }
    }

    // Write only the total cost of the MST to the output file
    private void writeMSTToFile(String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, false))) { // false means overwrite if file exists
            int totalCost = 0;
            // Calculate the total cost of the MST
            for (String node : graph.keySet()) {
                if (pathPredecessors.get(node) != null) {
                    String predecessor = pathPredecessors.get(node);
                    for (Edge edge : graph.get(predecessor)) {
                        if (edge.destination.equals(node)) {
                            totalCost += edge.weight;
                            break;
                        }
                    }
                }
            }
            // Write only the total cost to the file
            writer.write("Total cost of MST: " + totalCost + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the output file.");
            e.printStackTrace();
        }
    }

    // Load the graph from a file
    public void loadGraphFromFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();  // Skip the first line (which can be the header)
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

    public static void main(String[] args) {
        MSTWithPrims mst = new MSTWithPrims();
        Scanner scanner = new Scanner(System.in);

        try {
            // Ask the user for the input file path
            System.out.print("Enter the file path for the input graph (e.g., data.txt): ");
            String inputFilePath = scanner.nextLine();  // Read the file path from the user
            mst.loadGraphFromFile(inputFilePath);

            // Automatically pick the first node in the graph as the starting node for Prim's Algorithm
            String start = mst.graph.keySet().iterator().next();  // Get the first node from the graph

            mst.computeMST(start);

            // Write the MST result (total cost only) to a fixed output file
            String outputFilePath = "mst_output.txt";  // Fixed output file path
            mst.writeMSTToFile(outputFilePath);

            System.out.println("Total cost of the MST has been written to the output file.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the input file.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
