package com.solostudios.omnivoxscraper.impl.utils.config;

import lombok.*;
import lombok.experimental.Accessors;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Accessors(chain = true)
public class DomainConfig {
    
    private String  subDomain;
    private String  leaSubDomain;
    private String  omnivoxSite;
    private boolean usesSubDomain;
    /**
     * The URL for the main Omnivox Service.
     * <p>
     * Only modify this if you know what you're doing.
     *
     * @param mainUrl The new url for the main Omnivox service.
     */
    private URL     mainUrl;
    /**
     * The URL for the LEA service.
     * <p>
     * Only modify this if you know what you're doing.
     *
     * @param leadUrl The new LEA url to set.
     */
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
    
    public void setSubDomain(String subDomain) throws MalformedURLException {
        if (!URLEncoder.encode(omnivoxSite, StandardCharsets.UTF_8).equals(omnivoxSite)) {
            throw new MalformedURLException("The subdomain is not value!");
        }
        this.subDomain = subDomain;
        this.rebuildUrls();
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
