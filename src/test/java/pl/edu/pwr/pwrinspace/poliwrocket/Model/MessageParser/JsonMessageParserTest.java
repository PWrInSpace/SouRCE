package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class JsonMessageParserTest {

    @Mock
    private ISensorRepository mockSensorRepository;

    private JsonMessageParser jsonMessageParserUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        jsonMessageParserUnderTest = new JsonMessageParser(mockSensorRepository);
    }

    @Test
    void testParseMessage() {
        // Setup
        final Frame frame = new Frame("content", LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        when(mockSensorRepository.getSensorByName("name")).thenReturn(new Sensor());

        // Run the test
        jsonMessageParserUnderTest.parseMessage(frame);

        // Verify the results
    }
}
