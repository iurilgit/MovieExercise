import com.google.gson.Gson;

import java.util.*;

/**
 * Created by ruili1 on 1/21/18.
 */
public class CanonicalForm implements Comparable<CanonicalForm> {

    static int counter = 0 ;
    Integer canonicalId = null;
    Set<String> references = null;

    Integer release_year = null;
    Integer run_time = null;
    String title = null;
    String description = null;
    List<String> content_rating = null;
    List<String> genre = null;
    List<String> cast_and_crew_all = null;
    List<String> role_director = null;
    List<String> role_actor = null;

    /**
     * Hack: use first movie as the canonical form
     * @param movies a set of Movie objs
     * @return a canonical form (Movie obj)
     */
    public CanonicalForm(Set<Movie> movies){

        // canonical ID
        counter++;
        canonicalId = counter;

        // references: union
        references = new HashSet<>();
        for(Movie movie : movies){
            references.add(movie.reference_id);
        }

        // title: majority voting
        List<String> titles = new ArrayList<>();
        for(Movie movie : movies){
            titles.add(movie.title);
            break;
        }
        title = Utils.majorityVotingString(titles);

        // description: majority voting
        List<String> descriptions = new ArrayList<>();
        for(Movie movie : movies){
            titles.add(movie.description);
            break;
        }
        description = Utils.majorityVotingString(descriptions);

        // release_year: majority voting
        List<Integer> years = new ArrayList<>();
        for(Movie movie : movies){
            if(movie.years != null) {
                years.addAll(movie.years);
            }
        }
        release_year = Utils.majorityVotingInteger(years);

        // run_time: average
        run_time = 0;
        for(Movie movie : movies){
            if(movie.run_time != null) {
                run_time += movie.run_time;
            }
        }
        run_time /= movies.size();

        // content_rating: union of all content_ratings
        HashSet set = new HashSet<>();
        for(Movie movie : movies){
            if(movie.content_rating != null) {
                set.addAll(movie.content_rating);
            }
        }
        content_rating = new ArrayList<>(set);
        Collections.sort(content_rating);

        // genre: union of all content_ratings
        set = new HashSet<>();
        for(Movie movie : movies){
            if(movie.genre != null) {
                set.addAll(movie.genre);
            }
        }
        genre = new ArrayList<>(set);
        Collections.sort(genre);

        // cast_and_crew_all: a list of serialized Person objects for "crew"
        Set<Person> crew = new HashSet<>();
        for(Movie movie : movies){
            if(movie.crew != null) {
                crew.addAll(movie.crew);
            }
        }
        cast_and_crew_all = serializePersonSet(crew);

        // role_director: a list of serialized Person objects for "directors"
        Set<Person> directors = new HashSet<>();
        for(Movie movie : movies){
            if(movie.directors != null) {
                directors.addAll(movie.directors);
            }
        }
        role_director = serializePersonSet(directors);

        // role_actor: a list of serialized Person objects for "actors"
        Set<Person> actors = new HashSet<>();
        for(Movie movie : movies){
            if(movie.actors != null) {
                actors.addAll(movie.actors);
            }
        }
        role_actor = serializePersonSet(actors);
    }

    private List<String> serializePersonSet(Set<Person> personSet){

        Set<String> strSet = new HashSet<>();
        for(Person p : personSet){
            strSet.add(new Gson().toJson(p));
        }

        List<String> sortedList = new ArrayList<>(strSet);
        Collections.sort(sortedList);
        return sortedList;
    }

    private String getKeyInfo(){

        return Utils.normalizeStr(title) + " " + release_year;
    }

    @Override
    public int compareTo(CanonicalForm o) {

        return (this.canonicalId.compareTo(o.canonicalId));
//        return (this.getKeyInfo().compareTo(o.getKeyInfo()));
    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("canonicalID: " + canonicalId + "\n"
                + "title: " + title + "\n"
                + "release_year: " + release_year + "\n"
                + "references: " + references + "\n"
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

        return sb.toString();
    }

}
