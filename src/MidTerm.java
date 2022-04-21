import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MidTerm {
    public void showSnippet(String path,String Query){
        HashMap<String,String> documents = new HashMap<>(); //hash-map to save document title-snippet
        ArrayList<String> QueryKeywords = new ArrayList<>(); // arraylist to save query keywords
        QueryKeywords = ExtractQueryKeywords(Query); //extrac keywords from query
        HashMap<String,ArrayList<String>> documentSnippets = new HashMap<>();
        documentSnippets = makeSnippet(path,QueryKeywords);
        int max = countMatchRanking(documentSnippets,QueryKeywords);
        Iterator<String> it = documentSnippets.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            System.out.println(key);
            System.out.println(documentSnippets.get(key));
            System.out.println(countMatchRanking(documentSnippets,QueryKeywords));
        }
    }
    public int countMatchRanking(HashMap<String,ArrayList<String>> documentSnippets,ArrayList<String> queryKeywords){
        Iterator<String> it = documentSnippets.keySet().iterator();
        ArrayList<String> tempsnippet = new ArrayList();
        int max=0;
        int matchRank=0;
        while(it.hasNext()){
            String key = it.next();
            tempsnippet= documentSnippets.get(key);
            for(int i=0;i<tempsnippet.size();i++){
                for(int j=0;j<queryKeywords.size();j++){
                    if(Objects.equals(tempsnippet.get(i),queryKeywords.get(j))) {
                        matchRank++;
                    }
                }
                if(max<matchRank) max=matchRank;
                matchRank=0;
            }
        }
        return max;
    }
    public ArrayList<String> ExtractQueryKeywords(String str){ //creates query vector(hashmap)
        ArrayList<String> QueryKeywords = new ArrayList<>();
        KeywordExtractor k = new KeywordExtractor();
        String keyword; //temporary string holder;
        KeywordList kl = k.extractKeyword(str, true);
        for(int i=0;i<kl.size();i++){
            Keyword kwrd = kl.get(i);
            keyword=kwrd.getString();  //get keyword from query
            QueryKeywords.add(i,keyword); // put keyword into QueryKeyowrds list
        }
        return QueryKeywords;
    }
    public HashMap<String,ArrayList<String>> makeSnippet(String path,ArrayList<String> QueryKeywords) {
        HashMap<String,ArrayList<String>> documentSnippets = new HashMap<>(); //document title-snippets.
        int snippetCount=0; //counter for snippet word.
        int matchingScore=0;
        int maxMatchingSore = 0;
        ArrayList<String> returnsnippet = new ArrayList<>(); //stringlist to return
        ArrayList<String> tempsnippet = new ArrayList<>(); //stringlist for snippet
        try{
            File XmlFile = new File(path);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
            Document doc = dBuilder.parse(XmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("doc"); // read documents form xml file
            for(int temp=0;temp<nList.getLength();temp++) {
                String title = nList.item(temp).getChildNodes().item(0).getTextContent();
                String str = getText(nList, temp); //get body text into string
                StringTokenizer bodytokens = new StringTokenizer(str); //s
                returnsnippet = new ArrayList<>(); //initialize snippets.
                tempsnippet = new ArrayList<>();
                snippetCount = 0; // counter to count character size
                matchingScore = 0;
                while(bodytokens.hasMoreTokens()){
                    String word = bodytokens.nextToken(); //get next word
                    tempsnippet.add(word);
                    snippetCount=snippetCount+word.length()+1; //accumulate snippetCount and space
                    if(snippetCount>30) { // if word count is over maximum snippet length
                        if (maxMatchingSore < matchingScore) { // if matching score is maximum
                            maxMatchingSore = matchingScore; //swap max value
                            returnsnippet = new ArrayList<>(); //initialize returnsnippet
                            for (int i = 0; i < tempsnippet.size(); i++) {
                                returnsnippet.add(i, tempsnippet.get(i)); //copy tempsnippet to returnsnippet
                            }
                        }
                        tempsnippet = new ArrayList<>(); //initialize tempsnippet
                        snippetCount = 0; //initialize temporary values for next loop
                        matchingScore = 0; //initialzie temp matchingScore
                    }
                    else matchingScore+=findWordInQueryKeywords(word,QueryKeywords); //find word in query and accumulate matching score
                }
                documentSnippets.put(title,returnsnippet); // put into documentSnippets.
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return documentSnippets;
    }
    public int findWordInQueryKeywords(String word,ArrayList<String> QueryKeywords){
        int rank = 0;
        for(int i=0;i<QueryKeywords.size();i++){
            if(Objects.equals(word,QueryKeywords.get(i))) rank++;
        }
        return rank;
    }
    public  String getText(NodeList nList,int temp){ // get body text into string
        return nList.item(temp).getChildNodes().item(1).getTextContent();
    }
}
