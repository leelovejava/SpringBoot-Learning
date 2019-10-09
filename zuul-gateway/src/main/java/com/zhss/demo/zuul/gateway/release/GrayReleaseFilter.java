package com.zhss.demo.zuul.gateway.release;

import org.springframework.context.annotation.Configuration;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 灰度发布过滤器
 * @author leelovejava
 */
@SuppressWarnings("unused")
@Configuration
public class GrayReleaseFilter extends ZuulFilter {

    @Resource
    private GrayReleaseConfigManager grayReleaseConfigManager;

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();

        // http://localhost:9000/order/order?xxxx

        Map<String, GrayReleaseConfig> grayReleaseConfigs =
                grayReleaseConfigManager.getGrayReleaseConfigs();
        for (String path : grayReleaseConfigs.keySet()) {
            if (requestURI.contains(path)) {
                GrayReleaseConfig grayReleaseConfig = grayReleaseConfigs.get(path);
                if (grayReleaseConfig.getEnableGrayRelease() == 1) {
                    System.out.println("启用灰度发布功能");
                    return true;
                }
            }
        }

        System.out.println("不启用灰度发布功能");

        return false;
    }

    @Override
    public Object run() {
//		Random random = new Random();
//		int seed = random.nextInt() * 100;
//
//        if (seed == 50) {
//            RibbonFilterContextHolder.getCurrentContext().add("version", "new");
//        }  else {
//            RibbonFilterContextHolder.getCurrentContext().add("version", "current");
//        }

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String gray = request.getParameter("gray");

        if ("true".equals(gray)) {
            RibbonFilterContextHolder.getCurrentContext().add("version", "new");
        } else {
            RibbonFilterContextHolder.getCurrentContext().add("version", "current");
        }

        return null;
    }
}