package sean.plugin;

import hudson.PluginWrapper;
import hudson.model.Hudson;

import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;

import sean.annotation.PluginRequest;

/**
 * 
 * Contains annotated methods which are processed as 
 * rest paths on the plugin's uri.
 * 
 * For example the uri '/plugin/awesome/hello'
 * will execute the method sayHello()
 * 
 * @author sean
 *
 */
public class PluginRequestHandler {

	private Map<String, String> params;
	private PrintWriter out;
	
	private Gson gson = new Gson();

	public PluginRequestHandler(Map<String, String> params, PrintWriter out) {
		
		this.params = params;
		this.out = out;
	}
	
	/**
	 * Says Hello
	 * @throws Exception
	 */
	@PluginRequest(name="hello")
	public void sayHello() {
		out.println("Hello!");		
	}
	
	/**
	 * Says Hello with JSON!
	 * @throws Exception
	 */
	@PluginRequest(name="hello-json")
	public void sayHelloJson() {
		String json = gson.toJson(new String("Hello!"));
		out.println(json);		
	}
	
	
	
	/**
	 * Returns the plugin version that's defined in pom.xml
	 * 
	 * @throws Exception
	 */
	@PluginRequest(name="version")
	public void printVersion() {
		
		PluginWrapper plugin = Hudson.getInstance().getPluginManager().getPlugin("awesome");
        out.println(plugin.getVersion());
	}	
	
}