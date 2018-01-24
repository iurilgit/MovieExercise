import java.util.*;

/**
 * Created by ruili1 on 1/20/18.
 */
public class Utils {

    static final Set<String> genreDict = new HashSet<>(Arrays.asList(
            "Action",
            "Adventure",
            "Animation",
            "Biography",
            "Comedy",
            "Crime",
            "Documentary",
            "Drama",
            "Family",
            "Fantasy",
            "Film Noir",
            "History",
            "Horror",
            "Music",
            "Musical",
            "Mystery",
            "Romance",
            "Sci-Fi",
            "Short",
            "Sport",
            "Superhero",
            "Thriller",
            "War",
            "Western"));

    public static String normalizeStr(String s){

        String updated = s.replaceAll("(\\(.*\\))|(\\[.*\\])|(\\（.*\\）)", ""); // remove all info in () and []
        updated = updated.replaceAll("[\\-—–:,]", " "); // remove special chars: -,:
        updated = updated.replaceAll("[\\p{Zs}\\s]+", " "); // remove extra spaces

        return updated.trim().toLowerCase();
    }

    public static int numberOfConnectedComponents(int[] nodes, List<int[]> edges) {

        int count = nodes.length;

        for(int i=0; i<count; i++){
            nodes[i]=i;
        }

        for(int[] edge : edges){
            int x = edge[0];
            int y = edge[1];

            int xRoot = getRoot(nodes, x);
            int yRoot = getRoot(nodes, y);

            if(xRoot!=yRoot){
                count--;
                union(nodes, xRoot, yRoot);
            }
        }

        return count;
    }

    private static void union(int[] arr, int x, int y){

        for(int i = 0; i<arr.length; i++){
            if(arr[i] == x){
                arr[i] = y;
            }
        }
    }

    private static int getRoot(int[] arr, int i){

        while(arr[i]!=i){
            arr[i]= arr[arr[i]];
            i=arr[i];
        }
        return i;
    }

    public static String majorityVotingString(List<String> list){

        int majorityFormCount = 0;
        String majorityForm = null;
        for(String o : list){
            if(majorityFormCount == 0){
                majorityForm = o;
            }
            if(o.equals(majorityForm)){
                majorityFormCount ++;
            }else{
                majorityFormCount --;
            }
        }

        return majorityForm;
    }

    public static Integer majorityVotingInteger(List<Integer> list){

        int majorityFormCount = 0;
        Integer majorityForm = null;
        for(Integer o : list){
            if(majorityFormCount == 0){
                majorityForm = o;
            }
            if(o.equals(majorityForm)){
                majorityFormCount ++;
            }else{
                majorityFormCount --;
            }
        }

        return majorityForm;
    }


    public static void main(String[] args){

        // test normalizeStr()
//        System.out.println(normalizeStr("NAME (1) () [a] b -—– ::（字幕版）"));
//        System.out.println(normalizeStr("name ( ab ) []"));

        // test majorityVoting()
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("a");
        list.add("c");
        System.out.println(majorityVotingString(list));
    }
}
