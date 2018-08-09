package com.zbensoft.e.payment.api.common;

public class PageHelperUtil {
	
	public static final int DELETE_NO=MessageDef.DELETE_FLAG.UNDELETE;//0表示未删除
	public static final int DELETE_YES=MessageDef.DELETE_FLAG.DELETED;//1表示已删除

	//得到流水号
	public static String getSeq(){
		return System.currentTimeMillis()+"";
	}
	
	//得到当前时间
	public static java.util.Date getCurrentDate(){
		return new java.util.Date();
	}
	
	//得到当前的页数，是第几页
    /**
     * Description: <br> 
     *  得到当前第几页 为了 赋值给javaben的pageNow
     * @author <br>
     * @taskId <br>
     * @param request
     * @return <br>
     */ 
    public static int getPageNum(String start,String length) {
        // 分页 和条数 start
        int currentPage = start == null ? 0 : Integer.parseInt(start);
        int showCount = length == null ? 10 : Integer.parseInt(length);
        // System.out.println("得到当前页:"+currentPage+";每页行数"+showCount);

        if (currentPage != 0) {// 获取页数
            currentPage = currentPage / showCount;
        }
        currentPage = currentPage + 1;
        //System.out.println("currentPage:" + currentPage + ";showCount:" + showCount);
        // 分页 和条数 end
        return currentPage;
    }
    
    /**
     * Description: <br> 
     *  得到每页显示的数目 为了赋值给javaben的pageSize
     * @author <br>
     * @taskId <br>
     * @param request
     * @return <br>
     */ 
    public static int getPageSize(String start,String length) {
        // 分页 和条数 start
        int showCount = length == null ? 10 : Integer.parseInt(length);
        // 分页 和条数 end
        return showCount;
    }
    
}
