package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ApplicationServerService;
import com.zbensoft.e.payment.db.domain.ApplicationServer;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/applicationServer")
@RestController
public class ApplicationServerController {
	@Autowired
	ApplicationServerService applicationServerService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 接口统计数据
	@PreAuthorize("hasRole('R_SYSTEM_AS_Q')")
	@ApiOperation(value = "Query ApplicationServer，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ApplicationServer>> selectPage(@RequestParam(required = false) String applicationServerCode, @RequestParam(required = false) String name, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ApplicationServer applicationServer = new ApplicationServer();
		applicationServer.setApplicationServerCode(applicationServerCode);
		applicationServer.setName(name);
		List<ApplicationServer> list = new ArrayList<ApplicationServer>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = applicationServerService.selectPage(applicationServer);

		} else {
			list = applicationServerService.selectPage(applicationServer);
		}

		int count = applicationServerService.count(applicationServer);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ApplicationServer>>(new ArrayList<ApplicationServer>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ApplicationServer>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SYSTEM_AS_Q')")
	@ApiOperation(value = "Query ApplicationServer", notes = "")
	@RequestMapping(value = "/{applicationServerCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ApplicationServer> selectByPrimaryKey(@PathVariable("applicationServerCode") String applicationServerCode) {
		ApplicationServer applicationServer = applicationServerService.selectByPrimaryKey(applicationServerCode);
		if (applicationServer == null) {
			return new ResponseRestEntity<ApplicationServer>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ApplicationServer>(applicationServer, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SYSTEM_AS_E')")
	@ApiOperation(value = "Add ApplicationServer", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtaskType(@RequestBody ApplicationServer applicationServer, BindingResult result, UriComponentsBuilder ucBuilder) {
		// 校验
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		if (applicationServerService.isExist(applicationServer)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		applicationServerService.insert(applicationServer);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, applicationServer, CommonLogImpl.SYSTEM_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/applicationServer/{applicationServerCode}").buildAndExpand(applicationServer.getApplicationServerCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SYSTEM_AS_E')")
	@ApiOperation(value = "Edit TaskType", notes = "")
	@RequestMapping(value = "{applicationServerCode}", method = RequestMethod.PUT)
	public ResponseRestEntity<ApplicationServer> updatetaskType(@PathVariable("applicationServerCode") String applicationServerCode, @RequestBody ApplicationServer applicationServer) {

		ApplicationServer ApplicationServer = applicationServerService.selectByPrimaryKey(applicationServerCode);

		if (ApplicationServer == null) {
			return new ResponseRestEntity<ApplicationServer>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		ApplicationServer.setApplicationServerCode(applicationServer.getApplicationServerCode());
		ApplicationServer.setName(applicationServer.getName());
		ApplicationServer.setIpAddr(applicationServer.getIpAddr());
		ApplicationServer.setLastTime(applicationServer.getLastTime());
		ApplicationServer.setRemark(applicationServer.getRemark());
		applicationServerService.updateByPrimaryKey(ApplicationServer);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, ApplicationServer, CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<ApplicationServer>(ApplicationServer, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SYSTEM_AS_E')")
	@ApiOperation(value = "Edit Part TaskType", notes = "")
	@RequestMapping(value = "{applicationServerCode}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ApplicationServer> updatetaskTypeSelective(@PathVariable("applicationServerCode") String applicationServerCode, @RequestBody ApplicationServer applicationServer) {

		ApplicationServer type = applicationServerService.selectByPrimaryKey(applicationServerCode);

		if (type == null) {
			return new ResponseRestEntity<ApplicationServer>(HttpRestStatus.NOT_FOUND);
		}
		applicationServerService.updateByPrimaryKeySelective(applicationServer);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, applicationServer, CommonLogImpl.BI_REPORT);
		return new ResponseRestEntity<ApplicationServer>(type, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SYSTEM_AS_E')")
	@ApiOperation(value = "Delete TaskType", notes = "")
	@RequestMapping(value = "/{applicationServerCode}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ApplicationServer> deletetaskType(@PathVariable("applicationServerCode") String applicationServerCode) {

		ApplicationServer applicationServer = applicationServerService.selectByPrimaryKey(applicationServerCode);
		if (applicationServer == null) {
			return new ResponseRestEntity<ApplicationServer>(HttpRestStatus.NOT_FOUND);
		}

		applicationServerService.deleteByPrimaryKey(applicationServerCode);
		// 删除日志开始
		ApplicationServer delBean = new ApplicationServer();
		delBean.setApplicationServerCode(applicationServerCode);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.SYSTEM_MANAGE);
		// 删除日志结束
		return new ResponseRestEntity<ApplicationServer>(HttpRestStatus.NO_CONTENT);
	}
}
