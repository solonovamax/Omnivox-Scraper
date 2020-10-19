package com.solostudios.omnivoxscraper.impl.utils;

import com.gargoylesoftware.htmlunit.html.*;
import com.solostudios.omnivoxscraper.impl.OmniScraperImpl;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class OmniPageIndex {
    private final OmniScraperImpl  omniScraperImpl;
    private final List<String>     indexByFrenchName;
    private final List<String>     indexByEnglishName;
    private       Map<String, URL> services;
    
    public OmniPageIndex(OmniScraperImpl omniScraperImpl) {
        this.omniScraperImpl = omniScraperImpl;
        services = new HashMap<>();
        indexByFrenchName = new ArrayList<>();
        indexByEnglishName = new ArrayList<>();
    }
    
    public void init(HtmlPage page) {
        logger.debug("Indexing services");
        //yucky hardcoded shit.
        services = page.getByXPath("//ul/li[not(@data-categorie) and ../li/@data-categorie='OMNIVOX' and (not(a/@class) or " +
                                   "a/@class!=' produit-skytech') and a/@href and a/@href != 'javascript:;']")
                       .stream().map(e -> (DomNode) e)
                       .map(e -> e.getFirstByXPath("./a/span/text()")).map(e -> (DomText) e)
                       .collect(HashMap::new, (Map<String, URL> map, DomText e) -> map.put(e.toString(), omniScraperImpl.getOmniURL(
                               ((DomAttr) e.getFirstByXPath("../../@href")).getNodeValue())), Map::putAll);
    }
    
    private Map<String, URL> buildServices(List<HtmlElement> serviceNodes) {
        return serviceNodes.stream()
                           .map(e -> e.getFirstByXPath("./a/span/text()"))
                           .map(e -> (DomText) e)
                           .collect(HashMap::new, (Map<String, URL> map, DomText e) -> map.put(e.toString(), omniScraperImpl.getOmniURL(
                                   ((DomAttr) e.getFirstByXPath("../../@href")).getNodeValue())), Map::putAll);
    }
    
    public URL getService(String serviceName) {
        return services.get(serviceName);
    }
    
    public Map<String, URL> getServices() {
        return Map.copyOf(services);
    }
}
