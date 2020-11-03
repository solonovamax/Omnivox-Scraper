package com.solostudios.omnivoxscraper.impl.utils.index;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.solostudios.omnivoxscraper.impl.OmniScraperImpl;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Index of Services on omnivox.
 *
 * @author solonovamax
 */
@Slf4j
@ToString
@EqualsAndHashCode
public class OmniPageIndex {
    static        Yaml                yaml = new Yaml();
    private final Map<String, URL>    serviceUrls;
    @ToString.Exclude
    private final OmniScraperImpl     scraper;
    @ToString.Exclude
    private final Map<String, String> frenchIndexes;
    @ToString.Exclude
    private final Map<String, String> englishIndexes;
    
    public OmniPageIndex(OmniScraperImpl scraper) {
        this.scraper = scraper;
        serviceUrls = new HashMap<>();
        englishIndexes = getYamlResource("services_en_US.yml");
        logger.debug("Loaded the index {}", englishIndexes);
        frenchIndexes = getYamlResource("services_fr_FR.yml");
        logger.debug("Loaded the index {}", frenchIndexes);
    }
    
    /**
     * Gets a YAML file from the specified name.
     *
     * @param name Name of the YAML file to get.
     *
     * @return a map representing the YAML file.
     */
    private static Map<String, String> getYamlResource(String name) {
        logger.debug("Loading YAML resource {}", name);
        InputStream inputStream = OmniPageIndex.class.getClassLoader().getResourceAsStream(name);
        return yaml.load(inputStream);
    }
    
    /**
     * Loads and builds the indexes from a page.
     * <p>
     * Must be called before {@link #getServiceUrl} or {@link #getServiceUrls()}
     *
     * @param page The page from which to build the indexes.
     */
    public void loadIndexes(HtmlPage page) {
        logger.debug("Indexing services");
        Map<String, URL> serviceMap = buildServiceMap(page);
        
        Optional<Map.Entry<String, URL>> randomEntry = serviceMap.entrySet().stream().findFirst();
        
        if (randomEntry.isEmpty())
            throw new IllegalStateException("The list of services was empty.");
        
        if (englishIndexes.containsValue(randomEntry.get().getKey())) {
            buildServicesIndex(serviceMap, englishIndexes);
        } else if (frenchIndexes.containsValue(randomEntry.get().getKey())) {
            buildServicesIndex(serviceMap, frenchIndexes);
        } else {
            throw new IllegalStateException("Could not find proper index for random key " + randomEntry.get().getKey());
        }
    }
    
    /**
     * Builds a map of the services.
     *
     * @param page The page with from which to build the map.
     *
     * @return The map of the services to urls for that service.
     */
    private Map<String, URL> buildServiceMap(HtmlPage page) {
        //noinspection CodeBlock2Expr
        return page.getByXPath("//ul/li[not(@data-categorie) and ../li/@data-categorie='OMNIVOX' and " + //yucky hardcoded shit.
                               "(not(a/@class) or a/@class!=' produit-skytech') and a/@href and a/@href != " +
                               "'javascript:;']")
                   .parallelStream()
                   .map(e -> (DomNode) e)
                   .map(e -> (DomText) e.getFirstByXPath("./a/span/text()"))
                   .collect(HashMap::new, (Map<String, URL> map, DomText e) -> {
                                map.put(e.toString().strip(),
                                        scraper.getOmniURL(((DomAttr) e.getFirstByXPath("../../@href")).getNodeValue()));
                            },
                            Map::putAll);
    }
    
    /**
     * Builds an internationalized map from the services map and correct language map.
     *
     * @param services    The service map with which to build the i18ned map.
     * @param i18nIndexes The language map with which to build the i18ned map.
     */
    private void buildServicesIndex(Map<String, URL> services, Map<String, String> i18nIndexes) {
        for (Map.Entry<String, String> i18nEntry : i18nIndexes.entrySet()) {
            if (!services.containsKey(i18nEntry.getValue()))
                logger.warn("Could not find service for entry {{}}. Marking it as null.", i18nEntry);
            serviceUrls.put(i18nEntry.getKey(), services.remove(i18nEntry.getValue()));
        }
        logger.debug("Here is the i18n map: {}", serviceUrls);
    }
    
    @Nullable
    public URL getServiceUrl(OmnivoxService omnivoxService) {
        return getServiceUrl(omnivoxService.getName());
    }
    
    @Nullable
    public URL getServiceUrl(String serviceName) {
        return serviceUrls.get(serviceName);
    }
    
    public Map<String, @Nullable URL> getServiceUrls() {
        return new HashMap<>(serviceUrls);
    }
}