package fawf.router;

import java.io.InputStream;
import java.util.Map;

import fawf.router.Router.UriResource;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;

/**
 * General nanolet to inherit from if you provide stream data, only chucked
 * responses will be generated.
 */
public abstract class StreamResponder implements UriResponder {

    public abstract String getMimeType();

    public abstract IStatus getStatus();

    public abstract InputStream getData();

    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return NanoHTTPD.newChunkedResponse(getStatus(), getMimeType(), getData());
    }

    public Response post(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return get(uriResource, urlParams, session);
    }

    public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return get(uriResource, urlParams, session);
    }

    public Response delete(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return get(uriResource, urlParams, session);
    }

    public Response other(String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        return get(uriResource, urlParams, session);
    }
}