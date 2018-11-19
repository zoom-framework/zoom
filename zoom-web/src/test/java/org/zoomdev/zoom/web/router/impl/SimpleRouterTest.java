package org.zoomdev.zoom.web.router.impl;

import org.junit.Before;
import org.junit.Test;
import org.zoomdev.zoom.mock.MockHttpServletRequest;
import org.zoomdev.zoom.web.action.impl.GroupActionHandler;
import org.zoomdev.zoom.web.router.Router;

import static org.junit.Assert.assertEquals;

public class SimpleRouterTest {


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {


        BracesRouterParamRule rule = new BracesRouterParamRule();
        assertEquals(rule.match("{action}"), true);
        assertEquals(rule.getParamName("{action}"), "action");
        assertEquals(rule.match("action"), false);

        SimpleRouter router = new SimpleRouter(new BracesRouterParamRule());

        Router.RemoveToken removeToken1 = router.register("/{action}/index", new MockAction("index", "action"));
        Router.RemoveToken removeToken2 = router.register("/{action}/{edit}", new MockAction("paths", "action", "edit"));
        Router.RemoveToken removeToken3 = router.register("/index/{action}/add", new MockAction("add", "action"));
        Router.RemoveToken removeToken4 = router.register("/{action}/put/{id}", new MockAction("put", "action", "id"));
        Router.RemoveToken removeToken5 = router.register("/public/index", new MockAction("public/index"));
        Router.RemoveToken removeToken6 = router.register("/public/index", new MockAction("public/index/post"));
        Router.RemoveToken removeToken7 = router.register("/{action}/put/{id}",
                new MockAction("put/id", "action", "id"));
        Router.RemoveToken removeToken8 =
                router.register("/index/{action}/add",
                        new MockAction("action/add", "action"));


        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("/main/index");

        assertEquals(router.match("/action/index", new MockHttpServletRequest("/module/index")).getMapping(), "index");
        assertEquals(router.match("/action/edit", new MockHttpServletRequest("/test/edit")), new MockAction("paths"));
        assertEquals(router.match("/index/action/add",
                new MockHttpServletRequest("add")),
                new GroupActionHandler(new MockAction("action/add"), new MockAction("add")));

        assertEquals(router.match("/action/put/id",
                new MockHttpServletRequest("put")),
                new GroupActionHandler(
                        new MockAction("put/id"),
                        new MockAction("put")
                ));


        assertEquals(router.match("/public/index", new MockHttpServletRequest("/public/index")),
                new GroupActionHandler(new MockAction("public/index/post"), new MockAction("public/index")));


        removeToken6.remove();
        //移除了精确匹配，所以匹配到了含有一个精确匹配的
        assertEquals(router.match("/public/index",
                new MockHttpServletRequest("/public/index")),
                new MockAction("public/index"));

        removeToken4.remove();
        assertEquals(router.match("/action/put/id", new MockHttpServletRequest("put")), new MockAction("put/id"));


        removeToken3.remove();
        assertEquals(router.match("/index/action/add", new MockHttpServletRequest("add")), new MockAction("action/add"));

        removeToken2.remove();
        assertEquals(router.match("/action/edit", new MockHttpServletRequest("/test/edit")), null);

        removeToken1.remove();
        assertEquals(router.match("/action/index", new MockHttpServletRequest("/module/index")), null);

        removeToken5.remove();

        assertEquals(router.actionMap.size(), 0);

        removeToken7.remove();

        removeToken8.remove();
        assertEquals(router.node.isEmpty(), true);
    }

}
