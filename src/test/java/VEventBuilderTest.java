import com.solostudios.omnivoxscraper.impl.utils.ical.ICalUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Slf4j
public class VEventBuilderTest {
    
    @Test
    void someTest() {
        logger.info(ICalUtil.getEventBuilder(LocalDateTime.now(), Duration.of(2, ChronoUnit.HOURS)).build().toString());
        logger.info(ICalUtil.getEventBuilder(LocalDateTime.now()).build().toString());
    }
    
}
