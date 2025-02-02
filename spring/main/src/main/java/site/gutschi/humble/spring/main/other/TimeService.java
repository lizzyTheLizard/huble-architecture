package site.gutschi.humble.spring.main.other;

import org.springframework.stereotype.Service;
import site.gutschi.humble.spring.common.api.TimeApi;

import java.time.Instant;

@Service
public class TimeService implements TimeApi {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
