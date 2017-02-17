package fwaf.handlers;

import fawf.router.FixedLengthResponder;
import fawf.router.UriResponder;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class ErrorNotImplementedHandler extends FixedLengthResponder {
	public String getText() {
        return "<html><body><h2>The uri is mapped in the router, but no handler is specified. <br> Status: Not implemented!</h3></body></html>";
    }

    @Override
    public String getMimeType() {
        return "text/html";
    }

    @Override
    public IStatus getStatus() {
        return Status.OK;
    }
}