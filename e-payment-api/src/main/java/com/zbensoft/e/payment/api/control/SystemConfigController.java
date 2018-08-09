package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

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
import com.zbensoft.e.payment.api.service.api.SystemConfigService;
import com.zbensoft.e.payment.db.domain.ApplicationServer;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.SystemConfig;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/systemConfig")
@RestController
public class SystemConfigController {
	@Autowired
	SystemConfigService systemConfigService;
	@Autowired
	ApplicationServerService applicationServerService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SYSTEM_SC_Q')")
	@ApiOperation(value = "Query systemConfigMapper，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SystemConfig>> selectPage(@RequestParam(required = false) String applicationServerCode, @RequestParam(required = false) String code, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		SystemConfig systemConfig = new SystemConfig();
		systemConfig.setApplicationServerCode(applicationServerCode);
		systemConfig.setCode(code);
		List<SystemConfig> list = new ArrayList<SystemConfig>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = systemConfigService.selectPage(systemConfig);

		} else {
			list = systemConfigService.selectPage(systemConfig);
		}

		int count = systemConfigService.count(systemConfig);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SystemConfig>>(new ArrayList<SystemConfig>(), HttpRestStatus.NOT_FOUND);
		}
		List<SystemConfig> listNew = new ArrayList<SystemConfig>();
		for (SystemConfig bean : list) {
			ApplicationServer applicationServer = applicationServerService.selectByPrimaryKey(bean.getApplicationServerCode());
			if (applicationServer != null) {
				bean.setName(applicationServer.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<SystemConfig>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SYSTEM_SC_Q')")
	@ApiOperation(value = "Query SystemConfig", notes = "")
	@RequestMapping(value = "/{applicationServerCode}/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SystemConfig> selectByPrimaryKey(@PathVariable("applicationServerCode") String applicationServerCode, @PathVariable("code") String code) {
		SystemConfig bean = new SystemConfig();
		bean.setApplicationServerCode(applicationServerCode);
		bean.setCode(code);
		SystemConfig consumerCoupon = systemConfigService.selectByPrimaryKey(bean);
		if (consumerCoupon == null) {
			return new ResponseRestEntity<SystemConfig>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SystemConfig>(consumerCoupon, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SYSTEM_SC_E')")
	@ApiOperation(value = "Add SystemConfig", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody SystemConfig systemConfig, BindingResult result, UriComponentsBuilder ucBuilder) {

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		SystemConfig bean = systemConfigService.selectByPrimaryKey(systemConfig);
		if (bean != null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		systemConfigService.insert(systemConfig);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, systemConfig, CommonLogImpl.SYSTEM_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/systemConfig/{code}").buildAndExpand(systemConfig.getCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SYSTEM_SC_E')")
	@ApiOperation(value = "Edit ConsumerCoupon", notes = "")
	@RequestMapping(value = "/{applicationServerCode}/{code}", method = RequestMethod.PUT)
	public ResponseRestEntity<SystemConfig> update(@PathVariable("applicationServerCode") String applicationServerCode, @PathVariable("code") String code, @Valid @RequestBody SystemConfig systemConfig,
			BindingResult result) {
		SystemConfig bean = new SystemConfig();
		bean.setApplicationServerCode(applicationServerCode);
		bean.setCode(code);
		SystemConfig currentConsumerCoupon = systemConfigService.selectByPrimaryKey(bean);

		if (currentConsumerCoupon == null) {
			return new ResponseRestEntity<SystemConfig>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerCoupon.setApplicationServerCode(systemConfig.getApplicationServerCode());
		currentConsumerCoupon.setCode(systemConfig.getCode());
		currentConsumerCoupon.setValue(systemConfig.getValue());
		currentConsumerCoupon.setDefaultValue(systemConfig.getDefaultValue());
		currentConsumerCoupon.setRemark(systemConfig.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SystemConfig>(currentConsumerCoupon, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		systemConfigService.updateByPrimaryKey(currentConsumerCoupon);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerCoupon, CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<SystemConfig>(currentConsumerCoupon, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SYSTEM_SC_E')")
	@ApiOperation(value = "Edit Part SystemConfig", notes = "")
	@RequestMapping(value = "/{applicationServerCode}/{code}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SystemConfig> updateConsumerCouponSelective(@PathVariable("applicationServerCode") String applicationServerCode, @PathVariable("code") String code, @RequestBody SystemConfig systemConfig) {

		SystemConfig bean = new SystemConfig();
		bean.setApplicationServerCode(applicationServerCode);
		bean.setCode(code);
		SystemConfig currentConsumerCoupon = systemConfigService.selectByPrimaryKey(bean);

		if (currentConsumerCoupon == null) {
			return new ResponseRestEntity<SystemConfig>(HttpRestStatus.NOT_FOUND);
		}
		currentConsumerCoupon.setApplicationServerCode(systemConfig.getApplicationServerCode());
		currentConsumerCoupon.setCode(systemConfig.getCode());
		currentConsumerCoupon.setValue(systemConfig.getValue());
		currentConsumerCoupon.setDefaultValue(systemConfig.getDefaultValue());
		currentConsumerCoupon.setRemark(systemConfig.getRemark());
		systemConfigService.updateByPrimaryKeySelective(currentConsumerCoupon);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerCoupon, CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<SystemConfig>(currentConsumerCoupon, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SYSTEM_SC_E')")
	@ApiOperation(value = "Delete SystemConfig", notes = "")
	@RequestMapping(value = "/{applicationServerCode}/{code}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SystemConfig> deleteConsumerCoupon(@PathVariable("applicationServerCode") String applicationServerCode, @PathVariable("code") String code) {
		SystemConfig bean = new SystemConfig();
		bean.setApplicationServerCode(applicationServerCode);
		bean.setCode(code);
		SystemConfig consumerCoupon = systemConfigService.selectByPrimaryKey(bean);
		if (consumerCoupon == null) {
			return new ResponseRestEntity<SystemConfig>(HttpRestStatus.NOT_FOUND);
		}

		systemConfigService.deleteByPrimaryKey(bean);
		// 删除日志开始
		SystemConfig coupo = new SystemConfig();
		coupo.setApplicationServerCode(applicationServerCode);
		coupo.setCode(code);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, coupo, CommonLogImpl.SYSTEM_MANAGE);
		// 删除日志结束
		return new ResponseRestEntity<SystemConfig>(HttpRestStatus.NO_CONTENT);
	}

	// 默认修改
	@PreAuthorize("hasRole('R_SYSTEM_SC_E')")
	@ApiOperation(value = "Default ConsumerCoupon", notes = "")
	@RequestMapping(value = "/value/{applicationServerCode}/{code}", method = RequestMethod.PUT)
	public ResponseRestEntity<SystemConfig> Default(@PathVariable("applicationServerCode") String applicationServerCode, @PathVariable("code") String code, @Valid @RequestBody SystemConfig systemConfig,
			BindingResult result) {
		SystemConfig bean = new SystemConfig();
		bean.setApplicationServerCode(applicationServerCode);
		bean.setCode(code);
		SystemConfig currentConsumerCoupon = systemConfigService.selectByPrimaryKey(bean);

		if (currentConsumerCoupon == null) {
			return new ResponseRestEntity<SystemConfig>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerCoupon.setApplicationServerCode(systemConfig.getApplicationServerCode());
		currentConsumerCoupon.setCode(systemConfig.getCode());
		currentConsumerCoupon.setValue(systemConfig.getDefaultValue());
		currentConsumerCoupon.setDefaultValue(systemConfig.getDefaultValue());
		currentConsumerCoupon.setRemark(systemConfig.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SystemConfig>(currentConsumerCoupon, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		systemConfigService.updateByPrimaryKey(currentConsumerCoupon);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerCoupon, CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<SystemConfig>(currentConsumerCoupon, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

}
