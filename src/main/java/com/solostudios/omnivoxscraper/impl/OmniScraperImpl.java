package com.solostudios.omnivoxscraper.impl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.solostudios.omnivoxscraper.api.OmniScraper;
import com.solostudios.omnivoxscraper.api.calendar.OmniCalendar;
import com.solostudios.omnivoxscraper.api.calendar.documents.DocumentManager;
import com.solostudios.omnivoxscraper.impl.config.AuthConfig;
import com.solostudios.omnivoxscraper.impl.config.DomainConfig;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;


public class OmniScraperImpl implements OmniScraper {
    private final static BrowserVersion   DEFAULT_CHROME_VERSION = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME)
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
            .build();
    private final        WebClient        client;
    private final        AuthConfig       authConfig;
    private final        DomainConfig     domainConfig;
    private              HtmlPage         homePage;
    private              Map<String, URL> indexedServices;
    
    public OmniScraperImpl(@NotNull AuthConfig authConfig) {
        this(authConfig, DomainConfig.getDefault(), DEFAULT_CHROME_VERSION);
    }
    
    public OmniScraperImpl(@NotNull AuthConfig authConfig, @NotNull DomainConfig domainConfig, @NotNull BrowserVersion version) {
        this.authConfig = authConfig;
        this.domainConfig = domainConfig;
        
        client = new WebClient(version);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setDownloadImages(false);
        client.getOptions().setPopupBlockerEnabled(true);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setTimeout(5000);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setSSLClientProtocols(new String[]{"TLSv1.2"});
        
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.SEVERE);
        
    }
    
    public Map<String, URL> getIndexedServices() {
        return indexedServices == null ? Collections.emptyMap() : Collections.unmodifiableMap(indexedServices);
    }
    
    public HtmlPage getHomePage() {
        return homePage;
    }
    
    @Override
    public String getSubdomain() {
        List<Integer> list = Arrays.asList(1, 1, 2, 3, 3, 3, 4);
        return null;
    }
    
    @Override
    public WebClient getWebClient() {
        return null;
    }
    
    @Override
    public OmniCalendar getOmniCalendar() {
        return null;
    }
    
    @Override
    public DocumentManager getOmniDocument() {
        return null;
    }
    
    @Override
    public void shutdown() {
    }
    
    public void login() throws LoginException {
        if (authConfig.getPassword() == null || authConfig.getPassword().isBlank())
            throw new LoginException("You may not have a null or empty password!");
        if (authConfig.getUsername() == null || authConfig.getUsername().isBlank())
            throw new LoginException("You may not have a null or empty username!");
        
        verifyLogin();
        
        indexHomePage();
    }
    
    public void verifyLogin() {
        try {
//            System.out.printf("Url: %s", domainConfig.getMainUrl().toString());
            URL      url      = new URL(domainConfig.getMainUrl(), "/Login/Account/Login");
            HtmlPage response = client.getPage(url);
            HtmlForm form     = response.getFormByName("formLogin");
            
            String k = form.getInputByName("k").getValueAttribute();
            WebRequest loginPostRequest = new WebRequest(
                    new URL(domainConfig.getMainUrl(), "/intr/Module/Identification/Login/Login.aspx"), HttpMethod.POST);
            List<NameValuePair> requestParams = new ArrayList<>();
            requestParams.add(new NameValuePair("NoDA", authConfig.getUsername()));
            requestParams.add(new NameValuePair("PasswordEtu", authConfig.getPassword()));
            requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
            requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
            requestParams.add(new NameValuePair("k", k));
            loginPostRequest.setRequestParameters(requestParams);
            
            client.getPage(loginPostRequest);
        } catch (IOException e) {
            throw new IllegalStateException("URL was invalid when it shouldn't have been.", e);
        }
    }
    
    public void indexHomePage() {
        try {
            this.homePage = client.getPage(new URL(domainConfig.getMainUrl(), "/intr"));
        } catch (IOException e) {
            throw new IllegalStateException("URL was invalid when it shouldn't have been.", e);
        }
        homePage.getByXPath(
                "//ul/li[not(@data-categorie) and " +
                "../li/@data-categorie='OMNIVOX' and " +
                "(not(a/@class) or a/@class!=' produit-skytech') " +
                "and a/@href and" +
                " a/@href != 'javascript:;']")
                .stream()
                .map(e -> (DomNode) e)
                .map(e -> e.getFirstByXPath("./a/span/text()"))
                .map(e -> (DomText) e)
                .collect(HashMap::new, (Map<String, URL> map, DomText e) -> {
                    String urlString = ((DomAttr) e.getFirstByXPath("../../@href")).getNodeValue();
                    String key       = e.toString();
                    try {
                        map.put(key, new URL(domainConfig.getMainUrl(), urlString));
                    } catch (MalformedURLException malformedURLException) {
                        System.out.printf("Malformed url. Skipping url %s, with key %s.\n", urlString, key);
                    }
                }, Map::putAll)
                .forEach((a, b) -> System.out.printf("key: %s, value: %s\n", a, b));
    }
}
