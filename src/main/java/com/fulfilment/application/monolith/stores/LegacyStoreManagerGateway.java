package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOG = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void createStoreOnLegacySystem(Store store) {
    LOG.infof("Creating store on legacy system: %s", store.name);
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    LOG.infof("Updating store on legacy system: %s", store.name);
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    try {
      Path tempFile = Files.createTempFile(store.name, ".txt");
      LOG.debugf("Temporary file created at: %s", tempFile);

      String content =
          "Store created. [ name ="
              + store.name
              + " ] [ items on stock ="
              + store.quantityProductsInStock
              + "]";
      Files.write(tempFile, content.getBytes());
      LOG.debugf("Data written to temporary file for store: %s", store.name);

      String readContent = new String(Files.readAllBytes(tempFile));
      LOG.debugf("Data read from temporary file: %s", readContent);

      Files.delete(tempFile);
      LOG.debug("Temporary file deleted");

    } catch (Exception e) {
      LOG.errorf(e, "Failed to write store data to legacy system for store: %s", store.name);
    }
  }
}
