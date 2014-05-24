import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopTwitRatingFetcher {

    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final String baseUrl = "http://toptwit.ru/users/?page=";
    private static final Pattern regex = Pattern.compile("<a href=\"/users/(\\w+)\" class=\"rating\">");

    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 1; i <= 300; i++) {
            HttpGet httpGet = new HttpGet(baseUrl + i);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            String content = EntityUtils.toString(response.getEntity());
            Matcher matcher = regex.matcher(content);
            while (matcher.find())
                System.out.println(matcher.group(1));

            Thread.sleep(1000);
        }
    }

}
