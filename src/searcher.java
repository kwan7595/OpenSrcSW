import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
public class searcher {
    HashMap<String, Integer> Query = new HashMap<>();

    public void searcher(String path, String q) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        String QueryString = q;
        Query = ExtractKeyword(QueryString); //create query vector
        HashMap<String, ArrayList<Double>> weightHash = readInvertedFile(path); // read inverted file and get weight HashMap
        HashMap<String, Double> queryDocSimilarity = CalcSim(weightHash, Query); //calculates similarity of query and each document
        simRanking(queryDocSimilarity);
    }
    public HashMap<String, Double> CalcSim(HashMap _weightHash, HashMap _Query) throws ParserConfigurationException, IOException, SAXException {
        HashMap<String, ArrayList<Double>> weightHash = _weightHash; //initializes hashmap format
        HashMap<String, Integer> Query = _Query;
        HashMap<String, Double> InnerProduct = InnerProduct(weightHash, Query);
        ArrayList<String> doctitle = getDocumentTitle("./output/collection.xml");
        HashMap<String, Double> queryDocSim = new HashMap<>(); //total similarity vector.
        double QuerySize = 0.0;
        double DocVecSize = 0.0;
        for (int i = 0; i < doctitle.size(); i++) {
            queryDocSim.put(doctitle.get(i), 0.0); //initialize querydocsim hashmap with title,0.0
        }
        for(int i=0;i<doctitle.size();i++){
            double sim=0.0;
            DocVecSize=0.0;
            Iterator<String> it = Query.keySet().iterator();
            while(it.hasNext()) {
                String key = it.next();
                if(i==0) QuerySize += Query.get(key)*Query.get(key);
                double weight = weightHash.get(key).get(i);
                DocVecSize += weight * weight;
            }
            if(QuerySize==0||DocVecSize==0) sim =0.0;
            else sim = (InnerProduct.get(doctitle.get(i)))/((Math.sqrt(QuerySize))*(Math.sqrt(DocVecSize)));
            queryDocSim.put(doctitle.get(i),sim);
        }
        return queryDocSim;
    public void simRanking(HashMap _queryDocSim) {
        HashMap<String, Double> queryDocSim = _queryDocSim;
        List<String> keySet = new ArrayList<>(queryDocSim.keySet());
        keySet.sort((o1, o2) -> Double.compare(queryDocSim.get(o2),queryDocSim.get(o1)));
        int counter=0;
        for (String key : keySet) {
                if(queryDocSim.get(key)==0.0){
                    if(counter==0){
                        System.out.println("검색된 문서가 없습니다");
                        break;
                    }
                    break;
                }
                else {
                    if(counter>=3) break;
                    System.out.println(String.format("Key : %s, Value : %s", key, queryDocSim.get(key)));
                }
                counter++;
        }
    }
}