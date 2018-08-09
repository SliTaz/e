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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultIdNumberService;
import com.zbensoft.e.payment.db.domain.FraultIdNumber;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultIdNumber")
@RestController
public class FraultIdNumberController {
	@Autowired
	FraultIdNumberService fraultIdNumberService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_FRAUD_ID_Q')")
	@ApiOperation(value = "Query FraultIdNumber,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultIdNumber>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String idNumber, @RequestParam(required = false) Integer suspiciouType,
			@RequestParam(required = false) Integer sourceType, @RequestParam(required = false) Integer status, @RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		FraultIdNumber addr = new FraultIdNumber();
		addr.setIdNumber(idNumber);
		addr.setSuspiciouType(suspiciouType);
		addr.setStatus(status);
		addr.setSourceType(sourceType);
		;
		addr.setRemark(remark);

		int count = fraultIdNumberService.count(addr);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultIdNumber>>(new ArrayList<FraultIdNumber>(), HttpRestStatus.NOT_FOUND);
		}

		List<FraultIdNumber> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultIdNumberService.selectPage(addr);
		} else {
			list = fraultIdNumberService.selectPage(addr);
		}

		return new ResponseRestEntity<List<FraultIdNumber>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_FRAUD_ID_Q')")
	@ApiOperation(value = "Query Task", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultIdNumber> selectByPrimaryKey(@PathVariable("id") String id) {

		id = CommonFun.getRelVid(id);
		FraultIdNumber addr = fraultIdNumberService.selectByPrimaryKey(id);
		if (addr == null) {
			return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultIdNumber>(addr, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_ID_E')")
	@ApiOperation(value = "Add FraultIdNumber", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody FraultIdNumber addr, BindingResult result, UriComponentsBuilder ucBuilder) {
		addr.setCreateTime(PageHelperUtil.getCurrentDate());
		addr.setIdNumber(CommonFun.getRelVid(addr.getIdNumber()));

		// 校验
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultIdNumberService.insert(addr);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, addr, CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultIdNumber/{id}").buildAndExpand(addr.getIdNumber()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_ID_E')")
	@ApiOperation(value = "Edit FraultIdNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultIdNumber> updatetask(@PathVariable("id") String id, @RequestBody FraultIdNumber addr) {
		id = CommonFun.getRelVid(id);
		addr.setIdNumber(CommonFun.getRelVid(addr.getIdNumber()));
		FraultIdNumber type = fraultIdNumberService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setIdNumber(addr.getIdNumber());
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());

		fraultIdNumberService.updateByPrimaryKey(type);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultIdNumber>(type, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_ID_E')")
	@ApiOperation(value = "Edit Part FraultIdNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultIdNumber> updatetaskSelective(@PathVariable("id") String id, @RequestBody FraultIdNumber addr) {
		id = CommonFun.getRelVid(id);
		FraultIdNumber type = fraultIdNumberService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NOT_FOUND);
		}
		fraultIdNumberService.updateByPrimaryKeySelective(type);

		return new ResponseRestEntity<FraultIdNumber>(type, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_ID_E')")
	@ApiOperation(value = "Delete FraultIdNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultIdNumber> deletetask(@PathVariable("id") String id) {
		id = CommonFun.getRelVid(id);
		FraultIdNumber task = fraultIdNumberService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NOT_FOUND);
		}

		fraultIdNumberService.deleteByPrimaryKey(id);
		// 删除日志开始
		FraultIdNumber delBean = new FraultIdNumber();
		delBean.setIdNumber(id);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.FRAULT_MANAGEMENT);
		// 删除日志结束
		return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_FRAUD_ID_E')")
	@ApiOperation(value = "enable the specified FraultIdNumber", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultIdNumber> enableTask(@PathVariable("id") String id) {
		id = CommonFun.getRelVid(id);
		FraultIdNumber task = fraultIdNumberService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		task.setStatus(0);
		fraultIdNumberService.updateByPrimaryKey(task);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_FRAUD_ID_E')")
	@ApiOperation(value = "enable the specified FraultIdNumber", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultIdNumber> disableSysUser(@PathVariable("id") String id) {
		id = CommonFun.getRelVid(id);
		FraultIdNumber task = fraultIdNumberService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		task.setStatus(1);
		fraultIdNumberService.updateByPrimaryKey(task);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task, CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultIdNumber>(HttpRestStatus.OK);
	}
}
