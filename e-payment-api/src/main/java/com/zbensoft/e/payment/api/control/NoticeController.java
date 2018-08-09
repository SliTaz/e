package com.zbensoft.e.payment.api.control;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.NoticeService;
import com.zbensoft.e.payment.db.domain.Notice;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RequestMapping(value = "/notice")
@RestController
public class NoticeController {
	private static final Logger log = LoggerFactory.getLogger(NoticeController.class);
	@Autowired
	NoticeService noticeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@Value("${upload.notice.tmp.folder}")
	private String UPLOAD_FILE_FOLDER;
	
	

	// 查询通知，支持分页
	@PreAuthorize("hasRole('R_NOTICE_Q')")
	@ApiOperation(value = "Query Notice，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<Notice>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String title, @RequestParam(required = false) Integer noticeLevel,
			@RequestParam(required = false) Integer noticeStatus,
			@RequestParam(required = false) String releaseTimeStart,
			@RequestParam(required = false) String releaseTimeEnd, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		Notice notice = new Notice();
		notice.setNoticeId(id);
		notice.setTitle(title);
		
			notice.setNoticeLevel(noticeLevel);
	
			notice.setNoticeStatus(noticeStatus);
		
		notice.setReleaseTimeStart(releaseTimeStart);
		notice.setReleaseTimeEnd(releaseTimeEnd);
		List<Notice> list = new ArrayList<Notice>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = noticeService.selectPage(notice);

		} else {
			list = noticeService.selectPage(notice);
		}

		int count = noticeService.count(notice);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<Notice>>(new ArrayList<Notice>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<Notice>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询通知
	@PreAuthorize("hasRole('R_NOTICE_Q')")
	@ApiOperation(value = "Query Notice", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<Notice> selectByPrimaryKey(@PathVariable("id") String id) {
		Notice notice = noticeService.selectByPrimaryKey(id);
		if (notice == null) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<Notice>(notice, HttpRestStatus.OK);
	}

	// 新增通知
	@PreAuthorize("hasRole('R_NOTICE_E')")
	@ApiOperation(value = "Add Notice", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody Notice notice, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		if ("".equals(notice.getContentImgUrl())) {
			return new ResponseRestEntity<Void>(HttpRestStatus.IMAGE_UPLOAD_FAILED_ERROR,
					localeMessageSourceService.getMessage("common.img.unload.fail"));
		}

		
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		notice.setNoticeId(IDGenerate.generateCommOne(IDGenerate.NOTICE));
		notice.setCreateTime(PageHelperUtil.getCurrentDate());
		notice.setCreateUser(userDetails.getUsername());
		if(notice.getNoticeStatus().intValue()==MessageDef.NOTICE_STATUE.PUBLISH){
			notice.setReleaseUser(userDetails.getUsername());
		}
		

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		noticeService.insert(notice);
		//新增日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, notice,CommonLogImpl.NOTICE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/notice/{id}").buildAndExpand(notice.getNoticeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,
				localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改通知信息
	@PreAuthorize("hasRole('R_NOTICE_E')")
	@ApiOperation(value = "Edit Notice", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<Notice> update(@PathVariable("id") String id, @Valid @RequestBody Notice notice,
			BindingResult result) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Notice currentNotice = noticeService.selectByPrimaryKey(id);

		if (currentNotice == null) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND,
					localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		
		if ("".equals(notice.getContentImgUrl())) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.IMAGE_UPLOAD_FAILED_ERROR,
					localeMessageSourceService.getMessage("common.img.unload.fail"));
		}

		currentNotice.setTitle(notice.getTitle());
		currentNotice.setNoticeLevel(notice.getNoticeLevel());
		currentNotice.setType(notice.getType());
		currentNotice.setContent(notice.getContent());
		if(notice.getContentImgUrl()!=null&&notice.getContentImgUrl().length()>0&&!notice.getContentImgUrl().equals(currentNotice.getContentImgUrl())){
			boolean isDelete=deleteOldImg(currentNotice.getContentImgUrl());
			if(isDelete){
				currentNotice.setContentImgUrl(notice.getContentImgUrl());
			}
		}
		if(currentNotice.getContentImgUrl()==null||"".equals(currentNotice.getContentImgUrl())){
			currentNotice.setContentImgUrl(notice.getContentImgUrl());
		}
		currentNotice.setNoticeStatus(notice.getNoticeStatus());
		currentNotice.setIsViewSys(notice.getIsViewSys());
		currentNotice.setIsViewConsumer(notice.getIsViewConsumer());
		currentNotice.setIsViewMerchan(notice.getIsViewMerchan());
		currentNotice.setIsViewGov(notice.getIsViewGov());
		currentNotice.setIsViewApp(notice.getIsViewApp());
		if(notice.getNoticeStatus().intValue()==MessageDef.NOTICE_STATUE.PUBLISH){
			currentNotice.setReleaseUser(userDetails.getUsername());
		}
		currentNotice.setReleaseTime(notice.getReleaseTime());
		currentNotice.setCreateUser(notice.getCreateUser());
		currentNotice.setCreateTime(notice.getCreateTime());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Notice>(currentNotice, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		noticeService.updateByPrimaryKey(currentNotice);
		
		
		//修改日志
     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentNotice,CommonLogImpl.NOTICE);
		return new ResponseRestEntity<Notice>(currentNotice, HttpRestStatus.OK,
				localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	private boolean deleteOldImg(String contentImgUrl) {
		if (contentImgUrl != null && contentImgUrl.length() > 0) {
			File deleteFile = new File(UPLOAD_FILE_FOLDER + contentImgUrl);
			if (deleteFile.exists() && deleteFile.isFile()) {
				return deleteFile.delete();
			}
		}
		return false;
	}

	// 修改部分通知信息
	@PreAuthorize("hasRole('R_NOTICE_E')")
	@ApiOperation(value = "Edit Part Notice", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<Notice> updateSelective(@PathVariable("id") String id, @RequestBody Notice notice) {

		Notice currentNotice = noticeService.selectByPrimaryKey(id);

		if (currentNotice == null) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND);
		}
		
		if ("".equals(notice.getContentImgUrl())) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.IMAGE_UPLOAD_FAILED_ERROR,
					localeMessageSourceService.getMessage("common.img.unload.fail"));
		}

		currentNotice.setNoticeId(id);
		currentNotice.setTitle(notice.getTitle());
		currentNotice.setNoticeLevel(notice.getNoticeLevel());
		currentNotice.setType(notice.getType());
		currentNotice.setContent(notice.getContent());
		if(notice.getContentImgUrl()!=null&&notice.getContentImgUrl().length()>0&&!notice.getContentImgUrl().equals(currentNotice.getContentImgUrl())){
			boolean isDelete=deleteOldImg(currentNotice.getContentImgUrl());
			if(isDelete){
				currentNotice.setContentImgUrl(notice.getContentImgUrl());
			}
		}
		if(currentNotice.getContentImgUrl()!=null||"".equals(currentNotice.getContentImgUrl())){
			currentNotice.setContentImgUrl(notice.getContentImgUrl());
		}
		currentNotice.setNoticeStatus(notice.getNoticeStatus());
		currentNotice.setIsViewSys(notice.getIsViewSys());
		currentNotice.setIsViewConsumer(notice.getIsViewConsumer());
		currentNotice.setIsViewMerchan(notice.getIsViewMerchan());
		currentNotice.setIsViewGov(notice.getIsViewGov());
		currentNotice.setIsViewApp(notice.getIsViewApp());
		currentNotice.setReleaseUser(notice.getReleaseUser());
		currentNotice.setReleaseTime(notice.getReleaseTime());
		currentNotice.setCreateUser(notice.getCreateUser());
		currentNotice.setCreateTime(notice.getCreateTime());
		noticeService.updateByPrimaryKeySelective(currentNotice);
		//修改日志
	     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentNotice,CommonLogImpl.NOTICE);
		return new ResponseRestEntity<Notice>(currentNotice, HttpRestStatus.OK);
	}

	// 删除指定通知
	@PreAuthorize("hasRole('R_NOTICE_E')")
	@ApiOperation(value = "Delete Notice", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<Notice> delete(@PathVariable("id") String id) {

		Notice notice = noticeService.selectByPrimaryKey(id);
		if (notice == null) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND);
		}
		if(notice.getContentImgUrl()!=null&&notice.getContentImgUrl().length()>0){
			deleteOldImg(notice.getContentImgUrl());
		}
		noticeService.deleteByPrimaryKey(id);
		//删除日志开始
		Notice delBean = new Notice();
		delBean.setNoticeId(id);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.NOTICE);
		//删除日志结束
		return new ResponseRestEntity<Notice>(HttpRestStatus.NO_CONTENT);
	}

	// 查询通知，支持分页
	@ApiOperation(value = "Query Notice，Support paging", notes = "")
	@RequestMapping(value = "/getAppNotice", method = RequestMethod.GET)
	public ResponseRestEntity<List<Notice>> getAppNotice(@RequestParam(required = true) String type,
			@RequestParam(required = true) String start, @RequestParam(required = true) String length) {
		Notice notice = new Notice();
		if (MessageDef.NOTICE_VIEW_TYPE.CONSUMER_STRING.equals(type)) {
			notice.setIsViewConsumer(MessageDef.VIEW.VIEW);
		} else if (MessageDef.NOTICE_VIEW_TYPE.MERCHANT_STRING.equals(type)) {
			notice.setIsViewMerchan(MessageDef.VIEW.VIEW);
		} else if (MessageDef.NOTICE_VIEW_TYPE.GOV_STRING.equals(type)) {
			notice.setIsViewGov(MessageDef.VIEW.VIEW);
		} else if (MessageDef.NOTICE_VIEW_TYPE.SYS_STRING.equals(type)) {
			notice.setIsViewSys(MessageDef.VIEW.VIEW);
		} else if (MessageDef.NOTICE_VIEW_TYPE.APP_STRING.equals(type)) {
			notice.setIsViewApp(MessageDef.VIEW.VIEW);
		} else {
			return new ResponseRestEntity<List<Notice>>(HttpRestStatus.NOT_EXIST, "平台类型不存在");
		}
		notice.setNoticeStatus(MessageDef.NOTICE_STATUE.PUBLISH);

		List<Notice> list = new ArrayList<Notice>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = noticeService.selectPage(notice);

		} else {
			list = noticeService.selectPage(notice);
		}

		int count = noticeService.count(notice);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<Notice>>(new ArrayList<Notice>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<Notice>>(list, HttpRestStatus.OK, count, count);
	}
	
	@ApiOperation(value = "getNoticeRecords", notes = "")
	@RequestMapping(value = "/getNoticeRecords", method = RequestMethod.POST)
	public ResponseRestEntity<List<Notice>> getNoticeRecords(@RequestBody Notice notice) {
		
		List<Notice> list = new ArrayList<Notice>();
		int count = noticeService.count(notice);
		
		if (count == 0) {
			return new ResponseRestEntity<List<Notice>>(new ArrayList<Notice>(), HttpRestStatus.NOT_FOUND);
		}
		
		PageHelper.startPage(1, 10);
		list = noticeService.selectNewestRecords(notice);
		return new ResponseRestEntity<List<Notice>>(list, HttpRestStatus.OK, count, count);
	}
	
	@ApiOperation(value = "getNoticeById", notes = "")
	@RequestMapping(value = "/getNoticeById", method = RequestMethod.POST)
	public ResponseRestEntity<Notice> getNoticeById(@RequestParam(required = false) String id) {
		Notice notice = noticeService.selectByPrimaryKey(id);
		if (notice == null) {
			return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<Notice>(notice, HttpRestStatus.OK);
		
	}
	

	
	 @ApiOperation(value = "Upload files", notes = "Upload files test")
	    @RequestMapping(value = "/upload/{times}", produces = { "application/json" }, method =RequestMethod.POST )
	    public ResponseRestEntity<Object> upload(@ApiParam(value = "logo", required = true) @RequestParam(value = "logo", required = true) MultipartFile logo,HttpServletRequest request,
	    		@PathVariable("times") String times){
		 try {
			if (logo != null) {
				String fileRelName = logo.getOriginalFilename();
				//去除后缀名
				String fileRelNameNew = fileRelName .substring(0,fileRelName .lastIndexOf("."));
				File filePath = new File(UPLOAD_FILE_FOLDER);
				if (!filePath.exists() && !filePath.isDirectory()) {
					filePath.mkdirs();
				}
				File destFile = new File(UPLOAD_FILE_FOLDER + times+fileRelNameNew);
				logo.transferTo(destFile);
				return new ResponseRestEntity<Object>(HttpRestStatus.OK);
			}
			return new ResponseRestEntity<Object>(HttpRestStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("file upload error", e);
			return new ResponseRestEntity<Object>(HttpRestStatus.NOT_FOUND);
		} 
	}
	 
	// 修改通知信息
		@PreAuthorize("hasRole('R_NOTICE_E')")
		@ApiOperation(value = "Edit Notice", notes = "")
		@RequestMapping(value = "/removeImgs/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<Notice> removeImgs(@PathVariable("id") String id) {

			Notice currentNotice = noticeService.selectByPrimaryKey(id);

			if (currentNotice == null) {
				return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND,
						localeMessageSourceService.getMessage("common.update.not_found.message"));
			}
			if(currentNotice.getContentImgUrl()!=null&&!"".equals(currentNotice.getContentImgUrl())){
				File deleteFile = new File(UPLOAD_FILE_FOLDER + currentNotice.getContentImgUrl());
				if(deleteFile.exists()){
					deleteFile.delete();
				}else{
					return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND,
							localeMessageSourceService.getMessage("common.update.not_found.message"));
				}
			}else{
				return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND,
						localeMessageSourceService.getMessage("common.update.not_found.message"));
			}

			currentNotice.setContentImgUrl("");
			noticeService.updateByPrimaryKey(currentNotice);
			//修改日志
		     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentNotice,CommonLogImpl.NOTICE);
			return new ResponseRestEntity<Notice>(currentNotice, HttpRestStatus.OK,
					localeMessageSourceService.getMessage("common.update.ok.message"));
		}
	


		// 用户发布
		@PreAuthorize("hasRole('R_NOTICE_E')")
		@ApiOperation(value = "enable the specified notice", notes = "")
		@RequestMapping(value = "/published/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<Notice> unpublishedNotice(@PathVariable("id") String id) {

			Notice notice = noticeService.selectByPrimaryKey(id);
			if (notice == null) {
				return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND);
			}
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			notice.setReleaseUser(userDetails.getUsername());
			notice.setNoticeStatus(2);
			notice.setReleaseTime(PageHelperUtil.getCurrentDate());
			noticeService.updateByPrimaryKey(notice);
			//修改日志
		     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_PUBLISHED, notice,CommonLogImpl.NOTICE);
			return new ResponseRestEntity<Notice>(HttpRestStatus.OK);
		}


		@PreAuthorize("hasRole('R_NOTICE_E')")
		@ApiOperation(value = "enable the specified notice", notes = "")
		@RequestMapping(value = "/revoked/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<Notice> revokedNotice(@PathVariable("id") String id) {

			Notice notice = noticeService.selectByPrimaryKey(id);
			if (notice == null) {
				return new ResponseRestEntity<Notice>(HttpRestStatus.NOT_FOUND);
			}
			
			notice.setNoticeStatus(3);
			
			noticeService.updateByPrimaryKey(notice);
			//修改日志
		     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_REVOKED, notice,CommonLogImpl.NOTICE);
			return new ResponseRestEntity<Notice>(HttpRestStatus.OK);
		}
		
		@ApiOperation(value = "download notice picture", notes = "")
		@RequestMapping(value = "/get/noticepicture/path/{path}", method = RequestMethod.GET)
		public ResponseEntity<InputStreamResource> getNoticeImageByPath(@PathVariable("path") String path) {
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
				headers.add("Pragma", "no-cache");
				headers.add("Expires", "0");
				headers.add("charset", "utf-8");
				// 设置下载文件名
				String fileName = path;
				fileName = URLEncoder.encode(fileName, "UTF-8");
				File readPath = new File(UPLOAD_FILE_FOLDER);
				if (!readPath.exists() && !readPath.isDirectory()) {
					readPath.mkdir();
				}

				InputStreamResource resource = new InputStreamResource(new FileInputStream(UPLOAD_FILE_FOLDER + path));
				return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("image/jpeg")).body(resource);
			} catch (Exception e) {
				log.error("", e);
			}

			return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
		}
}