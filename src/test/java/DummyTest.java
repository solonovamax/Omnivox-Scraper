import com.solostudios.omnivoxscraper.impl.OmniScraperImpl;
import com.solostudios.omnivoxscraper.impl.config.AuthConfig;
import org.junit.Test;

import javax.security.auth.login.LoginException;


public class DummyTest {
    @Test
    public void testScraper() throws LoginException {
        String     username = System.getProperty("OMNIVOX_USERNAME");
        String     password = System.getProperty("OMNIVOX_PASSWORD");
        AuthConfig config   = new AuthConfig(username, password);
        
        OmniScraperImpl scraper = new OmniScraperImpl(config);
        scraper.login();

//        System.out.println(scraper.getHomePage().asXml());
    }
}
