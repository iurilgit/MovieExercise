/**
 * Created by ruili1 on 1/21/18.
 */

import com.google.gson.Gson;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.io.IOException;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Similarity {

    Double title = 0.0;
    Double year = 0.0;
    Double length = 0.0;
    Double rating = 0.0;
    Double genre  = 0.0;
    Double director = 0.0;
    Double actor = 0.0;

    public Similarity(Movie m1, Movie m2){

        // title similarity (Jaro Winkler distance)
        JaroWinklerDistance distance = new JaroWinklerDistance();
        title = distance.apply(Utils.normalizeStr(m1.title), Utils.normalizeStr(m2.title));

        // release_year distance: 1.0 if matches, 0.0 otherwise
        year = integerSetSimilarity(m1.years, m2.years, 1);

        // run time distance (ratio)
        if(m1.run_time != null && m2.run_time != null) {
            double len1 = (double) (m1.run_time);
            double len2 = (double) (m2.run_time);
            length = Math.min(len1, len2) / Math.max(len1, len2);
        }

        // rating: not used

        // genre: as long as there is an overlap, it's 1, otherwise it's 0
        genre = stringSetSimilarity(m1.genre, m2.genre, 1);

        // director
        director = personSetSimilarity(m1.directors, m2.directors, 1);

        // actor
        actor = personSetSimilarity(m1.actors, m2.actors, 2);
    }

    private double integerSetSimilarity(Set<Integer> set1, Set<Integer> set2, int thresh){

        if(set1 != null && set2 != null) {
            Set<Integer> intersecton = new HashSet<Integer>(set1); // make deep copy
            intersecton.retainAll(set2);
            return intersecton.size() >= thresh ? 1.0 : 0.0;
        }else{
            return 0.0;
        }
    }

    private double stringSetSimilarity(Set<String> set1, Set<String> set2, int thresh){

        if(set1 != null && set2 != null) {
            Set<String> intersecton = new HashSet<String>(set1); // make deep copy
            intersecton.retainAll(set2);
            return intersecton.size() >= thresh ? 1.0 : 0.0;
        }else{
            return 0.0;
        }
    }

    /**
     * Get the person set similarity: if two sets of Person has overlapped Person num >= thresh, return true, otherwise false.
     * @param set1
     * @param set2
     * @param thresh 1, 2, 3 to mean the min number of Person in common
     * @return
     */
    private double personSetSimilarity(Set<Person> set1, Set<Person> set2, int thresh){

        if(set1 != null && set2 != null) {
            Set<Person> intersecton = new HashSet<Person>(set1); // make deep copy
            intersecton.retainAll(set2);
            return intersecton.size() >= thresh ? 1.0 : 0.0;
        }else{
            return 0.0;
        }
    }

    public boolean same(double thresh){

        double s1 = (title + year)/2;
        double s2 = (title + actor + director)/3;
        double s3 = (year + actor + director)/3;

        return s1 >= thresh || s2 >= thresh || s3 >= thresh;
    }

    @Override
    public String toString(){

        return "title: " + title
                + ", year: " + year
                + ", length: " + length
                + ", rating: " + rating
                + ", genre: " + genre
                + ", director: " + director
                + ", actor: " + actor;
    }

    public static void main(String[] args) throws IOException {

        // load the file content into a string
        String filePath = "/Users/ruili1/Downloads/movies_exercise/two_movies4.json";
        String moviesStr = new String(Files.readAllBytes(Paths.get(filePath)));

        // deserialize the string to Movies object
        Movies movies = new Gson().fromJson(moviesStr, Movies.class);
        movies.structurizeMoreInfo();
        System.out.println(movies.toString());

        // test similarity measure
        Similarity similarity = new Similarity(movies.get(0), movies.get(1));
        System.out.println(similarity);
        System.out.println(similarity.same(1.0));
    }

}
