package com.solostudios.omnivoxscraper.impl;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.solostudios.omnivoxscraper.api.OmniScraper;
import com.solostudios.omnivoxscraper.api.calendar.OmniCalendar;
import com.solostudios.omnivoxscraper.api.calendar.documents.DocumentManager;
import com.solostudios.omnivoxscraper.impl.calendar.OmniCalendarImpl;
import com.solostudios.omnivoxscraper.impl.utils.config.AuthConfig;
import com.solostudios.omnivoxscraper.impl.utils.config.DomainConfig;
import com.solostudios.omnivoxscraper.impl.utils.index.OmniPageIndex;
import com.solostudios.omnivoxscraper.impl.utils.index.OmnivoxService;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class OmniScraperImpl implements OmniScraper {
    private static final BrowserVersion   DEFAULT_CHROME_VERSION = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 " +
                          "Safari/537.36")
            .build();
    private final        WebClient        client;
    private final        AuthConfig       authConfig;
    private final        DomainConfig     domainConfig;
    @Getter
    private final        OmniPageIndex    pageIndex;
    @Getter
    private final        OmniCalendarImpl calendar;
    private final        Yaml             yaml;
    @Getter
    private              HtmlPage         homePage;
    
    public OmniScraperImpl(@NotNull AuthConfig authConfig) {
        this(authConfig, DomainConfig.getDefault(), DEFAULT_CHROME_VERSION);
    }
    
    public OmniScraperImpl(@NotNull AuthConfig authConfig, @NotNull DomainConfig domainConfig, @NotNull BrowserVersion version) {
        this.authConfig = authConfig;
        this.domainConfig = domainConfig;
        this.yaml = new Yaml();
        
        logger.debug("Constructing Web Client");
        client = new WebClient(version);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setDownloadImages(false);
        client.getOptions().setPopupBlockerEnabled(true);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setTimeout(5000);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setSSLClientProtocols(new String[]{ "TLSv1.2" });
        client.setIncorrectnessListener((a, b) -> {});
        
        logger.debug("Initializing Index");
        pageIndex = new OmniPageIndex(this);
        logger.debug("Initializing Calendar");
        calendar = new OmniCalendarImpl(this);
    }
    
    @Override
    public String getSubdomain() {
        return domainConfig.getSubDomain();
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
        //empty because lazy
    }
    
    public WebClient getWebClient() {
        return client;
    }
    
    @SneakyThrows(IOException.class)
    public void login() throws LoginException {
        logger.info("Begining Login process");
        
        if (authConfig.getPassword() == null || authConfig.getPassword().isBlank()) {
            logger.warn("Password was invalid!");
            throw new LoginException("You may not have a null or empty password!");
        }
        if (authConfig.getUsername() == null || authConfig.getUsername().isBlank()) {
            logger.warn("Username was invalid!");
            throw new LoginException("You may not have a null or empty username!");
        }
        
        doLogin();
        
        initialScraping();
    }
    
    @SneakyThrows(IOException.class)
    public void doLogin() {
        URL      url      = getOmniURL("/Login/Account/Login?L=ANG");
        HtmlPage response = null;
        logger.debug("Getting login page");
        response = client.getPage(url);
        HtmlForm form = response.getFormByName("formLogin");
        
        logger.debug("Filling login form");
        String              k                = form.getInputByName("k").getValueAttribute();
        WebRequest          loginPostRequest = new WebRequest(getOmniURL("/intr/Module/Identification/Login/Login.aspx"), HttpMethod.POST);
        List<NameValuePair> requestParams    = new ArrayList<>();
        requestParams.add(new NameValuePair("NoDA", authConfig.getUsername()));
        requestParams.add(new NameValuePair("PasswordEtu", authConfig.getPassword()));
        requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
        requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
        requestParams.add(new NameValuePair("k", k));
        loginPostRequest.setRequestParameters(requestParams);
        
        logger.debug("Completing login POST request");
        client.getPage(loginPostRequest);
        
        logger.info("Logged in successfully");
    }
    
    private void initialScraping() throws IOException {
        logger.info("Beggining inital scraping");
        this.homePage = getPage(getOmniURL("/intr"));
        logger.info("Indexing services");
        pageIndex.loadIndexes(homePage);
        
        logger.info("Parsing Calendar");
        calendar.loadPage(getPage(getServiceUrl(OmnivoxService.SCHEDULE)));
    }
    
    public URL getOmniURL(String relativeURL) {
        try {
            return new URL(domainConfig.getMainUrl(), relativeURL);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("There is literally no reason for there to be a malformed url within the api. " +
                                            "(This should never be called outside of the api.)" +
                                            "Why is this happening. " +
                                            "If you're currently developing for the api then I'm writing this exception message to let " +
                                            "you know that you fucked up. " +
                                            "Write better code next time.", e);
        }
    }
    
    @SneakyThrows
    public HtmlPage getPage(URL url) {
        return client.getPage(url);
    }

//    public void closePage(HtmlPage page) {
//        client
//    }
    
    public URL getServiceUrl(OmnivoxService omnivoxService) {
        return pageIndex.getServiceUrl(omnivoxService);
    }
    
    public URL getServiceUrl(String omnivoxService) {
        return pageIndex.getServiceUrl(omnivoxService);
    }
}
