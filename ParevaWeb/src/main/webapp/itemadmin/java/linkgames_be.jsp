<%  
    com.mixmobile.anyx.sdk.AdminRequest aReq = new com.mixmobile.anyx.sdk.AdminRequest( request );
    
    String action = aReq.get("action");
    String game1 = aReq.get("game1");
    game1 = game1.substring( game1.indexOf("_") + 1 );
    String game2 = aReq.get("game2");
    
    if( action.startsWith("link")){
        String sqlstr = "SELECT count(*) FROM javaGamesLinks WHERE " +
        "( aGame1='" + game1 + "' AND aGame2='" + game2 + "' ) OR (aGame1='" + game2 + "' AND aGame2='" + game1 + "') ";
        System.out.println( sqlstr );
        java.sql.Connection con = com.mixmobile.anyx.sdk.DBHStatic.getConnection();
        java.sql.ResultSet rs = com.mixmobile.anyx.sdk.DBHStatic.getRs( con, sqlstr );
        if( rs.next() && rs.getInt(1) > 0 ){
            rs.close();
            com.mixmobile.anyx.sdk.DBHStatic.closeConnection( con );
            %><span class="blue_11" id="text"><b>These games are already linked</b></span>&nbsp;<script type="text/javascript">window.setTimeout("new Effect.Fade('text')", 2000 );</script><%
            return;
        }
        rs.close();
        
        sqlstr = "SELECT count(javaGames.aUnique) FROM javaGames WHERE (javaGames.aUnique='"+game1+"' OR javaGames.aUnique='"+game2+"')";
        System.out.println( sqlstr );
        rs = com.mixmobile.anyx.sdk.DBHStatic.getRs( con, sqlstr );
        if( rs.next() && rs.getInt(1) < 2 ){
            rs.close();
            com.mixmobile.anyx.sdk.DBHStatic.closeConnection( con );
            %><span class="red_12" id="text"><b>System Error: Game Not Recognized</></span>&nbsp;<script type="text/javascript">window.setTimeout("new Effect.Fade('text')", 2000 );</script><%
            return;
        }
        rs.close();
        
        sqlstr = "INSERT INTO javaGamesLinks VALUES('"+com.mixmobile.anyx.sdk.Misc.generateUniqueId()+"','" + game1 + "', '"+ game2 +"')";
        if( com.mixmobile.anyx.sdk.DBHStatic.execUpdate( con, sqlstr  ) > 0 ){
            com.mixmobile.anyx.sdk.DBHStatic.closeConnection( con );
            %><span class="green_11" id="text"><b>Games Linked</b></span>&nbsp;<script type="text/javascript">window.setTimeout("new Effect.Fade('text')", 2000 );</script><%
            return ;
        }
        else{
            com.mixmobile.anyx.sdk.DBHStatic.closeConnection( con );
            %><span class="red_12" id="text"><b>System Error: Game link Not Saved.</b>&nbsp;</span><span class="grey_11" id="text">Reload page and use switch to link the games</span>&nbsp;<script type="text/javascript">window.setTimeout("new Effect.Fade('text')", 2000 );</script><%
            return;
        }
        
            
    }
    else if( action.startsWith("unlink")){
        
        String sqlstr = "DELETE FROM javaGamesLinks WHERE " +
        "(aGame1='" + game1 + "' AND aGame2 ='" + game2 + "') OR " +
        "(aGame1='" + game2 + "' AND aGame2 ='" + game1 + "')";
        java.sql.Connection con = com.mixmobile.anyx.sdk.DBHStatic.getConnection();
        if( com.mixmobile.anyx.sdk.DBHStatic.execUpdate( con, sqlstr ) > 0 ){
            com.mixmobile.anyx.sdk.DBHStatic.closeConnection( con );
            %><span class="green_11" id="text"><b>Link removed successfully</b></span>&nbsp;<script type="text/javascript">window.setTimeout("new Effect.Fade('text')", 2000 );</script><%
            return;
        }
        else{
            com.mixmobile.anyx.sdk.DBHStatic.closeConnection( con );
            %><span class="red_12" id="text"><b>System Error: Link Not Broken.</b>&nbsp;</span><span class="grey_11" id="text">Reload page and use switch to brake the link</span>&nbsp;<script type="text/javascript">window.setTimeout("new Effect.Fade('text')", 2000 );</script><%
            return;
        }
       
    }
    else{
    %>NOTHING HAPPEND<%
    return;
    }
%>