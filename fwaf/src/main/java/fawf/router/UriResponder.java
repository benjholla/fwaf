package fawf.router;

import java.util.Map;

import fawf.router.Router.UriResource;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface UriResponder {

    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session);

    public Response put(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session);

    public Response post(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session);

    public Response delete(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session);

    public Response other(String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session);
    
}
