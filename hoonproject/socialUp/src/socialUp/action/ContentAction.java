package socialUp.action;

import java.io.File;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import socialUp.action.main.BaseActionSupport;
import socialUp.common.AuthInfo;
import socialUp.common.AuthService;
import socialUp.common.DaoFactory;
import socialUp.common.ServiceFactory;
import socialUp.common.util.CmnUtil;
import socialUp.common.util.CookieUtil;
import socialUp.common.util.DateTime;
import socialUp.common.util.DebugUtil;
import socialUp.common.util.NumUtil;
import socialUp.service.content.ContentService;
import socialUp.service.content.ContentServiceImpl;
import socialUp.service.content.dao.ContentSourceTblDAO;
import socialUp.service.content.dao.ContentSourceTblDAOImpl;
import socialUp.service.content.dao.ContentTitleListTblDAO;
import socialUp.service.content.dao.ContentTitleListTblDAOImpl;
import socialUp.service.content.dto.ContentBranchDTO;
import socialUp.service.content.dto.ContentDtlCommentDTO;
import socialUp.service.content.dto.ContentJoinMemDTO;
import socialUp.service.content.dto.ContentSourceTblDTO;
import socialUp.service.content.dto.ContentDtlTblDTO;
import socialUp.service.content.dto.ContentTitleTblDTO;
import socialUp.service.member.MemberService;
import socialUp.service.member.MemberServiceImpl;
import socialUp.service.member.dto.MemTblDTO;

import com.opensymphony.xwork2.ActionSupport;
import com.sun.jmx.remote.util.Service;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.xml.internal.ws.util.ServiceFinder;

public class ContentAction extends BaseActionSupport 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4531298069661381976L;


	/**
	 * 
	 */

	public Logger log = Logger.getLogger(this.getClass());

	private ContentSourceTblDTO 		contentSource = null;
	private ContentTitleTblDTO 			contentTitle = null;
	
	private List<ContentTitleTblDTO> 	contentTitleList 	= null;
	private List<ContentTitleTblDTO> 	contentTitleList2 	= null;
	private List<ContentSourceTblDTO> 	contentSourceList 	= null;
	private List<ContentDtlTblDTO> 		contentDtlList 		= null;
	private List<ContentBranchDTO>		contentBranchList   = null;
	private List<ContentJoinMemDTO>		contentJoinMemList 	= null;
	private List<ContentDtlCommentDTO>	contentDtlCommentList = null;
	private SyndFeed 					syndFeed			= null;
	
	 
	public List<ContentDtlCommentDTO> getContentDtlCommentList() {
		return contentDtlCommentList;
	}

	public void setContentDtlCommentList(
			List<ContentDtlCommentDTO> contentDtlCommentList) {
		this.contentDtlCommentList = contentDtlCommentList;
	}

	
	public List<ContentSourceTblDTO> getContentSourceList() {
		return contentSourceList;
	}

	public void setContentSourceList(List<ContentSourceTblDTO> contentSourceList) {
		this.contentSourceList = contentSourceList;
	}
	
	public List<ContentTitleTblDTO> getContentTitleList() {
		return contentTitleList;
	}

	public void setContentTitleList(List<ContentTitleTblDTO> contentTitleList) {
		this.contentTitleList = contentTitleList;
	}

	public List<ContentDtlTblDTO> getContentDtlList() {
		return contentDtlList;
	}
	
	public ContentSourceTblDTO getContentSource() {
		return contentSource;
	}

	public void setContentSource(ContentSourceTblDTO contentSource) {
		this.contentSource = contentSource;
	}
	
	public ContentTitleTblDTO getContentTitle() {
		return contentTitle;
	}

	public List<ContentBranchDTO> getContentBranchList() {
		return contentBranchList;
	}

	public void setContentBranchList(List<ContentBranchDTO> contentBranchList) {
		this.contentBranchList = contentBranchList;
	}

	public List<ContentJoinMemDTO> getContentJoinMemList() {
		return contentJoinMemList;
	}

	public void setContentJoinMemList(List<ContentJoinMemDTO> contentJoinMemList) {
		this.contentJoinMemList = contentJoinMemList;
	}
	
	public List<ContentTitleTblDTO> getContentTitleList2() {
		return contentTitleList2;
	}

	public void setContentTitleList2(List<ContentTitleTblDTO> contentTitleList2) {
		this.contentTitleList2 = contentTitleList2;
	}

	public SyndFeed getSyndFeed() {
		return syndFeed;
	}
	
	


	

	/**
	 * 최초  컨텐츠를 등록하기 위한 화면을 호출
	 * 
	 * @return
	 * @throws Exception
	 */
	public String AddForm () throws Exception 
	{
		String returnVal = "DEFAULT";
		
		log.debug("contentFirstAddForm 시작");
		
		//회원테이블 조회용 객체 생성
		ContentService contentService = (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		String refTtNo 	= CmnUtil.nvl(this.getRequest().getParameter("refTtNo"));
		
		
		
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		

				 
		
		
		// 컨텐츠 타이틀을 새롭게 만들면서 기존 다른사람의 컨텐츠 타이틀을 브랜치를 가져올때
		// 해당 컨텐트에 다른 기존 브랜치 정보가 있으면 가져온다.
		if ( !"".equals(refTtNo) )
		{
			
			ContentTitleTblDTO contentTitleParam  = new ContentTitleTblDTO();
			contentTitleParam.setTt_no(refTtNo);
			
		
			// 수집 컨텐츠 등록정보
			this.contentTitle = contentService.selectContentBranch(contentTitleParam);
		}
		
		
		return  returnVal;
	
	} 
	
	/**
	 * 최초 컨텐츠를 실제 등록처리
	 * 
	 * @return
	 * @throws Exception
	 */
	public String AddFinish() throws Exception 
	{
		String returnVal = "DEFAULT";
		
		log.debug("contentFirstAddProc 시작");
		
		
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜

		
		// 회원테이블 조회용 객체 생성
		ContentService contentService = (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		 
		
		String title_name 			= this.getParam("title_name");				
		String title_desc 			= this.getParam("title_desc");
		String title_short_name 	= this.getParam("title_short_name");
		String order_mem_open_yn 	= this.getParam("order_mem_open_yn");
		String branch_conf_yn 		= this.getParam("branch_conf_yn");
		String order_mem_join_yn 	= this.getParam("order_mem_join_yn");
		String order_mem_join_metd	= this.getParam("order_mem_join_metd");
		String order_mem_join_passwd= this.getParam("order_mem_join_passwd");
		String rss_source			= this.getParam("rss_source");
		String tw_source			= this.getParam("tw_source");
		String branchTtno			= this.getParam("branchTtno");				// 최초 생성시 다른 컨텐츠 타이틀을포함하고 생성할떄
		

		String[] rssSourceArr  	= null;	// rss 글타래 목록
		String[] twSourceArr  	= null; // 트위터 글타래 목록												
		
		//Rss 수집목록 생성
		rss_source = rss_source.replaceAll("\r\n", ",");
		rssSourceArr = rss_source.split(",");
		
		//트위터 수집목록 생성
		tw_source = tw_source.replaceAll("\r\n", ",");
		twSourceArr = tw_source.split(",");
		
		
		List<ContentSourceTblDTO> contentSourceParamArr = new ArrayList<ContentSourceTblDTO>();	// 타이틀 테이블에 들어갈정보 배열
		ContentTitleTblDTO 	contentTitleParam = new ContentTitleTblDTO();		// 타이틀 컨텐츠 정보
		

		contentTitleParam.setMt_no(authInfo.getMt_no());
		contentTitleParam.setTitle_name(title_name);
		contentTitleParam.setTitle_desc(title_desc);
		contentTitleParam.setTitle_short_name(title_short_name);
		contentTitleParam.setTitle_kind("00");
		contentTitleParam.setOrder_mem_open_yn(order_mem_open_yn);
		contentTitleParam.setBranch_conf_yn(branch_conf_yn);
		contentTitleParam.setOrder_mem_join_yn(order_mem_join_yn);
		contentTitleParam.setOrder_mem_join_metd(order_mem_join_metd);
		contentTitleParam.setOrder_mem_join_passwd(order_mem_join_passwd);
		contentTitleParam.setCreate_no(authInfo.getMt_no());
		contentTitleParam.setCreate_dt(sCurrentDate);
		contentTitleParam.setUpdate_no(authInfo.getMt_no());
		contentTitleParam.setUpdate_dt(sCurrentDate);
		
		// RSS 글타래 등록
		for (int i=0;i<rssSourceArr.length;i++)
		{
			// 관심컨텐츠가 공백이 아니라면 등록한다.
			if (rssSourceArr[i] != null
					&& !"".equals(rssSourceArr[i].trim())
				)
			{
				// 자신의 컨텐츠 정보 생성
				ContentSourceTblDTO favoritContentSource = new ContentSourceTblDTO(); 
				
				
				favoritContentSource.setMt_no(authInfo.getMt_no());
				favoritContentSource.setRss2_url(rssSourceArr[i]);
				favoritContentSource.setSource_kind("01");				// 01:rss,02:트위터
				favoritContentSource.setSource_dtl_kind("");
				favoritContentSource.setSource_owner_kind("03");				// 본인이 직접등록
				favoritContentSource.setSource_login_id("");
				favoritContentSource.setReg_stat("02");
				favoritContentSource.setCreate_dt(sCurrentDate);
				favoritContentSource.setCreate_no(authInfo.getMt_no());
				favoritContentSource.setUpdate_dt(sCurrentDate);
				favoritContentSource.setUpdate_no(authInfo.getMt_no());
				contentSourceParamArr.add(favoritContentSource);
				
			}
		}
		
		// 트위터 글타래 등록
		for (int i=0;i<twSourceArr.length;i++)
		{
			// 관심컨텐츠가 공백이 아니라면 등록한다.
			if (twSourceArr[i] != null
					&& !"".equals(twSourceArr[i].trim())
				)
			{
				// 자신의 컨텐츠 정보 생성
				ContentSourceTblDTO favoritContentSource = new ContentSourceTblDTO(); 
				
				favoritContentSource.setMt_no(authInfo.getMt_no());
				favoritContentSource.setRss2_url(twSourceArr[i]);
				favoritContentSource.setSource_kind("02");				// 01:rss,02:트위터
				favoritContentSource.setSource_dtl_kind("");
				favoritContentSource.setSource_owner_kind("03");				// 본인이 직접등록
				favoritContentSource.setSource_login_id("");
				favoritContentSource.setReg_stat("02");
				favoritContentSource.setCreate_dt(sCurrentDate);
				favoritContentSource.setCreate_no(authInfo.getMt_no());
				favoritContentSource.setUpdate_dt(sCurrentDate);
				favoritContentSource.setUpdate_no(authInfo.getMt_no());
				contentSourceParamArr.add(favoritContentSource);
				
			}
		}
		
		
		// 최초 자신의 고리 개설
		contentService.addMemContent(contentSourceParamArr, contentTitleParam , branchTtno);
		
		this.contentTitle = contentTitleParam;
		
		return  returnVal;
	
	}

	/**
	 * 컨텐츠를 수정할 화면
	 * 
	 * @return
	 * @throws Exception
	 */
	public String EditForm	 () throws Exception 
	{
		String returnVal = "DEFAULT";

		
		log.debug("contentEditForm 시작");
		/*
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");

		String tt_no = this.getParam("tt_no");
		
		// 회원테이블 조회용 객체 생성
		ContentTitleTblDTO	contentTitleList		= new ContentTitleTblDTO();
		ContentSourceTblDTO	contentSource			= new ContentSourceTblDTO();
		ContentService 		contentService 			= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		
		log.debug("tt_no:" +tt_no);
		contentTitleList.setTt_no(tt_no);
		contentTitleList.setMt_no(authInfo.getMt_no());
		
		contentSource.setTt_no(tt_no);
		contentSource.setMt_no(authInfo.getMt_no());
		
		
		// 수정할 타이틀의 내용
		this.contentTitleList = contentService.selectContentTitleList(contentTitleList);
		
		// 수정할 의 내용
		this.contentSourceList = contentService.selectContentSourceTbl(contentSource);
		*/
		
		
		return  returnVal;
	
	}
	
	/**
	 * 컨텐츠를 수정처리
	 * 
	 * @return
	 * @throws Exception
	 */
	public String EditFinish() throws Exception {
		String returnVal = "DEFAULT";
		
		
		log.debug("contentEditFinish 시작");
		/*
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");

		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String tt_no 					= this.getParam("tt_no");
		String validatecontentTitle		= this.getParam("validatecontentTitle");
		String title_name				= this.getParam("title_name");	
		String order_mem_open_yn		= this.getParam("order_mem_open_yn");
		String branch_conf_yn			= this.getParam("branch_conf_yn");
		String order_mem_join_yn		= this.getParam("order_mem_join_yn");
		String order_mem_join_passwd	= this.getParam("order_mem_join_passwd");
		String validatecontentTitleChk 	= "";

		String[] cs_no_arr				= this.getParam("cs_no_arr").split(",");

		// 넘어온 타이틀 값들로 인증문장생성
		validatecontentTitleChk  = title_name 
									+ order_mem_open_yn
									+ branch_conf_yn
									+ order_mem_join_yn
									+ order_mem_join_passwd;
		
		
		// 업무처리할 객체 생성
		ContentService 		contentService 			= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentTitleTblDTO contentTitle = null;					// 수정될 컨텐츠 타이틀 정보
		List<ContentSourceTblDTO> contentSourceList = new ArrayList<ContentSourceTblDTO>(); 	// 수정될 컨텐츠 소스 정보
		
		
		if (log.isDebugEnabled())
		{
			log.debug("validatecontentTitle:"+ validatecontentTitle);
			log.debug("validatecontentTitleChk:" + validatecontentTitleChk);
			
			
		}
		
		//  컨텐츠 타이틀에 변동사항이 있다면 
		if (!validatecontentTitle.equals(validatecontentTitleChk))
		{
			log.debug("-- contenttitle 변경내용 반영 --  tt_no:" + tt_no);
			
			contentTitle  = new ContentTitleTblDTO();
			
			contentTitle.setTt_no(tt_no);
			contentTitle.setTitle_name(title_name);	
			contentTitle.setOrder_mem_open_yn(order_mem_open_yn);
			contentTitle.setBranch_conf_yn(branch_conf_yn);
			contentTitle.setOrder_mem_join_yn(order_mem_join_yn);
			contentTitle.setOrder_mem_join_passwd(order_mem_join_passwd);
			contentTitle.setCreate_dt(sCurrentDate);
			contentTitle.setCreate_no(authInfo.getMt_no());
		}
		
		for (int i=0;i<cs_no_arr.length;i++)
		{
			// 공백으로 들어올수있기 때문에 (ex 1,2,3,4,) 
			if (!"".equals(cs_no_arr[i]))
			{
				String cs_no  = cs_no_arr[i];
				String source_dtl_kind		= this.getParam("source_dtl_kind" + cs_no);
				String source_login_id		= this.getParam("source_login_id" + cs_no);
				String rss2_url				= this.getParam("rss2_url" + cs_no);
				String use_yn				= this.getParam("use_yn" + cs_no);
				String source_owner_kind	= this.getParam("source_owner_kind" + cs_no);
				String validateEditDate		= this.getParam("validateEditDate" + cs_no);
				String validateEditDateChk  = "";
				
				validateEditDateChk = source_dtl_kind
							 			+ source_login_id 
								 		+ rss2_url
								 		+ use_yn;
			 		
				
				//  컨텐츠 소스에 변동사항이 있다면
				if (log.isDebugEnabled()) log.debug("cs_no:" + cs_no);
				if (!validateEditDate.equals(validateEditDateChk))
				{
					log.debug("-- contentSource 변경내용 반영 --  cs_no,use_yn:" + cs_no  + ","  + use_yn);
					ContentSourceTblDTO contentSource  = new ContentSourceTblDTO();
					
					contentSource.setCs_no(cs_no);
					contentSource.setSource_dtl_kind(source_dtl_kind);
					contentSource.setSource_login_id(source_login_id);
					contentSource.setRss2_url(rss2_url);
					contentSource.setUse_yn(use_yn);
					contentSource.setCreate_dt(sCurrentDate);
					contentSource.setCreate_no(authInfo.getMt_no());
					 
					contentSourceList.add(contentSource);
				}
			}
			
		}
		
		contentService.updtaeMemContent(contentSourceList, contentTitle);
		*/
		
		return  returnVal;
	
	}
	
	
	
	/**
	 * 컨텐츠를 리스트를 보여준다.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String DtlList() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("ContentDtlList 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		//if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");

		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String tt_no 			= this.getParam("tt_no");
		String choicePageNum	= CmnUtil.nvl(this.getParam("choicePageNum"),"");
		
		// 페이지 설정이 없으면 1페이지함
		if ("".equals(choicePageNum)) choicePageNum = "1";
				
 
		// 업무처리할 객체 생성
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentDtlTblDTO 	contentDtl 		= new ContentDtlTblDTO();
		
		contentDtl.setTt_no(tt_no);
		contentDtl.setChoicePageNum(NumUtil.toInt(choicePageNum));
		
		// 해당 컨텐츠 타이틀정보 및 글상세정보
		this.contentTitle =  contentService.selectContentDtlPageList(contentDtl);
		
		
		return  returnVal;
		
	}

	
	/**
	 * 해당 게시물 내용을 확인한다.
	 * 
	 * 게시물의 내용을 보기 위해서는 
	 * 1. 게시물이 자신의 소유 이거나
	 * 2. 해당게시물 소유주가  글고리 타사용자에게 공개를 했거나 (order_mem_open_yn: Y)
	 * 3. 읽을려는 사용자가 해당 글고리의 참여자 (해당글고리 content_title_tbl가 관리하는   content_source_tbl에 글을 읽을려는 사람이 소속되어있어야 한다.)
	 * @return
	 * @throws Exception
	 */
	public String DtlView() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("ContentDtlDtlView 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());

		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String cd_no 			= this.getParam("cd_no");
		String tt_no 			= this.getParam("tt_no");
		String mt_no 			= authInfo.getMt_no();
		
		ContentService 		contentService 			= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentDtlTblDTO contentDtl = new ContentDtlTblDTO();
		
		contentDtl.setMt_no(mt_no);
		contentDtl.setCt_no(cd_no);
		contentDtl.setTt_no(tt_no);
		
		contentDtl.setHit_flag("+1");
		
		
		// 해당 페이지를 볼수있는 권한이 없다면 볼수있는 방법을 안내한다.
		if (!contentService.isContentDtlViewAuth(contentDtl)) 
		{
			
			ContentTitleTblDTO contentTitleParam = new ContentTitleTblDTO();
			
			contentTitleParam.setTt_no(tt_no);		
			this.contentTitle = contentService.selectContentTitleList(contentTitleParam).get(0);
			
			 returnVal = "CONTENT_READ_AUTH";
		}
		else
		{
		// 페이지에 출력될 값 설정
		contentDtl.setMt_no("");			// 상세내용 조회시 현재 로그인 사용자의 번호는 필요없다.
		contentDtl.setTt_no("");			// 다른 브랜치에도 읽을수 있기 때문에 tt_no는 제외하고 cd_no만 넘긴다.
		this.contentTitle = contentService.selectContentDtlView(contentDtl);
		}
		
					
		return  returnVal;
		
	}
	
	/**
	 * 해당 게시물 내용을 수정한다.
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String DtlEditForm() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("ContentDtlEditForm 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentDtlTblDTO 	contentDtl 		= new ContentDtlTblDTO();
		
		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String cd_no 			= CmnUtil.nvl(this.getParam("cd_no"));
		String mt_no 			= authInfo.getMt_no();
		
		// 기존의 글을 수정할때 자신의 기존글의내용을 가져온다.
		if ( !"".equals(cd_no))
		{
			contentDtl.setCd_no(cd_no);
			contentDtl.setMt_no(mt_no);
			
			this.contentTitle = contentService.selectContentDtlView(contentDtl);
		}
		
				
		return  returnVal;
		
	}
	
	/**
	 * 해당 게시물 목록에 새로운글을 등록한다.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String DtlEditFinish() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("contentDtlEditFinish 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		String tt_no 			= this.getParam("tt_no");
		String newFlag			= this.getParam("newFlag");
		String cd_no			= this.getParam("cd_no");
		String content_title	= this.getParam("content_title");
		String ir1				= this.getParam("ir1");
		String categories		= this.getParam("categories");
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		
		
		//신규 등록일때
		if ("N".equals(newFlag))
		{
			ContentDtlTblDTO contentDtlParam = new ContentDtlTblDTO();
			
			contentDtlParam.setTt_no(tt_no);
			contentDtlParam.setMt_no(authInfo.getMt_no());
			contentDtlParam.setAuthor_nm(authInfo.getMem_nm());
			contentDtlParam.setContent_title(content_title);
			contentDtlParam.setContent_desc(ir1);
			contentDtlParam.setSource_kind("00");		// 00:자체생산 소스
			contentDtlParam.setCategories(categories);			
			contentDtlParam.setUse_yn("Y");				// 사용여부
			contentDtlParam.setCreate_dt(sCurrentDate);
			contentDtlParam.setCreate_no(authInfo.getMt_no());
			
			contentService.insertContentDtl(contentDtlParam);
			
		}
		//기존글 업데이트 일때
		else if ("U".equals(newFlag))
		{
			ContentDtlTblDTO contentDtlParam = new ContentDtlTblDTO();
			
			contentDtlParam.setCd_no(cd_no);
			contentDtlParam.setMt_no(authInfo.getMt_no());
			contentDtlParam.setContent_title(content_title);
			contentDtlParam.setContent_desc(ir1);
			contentDtlParam.setCategories(categories);
			contentDtlParam.setCreate_dt(sCurrentDate);
			contentDtlParam.setCreate_no(authInfo.getMt_no());
			
			contentService.updateContentDtl(contentDtlParam);
			
		}
		//기존글 삭제 일때
		else if ("D".equals(newFlag))
		{
			ContentDtlTblDTO contentDtlParam = new ContentDtlTblDTO();
			
			contentDtlParam.setCd_no(cd_no);
			contentDtlParam.setMt_no(authInfo.getMt_no());
			contentDtlParam.setUse_yn("N");
			contentDtlParam.setCreate_dt(sCurrentDate);
			contentDtlParam.setCreate_no(authInfo.getMt_no());
			
			contentService.updateContentDtl(contentDtlParam);
		}
				
		return  returnVal;
		
	}

	
	/**
	 * 브랜치 생성을 위한 화면
	 * 
	 * @return
	 * @throws Exception
	 */
	public String BranchTitleForm() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("BranchTitleForm 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		String tt_no 			= this.getParam("tt_no");
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
	
		ContentTitleTblDTO contentTitleParam = new ContentTitleTblDTO();
		
		
		// branch 를 원하는 content title의 정보
		contentTitleParam.setTt_no(tt_no);		
		this.contentTitle = contentService.selectContentTitleList(contentTitleParam).get(0);
		
		// 잔신이 소유한 content title의 정보
		contentTitleParam =  new ContentTitleTblDTO();
		contentTitleParam.setMt_no(authInfo.getMt_no());
		this.contentTitleList = contentService.selectContentTitleList(contentTitleParam);
		
			
		
		return  returnVal;
		
	}

	/**
	 * 브랜치 생성& 처리결과
	 * 
	 * @return
	 * @throws Exception
	 */
	public String BranchTitleFinish() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("BranchTitleFinish 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		String tt_no 			= this.getParam("tt_no");
		String refTtNo			= this.getParam("refTtNo");
		
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		
		if ("".equals(tt_no) || "".equals(refTtNo) ) throw new Exception("대상및 획득할 컨텐츠 타이틀 값을 알 수 없습니다.");
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentBranchDTO contentBranchParam = new ContentBranchDTO();
		
		contentBranchParam.setTt_no(tt_no);					// 브랜치 획득할 컨텐츠 타이틀
		contentBranchParam.setMt_no(authInfo.getMt_no());	// 회원번호
		contentBranchParam.setOrgBranchTtNo(refTtNo);		// 브랜치 대상 컨텐츠 타이틀
		contentBranchParam.setCreate_dt(sCurrentDate);
		contentBranchParam.setCreate_no(authInfo.getMt_no());
		
		contentService.insertContentNewBranch(contentBranchParam);
		
			
		return  returnVal;
		
	}
	

	/**
	 * 선택한 컨텐츠 타이틀에 가입을 위한  위한  가입화면
	 * 
	 * @return
	 * @throws Exception
	 */
	public String JoinMemForm() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("JoinMemForm 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		String tt_no 			= this.getParam("tt_no");
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentTitleTblDTO 	contentTitleParam = new ContentTitleTblDTO();
		ContentJoinMemDTO	contentJoinMemParam = new ContentJoinMemDTO();
		
		contentTitleParam.setTt_no(tt_no);
		contentJoinMemParam.setTt_no(tt_no);
		contentJoinMemParam.setMt_no(authInfo.getMt_no());
		
		this.contentTitle  		= contentService.selectContentTitleList(contentTitleParam).get(0);
		this.contentJoinMemList = contentService.selectContentJoinMemList(contentJoinMemParam);
			
		return  returnVal;
		
	}

	/**
	 * 타이틀참여 & 처리결과
	 * 
	 * @return
	 * @throws Exception
	 */
	public String JoinMemFinish() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("JoinMemFinish 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		String tt_no 							= this.getParam("tt_no");
		String order_mem_join_passwd 			= this.getParam("join_passwd");
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentTitleTblDTO	contentTitleParam	= new ContentTitleTblDTO();
		
		contentTitleParam.setTt_no(tt_no);					// 참여원하는 컨텐츠 타이틀의 번호
		contentTitleParam.setOrder_mem_join_passwd(order_mem_join_passwd);
		//contentTitleParam.setMt_no(authInfo.getMt_no());
		contentTitleParam.setCreate_dt(sCurrentDate);
		contentTitleParam.setCreate_no(authInfo.getMt_no());
		
		this.contentTitle = contentService.addContentJoin(contentTitleParam);
			
		return  returnVal;
		
	}



	/**
	 * 선택한 컨텐츠 타이틀에 나의 컨텐츠 리소스를 스스로 소속 시키기 위한 화면
	 * 
	 * @return
	 * @throws Exception
	 */
	public String JoinContForm() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("contentJoinContFrom 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		String tt_no 			= this.getParam("tt_no");
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		
		
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentTitleTblDTO 	contentTitleParam = new ContentTitleTblDTO();
		ContentJoinMemDTO	contentJoinMemParam = new ContentJoinMemDTO();
		ContentBranchDTO	contentBranchParam = new ContentBranchDTO();
		
	
		contentJoinMemParam.setTt_no(tt_no);				// 참여를 원하는 tt_no
		contentJoinMemParam.setMt_no(authInfo.getMt_no());	// 자신의 회원번호
		
		
		
		// 내가 여기 컨텐츠에 소속될 권한이 있는지 check(참여는 참여를 원하는 컨텐츠에 join mem 여야만 가능함.
		this.contentJoinMemList = contentService.selectContentJoinMemList(contentJoinMemParam);
		
		// 내가 가지고 있는 컨텐츠 타이틀 목록
		contentTitleParam.setMt_no(authInfo.getMt_no());
		this.contentTitleList = contentService.selectContentTitleList(contentTitleParam);
		
		// 내가 참여를 원하는 컨텐츠타이틀의 브랜치목록을 가져온다.(jsp에서  기존에내가 등록한정보가 있는지 내가 등록가능한지 분류한다)
		contentBranchParam.setTt_no(tt_no);
		contentBranchParam.setMt_no(authInfo.getMt_no());
		this.contentBranchList = contentService.selectContentBranchSelfJoinList(contentBranchParam);
		
		// 내가 참여를 원하는 컨텐츠 타이틀의 정보
		contentTitleParam  = new ContentTitleTblDTO();
		contentTitleParam.setTt_no(tt_no);
		this.contentTitle  = contentService.selectContentTitleList(contentTitleParam).get(0);
		
		return  returnVal;
		
	}

	/**	
	 * 선택한 컨텐츠 타이틀에 나의 컨텐츠 리소스를 스스로 소속 시키기 위한 화면 참여 & 처리결과
	 * 
	 * @return
	 * @throws Exception
	 */
	public String JoinContFinish() throws Exception {
		String returnVal = "DEFAULT";
		
		log.debug("contentJoinContFinish 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");
		
		
		String   joinWantTtNo	= this.getParam("joinWantTtNo");	// 참여를 원한는 컨텐츠 번호
		String   myContStr 		= this.getParam("MyContStr");		// 내가 가진 컨텐츠 목록
		String   branchStr 		= this.getParam("BranchStr");		// 참여를 원하는 나의  컨텐츠 목록
		
		String[] myContList = myContStr.split(",");
		String[] branchList = branchStr.split(","); 
		
		String sCurrentDate 	= DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜

		log.debug("joinWantTtNo:" + joinWantTtNo);
		log.debug("branchList.length:" + branchList.length);
		
		// 참여를 원하는 컨텐츠(joinWantTtNo)에 선택한 나의 컨텐츠목록(branchList)을 참여시킨다. 
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		List<ContentBranchDTO> joinWantbranchList = null; 
		ContentBranchDTO 	addContentBranchParam 	= new ContentBranchDTO(); 
		ContentBranchDTO 	delContentBranchParam	= new ContentBranchDTO();
		ContentBranchDTO	contentBranchParam 		= new ContentBranchDTO();

		List<String> addTtNos =  new ArrayList<String>();
		List<String> delTtNos =  new ArrayList<String>();
		
		String joinWantMtNo		= "";		// 조인을 원하는 tt의 소유자의 mt_no
		
		
		// 내가 참여를 원하는 컨텐츠타이틀의 브랜치 정보에  소속된어 있는 나의 컨텐츠 타이틀 정보를 가져온다.
		contentBranchParam.setTt_no(joinWantTtNo);
		contentBranchParam.setMt_no(authInfo.getMt_no());
		joinWantbranchList = contentService.selectContentBranchSelfJoinList(contentBranchParam);
		
		// 참여원하는 ct 브랜치의 소유자정보 저장
		if (joinWantbranchList.size() > 0) joinWantMtNo = joinWantbranchList.get(0).getMt_no();
		
				
		//	참여를 원하는 컨텐츠가 기존의 목록에 존재하지 않는다면 신규 추가건 
		for (String ttNo : branchList )
		{
			boolean isExists = false;
			
			// 수정불가능한것일때에는 tt 번호가 : 뒤에 붙어온다.
			if (ttNo.indexOf("dontMove:") >= 0)
			{
				String[] tempStr = ttNo.split(":");
				ttNo = tempStr[1];
			}
			
			for (ContentBranchDTO  cb : joinWantbranchList)
			{
				if (ttNo.equals(cb.getBelong_tt_no())) isExists = true;
			}
			
			if (!isExists) addTtNos.add(ttNo);
			
		}
		
		
		//	참여하지않는 목록에컨텐츠가 기존의 목록에 존재한다면 삭제건 
		for (String ttNo : myContList )
		{
			boolean isExists = false;
			
			for (ContentBranchDTO  cb : joinWantbranchList)
			{
				if (ttNo.equals(cb.getBelong_tt_no())) isExists = true;
			}
			
			if (isExists) delTtNos.add(ttNo);
		}
		
		// 참여원하는 컨텐츠 타이틀 정보
		addContentBranchParam.setTt_no(joinWantTtNo);
		addContentBranchParam.setMt_no(joinWantMtNo);
		addContentBranchParam.setOrgBranchTtNos((String[])addTtNos.toArray(new String[addTtNos.size()]));
		addContentBranchParam.setCreate_no(authInfo.getMt_no());
		addContentBranchParam.setCreate_dt(sCurrentDate);
		
		// 삭제워하는 컨텐츠 타이틀 정보
		delContentBranchParam.setTt_no(joinWantTtNo);
		delContentBranchParam.setMt_no(joinWantMtNo);
		delContentBranchParam.setOrgBranchTtNos((String[])delTtNos.toArray(new String[delTtNos.size()]));
		delContentBranchParam.setCreate_no(authInfo.getMt_no());
		delContentBranchParam.setCreate_dt(sCurrentDate);
		
		if (log.isDebugEnabled())
		{
			log.debug("addContentBranchParam:" + DebugUtil.DebugBo(addContentBranchParam));
			log.debug("delContentBranchParam:" + DebugUtil.DebugBo(delContentBranchParam));
			
		}
		
		contentService.insertContentBranchSelfJoin(addContentBranchParam,delContentBranchParam);
		
		
		
			
		return  returnVal;
		
	}


	/**	
	 * 선택한 컨텐츠 타이틀에 나의 컨텐츠 리소스를 스스로 소속 시키기 위한 화면 참여 & 처리결과
	 * 
	 * @return
	 * @throws Exception
	 */
	public String FeedRss() throws Exception 
	{
		String returnVal = "DEFAULT";
		
		log.debug("ContentFeedRss 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		//if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");

		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String tt_no 			= this.getParam("tt_no");
		String choicePageNum	= CmnUtil.nvl(this.getParam("choicePageNum"),"");
		
		// 페이지 설정이 없으면 1페이지함
		if ("".equals(choicePageNum)) choicePageNum = "1";
				
 
		// 업무처리할 객체 생성
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentDtlTblDTO 	contentDtl 		= new ContentDtlTblDTO();
		
		contentDtl.setTt_no(tt_no);
		contentDtl.setPageRowCount(50);		// rss는 50개의 row로 출력한다.
		contentDtl.setChoicePageNum(NumUtil.toInt(choicePageNum));
		
		// 해당 목록의 rss feed 생성
		this.syndFeed = contentService.makeFeedRss(contentDtl);
		
		return  returnVal;
		
	}
	
	/**	
	 * 선택한 컨텐츠 타이틀에 나의 컨텐츠 리소스를 스스로 소속 시키기 위한 화면 참여 & 처리결과
	 * 
	 * @return
	 * @throws Exception
	 */
	public String feedTwitter() throws Exception 
	{
		String returnVal = "DEFAULT";
		
		log.debug("ContentDtlList 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		//if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");

		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String tt_no 			= this.getParam("tt_no");
		String choicePageNum	= CmnUtil.nvl(this.getParam("choicePageNum"),"");
		
		// 페이지 설정이 없으면 1페이지함
		if ("".equals(choicePageNum)) choicePageNum = "1";
				
 
		// 업무처리할 객체 생성
		ContentService 		contentService 	= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentDtlTblDTO 	contentDtl 		= new ContentDtlTblDTO();
		
		contentDtl.setTt_no(tt_no);
		contentDtl.setChoicePageNum(NumUtil.toInt(choicePageNum));
		
		// 해당 컨텐츠 타이틀정보 및 글상세정보
		this.contentTitle =  contentService.selectContentDtlPageList(contentDtl);
		
		
		return  returnVal;
		
	}

	

	/**
	 * 컨텐츠를 등록할  화면 step1
	 * 
	 * @return
	 * @throws Exception
	 */
	public String RegSetp1() throws Exception 
	{
		String returnVal = "DEFAULT";

		log.debug("contentRegSetp1 시작");
		
		return  returnVal;
	
	}
	

	/**
	 * 컨텐츠를 등록할  화면 step1
	 * 
	 * @return
	 * @throws Exception
	 */
	public String RegSetp2() throws Exception 
	{
		String returnVal = "DEFAULT";

		log.debug("contentRegSetp2 시작");
		
		return  returnVal;
	
	}


	/**
	 * 고리 기본정보 관리
	 * 
	 * @return
	 * @throws Exception
	 */
	public String TitleMngForm() throws Exception 
	{
		String returnVal = "DEFAULT";

		log.debug("TitleMngForm 시작");
		
		String tt_no 			= this.getParam("tt_no");
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		
		ContentTitleListTblDAO 	contentTitleDAO 	= (ContentTitleListTblDAO)ServiceFactory.createService(ContentTitleListTblDAOImpl.class);
		ContentSourceTblDAO 	contentSourceTblDAO = (ContentSourceTblDAO)ServiceFactory.createService(ContentSourceTblDAOImpl.class);
		
		contentTitleDAO.initSql();
		contentSourceTblDAO.initSql();
		
		ContentTitleTblDTO 		contentTitle		= new ContentTitleTblDTO();
		ContentSourceTblDTO		contentSource		= new ContentSourceTblDTO();
		
		contentTitle.setTt_no(tt_no);
		contentTitle.setMt_no(authInfo.getMt_no());
		
		contentSource.setTt_no(tt_no);
		contentSource.setMt_no(authInfo.getMt_no());
		
		// 고리 기본정보
		this.contentTitleList = contentTitleDAO.selectContentTitleListTbl(contentTitle);
		log.debug("TitleMngForm 1");
		
		// 기존에  등록된 글타래 정보
		this.contentSourceList =  contentSourceTblDAO.selectContentSourceTbl(contentSource);
		log.debug("TitleMngForm 2");
		
		// 참여고리 정보(고리 등록/수정),고리참여자 정보 ,고리 홍보현황,고리전파현황
		//log.debug("contentTitle:" + socialUp.common.util.DebugUtil.DebugBo(contentTitle));
		this.getRequest().setAttribute("ETC_CNT", contentTitleDAO.selectGoreeeEtcCnt(contentTitle));
		log.debug("TitleMngForm 3");
		
		return  returnVal;
	
	}
	


	/**
	 * 고리 기본정보 관리 수정처리
	 * 
	 * @return
	 * @throws Exception
	 */
	public String UpdateFinish() throws Exception {
		String returnVal = "DEFAULT";
		
		
		log.debug("UpdateFinish 시작");
		
		AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
		if (!authInfo.isAuth()) throw new Exception("로그인한 사용자가 아닙니다.");

		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		String tt_no 					= this.getParam("tt_no");
		String title_desc 				= this.getParam("title_desc");
		String order_mem_open_yn		= this.getParam("order_mem_open_yn");
		String branch_conf_yn			= this.getParam("branch_conf_yn");
		String order_mem_join_yn		= this.getParam("order_mem_join_yn");
		String order_mem_join_metd		= this.getParam("order_mem_join_metd");
		String order_mem_join_passwd	= this.getParam("order_mem_join_passwd");
		String[] cs_no_arr				= this.getParams("cs_no");
		String[] cs_reg_stat_arr		= this.getParams("cs_reg_stat");
		String rss_source				= this.getParam("rss_source");
		String tw_source				= this.getParam("tw_source");
		String[] rssSourceArr  	= null;	// rss 글타래 목록
		String[] twSourceArr  	= null; // 트위터 글타래 목록			
		
		//Rss 수집목록 생성
		rss_source = rss_source.replaceAll("\r\n", ",");
		rssSourceArr = rss_source.split(",");
		
		//트위터 수집목록 생성
		tw_source = tw_source.replaceAll("\r\n", ",");
		twSourceArr = tw_source.split(",");
		
		
		
		// 업무처리할 객체 생성
		ContentService 		contentService 			= (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
		ContentTitleTblDTO contentTitle = null;					// 수정될 컨텐츠 타이틀 정보
		List<ContentSourceTblDTO> contentSourceList = new ArrayList<ContentSourceTblDTO>(); 	// 수정될 컨텐츠 소스 정보
		
		
		if (log.isDebugEnabled())
		{
			log.debug("order_mem_join_metd:" + order_mem_join_metd);
		}
		
		//  컨텐츠 타이틀에 변동사항이 있다면 
		{
			log.debug("-- contenttitle 변경내용 반영 --  tt_no:" + tt_no);
			
			contentTitle  = new ContentTitleTblDTO();
			
			contentTitle.setTt_no(tt_no);
			contentTitle.setTitle_desc(title_desc);
			contentTitle.setOrder_mem_open_yn(order_mem_open_yn);
			contentTitle.setBranch_conf_yn(branch_conf_yn);
			contentTitle.setOrder_mem_join_yn(order_mem_join_yn);
			contentTitle.setOrder_mem_join_metd(order_mem_join_metd);
			contentTitle.setOrder_mem_join_passwd(order_mem_join_passwd);
			contentTitle.setCreate_dt(sCurrentDate);
			contentTitle.setCreate_no(authInfo.getMt_no());
		}
		
		// 기존에 등록된 컨텐츠 소스는 삭제일때만 상태 업데이트 처리
		for (int i=0;i<cs_no_arr.length;i++)
		{
			// 공백으로 들어올수있기 때문에 (ex 1,2,3,4,) 
			if (!"".equals(cs_no_arr[i]) && "04".equals(cs_reg_stat_arr[i]))
			{
				//  컨텐츠 소스에 변동사항이 있다면
				if (log.isDebugEnabled()) log.debug("cs_no:" + cs_no_arr[i]);
				{
					ContentSourceTblDTO contentSource  = new ContentSourceTblDTO();
					
					contentSource.setCs_no(cs_no_arr[i]);
					contentSource.setUse_yn("N");
					contentSource.setReg_stat("04");
					contentSource.setCreate_dt(sCurrentDate);
					contentSource.setCreate_no(authInfo.getMt_no());
					contentSource.setProcKind("U");
					 
					contentSourceList.add(contentSource);
				}
			}
		}

		// RSS 글타래 등록
		for (int i=0;i<rssSourceArr.length;i++)
		{
			// 관심컨텐츠가 공백이 아니라면 등록한다.
			if (rssSourceArr[i] != null
					&& !"".equals(rssSourceArr[i].trim())
				)
			{
				// 자신의 컨텐츠 정보 생성
				ContentSourceTblDTO favoritContentSource = new ContentSourceTblDTO(); 
				
				favoritContentSource.setTt_no(tt_no);
				favoritContentSource.setMt_no(authInfo.getMt_no());
				favoritContentSource.setRss2_url( rssSourceArr[i].trim());
				favoritContentSource.setSource_kind("01");				// 01:rss,02:트위터
				favoritContentSource.setSource_dtl_kind("");
				favoritContentSource.setSource_owner_kind("03");				// 본인이 직접등록
				favoritContentSource.setSource_login_id("");
				favoritContentSource.setReg_stat("02");
				favoritContentSource.setRead_fail_count("0");
				favoritContentSource.setCreate_dt(sCurrentDate);
				favoritContentSource.setCreate_no(authInfo.getMt_no());
				favoritContentSource.setUpdate_dt(sCurrentDate);
				favoritContentSource.setUpdate_no(authInfo.getMt_no());
				favoritContentSource.setProcKind("I");
				contentSourceList.add(favoritContentSource);
				
			}
		}
		
		// 트위터 글타래 등록
		for (int i=0;i<twSourceArr.length;i++)
		{
			// 관심컨텐츠가 공백이 아니라면 등록한다.
			if (twSourceArr[i] != null
					&& !"".equals(twSourceArr[i].trim())
				)
			{
				// 자신의 컨텐츠 정보 생성
				ContentSourceTblDTO favoritContentSource = new ContentSourceTblDTO(); 
				
				favoritContentSource.setTt_no(tt_no);
				favoritContentSource.setMt_no(authInfo.getMt_no());
				favoritContentSource.setRss2_url(twSourceArr[i].trim());
				favoritContentSource.setSource_kind("02");				// 01:rss,02:트위터
				favoritContentSource.setSource_dtl_kind("");
				favoritContentSource.setSource_owner_kind("03");				// 본인이 직접등록
				favoritContentSource.setSource_login_id("");
				favoritContentSource.setReg_stat("02");
				favoritContentSource.setRead_fail_count("0");
				favoritContentSource.setCreate_dt(sCurrentDate);
				favoritContentSource.setCreate_no(authInfo.getMt_no());
				favoritContentSource.setUpdate_dt(sCurrentDate);
				favoritContentSource.setUpdate_no(authInfo.getMt_no());
				favoritContentSource.setProcKind("I");
				contentSourceList.add(favoritContentSource);
				
			}
		}
		
		

		contentService.updtaeMemContent(contentSourceList, contentTitle);

		
		return  returnVal;
	
	}
	
	/**
	 * 댓글 목록을 가져온다.
	 * 
	 * @param memtbldto
	 * @throws Exception 
	 */
	public String DtlCommentListAjax() throws Exception
	{
		log.debug("DtlCommentListAjax 시작");
		String returnVal = "DEFAULT";
		ContentDtlCommentDTO contentDtlCommentParam = new ContentDtlCommentDTO();
		
		String cd_no = this.getParam("cd_no");
		String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
		JSONObject 	retJson = new JSONObject();	
		
		retJson.put("result_code","00");
		retJson.put("result_msg","성공");
		contentDtlCommentParam.setCd_no(cd_no);

		try
		{
		// 컨텐츠 서비스 객체 생성
		ContentService contentService = (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
				
		
		this.contentDtlCommentList = contentService.selectContentDtlCommentList(contentDtlCommentParam);
		 
/*
		 
		 Map<String, Object> map = new HashMap<String, Object>();
		 JSONArray jsonArray = JSONArray.fromObject(contentDtlCommentList);
		 //map.put("commentList", jsonArray);			// beanList 라는 배열의 묶음으로처리
		  
		 JSONObject jsonObject = JSONObject.fromObject(map);
		 retJson.put("retValList", jsonArray); 	// beanList 묶음을 넘긴다.
		} catch(Exception e)
		{
			e.printStackTrace();
			retJson.put("result_code","-1");
			retJson.put("result_msg",e.getMessage());
		}
		
		flushString(retJson.toString());
*/
		
	
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return returnVal; 
		
	}

	/**
	 * 댓글을 등록한다.
	 * 
	 * @param memtbldto
	 * contentDtlCommentParam.cd_no
	 * contentDtlCommentParam.comment
	 * 
	 * @throws Exception 
	 */
	public String InsertComment() throws Exception
	{
		log.debug("InsertComment 시작");
		
		JSONObject 	retJson = new JSONObject();
		try 
		{
	
			String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
			AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
			String cd_no 	= this.getParam("cd_no");
			String comment 	= this.getParam("comment");
			String passwd 	= this.getParam("passwd");
			String remoteIp = this.getRequest().getRemoteAddr(); 
			
			
			retJson.put("result_code","00");
			retJson.put("result_msg","성공");
			
			
			// 컨텐츠 서비스 객체 생성
			ContentService contentService = (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
			ContentDtlCommentDTO contentDtlComment = new ContentDtlCommentDTO(); 
			
			contentDtlComment.setMt_no(authInfo.getMt_no());
			contentDtlComment.setCd_no(cd_no);
			contentDtlComment.setCreate_dt(sCurrentDate);
			contentDtlComment.setCreate_no(authInfo.getMt_no());
			contentDtlComment.setUse_yn("Y");
			contentDtlComment.setComment(comment);
			contentDtlComment.setCreate_ip(remoteIp);
			contentDtlComment.setPasswd(passwd);

	   	    contentService.insertContentDtlCommentList(contentDtlComment);


		}
		catch(Exception e)
		{

			e.printStackTrace();
			retJson.put("result_code","-1");
			retJson.put("result_msg",e.getMessage());
		}
		
		flushString(retJson.toString());
		
		
		return null;
	}
	
	/**
	 * 댓글을 수정한다.
	 * 
	 * @param memtbldto
	 * contentDtlCommentParam.cd_no
	 * contentDtlCommentParam.comment
	 * 
	 * @throws Exception 
	 */
	public String EditComment() throws Exception
	{
		log.debug("EditComment 시작");
		
		JSONObject 	retJson = new JSONObject();
		try 
		{
	
			String sCurrentDate = DateTime.getFormatString("yyyyMMddHHmmss"); // 현재날짜
			AuthInfo authInfo =  AuthService.getAuthInfo(this.getRequest(), this.getResponse());
			String cdc_no 	= this.getParam("cdc_no");
			String comment 	= this.getParam("comment");
			String passwd 	= this.getParam("passwd");
			String editType = this.getParam("editType");
			String cd_no = this.getParam("cd_no");
			String remoteIp = this.getRequest().getRemoteAddr(); 
			
			
			retJson.put("result_code","00");
			retJson.put("result_msg","성공");
			
			
			// 컨텐츠 서비스 객체 생성
			ContentService contentService = (ContentService)ServiceFactory.createService(ContentServiceImpl.class);
			ContentDtlCommentDTO contentDtlComment = new ContentDtlCommentDTO(); 
			
			contentDtlComment.setMt_no(authInfo.getMt_no());
			contentDtlComment.setCdc_no(cdc_no);
			contentDtlComment.setCreate_dt(sCurrentDate);
			contentDtlComment.setCreate_no(authInfo.getMt_no());
			contentDtlComment.setComment(comment);
			contentDtlComment.setCreate_ip(remoteIp);
			contentDtlComment.setPasswd(passwd);
			contentDtlComment.setCd_no(cd_no);
			
			
			if ("D".equals(editType)) contentDtlComment.setUse_yn("N");
	   	    
			// 수정된 내용이 적용되었다면 update_cnt 는 1이 된다.
			retJson.put("update_cnt",contentService.updateContentDtlComment(contentDtlComment));


		}
		catch(Exception e)
		{
			e.printStackTrace();
			retJson.put("result_code","-1");
			retJson.put("result_msg",e.getMessage());
		}
		
		flushString(retJson.toString());
		
		
		return null;
	}
	
	
	
}
