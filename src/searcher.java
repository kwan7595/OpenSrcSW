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
public class searcher{
    HashMap<String,Integer> Query = new HashMap<>();
    public void searcher(String path,String q) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        String QueryString = q;
        Query=ExtractKeyword(QueryString); //create query vector
        HashMap<String,ArrayList<Double>> weightHash = readInvertedFile(path); // read inverted file and get weight HashMap
        HashMap<String,Double> queryDocSimilarity=CalcSim(weightHash,Query); //calculates similarity of query and each document
        simRanking(queryDocSimilarity);
    }
    public HashMap<String,Integer> ExtractKeyword(String str){ //creates query vector(hashmap)
        HashMap<String,Integer> _Query = new HashMap<>();
        KeywordExtractor k = new KeywordExtractor();
        String keyword; //temporary string holder
        int tf; //temporary term frequency holder
        KeywordList kl = k.extractKeyword(str, true);
        for(int i=0;i<kl.size();i++){
            Keyword kwrd = kl.get(i);
            keyword=kwrd.getString();  //get keyword from query
            tf=kwrd.getCnt(); //get TF.
            _Query.put(keyword,tf); //puts keyword, tf into query hashmap
        }
        return _Query;
    }
    public HashMap readInvertedFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream filestream = new FileInputStream(path); //reading index.post
        ObjectInputStream objectInputStream = new ObjectInputStream(filestream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return (HashMap)object;
    }
    public HashMap<String, Double> CalcSim(HashMap _weightHash,HashMap _Query) throws ParserConfigurationException, IOException, SAXException {
        HashMap<String,ArrayList<Double>> weightHash = _weightHash; //initializes hashmap format
        HashMap<String,Integer> Query = _Query;
        ArrayList<String> doctitle=getDocumentTitle("./output/collection.xml");
        HashMap<String,Double> queryDocSim = new HashMap<>(); //total similarity vector.
        for(int i=0;i<doctitle.size();i++){
            //System.out.printf("%s\n",doctitle.get(i));
            queryDocSim.put(doctitle.get(i),0.0); //initialize querydocsim hashmap with title,0.0
            //System.out.printf("%f\n",queryDocSim.get(doctitle.get(i)));
        }
        Iterator<String> it = Query.keySet().iterator();
        while(it.hasNext()){
            String key = it.next(); //keyword
            int tf = Query.get(key); //TF
            //System.out.printf("key:%s,tf:%d\n",key,tf);
            int ndoc=0;
            ndoc = weightHash.get(key).size();
            ArrayList<Double> docweight = weightHash.get(key);
            for(int i=0;i<ndoc;i++){ //accumulates similarity value based on keyword.
                double sum = queryDocSim.get(doctitle.get(i));
                System.out.printf("pasta in doc %d=%f\n",i,docweight.get(i));
                sum+=tf*docweight.get(i); //calculates similaryty. querydoc[id] = tf*w. accumulates calculated value(multiple words)
                queryDocSim.put(doctitle.get(i),sum);
            }
        }
        return queryDocSim;
    }
    public ArrayList<String> getDocumentTitle(String path) throws ParserConfigurationException, IOException, SAXException { //get document title from collection.xml file
        File XmlFile = new File(path);
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
        Document doc = dBuilder.parse(XmlFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("doc"); // read documents form xml file
        ArrayList<String> docTitle = new ArrayList<>(nList.getLength());
        for(int i=0;i<nList.getLength();i++){
            docTitle.add(getTitle(nList,i)); //get document title from collection.xml file
        }
        return docTitle;
    }
    public String getTitle(NodeList nList,int index){
        return nList.item(index).getChildNodes().item(0).getTextContent();
    }
    public void simRanking(HashMap _queryDocSim){
        HashMap<String,Double> queryDocSim = _queryDocSim;
        List<String> keySet = new ArrayList<>(queryDocSim.keySet());
        keySet.sort((o1, o2) -> (int) (queryDocSim.get(o2) - queryDocSim.get(o1)));
        int counter=0;
        for (String key : keySet) {
            if(counter==3) break;
            System.out.println(String.format("Key : %s, Value : %s", key, queryDocSim.get(key)));
            counter++;
        }
    }
}