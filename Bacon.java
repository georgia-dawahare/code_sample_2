import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map;

/**
 * A program that finds an actor's "Bacon number"
 *
 * @author Evan Phillips, Dartmouth CS 10, Spring 2021
 * @author Georgia Dawahare, Dartmouth CS 10, Spring 2021
 */

public class Bacon {
    private final Graph<String, Set<String>> g; // vertices are actors & edge relationship is "appeared together in a movie"
    private final Map<String, String> IDMovie;  // maps movie IDs to movie names
    private final Map<String, String> IDActor;  // maps actor IDs to actor names
    private final Map<String, Set<String>> actorsMovies;  // maps actors to movies they've acted in

    /**
     * Constructor, instantiates instance variables
     */
    public Bacon() {
        g = new AdjacencyMapGraph<>();
        IDMovie = new HashMap<>();
        IDActor = new HashMap<>();
        actorsMovies = new HashMap<>();
    }

    /**
     * Creates a map (IDMovie) with movie ID and movie name pairs
     *
     * @param fileName name of file to be read from
     */

    public void makeIDMapMovie(String fileName){
        BufferedReader input;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        // Read the file
        try {
            // Line by line
            String line;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {
                // Comma separated
                String[] pieces = line.split("\\|");
                if (pieces.length != 2) {
                    System.err.println("bad separation in line " + lineNum + ":" + line);
                } else {
                    IDMovie.put(pieces[0], pieces[1]);  // Maps movie ID to movie name
                    lineNum++;
                }
            }
        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }
    }

    /**
     * Creates a map (IDActor) with actor ID and actor name pairs
     *
     * @param fileName name of file to be read from
     */

    public void makeIDMapActor(String fileName){
        BufferedReader input;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        // Read the file
        try {
            // Line by line
            String line;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {
                // Comma separated
                String[] pieces = line.split("\\|");
                if (pieces.length != 2) {
                    System.err.println("bad separation in line " + lineNum + ":" + line);
                } else {
                    IDActor.put(pieces[0], pieces[1]);   // Maps actor ID to actor name
                    g.insertVertex(pieces[1]);           // Creates vertex for each actor name
                    lineNum++;
                }
            }
        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }
    }

    /**
     * Creates a map (actorsMovies) with actor and sets of movies they've acted in pairs
     *
     * @param fileName name of file to be read from
     */
    public void makeActorMovieMap(String fileName){
        BufferedReader input;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        // Read the file
        try {

            // Line by line
            String line;
            int lineNum = 0;
            while ((line = input.readLine()) != null) {

                String[] pieces = line.split("\\|");
                if (pieces.length != 2) {
                    System.err.println("bad separation in line " + lineNum + ":" + line);
                }

                else {
                    Set<String> newSet;
                    // If actor is not in map, add actor and crete new set pair
                    if (!actorsMovies.containsKey(IDActor.get(pieces[1]))) {
                        newSet = null;
                        helpMakeActorMovieMap(pieces, newSet);
                    }
                    // Add movie to the actor's set pair if actor already in map
                    else {
                        helpMakeActorMovieMap(pieces, actorsMovies.get(IDActor.get(pieces[1])));
                    }
                }
            lineNum++;
            }

        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        }
        catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }
    }

    /**
     * Creates a graph (g) of actors where edge relationships are the movies in which connected actors appeared
     */

    public Graph<String, Set<String>> makeGraph() {
        for (String actor1 : actorsMovies.keySet()) {           // selects one actor
            Set<String> actor1MovieSet = actorsMovies.get(actor1);
            for (String movie : actor1MovieSet) {               // iterates over actor1's set of movies
                for (String actor2 : actorsMovies.keySet()) {   // selects actor to be compared to actor 1
                    Set<String> actor2MovieSet = actorsMovies.get(actor2);
                    if (!actor2.equals(actor1)) {               // ensures not comparing to same actor
                        for (String movie2 : actor2MovieSet) {  // iterates over actor2's set of movies
                            if (movie.equals(movie2)) {         // checks if movies are the same

                                Set<String> newEdgeSet;
                                // if edge already exists, add movie to edgeSet
                                if (g.hasEdge(actor1, actor2)) {
                                    helpMakeGraph(actor1, actor2, movie, g.getLabel(actor1, actor2));
                                }
                                // if edge does not exist, create one
                                else {
                                    newEdgeSet = null;
                                    helpMakeGraph(actor1, actor2, movie, newEdgeSet);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return g;
    }

    /**
     * Helper function which inserts directed edges into the graph
     *
     * @param actor1 vertex edge is directed from
     * @param actor2 vertex edge is directed to
     * @param movie movie to add to the edge
     * @param edgeSet set of movies between two vertices
     */
    public void helpMakeGraph(String actor1, String actor2, String movie, Set<String> edgeSet) {
        if (g.hasVertex(actor1) && g.hasVertex(actor2)) {   // checks that both vertices are in graph
            if (!g.hasEdge(actor1, actor2)) {               // creates new edgeSet if there is no edge
                edgeSet = new HashSet<>();
                edgeSet.add(movie);
            }
            else {
                edgeSet.add(movie);                         // adds movie to edgeSet if it already exists
                g.removeDirected(actor1, actor2);           // removes un-updated directed edge
            }
            g.insertDirected(actor1, actor2, edgeSet);      // inserts directed edge
        }
    }

    /**
     * Helper function which inserts directed edges into the graph
     *
     * @param pieces the line from the file that has the actor ID and the movie ID
     * @param set set of movies that the actor has acted in
     */
    public void helpMakeActorMovieMap(String[] pieces, Set<String> set) {
        // Checks if the ID maps contain the information in the file
        if (IDMovie.containsKey(pieces[0]) && IDActor.containsKey(pieces[1])) {
            // if actor has not yet been added to the map, create a new set
            if (!actorsMovies.containsKey(IDActor.get(pieces[1]))) {
                set = new HashSet<>();
            }
            // add the movie to the set
            set.add(IDMovie.get(pieces[0]));

            // pair the actor's name and the movies they've acted in into the map
            actorsMovies.put(IDActor.get(pieces[1]), set);
        }
    }

    /**
     * Code to run the game
     *
     * @param input Scanner
     * @param graph Graph of all actors
     */
    public void kevinBaconGame(Scanner input, Graph<String, Set<String>> graph){
        String universeCenter = "Kevin Bacon"; // set Kevin Bacon to be center originally
        GraphLibrary<String, Set<String>> graphLib = new GraphLibrary<>();  // to run methods
        Graph<String, Set<String>> bfsGraph = graphLib.bfs(graph, universeCenter);  // BFS with Kevin Bacon
        int totalActors = graph.numVertices();

        while (input.hasNext()){
            String line = input.next();

            if (line.equals("c")){
                int num = input.nextInt();
                Map<String, Double> actorToAvgSep = new HashMap<>();
                ArrayList<String> actorList = new ArrayList<>();

                if (num >= 0){
                    System.out.println("The top " + num + " centers of the universe sorted by average separation are: ");
                }
                else{
                    System.out.println("The bottom " + Math.abs(num) + " centers of the universe sorted by average separation are: ");
                }

                for (String actor: graph.vertices()){
                    Graph<String, Set<String>> tempBFS = graphLib.bfs(graph, actor);  // BFS with each actor
                    double avgSep = GraphLibrary.averageSeparation(tempBFS, actor);  // avg sep with that actor
                    actorToAvgSep.put(actor, avgSep);  // put in map to sort later
                    actorList.add(actor);

                    if (num > 0){
                        Comparator<String> comp = Comparator.comparingDouble(actorToAvgSep::get);  // sort by smallest avg sep
                        actorList.sort(comp);
                        }
                    }
                    if (num < 0){
                        Comparator<Object> comp = Comparator.comparingDouble(actorToAvgSep::get).reversed();  // sort by largest avg sep
                        actorList.sort(comp);
                }

                for (int i = 0; i < Math.abs(num); i++){
                    System.out.println(actorList.get(i));  // print the first num results
                }
            }

            if (line.equals("d")){
                int low = input.nextInt();
                int high = input.nextInt();

                List<String> degreeList = GraphLibrary.verticesByInDegree(graph);  // get list of vertices by indegree
                int check = 0;

                for (int i = degreeList.size() - 1; i > 0; i --){
                    String current = degreeList.get(i);
                    if (graph.inDegree(current) <= high && graph.inDegree(current) >= low){  // check that it is in bounds
                        System.out.println(current + " has this many degrees: " + graph.inDegree(current));
                        check += 1;
                    }

                }
                if (check == 0){
                    System.out.println("There are no actors within these two degrees");
                }

            }
            if (line.equals("i")){
                Set<String> missing = GraphLibrary.missingVertices(graph, bfsGraph);   // find missing vertices

                if (missing.size() != 0){
                    System.out.println("Actors with infinite separation: ");
                    for(String missingVertex: missing){
                        System.out.println(missingVertex + ", ");  // if missing, print
                    }
                }
                else{
                    System.out.println("No actors with infinite separation");
                }
            }

            if (line.equals("p")){
                String name = input.next();
                name += input.nextLine();
                List<String> path = graphLib.getPath(bfsGraph, name);  // get path for input

                if (path.size() != 0) {
                    System.out.println(name + "'s number is " + (path.size() - 1));
                }

                for (int i = 0; i < path.size() - 1; i ++){
                    String currentActor = path.get(i);
                    String connectedActor = path.get(i + 1);
                    System.out.println(currentActor + " appeared in " + bfsGraph.getLabel(currentActor, connectedActor) + " with " + connectedActor);  // get edge (movie) and print it
                }

                // if only connection is with universe center
                if (path.size() == 1){
                    System.out.println(name + " appeared in " + bfsGraph.getLabel(name, universeCenter) + " with " + universeCenter);
                }
            }

            if (line.equals("s")){
                int low = input.nextInt();
                int high = input.nextInt();

                Map<String, Integer> separationMap = new HashMap<>();

                for (String vertex: bfsGraph.vertices()){
                    int separation = graphLib.getPath(bfsGraph, vertex).size();  // size of path
                    separationMap.put(vertex, separation);  // put in map to sort later
                }

                ArrayList<String> separationList = new ArrayList<>(separationMap.keySet());


                Comparator<String> comp = Comparator.comparingInt(separationMap::get);  // sort by size of path
                separationList.sort(comp);
                int check = 0;

                for (String s : separationList) {
                    int separation = graphLib.getPath(bfsGraph, s).size();
                    if (separation <= high && separation >= low) {  // check bounds
                        System.out.println(s + " is separated from the current center by " + separation);
                        check++;
                    }
                }

                // if none in bounds
                if (check == 0){
                    System.out.println("No actors found within the separation bounds given");
                }

                }

            if (line.equals("u")){
                universeCenter = input.next();
                universeCenter += input.nextLine(); // account for two word names

                if (graph.hasVertex(universeCenter)) {
                    bfsGraph = graphLib.bfs(graph, universeCenter);
                    int size = totalActors - GraphLibrary.missingVertices(graph, bfsGraph).size();  // check for how many connections out of total

                    System.out.println(universeCenter + " is now the center of the acting universe, connected to " + size + "/" + totalActors + " actors with average separation of " + GraphLibrary.averageSeparation(bfsGraph, universeCenter));
                }
                else{
                    System.out.println(universeCenter + " is not a part of this universe");
                }
            }

            if (line.equals("q")){
                System.out.println("Game quit");
                break;
            }
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        Bacon test = new Bacon();  // throws FileNotFoundException
        test.makeIDMapMovie("");
        test.makeIDMapActor("");
        test.makeActorMovieMap("");
        Graph<String, Set<String>> testGraph = test.makeGraph();


        Bacon bacon = new Bacon();
        bacon.makeIDMapMovie("inputs/moviesTest.txt");
        bacon.makeIDMapActor("inputs/actorsTest.txt");
        bacon.makeActorMovieMap("inputs/movie-actorsTest.txt");
        System.out.println(bacon.makeGraph()); // shows hand-coded graph

        Bacon bacon1 = new Bacon();
        bacon1.makeIDMapMovie("inputs/movies.txt");
        bacon1.makeIDMapActor("inputs/actors.txt");
        bacon1.makeActorMovieMap("inputs/movie-actors.txt");
        Graph<String, Set<String>> g = bacon1.makeGraph();

        Scanner in = new Scanner(System.in);
        bacon1.kevinBaconGame(in, g);  // boundary cases include: setting center of universe to actor not in file, setting lower bound higher than upper bound for "s" and "d"
    }
}

