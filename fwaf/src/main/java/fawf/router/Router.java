package fawf.router;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fwaf.handlers.Error404NotFoundHandler;
import fwaf.handlers.ErrorNotImplementedHandler;

public abstract class Router extends NanoHTTPD {

    private static final Logger LOG = Logger.getLogger(Router.class.getName());

    private UriRouter router;

    public Router(int port) {
        super(port);
        router = new UriRouter();
    }

    public Router(String hostname, int port) {
        super(hostname, port);
        router = new UriRouter();
    }

	public void addMappings() {
		router.setNotImplemented(ErrorNotImplementedHandler.class);
		router.setNotFoundHandler(Error404NotFoundHandler.class);
	}

    public void addRoute(String url, Class<?> handler, Object... initParameter) {
        router.addRoute(url, 100, handler, initParameter);
    }

    public void addRoute(String url, Object handlerObject, Object... initParameter) {
        router.addRoute(url, 100, handlerObject, initParameter);
    }

    public void removeRoute(String url) {
        router.removeRoute(url);
    }
    
    public void setNotFoundHandler(Class<?> handler) {
    	router.setNotFoundHandler(handler);
    }
    
    public void setNotImplementedHandler(Class<?> handler){
    	router.setNotImplemented(handler);
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Try to find match
        return router.process(session);
    }
    
    public static String normalizeUri(String value) {
        if (value == null) {
            return value;
        }
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;

    }

    public static class UriResource {

        private static final Pattern PARAM_PATTERN = Pattern.compile("(?<=(^|/)):[a-zA-Z0-9_-]+(?=(/|$))");

        private static final String PARAM_MATCHER = "([A-Za-z0-9\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=\\s]+)";

        private static final Map<String, String> EMPTY = Collections.unmodifiableMap(new HashMap<String, String>());

        private final String uri;

        private final Pattern uriPattern;

        private final int priority;

        private final Class<?> handler;

        private final Object handlerObject;

        private final Object[] initParameter;

        private List<String> uriParams = new ArrayList<String>();

        public UriResource(String uri, int priority, Object handlerObject, Object... initParameter) {
            this.handler = null;
            this.handlerObject = handlerObject;
            this.initParameter = initParameter;
            if (uri != null) {
                this.uri = normalizeUri(uri);
                parse();
                this.uriPattern = createUriPattern();
            } else {
                this.uriPattern = null;
                this.uri = null;
            }
            this.priority = priority + uriParams.size() * 1000;
        }

        public UriResource(String uri, int priority, Class<?> handler, Object... initParameter) {
            this.handler = handler;
            this.handlerObject = null;
            this.initParameter = initParameter;
            if (uri != null) {
                this.uri = normalizeUri(uri);
                parse();
                this.uriPattern = createUriPattern();
            } else {
                this.uriPattern = null;
                this.uri = null;
            }
            this.priority = priority + uriParams.size() * 1000;
        }

        private void parse() {}

        private Pattern createUriPattern() {
            String patternUri = uri;
            Matcher matcher = PARAM_PATTERN.matcher(patternUri);
            int start = 0;
            while (matcher.find(start)) {
                uriParams.add(patternUri.substring(matcher.start() + 1, matcher.end()));
                patternUri = new StringBuilder(patternUri.substring(0, matcher.start()))//
                        .append(PARAM_MATCHER)//
                        .append(patternUri.substring(matcher.end())).toString();
                start = matcher.start() + PARAM_MATCHER.length();
                matcher = PARAM_PATTERN.matcher(patternUri);
            }
            return Pattern.compile(patternUri);
        }

		public Response process(Map<String, String> urlParams, IHTTPSession session) {
			String error = "fwaf error!";
			if (handlerObject != null || handler != null) {
				try {
					Object object = ((handlerObject != null) ? handlerObject : handler.newInstance());
					if (object instanceof UriResponder) {
						UriResponder responder = (UriResponder) object;
						switch (session.getMethod()) {
							case GET:
								return responder.get(this, urlParams, session);
							case POST:
								return responder.post(this, urlParams, session);
							case PUT:
								return responder.put(this, urlParams, session);
							case DELETE:
								return responder.delete(this, urlParams, session);
							default:
								return responder.other(session.getMethod().toString(), this, urlParams, session);
						}
					} else {
						String handlerName = "";
						if (handlerObject != null) {
							handlerName = handlerObject.getClass().getCanonicalName();
						} else {
							handlerName = handler.getCanonicalName();
						}
						return NanoHTTPD.newFixedLengthResponse(Status.OK, "text/plain", new StringBuilder("Return: ")
								.append(handlerName).append(".toString() -> ").append(object).toString());
					}
				} catch (Exception e) {
					error = "Error: " + e.getClass().getName() + " : " + e.getMessage();
					LOG.log(Level.SEVERE, error, e);
				}
			}
			return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, "text/plain", error);
		}

        @Override
        public String toString() {
            return new StringBuilder("UrlResource{uri='").append((uri == null ? "/" : uri))//
                    .append("', urlParts=").append(uriParams)//
                    .append('}')//
                    .toString();
        }

        public String getUri() {
            return uri;
        }

        public <T> T initParameter(Class<T> paramClazz) {
            return initParameter(0, paramClazz);
        }

        public <T> T initParameter(int parameterIndex, Class<T> paramClazz) {
            if (initParameter.length > parameterIndex) {
                return paramClazz.cast(initParameter[parameterIndex]);
            }
            LOG.severe("init parameter index not available " + parameterIndex);
            return null;
        }

        public Map<String, String> match(String url) {
            Matcher matcher = uriPattern.matcher(url);
            if (matcher.matches()) {
                if (uriParams.size() > 0) {
                    Map<String, String> result = new HashMap<String, String>();
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        result.put(uriParams.get(i - 1), matcher.group(i));
                    }
                    return result;
                } else {
                    return EMPTY;
                }
            }
            return null;
        }

    }

    public static class UriRouter {

        private List<UriResource> mappings;

        private UriResource error404Url;

        private Class<?> notImplemented;

        public UriRouter() {
            mappings = new ArrayList<UriResource>();
        }

        /**
         * Search in the mappings if the given url matches some of the rules If
         * there are more than one marches returns the rule with less parameters
         * e.g. mapping 1 = /user/:id mapping 2 = /user/help if the incoming uri
         * is www.example.com/user/help - mapping 2 is returned if the incoming
         * uri is www.example.com/user/3232 - mapping 1 is returned
         *
         * @param session
         * @return
         */
        public Response process(IHTTPSession session) {
            String work = normalizeUri(session.getUri());
            Map<String, String> params = null;
            UriResource uriResource = error404Url;
            for (UriResource u : mappings) {
                params = u.match(work);
                if (params != null) {
                    uriResource = u;
                    break;
                }
            }
            return uriResource.process(params, session);
        }

        private void addRoute(String url, int priority, Class<?> handler, Object... initParameter) {
            if (url != null) {
                if (handler != null) {
                    mappings.add(new UriResource(url, priority + mappings.size(), handler, initParameter));
                } else {
                    mappings.add(new UriResource(url, priority + mappings.size(), notImplemented));
                }
                sortMappings();
            }
        }

        private void addRoute(String url, int priority, Object handlerObject, Object... initParameter) {
            if (url != null) {
                if (handlerObject != null) {
                    mappings.add(new UriResource(url, priority + mappings.size(), handlerObject, initParameter));
                } else {
                    mappings.add(new UriResource(url, priority + mappings.size(), notImplemented));
                }
                sortMappings();
            }
        }

        private void sortMappings() {
            Collections.sort(mappings, new Comparator<UriResource>() {
                public int compare(UriResource o1, UriResource o2) {
                    return o1.priority - o2.priority;
                }
            });
        }

        private void removeRoute(String url) {
            String uriToDelete = normalizeUri(url);
            Iterator<UriResource> iter = mappings.iterator();
            while (iter.hasNext()) {
                UriResource uriResource = iter.next();
                if (uriToDelete.equals(uriResource.getUri())) {
                    iter.remove();
                    break;
                }
            }
        }

        public void setNotFoundHandler(Class<?> handler) {
            error404Url = new UriResource(null, 100, handler);
        }

        public void setNotFoundHandler(Object handlerObject) {
            error404Url = new UriResource(null, 100, handlerObject);
        }

        public void setNotImplemented(Class<?> handler) {
            notImplemented = handler;
        }

    }
    
}