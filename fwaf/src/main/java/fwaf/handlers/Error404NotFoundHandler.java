package fwaf.handlers;

import fawf.router.FixedLengthResponder;
import fawf.router.UriResponder;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 * Handling error 404 - unrecognized urls
 */
public class Error404NotFoundHandler extends FixedLengthResponder {

	public String getText() {
		return "<html><body><h3>Error 404: the requested page doesn't exist.</h3></body></html>";
	}

	@Override
	public String getMimeType() {
		return "text/html";
	}

	@Override
	public IStatus getStatus() {
		return Status.NOT_FOUND;
	}

}
