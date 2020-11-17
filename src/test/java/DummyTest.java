import com.solostudios.omnivoxscraper.impl.OmniScraperImpl;
import com.solostudios.omnivoxscraper.impl.utils.config.AuthConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;


class DummyTest {
    @Test
    void testScraper() throws LoginException {
//        Assertions.fail();
        
        System.out.println("Hello");
        
        Logger logger = LoggerFactory.getLogger(DummyTest.class);
        
        String     username = System.getProperty("OMNIVOX_USERNAME");
        String     password = System.getProperty("OMNIVOX_PASSWORD");
        AuthConfig config   = new AuthConfig(username, password);
        
        OmniScraperImpl scraper = new OmniScraperImpl(config);
        scraper.login();
    
    
    }
}
