import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// User class to represent each user
class User {
    int id;
    String name;
    String country;
    Set<User> connections; // Use a Set to avoid duplicate connections

    public User(int id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.connections = new HashSet<>();
    }

    // Method to add a connection to another user
    public void addConnection(User other) {
        connections.add(other);
    }

    @Override
    public String toString() {
        return name + " (" + country + ")";
    }
}

// SocialNetwork class to create connections between users based on country
class SocialNetwork {
    private List<User> users;

    public SocialNetwork(List<User> users) {
        this.users = users;
        connectUsersByCountry();
    }

    // Connects users from the same country
    private void connectUsersByCountry() {
        Map<String, List<User>> countryMap = new HashMap<>();

        // Group users by country
        for (User user : users) {
            countryMap.computeIfAbsent(user.country, k -> new ArrayList<>()).add(user);
        }

        // Connect users within the same country
        for (List<User> sameCountryUsers : countryMap.values()) {
            for (int i = 0; i < sameCountryUsers.size(); i++) {
                for (int j = i + 1; j < sameCountryUsers.size(); j++) {
                    User user1 = sameCountryUsers.get(i);
                    User user2 = sameCountryUsers.get(j);
                    user1.addConnection(user2);
                    user2.addConnection(user1);
                }
            }
        }
    }
}

// PercolationDetection class to find connected users by country
class PercolationDetection {
    private Set<Integer> visited = new HashSet<>();

    // Method to find all users connected within a specified country
    public Set<User> findConnectedUsersByCountry(List<User> users, String targetCountry) {
        Set<User> connectedUsers = new HashSet<>();

        for (User user : users) {
            if (!visited.contains(user.id) && user.country.equalsIgnoreCase(targetCountry)) {
                dfs(user, connectedUsers);
            }
        }
        return connectedUsers;
    }

    // DFS method to explore and add connected users to the set
    private void dfs(User user, Set<User> connectedUsers) {
        visited.add(user.id);
        connectedUsers.add(user);

        for (User connection : user.connections) {
            if (!visited.contains(connection.id)) {
                dfs(connection, connectedUsers);
            }
        }
    }
}

// Main class to run the program
public class Main2 {
    public static void main(String[] args) {
        List<User> users = loadUsersFromCSV("D:\\lab 5\\demo\\src\\SocialMediaUsersDataset.csv");

        if (users == null) {
            System.out.println("Failed to load users from CSV.");
            return;
        }

        // Create social network with connections
        SocialNetwork network = new SocialNetwork(users);

        // Create a PercolationDetection instance to find clusters
        PercolationDetection detector = new PercolationDetection();

        // Get country from user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a country to find connected users: ");
        String country = scanner.nextLine().trim();
        scanner.close();

        // Find and display connected users in the specified country
        Set<User> connectedUsers = detector.findConnectedUsersByCountry(users, country);

        // Display the connected users in the specified country
        if (connectedUsers.isEmpty()) {
            System.out.println("No connected users found in " + country + ".");
        } else {
            System.out.println("Connected users in " + country + ":");
            for (User user : connectedUsers) {
                System.out.println(user);
            }
        }
    }

    // Method to load users from the CSV file without any external libraries
    private static List<User> loadUsersFromCSV(String filePath) {
        List<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(","); // Split CSV by comma
                if (columns.length < 7) continue; // Skip malformed rows

                try {
                    int id = Integer.parseInt(columns[0]);
                    String name = columns[1];
                    String country = columns[6]; // Assuming "Country" is in the 7th column (index 6)

                    User user = new User(id, name, country);
                    users.add(user);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing a user ID: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
            return null;
        }

        return users;
    }
}
