package com.solostudios.omnivoxscraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.solostudios.omnivoxscraper.calandar.OmnivoxSchedule;
import com.solostudios.omnivoxscraper.calandar.OmnivoxSemester;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OmniScraper {
    //private final WebClient client;
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
        
        this.webClient = new WebClient();
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
            //OmnivoxSchedule schedule = new OmnivoxSchedule(this, getSimpleConnection(
            //        homepage.select("#ctl00_partOffreServices_offreV2_HOR").attr("href")).method(Connection.Method.POST).execute());
            //Connection.Response response = getSimpleConnection("/estd/vl.ovx?lk=%2festd%2fhrre%2fhoraire" +
            //                                                   ".ovx&c=VAN&l=ANG&s1=601&s2=701&s3=390&s4=203").execute();
            //Connection.Response response = getSimpleConnection("/estd/vl.ovx?lk=%2festd%2fhrre%2fhoraire.ovx").execute();
            HtmlPage schedulePage = ((HtmlAnchor) homepage.getFirstByXPath(
                    "/html/body/form/table/tbody/tr[1]/td/table/tbody/tr/td[1]/div[2]/ul[2]/li[11]/a")).click();
            
            //Connection.Response response1 = getSimpleConnection(
            //        homepage.select("#ctl00_partOffreServices_offreV2_HOR").attr("href")).execute();
            
            //System.out.println(response1.body());
            
            HtmlElement body = schedulePage.getBody();
            //System.out.println(body.getAttributes());
            //String javascripOnloadCode = body.getAttribute("onload");
            //Pattern locationReplaceURLPattern = Pattern.compile("location\\.replace\\('LoadSession\\.ovx\\?veriflogin=sso&Ref=(.*)" +
            //"&C=VAN&L=ANG&lk=%2Festd%2Fhrre%2Fhoraire%2Eovx'\\);");
            //Matcher locationReplaceURLMatcher = locationReplaceURLPattern.matcher(javascripOnloadCode);
            //locationReplaceURLMatcher.matches();
            //System.out.println(locationReplaceURLMatcher.toMatchResult().group(1));
            //String ref = locationReplaceURLMatcher.group(1);
            
            //updateCookies(response.cookies());
            
            
            //assert cookies.containsKey("comn") && cookies.containsKey("DTKS") && cookies.containsKey("lngomn") &&
            //       cookies.containsKey("L") && cookies.containsKey("k") && cookies.containsKey("TKSVANP") &&
            //       cookies.containsKey("ASPSESSIONIDCUCSQABR") && cookies.containsKey("TypeIndividu") && cookies.containsKey(
            //        "UsersNbCouleurs") && cookies.containsKey("TypeProfil") && cookies.containsKey("IdControlDefault") &&
            //       cookies.containsKey("IsSessionInitialise") && cookies.containsKey("Resolution") && cookies.containsKey(
            //        "ParametresModule") && cookies.containsKey("TKINTR") && cookies.containsKey(".ASPXROLES");
            
            OmnivoxSchedule schedule = new OmnivoxSchedule(this, schedulePage);
            //OmnivoxSchedule schedule1 = new OmnivoxSchedule(this, getSimpleConnection("/estd/hrre/horaire.ovx").execute());
            //System.out.println(homepage.select("#ctl00_partOffreServices_offreV2_HOR").attr("href"));
            //System.out.println(schedule.getSemesters().stream().filter(OmnivoxSemester::isCurrent).findFirst().get().getPage().asXml());
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
            
            //updateCookies(response.cookies());
            
            //FormElement form = (FormElement) document.select("form#formLogin").first();
            HtmlForm form = response.getFormByName("formLogin");
            
            String k = form.getInputByName("k").getValueAttribute();
            //String StatsEnvUsersNbCouleurs = document.select("input[name=StatsEnvUsersNbCouleurs]").first().val();
            //String StatsEnvUsersResolution = document.select("input[name=StatsEnvUsersResolution]").first().val();
            WebRequest loginPostRequest = new WebRequest(
                    new URL("https://" + this.subdomain + ".omnivox.ca/intr/Module/Identification/Login/Login.aspx"), HttpMethod.POST);
            //loginPostRequest.setAdditionalHeaders(
            //        Map.of("NoDA", username, "PasswordEtu", password, "TypeIdentification", "Etudiant", "TypeLogin", "PostSolutionLogin",
            //               "k", k));
            List<NameValuePair> requestParams = new ArrayList<>();
            requestParams.add(new NameValuePair("NoDA", username));
            requestParams.add(new NameValuePair("PasswordEtu", password));
            requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
            requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
            requestParams.add(new NameValuePair("k", k));
            loginPostRequest.setRequestParameters(requestParams);
            
            //updateCookies(loginResponse.cookies());
            
            HtmlPage homePage = webClient.getPage(loginPostRequest);
            //System.out.println(doc.html() + "\n\n\n\n\n\n\n\n");
            //System.out.println(loginResponse.url());
            
            return homePage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
