package fawf.router;

import java.io.InputStream;
import java.util.Map;

import fawf.router.Router.UriResource;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;

/**
 * General nanolet to inherit from if you provide text or html data, only
 * fixed size responses will be generated.
 */
public abstract class FixedLengthResponder extends StreamResponder {

    public abstract String getText();

    public abstract IStatus getStatus();

    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), getText());
    }

    @Override
    public InputStream getData() {
        throw new IllegalStateException("this method should not be called in a text based nanolet");
    }
}