import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by ruili1 on 1/20/18.
 */
public class Movies {

    private List<Movie> docs = null; // a list of movies
    transient private Map<CanonicalForm, Set<Movie>> clusters = null; // key: canonical form of a cluster, value: a set of movies in that cluster

    public Movies() {

        docs = new ArrayList<Movie>();
        clusters = new HashMap<CanonicalForm, Set<Movie>>();
    }

    public List<Movie> getDocs(){

        return docs;
    }

    public Movie get(int i){

        return docs.get(i);
    }

    /**
     * Due to the format of the json file, some fields are in string, not directly serializable,
     * so need to structurize it once deserialized.
     */
    public void structurizeMoreInfo() {

        for (Movie doc : docs) {
            doc.extractPersonInfo();
            doc.extractYears();
            doc.cleanGenre();
        }
    }

    public int size(){

        return docs.size();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (Movie doc : docs) {
            sb.append(doc.toString() + "\n\n");
        }

        return sb.toString();
    }

    public void analyze() {

        int count = 0;
        for (Movie doc : docs) {
            if(doc.release_date != null) {
                System.out.println(doc.release_date);
                count++;
            }else{
//                System.out.println(doc);
            }
        }

        System.out.println(count + " docs with release_year");
    }

    public void cluster() {

        // build a graph to depict the connections (similarity is high enough):
        int[] nodes = new int[docs.size()];
        List<int[]> edges = new ArrayList<>();

        // get the # of pairs that are "same" and add them as edges to the graph
        for (int i = 0; i < docs.size(); i++){
            for(int j = i+1; j < docs.size(); j++) {
                Movie doc1 = docs.get(i);
                Movie doc2 = docs.get(j);
                Similarity sim = new Similarity(doc1, doc2);
                if(sim.same(1.0)){
                    edges.add(new int[]{i, j});
                }
            }
        }
//        System.out.println(edges.size() + " paris are 'same'");

        int clusterNum = Utils.numberOfConnectedComponents(nodes, edges);
//        System.out.println(clusterNum + " clusters from connected component");
        convertConnectedComponentsToClusterMap(nodes);

//        printClusters();
    }

    private void convertConnectedComponentsToClusterMap(int[] nodes){

        // construct a cluster map with content being the doc ID
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for(int i = 0; i < nodes.length; i++){
            Set<Integer> set = new HashSet<>();
            if(map.containsKey(nodes[i])){
                set = map.get(nodes[i]);
            }
            set.add(i);
            map.put(nodes[i], set);
        }
//        System.out.println(map.keySet().size() + " clusters in the intermediate map");

        // extract canonical form of a movie and fill in clusters
        clusters = new HashMap<>();
        for(Integer k : map.keySet()) {
            Set<Movie> set = new HashSet<Movie>();
            for(int v : map.get(k)){
                set.add(docs.get(v));
            }
            CanonicalForm canonicalForm = new CanonicalForm(set);
            clusters.put(canonicalForm, set);
        }

//        // validate clusters
//        int count = 0;
//        for(CanonicalForm key : clusters.keySet()){
//            count += clusters.get(key).size();
//        }
//        System.out.println(clusters.keySet().size() + " clusters in the final map");
//        System.out.println(count + " movies in the cluster map");
    }

    private void printClusters() {

        List<CanonicalForm> sortedKeys = new ArrayList<CanonicalForm>(clusters.keySet());
        Collections.sort(sortedKeys);
        for (CanonicalForm key : sortedKeys) {
            System.out.println("[" + Utils.normalizeStr(key.title) + " " + key.release_year + "]");
            List<Movie> list = new ArrayList<>(clusters.get(key));
            Collections.sort(list);
            for (Movie doc : list) {
                System.out.println(doc.getKeyInfo());
            }
            System.out.println("");
        }

        System.out.println(sortedKeys.size() + " cluster");
    }

    public void serializeCanonicalFormsOfClusters(String outputFile){

        List<CanonicalForm> sortedKeys = new ArrayList<CanonicalForm>(clusters.keySet());
        Collections.sort(sortedKeys);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            FileUtils.writeStringToFile(new File(outputFile), gson.toJson(sortedKeys), "UTF-8");
            System.out.println(sortedKeys.size() + " canonical forms saved to " + outputFile);
        }catch(IOException e){
            System.err.println("issue generating " + outputFile);
        }
    }

    public static void main(String[] args) throws IOException {

        String inputFilePath;
        String outputFilePath;
        if(args.length != 2){
            System.out.println("Usage: inputFilePath outputFilePath");
//            inputFilePath = "./data/movies.json";
//            outputFilePath = "./data/output2.json";
            return;
        }else{
            inputFilePath = args[0];
            outputFilePath = args[1];
        }

        /* load the file content into a string */
        String moviesStr = FileUtils.readFileToString(new File(inputFilePath), "UTF-8");
//        System.out.println(moviesStr);

        /* deserialize the string to Movies object */
        Movies movies = new Gson().fromJson(moviesStr, Movies.class);
        movies.structurizeMoreInfo();
        System.out.println(movies.size() + " movies in " + inputFilePath);

        /* run analysis */
//        movies.analyze();

        /* run clustering and print out the results */
        movies.cluster();
        movies.serializeCanonicalFormsOfClusters(outputFilePath);
    }
}
