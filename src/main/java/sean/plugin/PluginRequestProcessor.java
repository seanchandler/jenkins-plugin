package sean.plugin;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import sean.annotation.PluginRequest;

/**
 * Processes the incoming HTTP data and calls the
 * appropriate annotation method in {@link PluginRequestHandler}.
 *
 * @author sean
 */
public class PluginRequestProcessor  {

    private static final Logger LOGGER = Logger.getLogger(PluginRequestProcessor.class.getName());
    private StaplerRequest  request;
    private StaplerResponse response;
    Map<String, String> parameters = new HashMap<String, String>();

    public PluginRequestProcessor(StaplerRequest request, StaplerResponse response) {
        this.request = request;
        this.response = response;
        Set<String> keys = request.getParameterMap().keySet();
        for(String param: keys) {
            parameters.put(param, request.getParameter(param));
        }
    }


    /**
     * Uses Jenkins's {@link StaplerRequest} getRestOfPath() method to parse the intended action
     * form the plugin uri. Uses reflection to match the action to the annotatated mehtod in
     * {@link PluginRequestHandler}
     *
     * @throws Exception
     */
    public void processAnnotation() throws Exception {

        // the action will come off the plugin url e.g. /plugin/example/action
        String pluginRequestAction = request.getRestOfPath().substring(1, request.getRestOfPath().length());

        Class<?> cls = Class.forName(PluginRequestHandler.class.getCanonicalName());
        Class<?> partypes[] = new Class[] {Map.class, PrintWriter.class};
        Object arglist[] = new Object[] {parameters, response.getWriter()};
        Constructor<?> ct = cls.getConstructor(partypes);
        Object impl = ct.newInstance(arglist);

        for (Method m : cls.getMethods()) {
            if (m.isAnnotationPresent(PluginRequest.class)) {
                try {

                    String pluginRequestValue = m.getAnnotation(PluginRequest.class).name();
                    String pluginRequestMethod = m.getAnnotation(PluginRequest.class).method();
                    if (pluginRequestValue.equals(pluginRequestAction) && pluginRequestMethod.equalsIgnoreCase(request.getMethod())) {

                        m.invoke(impl, new Object[] {});
                        response.setStatus(200);
                        return; // only first annotation match is executed
                    }
                } catch (Exception e) {
                    response.sendError(500, "Internal Server Error '" +
                            e.getMessage() + "'");
                }
            }
        }
        // Nothing matched so return error
        response.sendError(401, "Invalid command received: '" +
                request.getRestOfPath() + "'");
    }
}
