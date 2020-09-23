package com.solostudios.omnivoxscraper.impl.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class DomainConfig {
    private String  subDomain;
    private String  leaSubDomain;
    private String  omnivoxSite;
    private boolean usesSubDomain;
    private URL     mainUrl;
    private URL     leaUrl;
    
    public DomainConfig() {
        this.subDomain = "vaniercollege";
        this.leaSubDomain = "vaniercollege-estd";
        this.omnivoxSite = "omnivox.ca";
        this.usesSubDomain = true;
        rebuildUrls();
    }
    
    public void rebuildUrls() {
        try {
            mainUrl = new URL("https://" + subDomain + "." + omnivoxSite);
            leaUrl = new URL("https://" + leaSubDomain + "." + omnivoxSite);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL was verified, but somehow was illegal.", e);
        }
    }
    
    public DomainConfig(String subDomain, String leaSubDomain, String omnivoxSite, boolean usesSubDomain) {
        this.subDomain = subDomain;
        this.leaSubDomain = leaSubDomain;
        this.omnivoxSite = omnivoxSite;
        this.usesSubDomain = usesSubDomain;
        rebuildUrls();
    }
    
    public static DomainConfig getDefault() {
        return new DomainConfig();
    }
    
    public URL getMainUrl() {
        return mainUrl;
    }
    
    public URL getLeaUrl() {
        return leaUrl;
    }
    
    public String getOmnivoxSite() {
        return omnivoxSite;
    }
    
    public void setOmnivoxSite(String omnivoxSite) throws MalformedURLException {
        if (!URLEncoder.encode(omnivoxSite, StandardCharsets.UTF_8).equals(omnivoxSite)) {
            throw new MalformedURLException("The site is not value!");
        }
        this.omnivoxSite = omnivoxSite;
        this.rebuildUrls();
    }
    
    public boolean usesSubDomain() {
        return usesSubDomain;
    }
    
    public String getSubDomain() {
        return subDomain;
    }
    
    public void setSubDomain(String subDomain) throws MalformedURLException {
        if (!URLEncoder.encode(omnivoxSite, StandardCharsets.UTF_8).equals(omnivoxSite)) {
            throw new MalformedURLException("The subdomain is not value!");
        }
        this.subDomain = subDomain;
        this.rebuildUrls();
    }
    
    public String getLeaSubDomain() {
        return leaSubDomain;
    }
    
    public void setLeaSubDomain(String leaSubDomain) throws MalformedURLException {
        if (!URLEncoder.encode(omnivoxSite, StandardCharsets.UTF_8).equals(omnivoxSite)) {
            throw new MalformedURLException("The subdomain is not value!");
        }
        this.leaSubDomain = leaSubDomain;
        this.rebuildUrls();
    }
    
    public void setUsesSubDomain(boolean usesSubDomain) {
        this.usesSubDomain = usesSubDomain;
    }
    
}
