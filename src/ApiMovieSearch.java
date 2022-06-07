import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ApiMovieSearch {
    public static void main(String[] args) throws ParseException {
        String clientId = "GKHvHbF_5cs7i_9xgCEv"; //애플리케이션 클라이언트 아이디값"
        String clientSecret = "s8idgbAE0w"; //애플리케이션 클라이언트 시크릿값"

        String query;
        String text=null;
        Scanner s = new Scanner(System.in);
        System.out.print("검색어를 입력하세요:");
        query = s.nextLine();
        try {
            text = URLEncoder.encode(query, "UTF-8"); //query,encoding
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }
        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text;    // json 결과
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String response = get(apiURL,requestHeaders);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
        JSONArray infoArray =(JSONArray) jsonObject.get("items");
        for(int i=0;i<infoArray.size();i++){
            System.out.println("=item_"+i+" ===================");
            JSONObject item = (JSONObject) infoArray.get(i);
            System.out.println("title:\t\t\t" +item.get("title"));
            System.out.println("subtitle:\t\t"+item.get("subtitle"));
            System.out.println("director:\t\t"+item.get("director"));
            System.out.println("actors:\t\t\t"+item.get("actor"));
            System.out.println("userRating:\t\t"+item.get("userRating")+'\n');
        }
    }
    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
