package org.zoomdev.zoom.ioc.models;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.Initializeable;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocObject;

import java.util.concurrent.CountDownLatch;

/**
 * 逻辑： 商店销售商品，当商品售出之后推送到商户那边
 * <p>
 * 修改商品需要登录权限
 */
public class ShopService implements Initializeable, Destroyable {


    @Inject
    private ProductService productService;

    @Inject
    private CountDownLatch countDownLatch;

    private UserService userService;


    public ShopService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 都可以浏览
     */
    public String showProduct() {
        System.out.println("product is " + userService.getAdmin() + ":" + productService.getProduct());
        countDownLatch.countDown();
        return productService.getProduct();
    }


    public void editProduct(String info) {
        String user = userService.getAdmin();
        productService.editProduct(user, info);

        method.invoke(target);
    }


    public String getName() {
        return "Shop";
    }


    @Override
    public void destroy() {
        System.out.println("ShopService is destroied");
    }

    @Override
    public void initialize() {
        System.out.println("ShopService initialize");
    }

    private IocObject target;
    private IocMethodProxy method;

    public void setListener(IocObject target, IocMethodProxy method) {

        this.target = target;
        this.method = method;

    }
}
