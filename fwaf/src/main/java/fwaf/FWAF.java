package fwaf;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import fawf.router.FixedLengthResponder;
import fawf.router.Router;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.util.ServerRunner;
import fwaf.handlers.DebugHandler;
import fwaf.handlers.Error404NotFoundHandler;
import fwaf.handlers.ErrorNotImplementedHandler;

public class FWAF extends Router {
	
	private static final int PORT = 9090;
	private static final String FWAFID = "fwafid";
	private static final long INACTIVE = 10000;

	public static void main(String[] args) {
		ServerRunner.run(FWAF.class);
	}

	private static HashMap<String, State> previousStates = new HashMap<String, State>();
	
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

	private static class FWAFRouteHandler extends FixedLengthResponder {

		private static boolean isLegalStateTransition(State s1, State s2) {
			return true;
		}

		@Override
		public String getText() {
			return "not implemented";
		}

		private Response blockRequest(String reason){
			String text = "<html><body><h1>fwaf: Request Blocked</h1<br><h2>Reason: " + reason + "</h2></body></html>";
			ByteArrayInputStream inp = new ByteArrayInputStream(text.getBytes());
			int size = text.getBytes().length;
			return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);
		}
		
		private Response allowRequest(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session){
			String text = "<html><body>You may proceed...</body></html>";
			ByteArrayInputStream inp = new ByteArrayInputStream(text.getBytes());
			int size = text.getBytes().length;
			return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);
		}

		@Override
		public String getMimeType() {
			return "text/html";
		}

		@Override
		public IStatus getStatus() {
			return Status.OK;
		}

		@Override
		public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
			String sessionID = session.getCookies().read(FWAFID);
			State previousState = previousStates.get(sessionID);
	    	if(previousState != null){
	    		if((System.currentTimeMillis() - previousState.getTimestamp()) < INACTIVE){
	    			State nextState = new State(session.getUri(), System.currentTimeMillis());
	    			if(isLegalStateTransition(previousState, nextState)){
	    				// valid transition
	    				// TODO: type check inputs
	    				
	    				// the input passed sanitization checks and the transition is legal
	    				// update the previous state to the next state
	    				previousStates.put(sessionID, nextState);
	    				
	    				// allow the request
	    				return allowRequest(uriResource, urlParams, session);
	    			} else {
	    				// illegal state transition
	    				// PENALTY: you lose your session...
	    				previousStates.remove(sessionID);
	    				return blockRequest("Illegal state transition!");
	    			}
	    		} else {
	    			// expired session
	    			return blockRequest("Expired session");
	    		}
	    	} else {
	    		// invalid session
	    		// TODO: PENALTY: you get an IP ban??? solve a captcha???
				return blockRequest("Invalid session");
	    	}
		}

	}
    
}