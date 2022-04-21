import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class indexer {
    public void indexer(String path) throws IOException {
        HashMap<String, ArrayList<Double>> weighthash = new HashMap<>(); // hash map for weight
        HashMap<String, ArrayList<Integer>> tf = new HashMap<>(); //hash map for term frequency / document
        HashMap<String, Integer> df = new HashMap<>();
        try{
            File XmlFile = new File(path);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
            Document doc = dBuilder.parse(XmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("doc"); // read documents form index.xml file
            for(int temp=0;temp<nList.getLength();temp++) { // set tf,df values
                int id = temp; // document id
                String str = getText(nList, temp); //get body text into string
                StringTokenizer keywordParse = new StringTokenizer(str,"#");
                while(keywordParse.hasMoreTokens()) { // parse with # in index.xml's body
                    String keywords = keywordParse.nextToken();
                    StringTokenizer idfParse = new StringTokenizer(keywords, ":"); // parse with : in body
                    while (idfParse.hasMoreTokens()) { //parse tokens (keyword:term_frequency)
                        String keyword = idfParse.nextToken(); // get keyword
                        int frequency = Integer.parseInt(idfParse.nextToken()); //get keyword's term frequency
                        if(tf.containsKey(keyword)){ // if keyword is already in tf hash map
                            ArrayList<Integer> templist = new ArrayList<>(5);
                            templist = tf.get(keyword); //pull templist
                            templist.set(id,frequency); //set arraylist
                            tf.put(keyword,templist); //push arraylist
                        }
                        else{ //first seen --> push arraylist
                            ArrayList<Integer> tfs= new ArrayList<>(5); //initialize array list for tf
                            for(int i=0;i<5;i++) tfs.add(0);
                            tfs.set(id,frequency);
                            tf.put(keyword,tfs);
                        }
                        if (df.containsKey(keyword)) { // if term is already in df hash map
                            df.put(keyword, df.get(keyword) + 1); // accumulate frequency value
                        } else {
                            df.put(keyword,1); // put term in df hash map
                        }
                    }
                }
            }
            for(String keyword:tf.keySet()){ //calculate weightHash value
                ArrayList<Integer> templist = new ArrayList<>(5);
                ArrayList<Double> tempweight = new ArrayList<>(5); //list to return weight
                int N = nList.getLength(); // total number of document
                int term_frequency = 0;
                double w = 0;
                int idf = df.get(keyword); //get idf
                for(int i=0;i<N;i++){
                    templist = tf.get(keyword);
                    term_frequency=templist.get(i);
                    w = term_frequency * Math.log(((double)N)/(double)idf); // calculate weight for keyword
                    tempweight.add(w);
                }
                weighthash.put(keyword,tempweight); //push weight list to keyword
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        FileOutputStream filestream = new FileOutputStream("./output/index.post");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(filestream);
        objectOutputStream.writeObject(weighthash);
    }
    public  String getText(NodeList nList,int temp) { // get body text into string
        return nList.item(temp).getChildNodes().item(1).getTextContent();
    }

}
