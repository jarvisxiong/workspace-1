<%@ include file="coreimport.jsp"%>
<%@ include file="db.jsp"%>
<style>
table {
	border-collapse: separate;
	border-spacing: 0;
}

th,td {
	padding: 10px 15px;
}

thead {
	background: #395870;
	color: #fff;
}

.nth-child {
	background: #f0f0f2;
}

td {
	border-bottom: 1px solid #cecfd5;
	border-right: 1px solid #cecfd5;
}

td:first-child {
	border-left: 1px solid #cecfd5;
}
</style>

<%
	try {
            Transaction trans=dbsession.beginTransaction();
            Query query=null;

		String sqlstr1 = "select * from uk_notification_info  ORDER BY ABS('aUnique'),entrytime DESC LIMIT 1000";
		/**
		
		create table uk_notification_info (aUnique varchar(30),paramName varchar(50),paramValue varchar(50),entrytime datetime);
		
		 **/

		query= dbsession.createSQLQuery(sqlstr1).addScalar("aUnique").addScalar("paramName").addScalar("paramValue").addScalar("entrytime");

		String previousUniqueId = "";
		boolean change = false;
%>

<table style="width: 80%; align: center" align="center">
	<thead>
		<tr>
			<td>Unique</td>			
			<td>Entry time</td>
			<td>Param Name</td>
			<td>Param Value</td>
		</tr>
	</thead>
	<tbody>


		<%
                java.util.List result=query.list();
                
                if(result.size()>0){
			for(Object o:result) {
                            Object[]row=(Object[]) o;

					String aUnique = String.valueOf(row[0]);
					String paramName = String.valueOf(row[1]);
					String paramValue = String.valueOf(row[2]);
					String entrytime = String.valueOf(row[3]);

					if (previousUniqueId == null
							|| previousUniqueId.trim().length() <= 0) {
						previousUniqueId = aUnique;
						change = true;
					} else {
						if (previousUniqueId != null
								&& !(previousUniqueId.trim()
										.equalsIgnoreCase(aUnique.trim()))) {
							previousUniqueId = aUnique;
							change = true;
		%>


		<tr class="nth-child">
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>

		</tr>

		<%
			}
					}
		%>


		<tr>
			<%
				if (change) {
					change = false;
			%>
			<td><%=aUnique%></td>
			<td><%=entrytime%></td>
			<%
				} else {
			%>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<%
				}
			%>
			<td><%=paramName%></td>
			<td><%=paramValue%></td>
			

		</tr>

		<%
			}
        }
                else{
		%>
                no billing response received! 
	</tbody>
        <%}%>
</table>
<%
	trans.commit();
        dbsession.close();
        
        
        } catch (Exception e) {
		e.printStackTrace();
	}
%>
