import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ruili1 on 1/20/18.
 */
public class Movie implements Comparable<Movie> {

    // original members from json
    String release_date = null;
    Integer release_year = null;
    Integer run_time = null;
    Set<String> content_rating = null;
    String description = null;
    Set<String> genre = null;
    String title = null;
    Set<String> role_director = null;
    Set<String> cast_and_crew_all = null;
    Set<String> role_actor = null;
    String reference_id = null;

    // extracted members
    transient Set<Person> crew = null;
    transient Set<Person> actors = null;
    transient Set<Person> directors = null;
    transient Set<Person> otherCrews = null;
    transient Set<Integer> years = null;

    static Map<String, String> genreMap = null; // a normalization map for genre

    public void extractPersonInfo(){

        Gson gson = new Gson();
        crew = new HashSet<Person>();
        if(role_director != null) {
            for (String s : role_director) {
                crew.add(gson.fromJson(s, Person.class));
            }
        }
        if(cast_and_crew_all != null) {
            for (String s : cast_and_crew_all) {
                crew.add(gson.fromJson(s, Person.class));
            }
        }
        if(role_actor != null) {
            for (String s : role_actor) {
                crew.add(gson.fromJson(s, Person.class));
            }
        }

        // put every person in crew into sub groups: directors, actors and otherCrews
        directors = new HashSet<Person>();
        actors = new HashSet<Person>();
        otherCrews = new HashSet<Person>();
        for(Person p : crew){
            switch (p.personRole) {
                case "director":
                    directors.add(p);
                    break;
                case "actor":
                    actors.add(p);
                    break;
                default:
                    otherCrews.add(p);
            }
        }
    }

    /**
     * Some times, the release_year and the year in release_date don't match,
     * so extract the year from release_date and put them in a set.
     */
    public void extractYears(){

        if(release_year == null && release_date == null){
            return;
        }

        years = new HashSet<>();
        if(release_year != null){
            years.add(release_year);
        }

        if(release_date != null && release_date.matches("^\\d{4}-.*")){
            years.add(Integer.parseInt(release_date.substring(0, 4)));
        }
    }

    public void cleanGenre(){

        if(genre == null){
            return;
        }

        if(genreMap == null){
            genreMap = new HashMap<>();
            for(String s : Utils.genreDict){
                genreMap.put(Utils.normalizeStr(s), s);
            }
        }

        Set<String> genreCleaned = new HashSet<>();
        for(String s: genre){
            String normalized = Utils.normalizeStr(s);
            if(genreMap.containsKey(normalized)){
                genreCleaned.add(genreMap.get(normalized));
            }else {
                String[] words = s.split("\\s+");
                for (String word : words) {
                    word = Utils.normalizeStr(word);
                    if (genreMap.containsKey(word)){
                        genreCleaned.add(genreMap.get(word));
                    }
                }
            }
        }
        genre = genreCleaned;
    }

    public String getKeyInfo(){

        return title + "::" + years + "::" + run_time + "::" + reference_id;
    }

    @Override
    public int compareTo(Movie otherMovie) {

        return (this.getKeyInfo().compareTo(otherMovie.getKeyInfo()));
    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("title: " + title + "\n"
                + "reference_id: " + reference_id + "\n"
                + "release_date: " + release_date + "\n"
                + "release_year: " + release_year + "\n"
                + "run_time: " + run_time + "\n"
                + "description: " + description +"\n");

        if(content_rating != null){
            sb.append("content_rating:[" + content_rating.size() + "]\n");
            for(String s : content_rating){
                sb.append(s + "\n");
            }
        }else{
            sb.append("content_rating:[0]\n");
        }

        if(genre != null) {
            sb.append("genre:[" + genre.size() + "]\n");
            for (String s : genre) {
                sb.append(s + "\n");
            }
        }else{
            sb.append("genre:[0]\n");
        }

        if(role_director != null) {
            sb.append("role_director:[" + role_director.size() + "]\n");
            for (String s : role_director) {
                sb.append(s + "\n");
            }
        }else{
            sb.append("role_director:[0]\n");
        }

        if (cast_and_crew_all != null) {
            sb.append("cast_and_crew_all:[" + cast_and_crew_all.size() + "]\n");
            for(String s : cast_and_crew_all){
                sb.append(s + "\n");
            }
        }else{
            sb.append("cast_and_crew_all:[0]\n");
        }

        if(role_actor != null) {
            sb.append("role_actor:[" + role_actor.size() + "]\n");
            for (String s : role_actor) {
                sb.append(s + "\n");
            }
        }else{
            sb.append("role_actor:[0]\n");
        }

//        if(crew != null) {
//            sb.append("crew (extracted):[" + crew.size() + "]\n");
//            for (Person p : crew) {
//                sb.append(p.toString() + "\n");
//            }
//        }else{
//            sb.append("crew (extracted):[0]\n");
//        }

        if(directors != null) {
            sb.append("directors (extracted):[" + directors.size() + "]\n");
            for (Person p : directors) {
                sb.append(p.toString() + "\n");
            }
        }else{
            sb.append("directors (extracted):[0]\n");
        }

        if(actors != null) {
            sb.append("actors (extracted):[" + actors.size() + "]\n");
            for (Person p : actors) {
                sb.append(p.toString() + "\n");
            }
        }else{
            sb.append("actors (extracted):[0]\n");
        }

        if(otherCrews != null) {
            sb.append("other crews (extracted):[" + otherCrews.size() + "]\n");
            for (Person p : otherCrews) {
                sb.append(p.toString() + "\n");
            }
        }else{
            sb.append("other crews (extracted):[0]\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) throws IOException {

        // test deserialization of one object
//        String filePath = "/Users/ruili1/Downloads/movies_exercise/one_movie.json";
//        String movieStr = new String(Files.readAllBytes(Paths.get(filePath)));
//        System.out.println(movieStr);
//        Movie movie = new Gson().fromJson(movieStr, Movie.class);
//        System.out.println(movie.toString());

        // test custom comparator
//        String filePath = "/Users/ruili1/Downloads/movies_exercise/two_movies.json";
//        String moviesStr = new String(Files.readAllBytes(Paths.get(filePath)));
//        Movies movies = new Gson().fromJson(moviesStr, Movies.class);
//        System.out.println(movies.toString());
//        Collections.sort(movies.getDocs(), Collections.<Movie>reverseOrder());
//        System.out.println(movies.toString());

        String s =  "2005-"; //"2005-06-15T00:00:00Z"; //
        System.out.println(s.matches("^\\d{4}-.*"));
        System.out.println(s.substring(0,4));
    }
}
