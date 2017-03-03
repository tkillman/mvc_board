package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WriteFormAction implements CommandAction {//글 입력 폼 처리

    public String requestPro(HttpServletRequest request,HttpServletResponse response)throws Throwable {
       //제목글과 답변글의 구분
        int num=0,ref=1,re_step=0,re_level=0; 
        try{ 
          if(request.getParameter("num")!=null){
		 num=Integer.parseInt(request.getParameter("num"));
		 ref=Integer.parseInt(request.getParameter("ref"));
		 re_step=Integer.parseInt(request.getParameter("re_step"));
		 re_level=Integer.parseInt(request.getParameter("re_level"));
	   }
          
	}catch(Exception e){e.printStackTrace();}
		//해당 뷰에서 사용할 속성
		request.setAttribute("num", new Integer(num));
		request.setAttribute("ref", new Integer(ref));
		request.setAttribute("re_step", new Integer(re_step));
		request.setAttribute("re_level", new Integer(re_level));

		return "/MVC/writeForm.jsp";//해당 뷰
	}
}