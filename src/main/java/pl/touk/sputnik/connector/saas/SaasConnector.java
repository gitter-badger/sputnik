package pl.touk.sputnik.connector.saas;

import lombok.AllArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.Connector;
import pl.touk.sputnik.connector.github.GithubPatchset;
import pl.touk.sputnik.connector.http.HttpConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@AllArgsConstructor
public class SaasConnector implements Connector {

    private HttpConnector httpConnector;
    private GithubPatchset githubPatchset;

    private static final String FILES_URL_FORMAT = "/api/github/%s/pulls/%d/files";
    private static final String VIOLATIONS_URL_FORMAT = "/api/github/%s/pulls/%d/violations";

    public List<String> getReviewFiles() {
        return null;
    }

    @NotNull
    @Override
    public String listFiles() throws URISyntaxException, IOException {
        URI uri = httpConnector.buildUri(createUrl(githubPatchset, FILES_URL_FORMAT));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(new HttpGet(uri));
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    @NotNull
    @Override
    public String sendReview(String violationsAsJson) throws URISyntaxException, IOException {
        System.out.println(violationsAsJson);
        URI uri = httpConnector.buildUri(createUrl(githubPatchset, VIOLATIONS_URL_FORMAT));
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(violationsAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = httpConnector.logAndExecute(httpPost);
        return httpConnector.consumeAndLogEntity(httpResponse);
    }

    private String createUrl(GithubPatchset patchset, String formatUrl) {
        return String.format(formatUrl, patchset.getProjectPath(), patchset.getPullRequestId());
    }
}
