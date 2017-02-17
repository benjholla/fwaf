package fwaf;

import fawf.router.Router;
import fi.iki.elonen.util.ServerRunner;
import fwaf.handlers.DebugHandler;
import fwaf.handlers.Error404NotFoundHandler;
import fwaf.handlers.ErrorNotImplementedHandler;

public class FWAF extends Router {

	private static final int PORT = 9090;
	
    public static void main(String[] args) {
        ServerRunner.run(FWAF.class);
    }
	
	public FWAF() {
		super(PORT);
		addMappings();
		System.out.println("\nfwaf is running! Point your browers to http://localhost:" + PORT + "/ \n");
	}

	

    /**
     * Add the routes Every route is an absolute path Parameters starts with ":"
     * Handler class should implement @UriResponder interface If the handler not
     * implement UriResponder interface - toString() is used
     */
    @Override
    public void addMappings() {
    	setNotImplementedHandler(ErrorNotImplementedHandler.class);
		setNotFoundHandler(Error404NotFoundHandler.class);
    	
    	addRoute("/", DebugHandler.class);
    	addRoute("/a", DebugHandler.class);
    	addRoute("/b", DebugHandler.class);
    	addRoute("/c", DebugHandler.class);
    	addRoute("/d", DebugHandler.class);
    	
//        addRoute("/user", UserHandler.class);
//        addRoute("/user", UserHandler.class); // add it twice to execute the
//                                              // priority == priority case
//        addRoute("/user/help", GeneralHandler.class);
//        addRoute("/user/:id", UserHandler.class);
//        addRoute("/general/:param1/:param2", GeneralHandler.class);
//        addRoute("/photos/:customer_id/:photo_id", null);
//        addRoute("/test", String.class);
//        addRoute("/interface", UriResponder.class); // this will cause an error
//                                                    // when called
//        addRoute("/toBeDeleted", String.class);
//        removeRoute("/toBeDeleted");
//        addRoute("/stream", StreamUrl.class);
//        addRoute("/browse/(.)+", StaticPageTestHandler.class, new File("src/test/resources").getAbsoluteFile());
    }

    
}