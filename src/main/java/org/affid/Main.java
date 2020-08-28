package org.affid;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.affid.forcefirectedscheme.Edge;
import org.affid.forcefirectedscheme.GraphProcessor;
import org.affid.forcefirectedscheme.Point;
import org.affid.forcefirectedscheme.Vertex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class Main {

    static HashMap<Long, String> authors = new HashMap<>();
    static HashMap<String, String> titles = new HashMap<>();
    static HashMap<String, Integer> rate = new HashMap<>();
    static HashMap<String, Sphere> spheres = new HashMap<>();
    static ArrayList<String> allKeys = new ArrayList<>();
    static ArrayList<Integer> years;
    static ArrayList<Color> colors;

    static int maxRate;


    public static void main(String[] args) {
        HashMap<String, org.affid.Article> articles = readCSV("src/main/resources/ScopusDB.csv");
        turnRefsToIds(articles);
        updateRate(articles);
        cleanNulls(articles);

        //MyGraph.main(String.format("%d;0.002;%d;%d",900,6000,6000).split(";"));

//        fillKeys(articles);
//
//        org.affid.Color.addColor("Electric conductance",org.affid.Color.green);
//        org.affid.Color.addColor("calculation",org.affid.Color.red);
//        org.affid.Color.addColor("Atomic physics",org.affid.Color.yellow);
//        org.affid.Color.addColor("Physics",org.affid.Color.black);
//
        maxRate = Collections.max(rate.values());

        years = getYears(articles);

        colors = Color.getGradient(Color.orange, Color.sky, years.size());
//

        float C = maxRate*1.2f;
        int size = (int) (Math.log(articles.size()/Math.log(1.05)) * 2.5 * C);
        System.out.println(C);
        System.out.println(size);
        float temperature = 1000;
//
        spheres = generateSpheres(size*0.8f, articles);

        ArrayList<Integer> space = new ArrayList<>();
        space.add(size);
        space.add(size);
        space.add(size);

        HashMap<String, Vertex> vertices = new HashMap<>();
        ArrayList<Edge> edges = new ArrayList<>();

        makeVertices(vertices, edges);

        GraphProcessor gp = new GraphProcessor(vertices.values(), edges, space, C, temperature);

        gp.makeLayout(40);

        getCoordinates(vertices);

        //spheresToJSON();
//
        toJSON(articles);

    }

    private static float getAverageRate() {
        float sum = 0;
        for (int i : rate.values()) {
            sum += i;
        }
        return sum/rate.size();
    }

    private static ArrayList<Integer> getYears(HashMap<String,Article> articles){
        Set<Integer> yearsSet = new HashSet<>();
        for(Map.Entry<String,Article> article: articles.entrySet()){
            yearsSet.add(Integer.parseInt(article.getValue().getYear()));
        }
        ArrayList<Integer> years = new ArrayList<>(yearsSet);
        Collections.sort(years);
        return years;
    }
//    private static void replaceSpheres(int times, double k){
//        for(int i = 0; i < times; i++){
//            System.out.println(i);
//            org.affid.SphereOld.replaceSpheres(spheres, k);
//        }
//    }

    public static void spheresToJSON() {
        try {
            FileWriter writer2 = new FileWriter("output/spheres.js");
            StringWriter spheres1 = new StringWriter();
            spheres1.write("export default ");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(spheres1, spheres);
            writer2.write(spheres1.toString());
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void makeVertices(HashMap<String, Vertex> vertices, ArrayList<Edge> edges) {
        for (Map.Entry<String, Sphere> sphere : spheres.entrySet()) {
            vertices.put(sphere.getKey(), new Vertex(new Point((float) sphere.getValue().getX(), (float) sphere.getValue().getY(), (float) sphere.getValue().getZ())));
        }
        for (Map.Entry<String, Vertex> vertex : vertices.entrySet()) {
            for (String link : spheres.get(vertex.getKey()).getLinks()) {
                edges.add(new Edge(vertex.getValue(), vertices.get(link)));
            }
        }
    }

    private static void getCoordinates(HashMap<String, Vertex> vertices) {
        for (Map.Entry<String, Vertex> vertex : vertices.entrySet()) {
            spheres.get(vertex.getKey()).setX(vertex.getValue().getPos().getX());
            spheres.get(vertex.getKey()).setY(vertex.getValue().getPos().getY());
            spheres.get(vertex.getKey()).setZ(vertex.getValue().getPos().getZ());
        }
    }

    private static void toJSON(HashMap<String, Article> articles) {
        try {
            FileWriter writer = new FileWriter("output/database.js");
            FileWriter writer1 = new FileWriter("output/authors.js");
            FileWriter writer2 = new FileWriter("output/spheres.js");
            StringWriter database = new StringWriter();
            StringWriter authors1 = new StringWriter();
            StringWriter spheres1 = new StringWriter();
            spheres1.write("export default ");
            authors1.write("export default ");
            database.write("export default ");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(database, articles);
            mapper.writeValue(authors1, authors);
            mapper.writeValue(spheres1, spheres);
            writer.write(database.toString());
            writer1.write(authors1.toString());
            writer2.write(spheres1.toString());
            writer.close();
            writer1.close();
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static double getRandom(double edge) {
        return (Math.random() * edge) * (Math.pow(-1, Math.round(Math.random() * 1)));
    }

    private static HashMap<String, Sphere> generateSpheres(double edge, HashMap<String, Article> articles) {
        HashMap<String, Sphere> spheres1 = new HashMap<>();
        for (Map.Entry<String, Integer> article : rate.entrySet()) {
            Sphere sphere = new Sphere(((double) article.getValue() + 1) / (maxRate + 1),
                    edge, articles.get(article.getKey()).getRefs(),
                    colors.get(years.indexOf(Integer.parseInt(articles.get(article.getKey()).getYear()))));
            spheres1.put(article.getKey(), sphere);
        }
        //
        return spheres1;
    }

    private static void cleanNulls(HashMap<String, Article> articles) {
        ArrayList<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, Article> article : articles.entrySet()) {
            if (article.getValue().getRate() == 0 && article.getValue().getRefs().size() == 0)
                toDelete.add(article.getKey());
        }
        for (String key : toDelete) {
            titles.remove(articles.get(key).getName());
            articles.remove(key);
            rate.remove(key);
        }
        updateAuthors(articles);
    }

    private static void turnRefsToIds(HashMap<String, Article> articles) {
        for (Map.Entry<String, Article> article : articles.entrySet()) {
            ArrayList<String> links = article.getValue().getRefs();
            for (int i = 0; i < links.size(); i++) {
                links.set(i, titles.getOrDefault(links.get(i), "Удалить"));
            }
            links.removeIf(Predicate.isEqual("Удалить"));
            refreshRate(links);
            article.getValue().setRefs(links);
        }
    }

    private static void updateAuthors(HashMap<String, Article> articles) {
        HashMap<Long, String> newAuthors = new HashMap<>();
        for (Map.Entry<String, Article> article : articles.entrySet()) {
            for (Long i : article.getValue().getAuthors()) {
                newAuthors.put(i, authors.get(i));
            }
        }
        authors = newAuthors;
    }

    private static void updateRate(HashMap<String, Article> articles) {
        for (Map.Entry<String, Article> article : articles.entrySet()) {
            article.getValue().setRate(rate.get(article.getKey()));
        }
    }

    private static void refreshRate(ArrayList<String> links) {
        for (String i : links) {
            rate.put(i, rate.get(i) + 1);
        }
    }

    private static ArrayList<String> getFullCitation(CSVRecord record) {
        String[] links = record.get("Пристатейные ссылки").split("; ");
        ArrayList<String> citations = new ArrayList<>();
        for (String link : links) {
            if (getArticleName(link) != null)
                citations.add(getArticleName(link));
        }
        return citations;
    }

    private static void fillKeys(HashMap<String, Article> articles) {
        HashSet<String> set = new HashSet<>();
        for (Map.Entry<String, Article> article : articles.entrySet()) {
            set.addAll(article.getValue().getKeys());
        }
        allKeys.addAll(set);
        for (String key : allKeys)
            Color.addColor(key);
    }

    private static String getArticleName(String full) {
        int end = full.indexOf('(');
        if (end == -1)
            return null;
        int start = full.lastIndexOf(".,", end);
        if (start == -1 || start + 3 > end - 1)
            return null;
        return full.substring(start + 3, end - 1).equals("") ? null : full.substring(start + 3, end - 1);
    }

    private static HashMap<String, Article> readCSV(String path) {
        HashMap<String, Article> articles = new HashMap<>();
        try {
            Reader in = new FileReader(path);
            for (CSVRecord record : CSVFormat.DEFAULT.withFirstRecordAsHeader().parse((in))) {
                int size = record.get("Идентификатор автора(ов)").split(";").length;
                String[] ids = record.get("Идентификатор автора(ов)").split(";");
                if (!(ids[0].equals("[Отсутствует идентификатор автора]")
                        || (ids[0].equals("")))
                        && (record.get("DOI").length() > 0)) {
                    ArrayList<Long> author = new ArrayList<>();
                    String[] names = record.get("Авторы").split(", ");
                    for (int i = 0; i < size; i++) {
                        authors.put(Long.parseLong(ids[i]), names[i]);
                        author.add(Long.parseLong(ids[i]));
                    }
                    titles.put(record.get("Название"), record.get("DOI"));
                    rate.put(record.get("DOI"), 0);
                    ArrayList<String> links = getFullCitation(record);
                    ArrayList<String> keys = new ArrayList<>();
                    //Collections.addAll(keys,record.get("Ключевые слова указателя").split(";"));
                    articles.put(record.get("DOI"), new Article(author, record.get("Название"),
                            record.get("Год"), 0, record.get("DOI"),
                            record.get("Ссылка"), links, keys));
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }
}
