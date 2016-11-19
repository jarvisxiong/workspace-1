package ume.pareva.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.sdk.Misc;
import ume.pareva.util.CSVParseUtil;
import ume.pareva.util.ZACPA;



/**
 * Servlet implementation class CSVUpload
 */
public class CSVUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Autowired
	private CSVParseUtil csvparseutil;
	
	@Autowired
	private ZACPA zacpa;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CSVUpload() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//List<String[]> lines=csvparseutil.parseCSV("/var/quiz.csv");
		List<String[]> lines=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateInString = "2016-08-15 00:00:00";
		Date date=new Date();
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Calendar c = Calendar.getInstance(); //gives u calendar with current time
		c.setTime(date);
		List<String> clubs=new ArrayList<String>();
		for(int i=0;i<lines.size();i++){
			String[] data=lines.get(i);
			String clubUnique=data[12];
			if(!clubs.contains(clubUnique)){
				clubs.add(clubUnique);
			}
		}
		List<List<Map<String,Object>>> quizByClub=new ArrayList<List<Map<String,Object>>>();
		for (int i=0;i<clubs.size();i++){
			List<Map<String,Object>> quizList=new ArrayList<Map<String,Object>>();
			String clubUnique=clubs.get(i);
			for(int j=0;j<lines.size();j++){
				String[] data=lines.get(i);
				Map<String,Object> quizMap=new HashMap<String,Object>();
				if(data[12].equals(clubUnique)){
					Timestamp timestamp1=new Timestamp(c.getTimeInMillis());
					c.add(Calendar.DATE, 6);
					Timestamp timestamp2=new Timestamp(c.getTimeInMillis());
					System.out.println(timestamp1);
					System.out.println(timestamp2);
					c.add(Calendar.DATE, 1);
					Timestamp timestamp3=new Timestamp(c.getTimeInMillis());
					
					c.setTimeInMillis(timestamp3.getTime());
					quizMap.put("quizNo", Misc.generateUniqueId());
					quizMap.put("quizQuestion", data[2].replace("'", "\\'"));
					quizMap.put("startDateTime", timestamp1);
					quizMap.put("endDateTime", timestamp2);
					quizMap.put("quizMsg", data[9].replace("'", "\\'"));
					quizMap.put("aClubUnique", data[12]);
					quizList.add(quizMap);
				}
			}
			c.setTime(date);
			quizByClub.add(quizList);
		}
		
		
		for(int i=0;i<quizByClub.size();i++){
			List<Map<String,Object>> quizList=quizByClub.get(i);
			for(int j=0;j<quizList.size();j++){
				Map<String,Object> quizMap=quizList.get(i);
				String sql="insert into quizQuestionsWithClub(quizNo,quizQuestion,startDateTime,endDateTime,quizMsg,aClubUnique)"
						+ " VALUES('"
		                + quizMap.get("quizNo").toString()
		                + "','"
		                + quizMap.get("quizQuestion").toString()
		                + "','"
		                + Timestamp.valueOf(quizMap.get("startDateTime").toString())
		                + "','"
		                + Timestamp.valueOf(quizMap.get("endtDateTime").toString())
		                + "','"
		                + quizMap.get("quizMsg")
		                + "','"
		                + quizMap.get("aClubUnique")
		                +  "')";
				
				System.out.println(sql);
			//	zacpa.executeUpdateCPA(sql);
			}
		}
		
	/*	try{
			for(int i=0;i<lines.size();i++){
				String[] data=lines.get(i);
				System.out.println(data[2]);
				System.out.println(data[9]);
				System.out.println(data[12]);
				
				Timestamp timestamp1=new Timestamp(c.getTimeInMillis());
				c.add(Calendar.DATE, 6);
				Timestamp timestamp2=new Timestamp(c.getTimeInMillis());
				System.out.println(timestamp1);
				System.out.println(timestamp2);
				c.add(Calendar.DATE, 1);
				Timestamp timestamp3=new Timestamp(c.getTimeInMillis());
				
				c.setTimeInMillis(timestamp3.getTime());
				System.out.println("_________________________________________________________________________");
				for(int j=0;j<data.length;j++)
					System.out.println(data[j]);
				
				String sql="insert into quizQuestionsWithClub(quizNo,quizQuestion,startDateTime,endDateTime,quizMsg,aClubUnique)"
						+ " VALUES('"
		                + Misc.generateUniqueId()
		                + "','"
		                + data[2].replace("'", "\\'")
		                + "','"
		                + timestamp1
		                + "','"
		                + timestamp2
		                + "','"
		                + data[9].replace("'", "\\'")
		                + "','"
		                + data[12]
		                +  "')";
				zacpa.executeUpdateCPA(sql);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
*/	}
	
	 

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
