package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.util.UriComponentsBuilder;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.PayAppGatewayService;
import com.zbensoft.e.payment.api.service.api.PayAppService;
import com.zbensoft.e.payment.db.domain.PayApp;
import com.zbensoft.e.payment.db.domain.PayAppGateway;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/payApp")
@RestController
public class PayAppController {
	@Autowired
	PayAppService payAppService;

	@Autowired
	PayAppGatewayService payAppGatewayService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询支付应用，支持分页
	@PreAuthorize("hasRole('R_PAYMENT_A_Q')")
	@ApiOperation(value = "Query PayApp，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<PayApp>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String name, @RequestParam(required = false) String userId,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		PayApp payApp = new PayApp();
		payApp.setPayAppId(id);
		payApp.setName(name);
		payApp.setUserId(userId);
		payApp.setStatus(status);

		List<PayApp> list = new ArrayList<PayApp>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = payAppService.selectPage(payApp);

		} else {
			list = payAppService.selectPage(payApp);
		}

		int count = payAppService.count(payApp);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayApp>>(new ArrayList<PayApp>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<PayApp>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询支付应用
	@PreAuthorize("hasRole('R_PAYMENT_A_Q')")
	@ApiOperation(value = "Query PayApp", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<PayApp> selectByPrimaryKey(@PathVariable("id") String id) {
		PayApp payApp = payAppService.selectByPrimaryKey(id);
		if (payApp == null) {
			return new ResponseRestEntity<PayApp>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<PayApp>(payApp, HttpRestStatus.OK);
	}

	// 新增支付应用
	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Add PayApp", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody PayApp payApp, BindingResult result, UriComponentsBuilder ucBuilder) {

		payApp.setPayAppId(IDGenerate.generateCommOne(IDGenerate.PAY_APP));

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		if (payApp.getUserId() == null) {
			payApp.setUserId("");
		}
		payAppService.insert(payApp);

		// 关系表新增Start
		if (payApp.getPayGatewayId() != null) {
			String[] idStr = payApp.getPayGatewayId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				PayAppGateway payAppGateway = new PayAppGateway();
				payAppGateway.setPayAppId(payApp.getPayAppId());
				payAppGateway.setPayGatewayId(idStr[i]);
				payAppGatewayService.insert(payAppGateway);
			}
		}
		// 关系表新增End
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, payApp, CommonLogImpl.PAYMENT);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/payApp/{id}").buildAndExpand(payApp.getPayAppId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改支付应用信息
	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Edit PayApp", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayApp> update(@PathVariable("id") String id, @Valid @RequestBody PayApp payApp, BindingResult result) {

		PayApp currentPayApp = payAppService.selectByPrimaryKey(id);
		List<PayAppGateway> payAppGatewayList = payAppGatewayService.selectByPayAppId(id);

		if (currentPayApp == null) {
			return new ResponseRestEntity<PayApp>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentPayApp.setName(payApp.getName());
		currentPayApp.setUserId(payApp.getUserId());
		currentPayApp.setStatus(payApp.getStatus());
		currentPayApp.setRemark(payApp.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<PayApp>(currentPayApp, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		payAppService.updateByPrimaryKey(currentPayApp);

		// 关系表修改start(逻辑:先删除，然后增加)
		if (payAppGatewayList != null && payAppGatewayList.size() > 0) {
			for (PayAppGateway gateway : payAppGatewayList) {
				payAppGatewayService.deleteByPrimaryKey(gateway);
			}
		}

		if (payApp.getPayGatewayId() != null && !"".equals(payApp.getPayGatewayId())) {
			String[] idStr = payApp.getPayGatewayId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				PayAppGateway payAppGateway = new PayAppGateway();
				payAppGateway.setPayAppId(payApp.getPayAppId());
				payAppGateway.setPayGatewayId(idStr[i]);
				payAppGatewayService.insert(payAppGateway);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayApp, CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayApp>(currentPayApp, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分支付应用信息
	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Edit Part PayApp", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<PayApp> updateSelective(@PathVariable("id") String id, @RequestBody PayApp payApp) {

		PayApp currentPayApp = payAppService.selectByPrimaryKey(id);

		if (currentPayApp == null) {
			return new ResponseRestEntity<PayApp>(HttpRestStatus.NOT_FOUND);
		}
		currentPayApp.setPayAppId(id);
		currentPayApp.setName(payApp.getName());
		currentPayApp.setUserId(payApp.getUserId());
		currentPayApp.setStatus(payApp.getStatus());
		currentPayApp.setRemark(payApp.getRemark());
		payAppService.updateByPrimaryKeySelective(payApp);//

		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, payApp, CommonLogImpl.PAYMENT);

		return new ResponseRestEntity<PayApp>(currentPayApp, HttpRestStatus.OK);
	}

	// 删除指定支付应用
	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "Delete PayApp", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<PayApp> delete(@PathVariable("id") String id) {

		PayApp payApp = payAppService.selectByPrimaryKey(id);
		List<PayAppGateway> list = payAppGatewayService.selectByPayAppId(id);
		if (payApp == null) {
			return new ResponseRestEntity<PayApp>(HttpRestStatus.NOT_FOUND);
		}

		payAppService.deleteByPrimaryKey(id);
		// 关系表删除start
		if (list != null && list.size() > 0) {
			for (PayAppGateway gateway : list) {
				payAppGatewayService.deleteByPrimaryKey(gateway);
			}
		}
		// 关系表删除end
		// 删除日志开始
		PayApp delBean = new PayApp();
		delBean.setPayAppId(id);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.PAYMENT);
		// 删除日志结束
		return new ResponseRestEntity<PayApp>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "enable the specified payApp", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayApp> enablePayApp(@PathVariable("id") String id) {

		PayApp payApp = payAppService.selectByPrimaryKey(id);
		if (payApp == null) {
			return new ResponseRestEntity<PayApp>(HttpRestStatus.NOT_FOUND);
		}

		payApp.setStatus(1);
		payAppService.updateByPrimaryKey(payApp);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, payApp, CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayApp>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_PAYMENT_A_E')")
	@ApiOperation(value = "enable the specified payApp", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayApp> disablePayApp(@PathVariable("id") String id) {

		PayApp payApp = payAppService.selectByPrimaryKey(id);
		if (payApp == null) {
			return new ResponseRestEntity<PayApp>(HttpRestStatus.NOT_FOUND);
		}

		payApp.setStatus(0);
		payAppService.updateByPrimaryKey(payApp);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, payApp, CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayApp>(HttpRestStatus.OK);
	}

}