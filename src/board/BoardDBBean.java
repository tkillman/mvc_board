package board;

import java.sql.*;
import java.util.*;

//BoardDBBean bd = BoardDBBean.getInstance()
public class BoardDBBean {   
    private static BoardDBBean instance = new BoardDBBean();
   
    public static BoardDBBean getInstance() {
        return instance;
    }
   
    private BoardDBBean() {
    }
   
    private Connection getConnection() throws Exception {
	
    	String jdbcDriver = "jdbc:apache:commons:dbcp:pool";         
        return DriverManager.getConnection(jdbcDriver);
        
    }
    //writePro.jsp
    public void insertArticle(BoardDataBean article) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        //답변글인지 일반글인지를 구분해서 입력시켜주는 로직!!!
	
        
        int num=article.getNum();
        
        int ref=article.getRef();
        
        int re_step=article.getRe_step();
        
        int re_level=article.getRe_level();
        
        int number=0;
        
		String sql="";

        try {
            conn = getConnection();

            pstmt = conn.prepareStatement("select max(num) from board");
	    rs = pstmt.executeQuery();

	    if (rs.next())
	      number=rs.getInt(1)+1;
	    else
	      number=1;
  
	    if (num!=0)
	    { 
	    	
	      sql="update board set re_step=re_step+1 where ref= ? and re_step> ?";
	      pstmt = conn.prepareStatement(sql);
	      pstmt.setInt(1, ref);
	      pstmt.setInt(2, re_step);
	      pstmt.executeUpdate();
	      re_step=re_step+1;
	      re_level=re_level+1;
	      
	    }else{
		      ref=number;
		      re_step=0;
		      re_level=0;
	    }
            // 쿼리를 작성
            sql = "insert into board(num,writer,email,subject,passwd,reg_date,";
            sql+="ref,re_step,re_level,content,ip) values(board_num.NEXTVAL,?,?,?,?,?,?,?,?,?,?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, article.getWriter());
            pstmt.setString(2, article.getEmail());
            pstmt.setString(3, article.getSubject());
            pstmt.setString(4, article.getPasswd());
            pstmt.setTimestamp(5, article.getReg_date());
            pstmt.setInt(6, ref);
            pstmt.setInt(7, re_step);
            pstmt.setInt(8, re_level);
            pstmt.setString(9, article.getContent());
            pstmt.setString(10, article.getIp());

            pstmt.executeUpdate();
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
	    if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
    }
   
    //list.jsp : 페이징을 위해서 전체 DB에 입력된 행의수가 필요하다...!!!
    public int getArticleCount() throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int x=0;

        try {
            conn = getConnection();
           
            pstmt = conn.prepareStatement("select count(*) from board");
            rs = pstmt.executeQuery();

            if (rs.next()) {
               x= rs.getInt(1);
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return x;
    }

    // 검색 조건이 있는 경우
    public int getArticleCount(String selectName,String searchValue) throws Exception {
        
    	System.out.println(searchValue);
    	
    	Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        int x=0;

        try {
            conn = getConnection();
           
            stmt = conn.createStatement();
            String sql = "select count(*) from board where "+ selectName+ " like '%"+searchValue+"%'";

            rs = stmt.executeQuery(sql);

            if (rs.next()) {
               x= rs.getInt(1);
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return x;
    }
    
    
    //list.jsp ==> Paging!!! DB로부터 여러행을 결과로 받는다.
    public List getArticles(int start, int end) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List articleList=null;
        try {
            conn = getConnection();
           
            pstmt = conn.prepareStatement(
            "select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount,r  " +
            "from (select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount,rownum r " +
            "from (select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount " +
            "from board order by ref desc, re_step asc) order by ref desc, re_step asc ) where r >= ? and r <= ? ");
            pstmt.setInt(1, start);
	    pstmt.setInt(2, end);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                articleList = new ArrayList(end);
                do{
                  BoardDataBean article= new BoardDataBean();
		  article.setNum(rs.getInt("num"));
		  article.setWriter(rs.getString("writer"));
                  article.setEmail(rs.getString("email"));
                  article.setSubject(rs.getString("subject"));
                  article.setPasswd(rs.getString("passwd"));
                  article.setReg_date(rs.getTimestamp("reg_date"));
		  article.setReadcount(rs.getInt("readcount"));
                  article.setRef(rs.getInt("ref"));
                  article.setRe_step(rs.getInt("re_step"));
		  article.setRe_level(rs.getInt("re_level"));
                  article.setContent(rs.getString("content"));
		  article.setIp(rs.getString("ip"));
 
                  articleList.add(article);
		}while(rs.next());
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return articleList;
    }

    //검색 결과에 따른 list
    public List getArticles(int start, int end,String searchValue,String selectName) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List articleList=null;
        try {
            conn = getConnection();
           
            stmt = conn.createStatement();
            		
            String sql = "select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount,r  " +
                    "from (select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount,rownum r " +
                    "from (select num,writer,email,subject,passwd,reg_date,ref,re_step,re_level,content,ip,readcount " +
                    "from board where "+selectName+ "like '%"+searchValue+"%' order by ref desc, re_step asc) order by ref desc, re_step asc ) "+
                    		 "where r >= "+start+ "and r <= "+end;
                    
            
           
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                articleList = new ArrayList(end);
                do{
                  BoardDataBean article= new BoardDataBean();
                  article.setNum(rs.getInt("num"));
                  article.setWriter(rs.getString("writer"));
                  article.setEmail(rs.getString("email"));
                  article.setSubject(rs.getString("subject"));
                  article.setPasswd(rs.getString("passwd"));
                  article.setReg_date(rs.getTimestamp("reg_date"));
                  article.setReadcount(rs.getInt("readcount"));
                  article.setRef(rs.getInt("ref"));
                  article.setRe_step(rs.getInt("re_step"));
		  article.setRe_level(rs.getInt("re_level"));
                  article.setContent(rs.getString("content"));
		  article.setIp(rs.getString("ip"));
 
                  articleList.add(article);
		}while(rs.next());
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (stmt != null) try { stmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return articleList;
    }
    
    
    
    //read.jsp : DB로부터 한줄의 데이터를 가져온다.
    public BoardDataBean getArticle(int num) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BoardDataBean article=null;
        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(
            "update board set readcount=readcount+1 where num = ?");
            pstmt.setInt(1, num);
	    pstmt.executeUpdate();

            pstmt = conn.prepareStatement(
            "select * from board where num = ?");
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                article = new BoardDataBean();
                article.setNum(rs.getInt("num"));
		article.setWriter(rs.getString("writer"));
                article.setEmail(rs.getString("email"));
                article.setSubject(rs.getString("subject"));
                article.setPasswd(rs.getString("passwd"));
		article.setReg_date(rs.getTimestamp("reg_date"));
		article.setReadcount(rs.getInt("readcount"));
                article.setRef(rs.getInt("ref"));
                article.setRe_step(rs.getInt("re_step"));
		article.setRe_level(rs.getInt("re_level"));
                article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));    
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return article;
    }

    //updateForm.jsp : 수정폼에 한줄의 데이터를 가져올때.
    public BoardDataBean updateGetArticle(int num) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BoardDataBean article=null;
        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(
            "select * from board where num = ?");
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                article = new BoardDataBean();
                article.setNum(rs.getInt("num"));
		article.setWriter(rs.getString("writer"));
                article.setEmail(rs.getString("email"));
                article.setSubject(rs.getString("subject"));
                article.setPasswd(rs.getString("passwd"));
		article.setReg_date(rs.getTimestamp("reg_date"));
		article.setReadcount(rs.getInt("readcount"));
                article.setRef(rs.getInt("ref"));
                article.setRe_step(rs.getInt("re_step"));
		article.setRe_level(rs.getInt("re_level"));
                article.setContent(rs.getString("content"));
		article.setIp(rs.getString("ip"));    
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return article;
    }

    //updatePro.jsp : 실제 데이터를 수정하는 메소드.
    public int updateArticle(BoardDataBean article) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs= null;

        String dbpasswd="";
        String sql="";
	int x=-1;
        try {
            conn = getConnection();
           
	    pstmt = conn.prepareStatement("select passwd from board where num = ?");
            pstmt.setInt(1, article.getNum());
            rs = pstmt.executeQuery();
           
	if(rs.next()){
	    dbpasswd= rs.getString("passwd");
	    if(dbpasswd.equals(article.getPasswd())){
		sql="update board set writer=?,email=?,subject=?,passwd=?";
		sql+=",content=? where num=?";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, article.getWriter());
                pstmt.setString(2, article.getEmail());
                pstmt.setString(3, article.getSubject());
                pstmt.setString(4, article.getPasswd());
                pstmt.setString(5, article.getContent());
		pstmt.setInt(6, article.getNum());
                pstmt.executeUpdate();
		x= 1;
	    }else{
		x= 0;
	    }
	  }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
	    if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	 return x;
    }
   
    //deletePro.jsp : 실제 데이터를 삭제하는 메소드...
    public int deleteArticle(int num, String passwd) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs= null;
        String dbpasswd="";
        int x=-1;
        try {
	    conn = getConnection();

            pstmt = conn.prepareStatement(
            "select passwd from board where num = ?");
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();
           
            if(rs.next()){
		dbpasswd= rs.getString("passwd");
		if(dbpasswd.equals(passwd)){
		    pstmt = conn.prepareStatement("delete from board where num=?");
                    pstmt.setInt(1, num);
                    pstmt.executeUpdate();
		    x= 1; //글삭제 성공
		}else
		    x= 0; //비밀번호 틀림
	    }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
	return x;
    }
}
