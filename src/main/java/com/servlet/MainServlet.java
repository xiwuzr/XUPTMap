package com.servlet;

import com.pojo.PageInfo;

import com.service.impl.ServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/xupt/*")
public class MainServlet extends HttpServlet {
    ServiceImpl service;//服务类


    //初始化
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        System.out.println(context);
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        System.out.println(ac);
        //使用框架创建对象，spring框架
        service = ac.getBean("ServiceImpl", ServiceImpl.class);
    }

    //使用函数调用
    //修改为反射

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //截取方法的名称
        String url = req.getRequestURL().toString();
        String mtd = null;
        int start = url.lastIndexOf("/") + 1;
        System.out.println(url);
        int end = url.indexOf("?");
        if (end != -1) {
            mtd = url.substring(start, end);
        } else {
            mtd = url.substring(start);
        }
        System.out.println(mtd);
        switch (mtd) {
            case "showPlace":
                showPlace(req, resp);
                break;
            case "showPath":
                showPath(req, resp);
                break;
            case "selList":
                selList(req, resp);
                break;
            case "delete":
                delete(req, resp);
                break;
            case "insert":
                insert(req, resp);
                break;
            case "query":
                query(req, resp);
                break;
            case"PerTree":
                getPre(req,resp);break;
            default:
                break;
        }
    }
    public void getPre(HttpServletRequest req, HttpServletResponse resp){
        req.getSession().setAttribute("tree",service.getPer());
        try {
            resp.sendRedirect("/JSP/Tree.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse resp)//查询路径方法
    {

        //要修改session的寿命
        String start = req.getParameter("start");
        String end = req.getParameter("end");
        System.out.println(start+"   "+end+"**********");

        Map query = service.query(start, end);
        //req.removeAttribute("main");
//        req.getSession().invalidate();
//        req.removeAttribute("shortPath");
//        req.removeAttribute("lowPath");
//        req.removeAttribute("minSum");

        req.getSession().setAttribute("shortPath", query.get("shortPath"));
        req.getSession().setAttribute("lowPath", query.get("lowPath"));
        req.getSession().setAttribute("minSum", query.get("minSum"));
        List<String> list = (List<String>) query.get("main2");
//        System.out.println("&&&&&&&&&&&&");
//        for (String s : list) {
//            System.out.println(s);
//       }
//       System.out.println(query+"**********************");
//       query.clear();
//        System.out.println(query+"**********************");

        req.getSession().setAttribute("main", list);
        try {

            resp.sendRedirect("/JSP/query.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void insert(HttpServletRequest req, HttpServletResponse resp)
    {
        String name = req.getParameter("name");
        String msg = req.getParameter("msg");
        String start = req.getParameter("start");
        String end = req.getParameter("end");
        String length = req.getParameter("length");
        if(name != null && msg != null && !name.equals("") && !msg.equals(""))
        {
            if(service.insPlace(name, msg) == 1)
            {
                req.getSession().setAttribute("msg", "添加地点成功,3秒后跳转");
            }
            else {
                req.getSession().setAttribute("msg", "添加失败,3秒后跳转");
            }try {
                resp.sendRedirect("/JSP/message.jsp");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            if(service.insPath(start, end, Integer.parseInt(length)) == 1)
            {
                req.getSession().setAttribute("msg", "添加路径成功,3秒后跳转");
            }
            else {
                req.getSession().setAttribute("msg", "添加失败,3秒后跳转");
            }try {
                resp.sendRedirect("/JSP/message.jsp");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void delete(HttpServletRequest req, HttpServletResponse resp) {

        String name = req.getParameter("delete");
        String start = req.getParameter("deletestart");
        String end = req.getParameter("deleteend");
        if (name != null && !name.equals("")) {
            if (service.delPlace(name) >= 1) {
                req.getSession().setAttribute("msg", "删除成功,3秒后跳转");
            } else {
                req.getSession().setAttribute("msg", "删除失败,地点不存在,3秒后跳转");
            }
            try {
                resp.sendRedirect("/JSP/message.jsp");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (start != null && end != null) {
            if (service.delPath(start, end) == 1) {
                req.getSession().setAttribute("msg", "删除成功,3秒后跳转");
            } else {
                req.getSession().setAttribute("msg", "删除失败,路径不存在,3秒后跳转");
            }
            try {
                resp.sendRedirect("/JSP/message.jsp");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void selList(HttpServletRequest req, HttpServletResponse resp) {
        req.getSession().setAttribute("list", service.selList());
        try {
            String action = (String) req.getParameter("act");
            if (action.equals("query"))
                resp.sendRedirect("/JSP/pathQuery.jsp");
            else if (action.equals("delete1"))
                resp.sendRedirect("/JSP/delPlace.jsp");
            else if (action.equals("delete2"))
                resp.sendRedirect("/JSP/delPath.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //经典案例
    public void showPlace(HttpServletRequest req, HttpServletResponse resp) {
        int pageNum = 1;
        String pageNum1 = req.getParameter("pageNum");//获取页面序号
        if (pageNum1 != null && !pageNum1.equals("")) {
            pageNum = Integer.parseInt(pageNum1);
        }
        PageInfo pageInfo = service.showPlace(pageNum);
        req.getSession().setAttribute("pageInfo", pageInfo);
        try {
            resp.sendRedirect("/JSP/list.jsp");//转发
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPath(HttpServletRequest req, HttpServletResponse resp) {
        String name = req.getParameter("name");
        java.util.List list = service.showPath(name);
        System.out.println(list);
        req.getSession().setAttribute("path", list);
        req.getSession().setAttribute("name", name);
        try {
            resp.sendRedirect("/JSP/Place.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
