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
import com.zbensoft.e.payment.api.service.api.BankChargeInfoService;
import com.zbensoft.e.payment.db.domain.BankChargeInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/bankChargeInfo")
@RestController
public class BankChargeInfoController {
	@Autowired
	BankChargeInfoService bankChargeInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query bankChargeInfo,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankChargeInfo>> selectPage(@RequestParam(required = false) String refNo,
			@RequestParam(required = false) String chargeDate, @RequestParam(required = false) String chargeResult,
			 @RequestParam(required = false) String channelType ,@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		BankChargeInfo taskType = new BankChargeInfo();
		taskType.setRefNo(refNo);
		taskType.setChargeDate(chargeDate);
		taskType.setChargeResult(chargeResult);
		taskType.setChannelType(channelType);

		int count = bankChargeInfoService.count(taskType);
		if (count == 0) {
			return new ResponseRestEntity<List<BankChargeInfo>>(new ArrayList<BankChargeInfo>(), HttpRestStatus.NOT_FOUND);
		}

		List<BankChargeInfo> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = bankChargeInfoService.selectPage(taskType);
		} else {
			list = bankChargeInfoService.selectPage(taskType);
		}

		return new ResponseRestEntity<List<BankChargeInfo>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query TaskType", notes = "")
	@RequestMapping(value = "/{refNo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<BankChargeInfo> selectByPrimaryKey(@PathVariable("refNo") String refNo) {
		BankChargeInfo TaskType = bankChargeInfoService.selectByPrimaryKey(refNo);
		if (TaskType == null) {
			return new ResponseRestEntity<BankChargeInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<BankChargeInfo>(TaskType, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Add BankChargeInfo", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtaskType(@RequestBody BankChargeInfo taskType,BindingResult result,  UriComponentsBuilder ucBuilder) {
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		taskType.setVid(CommonFun.getRelVid(taskType.getVid()));
		bankChargeInfoService.insert(taskType);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, taskType,CommonLogImpl.TASK);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/bankChargeInfo/{refNo}").buildAndExpand(taskType.getRefNo()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit BankChargeInfo", notes = "")
	@RequestMapping(value = "{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<BankChargeInfo> updatetaskType(@PathVariable("refNo") String refNo, @RequestBody BankChargeInfo taskType) {

		BankChargeInfo type = bankChargeInfoService.selectByPrimaryKey(refNo);

		if (type == null) {
			return new ResponseRestEntity<BankChargeInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setRefNo(taskType.getRefNo());
		type.setVid(CommonFun.getRelVid(taskType.getVid()));
		type.setChannelType(taskType.getChannelType());
		type.setChargeAmount(taskType.getChargeAmount());
		type.setChargeDate(taskType.getChargeDate());
		type.setChargeResult(taskType.getChargeResult());
		type.setCurrencyType(taskType.getCurrencyType());
		bankChargeInfoService.updateByPrimaryKey(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.TASK);
		return new ResponseRestEntity<BankChargeInfo>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit Part BankChargeInfo", notes = "")
	@RequestMapping(value = "{refNo}", method = RequestMethod.PATCH)
	public ResponseRestEntity<BankChargeInfo> updatetaskTypeSelective(@PathVariable("refNo") String refNo, @RequestBody BankChargeInfo taskType) {

		BankChargeInfo type = bankChargeInfoService.selectByPrimaryKey(refNo);

		if (type == null) {
			return new ResponseRestEntity<BankChargeInfo>(HttpRestStatus.NOT_FOUND);
		}
		bankChargeInfoService.updateByPrimaryKeySelective(taskType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, taskType,CommonLogImpl.TASK);
		return new ResponseRestEntity<BankChargeInfo>(type, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Delete BankChargeInfo", notes = "")
	@RequestMapping(value = "/{refNo}", method = RequestMethod.DELETE)
	public ResponseRestEntity<BankChargeInfo> deletetaskType(@PathVariable("refNo") String refNo) {

		BankChargeInfo taskType = bankChargeInfoService.selectByPrimaryKey(refNo);
		if (taskType == null) {
			return new ResponseRestEntity<BankChargeInfo>(HttpRestStatus.NOT_FOUND);
		}

		bankChargeInfoService.deleteByPrimaryKey(refNo);
		//删除日志开始
		BankChargeInfo delBean = new BankChargeInfo();
		delBean.setRefNo(refNo);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.TASK);
		//删除日志结束
		return new ResponseRestEntity<BankChargeInfo>(HttpRestStatus.NO_CONTENT);
	}
	
}