package cn.itcast.erp.util;

import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ---
 * Author xushuai
 * Description
 */
public class ViewUtils {


    /**
     * 输出字符串到前端
     * @param jsonString
     */
    public static void write(String jsonString){
        try {
            //响应对象
            HttpServletResponse response = ServletActionContext.getResponse();
            //设置编码
            response.setContentType("text/html;charset=utf-8");
            //输出给页面
            response.getWriter().write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

