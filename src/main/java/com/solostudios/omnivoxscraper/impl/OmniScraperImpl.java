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
import com.solostudios.omnivoxscraper.impl.utils.OmniPageIndex;
import com.solostudios.omnivoxscraper.impl.utils.config.AuthConfig;
import com.solostudios.omnivoxscraper.impl.utils.config.DomainConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class OmniScraperImpl implements OmniScraper {
    private static final BrowserVersion DEFAULT_CHROME_VERSION = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 " +
                          "Safari/537.36")
            .build();
    private final        WebClient      client;
    private final        AuthConfig     authConfig;
    private final        DomainConfig   domainConfig;
    //        private final Logger        logger = LoggerFactory.getLogger(OmniScraperImpl.class);
    @Getter
    private final        OmniPageIndex  pageIndex;
    @Getter
    private              HtmlPage       homePage;
    
    public OmniScraperImpl(@NotNull AuthConfig authConfig) {
        this(authConfig, DomainConfig.getDefault(), DEFAULT_CHROME_VERSION);
    }
//    private              Map<String, URL> indexedServices;
    
    public OmniScraperImpl(@NotNull AuthConfig authConfig, @NotNull DomainConfig domainConfig, @NotNull BrowserVersion version) {
        this.authConfig = authConfig;
        this.domainConfig = domainConfig;
        pageIndex = new OmniPageIndex(this);
        
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

//        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.SEVERE);
//        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.SEVERE);
//        LogFactory.getFactory().setAttribute("com.gargoylesoftware.htmlunit", "org.slf4j.Logger");
//        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }
    
    @Override
    public String getSubdomain() {
        return domainConfig.getSubDomain();
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
        
        if (authConfig.getPassword() == null || authConfig.getPassword().isBlank()) {
            logger.warn("Password was invalid!");
            throw new LoginException("You may not have a null or empty password!");
        }
        if (authConfig.getUsername() == null || authConfig.getUsername().isBlank()) {
            logger.warn("Username was invalid!");
            throw new LoginException("You may not have a null or empty username!");
        }
        
        doLogin();
        
        try {
            this.homePage = client.getPage(getOmniURL("/intr"));
        } catch (IOException e) {
            logger.error("There was an unknown error while trying to login.", e);
        }
        pageIndex.init(homePage);
    }
    
    public void doLogin() {
        URL      url      = getOmniURL("/Login/Account/Login?L=ANG");
        HtmlPage response = null;
        try {
            response = client.getPage(url);
        } catch (IOException e) {
            logger.error("There was an unknown error while attempting ot fetch the login page. Maybe the password was incorrect?", e);
            System.exit(1);
        }
        HtmlForm form = response.getFormByName("formLogin");
        
        String k = form.getInputByName("k").getValueAttribute();
        WebRequest loginPostRequest = new WebRequest(
                getOmniURL("/intr/Module/Identification/Login/Login.aspx"), HttpMethod.POST);
        List<NameValuePair> requestParams = new ArrayList<>();
        requestParams.add(new NameValuePair("NoDA", authConfig.getUsername()));
        requestParams.add(new NameValuePair("PasswordEtu", authConfig.getPassword()));
        requestParams.add(new NameValuePair("TypeIdentification", "Etudiant"));
        requestParams.add(new NameValuePair("TypeLogin", "PostSolutionLogin"));
        requestParams.add(new NameValuePair("k", k));
        loginPostRequest.setRequestParameters(requestParams);
        
        try {
            client.getPage(loginPostRequest);
        } catch (IOException e) {
            logger.error("There was an unknown error while to send the login post.", e);
            System.exit(1);
        }
        
        logger.info("Logged in successfully");
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
}
