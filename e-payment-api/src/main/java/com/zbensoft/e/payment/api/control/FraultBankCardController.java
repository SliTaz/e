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
import com.zbensoft.e.payment.api.service.api.FraultBankCardService;
import com.zbensoft.e.payment.db.domain.FraultBankCard;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultBankCard")
@RestController

public class FraultBankCardController {

	@Autowired
	FraultBankCardService fraultBankCardService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_FRAUD_BC_Q')")
	@ApiOperation(value = "Query FraultBankCard,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultBankCard>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String bankCard, @RequestParam(required = false) Integer suspiciouType,
			@RequestParam(required = false) Integer sourceType, @RequestParam(required = false) Integer status, @RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultBankCard addr = new FraultBankCard();
		addr.setBankCard(bankCard);
		addr.setSuspiciouType(suspiciouType);
		addr.setStatus(status);
		addr.setSourceType(sourceType);
		;
		addr.setRemark(remark);

		int count = fraultBankCardService.count(addr);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultBankCard>>(new ArrayList<FraultBankCard>(), HttpRestStatus.NOT_FOUND);
		}

		List<FraultBankCard> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultBankCardService.selectPage(addr);
		} else {
			list = fraultBankCardService.selectPage(addr);
		}

		return new ResponseRestEntity<List<FraultBankCard>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_FRAUD_BC_Q')")
	@ApiOperation(value = "Query FraultBankCard", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultBankCard> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultBankCard addr = fraultBankCardService.selectByPrimaryKey(id);
		if (addr == null) {
			return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultBankCard>(addr, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_BC_E')")
	@ApiOperation(value = "Add FraultBankCard", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody FraultBankCard addr, BindingResult result, UriComponentsBuilder ucBuilder) {
		addr.setCreateTime(PageHelperUtil.getCurrentDate());
		// 校验
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultBankCardService.insert(addr);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, addr, CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultBankCard/{id}").buildAndExpand(addr.getBankCard()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_BC_E')")
	@ApiOperation(value = "Edit fraultBankCard", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultBankCard> updatetask(@PathVariable("id") String id, @RequestBody FraultBankCard addr) {

		FraultBankCard type = fraultBankCardService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setBankCard(addr.getBankCard());
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());

		fraultBankCardService.updateByPrimaryKey(type);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultBankCard>(type, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_BC_E')")
	@ApiOperation(value = "Edit Part FraultBankCard", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultBankCard> updatetaskSelective(@PathVariable("id") String id, @RequestBody FraultBankCard addr) {

		FraultBankCard type = fraultBankCardService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NOT_FOUND);
		}
		fraultBankCardService.updateByPrimaryKeySelective(type);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultBankCard>(type, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_BC_E')")
	@ApiOperation(value = "Delete FraultBankCard", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultBankCard> deletetask(@PathVariable("id") String id) {

		FraultBankCard task = fraultBankCardService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NOT_FOUND);
		}

		fraultBankCardService.deleteByPrimaryKey(id);
		// 删除日志开始
		FraultBankCard delBean = new FraultBankCard();
		delBean.setBankCard(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.FRAULT_MANAGEMENT);
		// 删除日志结束
		return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_FRAUD_BC_E')")
	@ApiOperation(value = "enable the specified FraultBankCard", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultBankCard> enableTask(@PathVariable("id") String id) {

		FraultBankCard task = fraultBankCardService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		task.setStatus(0);
		fraultBankCardService.updateByPrimaryKey(task);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_FRAUD_BC_E')")
	@ApiOperation(value = "enable the specified FraultBankCard", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultBankCard> disableSysUser(@PathVariable("id") String id) {

		FraultBankCard task = fraultBankCardService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		task.setStatus(1);
		fraultBankCardService.updateByPrimaryKey(task);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultBankCard>(HttpRestStatus.OK);
	}
}
