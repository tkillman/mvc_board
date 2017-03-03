package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import board.BoardDataBean;
import board.BoardDBBean;


public class ContentAction implements CommandAction {//글내용 처리

    public String requestPro(HttpServletRequest request,
        HttpServletResponse response)throws Throwable {
       
        int num = Integer.parseInt(request.getParameter("num"));//해당 글번호
        String pageNum = request.getParameter("pageNum");//해당 페이지 번호

        BoardDBBean dbPro = BoardDBBean.getInstance();//DB처리
        BoardDataBean article =  dbPro.getArticle(num);//해당 글번호에 대한 해당 레코드
 
        //해당 뷰에서 사용할 속성
        request.setAttribute("num", new Integer(num));
        request.setAttribute("pageNum", new Integer(pageNum));
        request.setAttribute("article", article);
       
        return "/MVC/content.jsp";//해당 뷰
    }
}