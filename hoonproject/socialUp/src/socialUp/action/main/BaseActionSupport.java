package socialUp.action.main;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import socialUp.common.util.CmnUtil;
import socialUp.common.util.CookieUtil;

import com.opensymphony.xwork2.ActionSupport;

public class BaseActionSupport extends ActionSupport implements ServletRequestAware, ServletResponseAware
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4469099709419770197L;
	
	/*jsp에 전달되어 사용될 값들*/
	protected String fromAction = "";		
	
	
	
	public String getFromAction() {
		return fromAction;
	}
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest request)
	{
	this.request=request;
	}
	@Override
	public void setServletResponse(HttpServletResponse response){
	this.response=response;
	}
	
	public String getParam(String param)
	{
		String retVal;
		retVal = CmnUtil.nvl(this.request.getParameter(param));
		
		return retVal;
	}

	public String[] getParams(String param)
	{
		return this.request.getParameterValues(param);
	}
	
	protected void flushString(String flushString)
	{
		try
		{
		this.response.setCharacterEncoding("UTF-8");
		this.response.setHeader("Cache-Control", "no-cache");
		this.response.getWriter().write(flushString);
		this.response.flushBuffer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
     
	}
	
}