package com.solostudios.omnivoxscraper.old;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.solostudios.omnivoxscraper.old.calandar.OmnivoxSchedule;
import com.solostudios.omnivoxscraper.old.calandar.OmnivoxSemester;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OmniScraper {
    private final String          username;
    private final String          password;
    private final String          subdomain;
    private final WebClient       webClient;
    private       OmnivoxSemester semesterData;
    
    public OmniScraper(String username, String password, String url) {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        
        BrowserVersion defaultChromeVersion = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME)
                .setUserAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537" +
                        ".36")
                .build();
    
        this.webClient = new WebClient(defaultChromeVersion);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setPopupBlockerEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setTimeout(5000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        
        
        this.username = username;
        this.password = password;
        
        Pattern urlPattern = Pattern.compile("(?:(?:https|http)://)?(.+)\\.omnivox\\.ca.*");
        Matcher urlMatcher = urlPattern.matcher(url);
        if (!urlMatcher.matches())
            throw new IllegalArgumentException("The url must fit the following pattern: (?:(?:https|http)://)?(.+)\\.omnivox\\.ca.*");
        this.subdomain = urlMatcher.group(1);
        
        HtmlPage homepage = initConnection();
        
        try {
            //TODO: Add compatibility for other portals. Dawson's portal uses the name "Course Schedule" instead of "My Schedule" and is
            // the 5th item in the list, whereas it's the 11th item in the list for Vanier.
            // here is a screenshot of dawson's "My Omnivox Services": https://imgur.com/kB9qNiK
            //
            HtmlPage schedulePage = ((HtmlAnchor) homepage.getFirstByXPath(
                    "/html/body/form/table/tbody/tr[1]/td/table/tbody/tr/td[1]/div[2]/ul[2]/li[11]/a")).click();
    
            OmnivoxSchedule schedule = new OmnivoxSchedule(this, schedulePage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String... args) {
        switch (args.length) {
            default:
                System.out.println(
                        "You must provide either 2 or 3 arguments. The first is your username, the second is your password, and the " +
                        "(optional) third is the URL you want to use. (defaults to vaniercollege.omnivox.ca)\n" +
                        "This will be made into an API later with a builder, but for now this is just for testing.");
                break;
            case 2:
                new OmniScraper(args[0], args[1], "https://vaniercollege.omnivox.ca/intr/");
                break;
            case 3:
                new OmniScraper(args[0], args[1], args[2]);
                break;
        }
    }
    
    public String getSubdomain() {
        return subdomain;
    }
    
    public WebClient getWebClient() {
        return webClient;
    }
    
    private HtmlPage initConnection() {
        try {
            HtmlPage response = webClient.getPage("https://" + this.subdomain + ".omnivox.ca/Login/Account/Login?ReturnUrl=%2fintr");
            HtmlForm form = response.getFormByName("formLogin");
            
            String k = form.getInputByName("k").getValueAttribute();
            WebRequest loginPostRequest = new WebRequest(
                    new URL("https://" + this.subdomain + ".omnivox.ca/intr/Module/Identification/Login/Login.aspx"), HttpMethod.POST);
            List<NameValuePair> requestParams = new ArrayList<>();
            requestParams.add(new NameValuePair("NoDA", username));
            requestParams.add(new NameValuePair("PasswordEtu", password));
            requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
            requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
            requestParams.add(new NameValuePair("k", k));
            loginPostRequest.setRequestParameters(requestParams);
            
            
            HtmlPage homePage = webClient.getPage(loginPostRequest);
            
            return homePage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
