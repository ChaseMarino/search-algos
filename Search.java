import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

class Search {
    static List<City> cities;
    static List<List<Integer>> adjList;
    static Map<String, Integer> cityMap;

    public static void dfs(City startCity, City endCity, BufferedWriter writer) throws IOException{

        Stack<Integer> stack = new Stack<Integer>();
        boolean[] visited = new boolean[cities.size()];
        Map<City, City> parent = new HashMap<City, City>();
    
        int startIndex = cityMap.get(startCity.getName());
        int endIndex = cityMap.get(endCity.getName());
    
        stack.push(startIndex);
        visited[startIndex] = true;
    
        while (!stack.isEmpty()) {
            int currIndex = stack.pop();
            City currCity = cities.get(currIndex);
    
            if (currIndex == endIndex) {
                System.out.println("\nDepth-First Search Results: ");
                writer.write("\n\nDepth-First Search Results: ");
                printPath(endCity, parent, writer);
                return;
            }
    
            List<Integer> neighbors = adjList.get(currIndex);
            Collections.sort(neighbors, Collections.reverseOrder()); // sort neighbors in descending order by name
    
            for (int i : neighbors) {
                if (!visited[i]) {
                    stack.push(i);
                    visited[i] = true;
                    parent.put(cities.get(i), currCity);
                }
            }
        }
    
        System.out.println("No path found.");
 
    }

    public static void bfs(City startCity, City endCity, BufferedWriter writer) throws IOException {
        Queue<City> queue = new LinkedList<>();
        Map<City, City> parent = new HashMap<>();
        Set<City> visited = new HashSet<>();
        
        queue.add(startCity);
        visited.add(startCity);
        parent.put(startCity, null);
        
        while (!queue.isEmpty()) {
            City currCity = queue.poll();
            if (currCity.equals(endCity)) {
                System.out.println("Breadth-First Search Results: ");
                writer.write("Breadth-First Search Results: ");
                printPath(currCity, parent, writer);
                return;
            }
            
            List<Integer> neighbors = adjList.get(cityMap.get(currCity.getName()));
            if (neighbors != null) {
                for (Integer neighbor : neighbors) {
                    if (!visited.contains(cities.get(neighbor))) {
                        visited.add(cities.get(neighbor));
                        queue.add(cities.get(neighbor));
                        parent.put(cities.get(neighbor), currCity);
                    }
                }
            }
        }
        System.out.println("\nNo path found from " + startCity + " to " + endCity);
        writer.write("No path found from " + startCity + " to " + endCity);
    }

    public static void printPath(City city, Map<City, City> parent, BufferedWriter writer) throws IOException {
        Stack<City> stack = new Stack<>();
        stack.push(city);
        while (parent.get(city) != null) {
            city = parent.get(city);
            stack.push(city);
        }
        
        int hops = stack.size() - 1;
        double distance = 0;
        City prevCity = stack.pop();
        System.out.println(prevCity.getName());
        while (!stack.isEmpty()) {
            City currCity = stack.pop();
            distance += getDistance(prevCity, currCity);
            writer.write("\n" + currCity.getName());
            System.out.println(currCity.getName());
            prevCity = currCity;
        }
        
        System.out.println("Hops: " + hops);
        writer.write("\nHops: " + hops);
        System.out.println("Distance: " + distance + " miles");
        writer.write("\nDistance: " + distance + " miles");

    }
    
    public static double getDistance(City city1, City city2) {
        double lat1 = city1.getLatitude();
        double lon1 = city1.getLongitude();
        double lat2 = city2.getLatitude();
        double lon2 = city2.getLongitude();
        return Math.sqrt((lat1 - lat2) * (lat1 - lat2) + (lon1 - lon2) * (lon1 - lon2)) * 100;
    }
    

    public static void astar(City startCity, City endCity, BufferedWriter writer) throws IOException{
        Map<City, Double> gScore = new HashMap<>(); // cost of getting to node
        gScore.put(startCity, 0.0);
        Map<City, Double> fScore = new HashMap<>(); // estimated total cost
        fScore.put(startCity, getDistance(startCity, endCity));
        PriorityQueue<City> openSet = new PriorityQueue<>(Comparator.comparingDouble(fScore::get));
        openSet.add(startCity);
        Map<City, City> parent = new HashMap<>();
    
        while (!openSet.isEmpty()) {
            City currentCity = openSet.poll();
            if (currentCity.equals(endCity)) {
                System.out.println("\nA* Search Results: ");
                writer.write("\n\nA* Search Results: ");
                printPath(currentCity, parent, writer);
                return;
            }
    
            for (int neighborIndex : adjList.get(cityMap.get(currentCity.getName()))) {
                City neighborCity = cities.get(neighborIndex);
                double tentativeGScore = gScore.get(currentCity) + getDistance(currentCity, neighborCity);
                if (!gScore.containsKey(neighborCity) || tentativeGScore < gScore.get(neighborCity)) {
                    gScore.put(neighborCity, tentativeGScore);
                    fScore.put(neighborCity, tentativeGScore + getDistance(neighborCity, endCity));
                    openSet.add(neighborCity);
                    parent.put(neighborCity, currentCity);
                }
            }
        }
    
        System.out.println("No path found from " + startCity.getName() + " to " + endCity.getName());
    }

    public static int getCityId(String cityName) {
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            if (city.getName().equalsIgnoreCase(cityName)) {
                return i;
            }
        }
        return -1; // city not found in list
    }
    
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java Search <input_file> <output_file>");
            System.exit(1);
        }
        String inputFile = args[0];
        String outputFile = args[1];
        String startCityName = "";
        String destCityName = "";

        try{
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            startCityName = reader.readLine().trim();
            destCityName = reader.readLine().trim();
            reader.close();
        }
        catch (Exception e){
            System.err.println("File not found: " + inputFile);
            System.exit(1);

        }

        try {
            readCityFile();
            readEdgeFile();

            int startCityId = getCityId(startCityName);
            int destCityId = getCityId(destCityName);
            
            if (startCityId == -1){
                System.out.println("No such city: " + startCityName);
            }

            if (destCityId == -1){
                System.out.println("No such city: " + destCityName);
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            bfs(cities.get(startCityId), cities.get(destCityId), writer);
            dfs(cities.get(startCityId), cities.get(destCityId), writer);
            astar(cities.get(startCityId), cities.get(destCityId), writer);

            writer.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void readCityFile() throws IOException {
        try{
            BufferedReader reader = new BufferedReader(new FileReader("city.dat"));
            cities = new ArrayList<>();
            cityMap = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                int id = cities.size();
                String name = parts[0];
                double lat = Double.parseDouble(parts[2]);
                double lon = Double.parseDouble(parts[3]);
                City city = new City(name, lat, lon);
                cities.add(city);
                cityMap.put(name, id);
            }
            reader.close();
        }
        catch (Exception e){
            System.err.println("File not found: city.dat");
            System.exit(1);
        }
    }

    private static void readEdgeFile() throws IOException {
        try{
            BufferedReader reader = new BufferedReader(new FileReader("edge.dat"));
            adjList = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                adjList.add(new ArrayList<>());
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                int id1 = getCityId(parts[0]);
                int id2 = getCityId(parts[1]);

                adjList.get(id1).add(id2);
                adjList.get(id2).add(id1);

        }
        reader.close();
        }
        catch (Exception e){
            System.err.println("File not found: edge.dat");
            System.exit(1);
        }
    }
        

}