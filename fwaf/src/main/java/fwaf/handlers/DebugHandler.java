package fwaf.handlers;

import java.io.ByteArrayInputStream;
import java.util.Map;

import fawf.router.FixedLengthResponder;
import fawf.router.Router.UriResource;
import fawf.router.UriResponder;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class DebugHandler extends FixedLengthResponder {

    @Override
    public String getText() {
        return "not implemented";
    }

    public String getText(Map<String, String> urlParams, IHTTPSession session) {
        String text = "<html><body>Reqest Method: " + session.getMethod().toString() + "<br>";
        text += "<h1>Uri parameters:</h1>";
        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            text += "<div> Param: " + key + "&nbsp;Value: " + value + "</div>";
        }
        text += "<h1>Query parameters:</h1>";
        for (Map.Entry<String, String> entry : session.getParms().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            text += "<div> Query Param: " + key + "&nbsp;Value: " + value + "</div>";
        }
        text += "</body></html>";

        return text;
    }

    @Override
    public String getMimeType() {
        return "text/html";
    }

    @Override
    public IStatus getStatus() {
        return Status.OK;
    }

    public Response get(UriResource uriResource, Map<String, String> urlParams, IHTTPSession session) {
        String text = getText(urlParams, session);
        ByteArrayInputStream inp = new ByteArrayInputStream(text.getBytes());
        int size = text.getBytes().length;
        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);
    }

}