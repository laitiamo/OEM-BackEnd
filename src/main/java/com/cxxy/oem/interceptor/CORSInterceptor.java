package com.cxxy.oem.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import javax.servlet.http.HttpServletResponse;

public class CORSInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        if(invocation.isActionInvocation()){
            // 加载配置文件
            PropKit.use("config.properties");
            Boolean ifDev = PropKit.getBoolean("devMode", false);
            //设置跨域地址
            String Origin  = PropKit.get("CORS_Path");
            // 开发模式下修改跨域地址
            if(ifDev){
                Origin = "http://localhost:8080";
            }
            Controller controller = (Controller) invocation.getController();
            HttpServletResponse response = controller.getResponse();
            //允许跨域访问的域名，若有端口需要写全(协议+域名+端口),若没有端口末尾不用加'/'
            response.setHeader("Access-Control-Allow-Origin", Origin);
            response.setHeader("Access-Control-Allow-Methods","*");
            response.setHeader("Access-Control-Allow-Credentials","true");
            //提示OPTION预检时，后端需要设置的两个常用自定义头
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
                    + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
            response.setHeader("Access-Control-Request-Headers", "Authorization,Origin, X-Requested-With,content-Type,Accept");
            response.setHeader("Access-Control-Expose-Headers", "*");
        }
        invocation.invoke();
    }
}
