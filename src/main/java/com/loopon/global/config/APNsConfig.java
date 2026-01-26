import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class ApnsConfig {

    @Value("${apns.team-id}") private String teamId;
    @Value("${apns.key-id}") private String keyId;
    @Value("${apns.p8-path}") private String p8Path;

    @Bean(destroyMethod = "close")
    public ApnsClient apnsClient() throws Exception {
        // classpath에서 p8 읽기 (keys/AuthKey_xxx.p8)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(p8Path)) {
            if (is == null) throw new IllegalArgumentException("APNs p8 not found: " + p8Path);

            ApnsSigningKey signingKey = ApnsSigningKey.loadFromInputStream(is, teamId, keyId);

            return new ApnsClientBuilder()
                    .setSigningKey(signingKey)
                    .build();
        }
    }
}