package sean.plugin;

import hudson.Plugin;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * WebRequestPluing extends the simplest form a Jenkins plugin
 * and acts as a servlet within the Jenkins container allowing
 * Jenkins to receive request and respond.
 *
 * @author sean
 *
 */
public class WebRequestPlugin extends Plugin {

    private static final Logger logger = Logger.getLogger(WebRequestPlugin.class.getName());

    @Override
    public void start() throws Exception {
        logger.info("WebRequestPlugin Plugin Started");
    }

    /**
     * This method is called whenever a request is received
     * by the pluign at it's url
     */
    @Override
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        try {
            PluginRequestProcessor processor = new PluginRequestProcessor(req, rsp);
            processor.processAnnotation();
            rsp.getWriter().flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            rsp.sendError(500, convertStackTraceToString(ex));
        }
    }

    private String convertStackTraceToString(Exception exception) {
        StringBuffer buffer = new StringBuffer();
        for(StackTraceElement e: exception.getStackTrace()) {
            buffer.append(e.toString() + "<br/>\n");
        }
        return buffer.toString();
    }

}
