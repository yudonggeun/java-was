package codesquad.context;

import codesquad.config.FilterConfig;
import codesquad.config.RouterConfig;
import codesquad.handler.LoginTable;
import codesquad.template.HtmlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApplicationContext {

    public static ApplicationContext context = new ApplicationContext();
    public static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    static {
        context.getRouterConfig();
        context.getFilterConfig();
        context.getHtmlManager();

        // router init
        context.getLoginHandler();
    }

    private Map<Class<?>, Object> soloObject;

    public void registerSingleTon(Object solo) {
        soloObject.putIfAbsent(solo.getClass(), solo);
    }

    public Object getSoloObject(Class<?> clazz) {
        return soloObject.get(clazz);
    }

    private FilterConfig filterConfig;

    public FilterConfig getFilterConfig() {
        if (filterConfig == null) {
            logger.info("init filter config");
            filterConfig = new FilterConfig(this);
        }
        return filterConfig;
    }

    private RouterConfig routerConfig;

    public RouterConfig getRouterConfig() {
        if (routerConfig == null) {
            logger.info("init router config");
            routerConfig = new RouterConfig();
        }
        return routerConfig;
    }

    private HtmlManager htmlManager;

    public HtmlManager getHtmlManager() {
        if (htmlManager == null) {
            logger.info("init html manager");
            htmlManager = new HtmlManager();
        }
        return htmlManager;
    }

    private LoginTable loginHandler;

    public LoginTable getLoginHandler() {
        if (loginHandler == null) {
            logger.info("init login handler");
            loginHandler = new LoginTable(getRouterConfig(), getHtmlManager());
        }
        return loginHandler;
    }
}
