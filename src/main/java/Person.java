import com.google.gson.Gson;

import java.util.*;

/**
 * Created by ruili1 on 1/20/18.
 */
public class Person {

    String personId = null;
    String personName = null;
    String personRole = null;
    String characterName = null;

    public Person(String personId, String personName, String personRole, String characterName){

        this.personId = personId;
        this.personName = personName;
        this.personRole = personRole;
        this.characterName = characterName;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this){
            return true;
        }

        if (!(o instanceof Person)) {
            return false;
        }

        Person p = (Person) o;
        if(p.personName != null){
            if(!p.personName.equals(personName)) {
                return false;
            }
        }else{
            if(personName != null){
                return false;
            }
        }

        if(p.personRole != null){
            if(!p.personRole.equals(personRole)) {
                return false;
            }
        }else{
            if(personRole != null){
                return false;
            }
        }

//        if(p.characterName != null){
//            if(!p.characterName.equals(characterName)) {
//                return false;
//            }
//        }else{
//            if(characterName != null){
//                return false;
//            }
//        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = 17;

        if(personName != null) {
            result = 31 * result + personName.hashCode();
        }
        if(personRole != null) {
            result = 31 * result + personRole.hashCode();
        }
//        if(characterName != null) {
//            result = 31 * result + characterName.hashCode();
//        }
        return result;
    }

    public String toString(){

        return "(personId = " + personId
                + ", personName = " + personName
                + ", personRole = " + personRole
                + ", characterName = " + characterName + ")";
    }

    public static void main(String[] args){

        // deserialize a person obj
//        String jsonStr = "{\"personId\":\"5k18q33odtjwwb1xox3y7l8yy\",\"personName\":\"Kenny Ortega\",\"personRole\":\"director\",\"characterName\":null}";
//        Person person = new Gson().fromJson(jsonStr, Person.class);
//        System.out.println(person.toString());

        // try custom equal()
        String json1 = "{\"personId\":\"5k18q33odtjwwb1xox3y7l8yy\",\"personName\":\"Kenny Ortega\",\"personRole\":\"director\"}";
        String json2 = "{\"personId\":\"440svutmum84mmuj7zi6ctwj4\",\"personName\":\"Kenny Ortega\",\"personRole\":\"director\"}";
        String json3 = "{\"personName\":\"Kenny Ortega\",\"personRole\":\"director\"}";

        Person p1 = new Gson().fromJson(json1, Person.class);
        Person p2 = new Gson().fromJson(json2, Person.class);
        Person p3 = new Gson().fromJson(json3, Person.class);
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p1.equals(p2));

        Set<Person> set1 = new HashSet<>();
        set1.add(p1);
        set1.add(p2);
//        System.out.println(set1);
        Set<Person> set2 = new HashSet<>();
        set2.add(p3);
        set1.addAll(set2);
        System.out.println(set1);


//        List<Person> personList = new ArrayList<Person>();
//        personList.add(new Person("id1", "name1", "role1", "charNeme1"));
//        personList.add(new Person("id2", "name2", "role2", "charNeme2"));
//        String s = new Gson().toJson(personList);
//        System.out.println(s);

    }
}
