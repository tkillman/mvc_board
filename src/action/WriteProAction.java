package action;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import board.BoardDataBean;
import board.BoardDBBean;

public class WriteProAction implements CommandAction {// 涝仿等 臂 贸府

	public String requestPro(HttpServletRequest request,
			HttpServletResponse response) throws Throwable {

		request.setCharacterEncoding("utf-8");// 茄臂 牢内爹

		BoardDataBean article = new BoardDataBean();// 单捞磐贸府 后
		article.setNum(Integer.parseInt(request.getParameter("num")));
		System.out.println(request.getParameter("num"));
		article.setWriter(request.getParameter("writer"));
		article.setEmail(request.getParameter("email"));
		article.setSubject(request.getParameter("subject"));
		article.setPasswd(request.getParameter("passwd"));
		article.setReg_date(new Timestamp(System.currentTimeMillis()));
		article.setRef(Integer.parseInt(request.getParameter("ref")));
		article.setRe_step(Integer.parseInt(request.getParameter("re_step")));
		article.setRe_level(Integer.parseInt(request.getParameter("re_level")));
		article.setContent(request.getParameter("content"));
		article.setIp(request.getRemoteAddr());

		BoardDBBean dbPro = BoardDBBean.getInstance();// DB贸府
		dbPro.insertArticle(article);

		return "/MVC/writePro.jsp";
	}
}