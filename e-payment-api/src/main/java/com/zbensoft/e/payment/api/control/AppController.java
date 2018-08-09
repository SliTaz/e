package com.zbensoft.e.payment.api.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.log.APP_LOG;
import com.zbensoft.e.payment.api.service.api.AppService;
import com.zbensoft.e.payment.db.domain.App;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/app")
@RestController
public class AppController {
	private static final Logger log = LoggerFactory.getLogger(AppController.class);

	@Autowired
	AppService appService;

	@Value("${upload.app.tmp.folder}")
	private String UPLOAD_FILE_FOLDER;

	@Value("${upload.app.tmp.folder.errlog}")
	private String APP_REPORT_LOG_FOLDER;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_APP_Q')")
	@ApiOperation(value = "Query application, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<App>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String name, @RequestParam(required = false) Integer status,
			@RequestParam(required = false) String version, @RequestParam(required = false) Integer type, @RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		App app = new App();
		app.setAppId(id);
		app.setName(name);
		app.setType(type);
		app.setVersion(version);
		app.setStatus(status);
		app.setRemark(remark);
		List<App> list = appService.selectPage(app);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = appService.selectPage(app);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = appService.selectPage(app);
		}

		int count = appService.count(app);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<App>>(new ArrayList<App>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<App>>(list, HttpRestStatus.OK, count, count);

	}

	// 查询应用
	@PreAuthorize("hasRole('R_APP_Q')")
	@ApiOperation(value = "Query application", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<App> selectByPrimaryKey(@PathVariable("id") String id) {
		App app = appService.selectByPrimaryKey(id);
		if (app == null) {
			return new ResponseRestEntity<App>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<App>(app, HttpRestStatus.OK);
	}

	// 新增应用
	@PreAuthorize("hasRole('R_APP_E')")
	@ApiOperation(value = "Add application", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createApp(@Valid @RequestBody App app, BindingResult result, UriComponentsBuilder ucBuilder) {
		app.setAppId(IDGenerate.generateCommOne(IDGenerate.APP));
		app.setCreateTime(PageHelperUtil.getCurrentDate());
		if (appService.isAppExist(app)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("app.create.conflict.message"));
		}

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		appService.insert(app);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, app, CommonLogImpl.APP_UPDATE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/app/{id}").buildAndExpand(app.getAppId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("app.create.created.message"));

	}

	// 修改应用信息
	@PreAuthorize("hasRole('R_APP_E')")
	@ApiOperation(value = "Modify the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<App> updateApp(@PathVariable("id") String id, @Valid @RequestBody App app, BindingResult result) {

		App currentApp = appService.selectByPrimaryKey(id);

		if (currentApp == null) {
			return new ResponseRestEntity<App>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("app.update.not_found.message"));
		}
		currentApp.setAppId(app.getAppId());
		currentApp.setName(app.getName());
		currentApp.setType(app.getType());
		currentApp.setPlatformType(app.getPlatformType());
		currentApp.setFileUrl(app.getFileUrl());
		currentApp.setVersion(app.getVersion());
		currentApp.setStatus(app.getStatus());

		currentApp.setRemark(app.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<App>(currentApp, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		appService.updateByPrimaryKey(currentApp);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentApp, CommonLogImpl.APP_UPDATE);
		return new ResponseRestEntity<App>(currentApp, HttpRestStatus.OK, localeMessageSourceService.getMessage("app.update.ok.message"));
	}

	// 修改部分应用信息
	@PreAuthorize("hasRole('R_APP_E')")
	@ApiOperation(value = "Modify part of the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<App> updateAppSelective(@PathVariable("id") String id, @RequestBody App app) {

		App currentApp = appService.selectByPrimaryKey(id);

		if (currentApp == null) {
			return new ResponseRestEntity<App>(HttpRestStatus.NOT_FOUND);
		}
		app.setAppId(id);
		appService.updateByPrimaryKeySelective(app);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, app, CommonLogImpl.APP_UPDATE);
		return new ResponseRestEntity<App>(currentApp, HttpRestStatus.OK);
	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_APP_E')")
	@ApiOperation(value = "Delete the specified app", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<App> deleteApp(@PathVariable("id") String id) {

		App app = appService.selectByPrimaryKey(id);
		if (app == null) {
			return new ResponseRestEntity<App>(HttpRestStatus.NOT_FOUND);
		}

		appService.deleteByPrimaryKey(id);
		// 删除日志开始
		App delBean = new App();
		delBean.setAppId(id);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.APP_UPDATE);
		// 删除日志结束
		return new ResponseRestEntity<App>(HttpRestStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('R_APP_E')")
	@RequestMapping(value = "/singleUpload", method = RequestMethod.POST)
	public Map<String, Object> singleFileUpload(HttpServletRequest request, @RequestParam("name") String name, @RequestParam("appId") String appId) throws Exception {
		// System.out.println("before name:"+name);

		String filePre = System.currentTimeMillis() + "";
		name = filePre + "_" + name;

		// System.out.println("after name:"+name);
		// System.out.println("consumerGroupId:"+consumerGroupId);

		String path = UPLOAD_FILE_FOLDER;// request.getSession().getServletContext().getRealPath("upload");
		// System.out.println("path:"+path);

		File targetFile = new File(path, name);
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}

		write(path, name, request.getInputStream());

		App updateApp = new App();
		updateApp.setAppId(appId);
		updateApp.setFileUrl(name);
		appService.updateByPrimaryKeySelective(updateApp);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, updateApp, CommonLogImpl.APP_UPDATE);
		String filePath = targetFile.getPath();
		Map<String, Object> result_map = new HashMap<String, Object>();
		result_map.put("filePath", filePath);
		result_map.put("fileName", targetFile.getName());
		return result_map;
	}

	@ApiOperation(value = "download app", notes = "")
	@RequestMapping(value = "/downloadApp", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadAppByPath(@RequestParam(required = true) String fileName) {
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("charset", "utf-8");
			// 设置下载文件名

			fileName = URLEncoder.encode(fileName, "UTF-8");

			headers.add("Content-Disposition", "filename=" + fileName);
			File readPath = new File(UPLOAD_FILE_FOLDER);
			if (!readPath.exists() && !readPath.isDirectory()) {
				readPath.mkdirs();
			}
			FileSystemResource file = new FileSystemResource(UPLOAD_FILE_FOLDER + "/" + fileName);
			if (!file.exists()||file.getFile().isDirectory()) {//修复报错2017-11-28 Chen
				return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
			}

			InputStreamResource resource = new InputStreamResource(file.getInputStream());
			RedisUtil.increment_COUNT_DOWNLOAD_APP();
			return ResponseEntity.ok().headers(headers).contentLength(file.contentLength()).contentType(MediaType.parseMediaType("application/force-download")).body(resource);
		} catch (Exception e) {
			log.error("", e);
		}

		return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
	}

	private void write(String path, String filename, InputStream in) {
		// System.out.println("写入文件");

		File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) {// 若创建文件夹不成功
				// System.out.println("Unable to create external cache directory");
			}
		}

		File targetfile = new File(path + filename);
		OutputStream os = null;
		try {
			os = new FileOutputStream(targetfile);
			int ch = 0;
			while ((ch = in.read()) != -1) {
				os.write(ch);
			}
			os.flush();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				os.close();
				in.close();
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	// 查询应用，支持分页
	@ApiOperation(value = "Query application by noAuth, support paging", notes = "")
	@RequestMapping(value = "/getApp", method = RequestMethod.GET)
	public ResponseRestEntity<List<App>> selectPageNoAuth(@RequestParam(required = true) Integer type, @RequestParam(required = true) Integer platformType, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		App app = new App();
		app.setType(type);
		app.setPlatformType(platformType);
		app.setStatus(MessageDef.PUBLISH.PUBLISH);
		List<App> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = appService.selectPage(app);
		} else {
			list = appService.selectPage(app);
		}

		int count = appService.count(app);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<App>>(new ArrayList<App>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<App>>(list, HttpRestStatus.OK, count, count);

	}

	// @PostMapping("/reportAppErrors")
	// public ResponseRestEntity<Picture> reportAppErrors(@RequestParam("avatar_file") MultipartFile file,
	// RedirectAttributes redirectAttributes, @PathVariable("id") String id) {

	@RequestMapping(value = "/reportAppErrors", method = RequestMethod.POST)
	public Map<String, Object> reportAppErrors(@RequestParam("errFile") MultipartFile errFile, @RequestParam("type") String type) throws Exception {

		String path = APP_REPORT_LOG_FOLDER;// request.getSession().getServletContext().getRealPath("upload");
		// System.out.println("path:"+path);
		File targetFile = new File(path, errFile.getOriginalFilename());
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}

		write(path, errFile.getOriginalFilename(), errFile.getInputStream());

		String filePath = targetFile.getPath();
		Map<String, Object> result_map = new HashMap<String, Object>();
		result_map.put("filePath", filePath);
		result_map.put("fileName", targetFile.getName());
		return result_map;
	}

	@RequestMapping(value = "/reportAppErrorMessage", method = RequestMethod.POST)
	public ResponseRestEntity<?> reportAppErrorMessage(@RequestParam("message") String message) throws Exception {
		APP_LOG.INFO(message);

		return new ResponseRestEntity<String>(HttpRestStatus.OK);
	}

	@ApiOperation(value = "download app", notes = "")
	@RequestMapping(value = "/downloadBuyerAndroidApp", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadBuyerAndroidApp() {
		try {
			String fileName = getLastFileName(MessageDef.APP_USER_TYPE.BUYER_APP, MessageDef.APP_PLATFORM_TYPE.ANDROID);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("charset", "utf-8");
			// 设置下载文件名

			fileName = URLEncoder.encode(fileName, "UTF-8");

			headers.add("Content-Disposition", "filename=" + fileName);
			File readPath = new File(UPLOAD_FILE_FOLDER);
			if (!readPath.exists() && !readPath.isDirectory()) {
				readPath.mkdirs();
			}
			FileSystemResource file = new FileSystemResource(UPLOAD_FILE_FOLDER + "/" + fileName);
			if (!file.exists()) {
				return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
			}

			InputStreamResource resource = new InputStreamResource(file.getInputStream());
			RedisUtil.increment_COUNT_DOWNLOAD_APP();
			return ResponseEntity.ok().headers(headers).contentLength(file.contentLength()).contentType(MediaType.parseMediaType("application/force-download")).body(resource);
		} catch (Exception e) {
			log.error("", e);
		}
		return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
	}

	@ApiOperation(value = "download app", notes = "")
	@RequestMapping(value = "/downloadSellerAndroidApp", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadSellerAndroidApp() {
		try {
			String fileName = getLastFileName(MessageDef.APP_USER_TYPE.SELLER_APP, MessageDef.APP_PLATFORM_TYPE.ANDROID);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("charset", "utf-8");
			// 设置下载文件名

			fileName = URLEncoder.encode(fileName, "UTF-8");

			headers.add("Content-Disposition", "filename=" + fileName);
			File readPath = new File(UPLOAD_FILE_FOLDER);
			if (!readPath.exists() && !readPath.isDirectory()) {
				readPath.mkdirs();
			}
			FileSystemResource file = new FileSystemResource(UPLOAD_FILE_FOLDER + "/" + fileName);
			if (!file.exists()) {
				return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
			}

			InputStreamResource resource = new InputStreamResource(file.getInputStream());
			RedisUtil.increment_COUNT_DOWNLOAD_APP();
			return ResponseEntity.ok().headers(headers).contentLength(file.contentLength()).contentType(MediaType.parseMediaType("application/force-download")).body(resource);
		} catch (Exception e) {
			log.error("", e);
		}
		return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
	}

	private String getLastFileName(int type, int platformType) {
		App app = new App();
		app.setType(type);
		app.setPlatformType(platformType);
		app.setStatus(MessageDef.PUBLISH.PUBLISH);
		List<App> list = null;
		String start = "0";
		String length = "1";
		/*
		 * 第一个参数是第几页；第二个参数是每页显示条数。
		 */
		int pageNum = PageHelperUtil.getPageNum(start, length);
		int pageSize = PageHelperUtil.getPageSize(start, length);
		PageHelper.startPage(pageNum, pageSize);
		list = appService.selectPage(app);

		if (list != null && list.size() > 0) {
			return list.get(0).getFileUrl();
		}
		return null;
	}

}