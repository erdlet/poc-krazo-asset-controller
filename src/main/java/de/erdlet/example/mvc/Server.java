package de.erdlet.example.mvc;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

  private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

  private static final String CONTEXT_ROOT = "";
  private static final String DEFAULT_PORT = "8080";
  private static final String LIBS_PATH = "/WEB-INF/classes";
  private static final String TARGET_RESOURCES = "target/classes";

  public static void main(String[] args) throws Exception {
    LOGGER.info("Starting Tomcat initialization...");

    final String webappDirLocation = "src/main/webapp/";
    LOGGER.debug("Using webapp dir '{}'", webappDirLocation);

    final Tomcat tomcat = new Tomcat();

    String port = System.getenv("PORT");
    LOGGER.info("Received port '{}' from ENV...", port);
    if (port == null || port.isEmpty()) {
      LOGGER.info("Port was not set via ENV... using default '{}'", DEFAULT_PORT);
      port = DEFAULT_PORT;
    }

    tomcat.setPort(Integer.valueOf(port));
    tomcat.getConnector();

    final String webappDirPath = new File(webappDirLocation).getAbsolutePath();
    final StandardContext ctx = (StandardContext) tomcat.addWebapp(CONTEXT_ROOT, webappDirPath);
    LOGGER.info("Configuring app with basedir '{}'", webappDirPath);

    final File targetClasses = new File(TARGET_RESOURCES);
    final String targetClassesPath = targetClasses.getAbsolutePath();
    LOGGER.debug("Adding additional WEB-INF classes from '{}'", targetClassesPath);
    final WebResourceRoot resources = new StandardRoot(ctx);
    resources.addPreResources(new DirResourceSet(resources, LIBS_PATH, targetClassesPath, "/"));
    ctx.setResources(resources);

    tomcat.start();
    tomcat.getServer().await();
  }
}
