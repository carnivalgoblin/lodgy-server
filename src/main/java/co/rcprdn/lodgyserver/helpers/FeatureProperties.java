package co.rcprdn.lodgyserver.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FeatureProperties {

  @Autowired
  private Environment env;

  private boolean isProdProfile() {
    String[] activeProfiles = env.getActiveProfiles();
    for (String profile : activeProfiles) {
      if ("prod".equals(profile)) {
        return true;
      }
    }
    return false;
  }
}
