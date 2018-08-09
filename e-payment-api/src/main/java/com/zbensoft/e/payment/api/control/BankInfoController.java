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
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.db.domain.BankInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/bankInfo")
@RestController
public class BankInfoController {
	@Autowired
	BankInfoService bankInfoService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_PAYMENT_B_I_Q') or hasRole('R_BUYER_B_C_Q') or hasRole('R_BUYER_B_C_E') or hasRole('R_SELLER_B_C_Q') or hasRole('R_SELLER_B_C_E') or hasRole('CONSUMER') or hasRole('MERCHANT') or hasRole('R_REC_R_Q') or hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query BankInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankInfo>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start,@RequestParam(required = false) String length) {
		BankInfo bankInfo = new BankInfo();
		bankInfo.setBankId(id);
		bankInfo.setName(name);
		bankInfo.setDeleteFlag(0);
		int count = bankInfoService.count(bankInfo);
		if (count == 0) {
			return new ResponseRestEntity<List<BankInfo>>(new ArrayList<BankInfo>(), HttpRestStatus.NOT_FOUND);
		}
		List<BankInfo> list = null;
		// 分页 start
				if (start != null && length != null) {// 需要进行分页
					/*
					 * 第一个参数是第几页；第二个参数是每页显示条数。
					 */
					int pageNum = PageHelperUtil.getPageNum(start, length);
					int pageSize = PageHelperUtil.getPageSize(start, length);
					PageHelper.startPage(pageNum, pageSize);
					list = bankInfoService.selectPage(bankInfo);
					// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
					// System.out.println("list.size:"+list.size());

				} else {
					list = bankInfoService.selectPage(bankInfo);
				}

				// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BankInfo>>(new ArrayList<BankInfo>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<BankInfo>>(list, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_I_Q')")
	@ApiOperation(value = "Query BankInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<BankInfo> selectByPrimaryKey(@PathVariable("id") String id) {
		BankInfo bankInfo = bankInfoService.selectByPrimaryKey(id);
		if (bankInfo == null) {
			return new ResponseRestEntity<BankInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<BankInfo>(bankInfo, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_I_E')")
	@ApiOperation(value = "Add BankInfo", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBankInfo(@Valid @RequestBody BankInfo bankInfo, BindingResult result, UriComponentsBuilder ucBuilder) {
		//bankInfo.setBankId(System.currentTimeMillis()+"");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		BankInfo bean = bankInfoService.selectByPrimaryKey(bankInfo.getBankId());
		bankInfo.setDeleteFlag(0);
		if (bankInfoService.isBankInfoExist(bankInfo) || bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		
		bankInfoService.insert(bankInfo);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, bankInfo,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/bankInfo/{id}").buildAndExpand(bankInfo.getBankId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("bankInfo.create.created.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_I_E')")
	@ApiOperation(value = "Edit BankInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<BankInfo> updateBankInfo(@PathVariable("id") String id, @Valid @RequestBody BankInfo bankInfo, BindingResult result) {

		BankInfo currentBankInfo = bankInfoService.selectByPrimaryKey(id);

		if (currentBankInfo == null) {
			return new ResponseRestEntity<BankInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentBankInfo.setName(bankInfo.getName());
		currentBankInfo.setCode(bankInfo.getCode());
		currentBankInfo.setIbp(bankInfo.getIbp());
		currentBankInfo.setRif(bankInfo.getRif());
		currentBankInfo.setDeleteFlag(bankInfo.getDeleteFlag());
		currentBankInfo.setRemark(bankInfo.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<BankInfo>(currentBankInfo,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		bankInfoService.updateByPrimaryKey(currentBankInfo);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBankInfo,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<BankInfo>(currentBankInfo, HttpRestStatus.OK,localeMessageSourceService.getMessage("bankInfo.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_I_E')")
	@ApiOperation(value = "Edit Part BankInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<BankInfo> updateBankInfoSelective(@PathVariable("id") String id, @RequestBody BankInfo bankInfo) {

		BankInfo currentBankInfo = bankInfoService.selectByPrimaryKey(id);

		if (currentBankInfo == null) {
			return new ResponseRestEntity<BankInfo>(HttpRestStatus.NOT_FOUND);
		}
		currentBankInfo.setBankId(id);
		currentBankInfo.setName(bankInfo.getName());
		currentBankInfo.setCode(bankInfo.getCode());
		currentBankInfo.setDeleteFlag(bankInfo.getDeleteFlag());
		currentBankInfo.setRemark(bankInfo.getRemark());
		bankInfoService.updateByPrimaryKeySelective(bankInfo);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, bankInfo,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<BankInfo>(currentBankInfo, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_I_E')")
	@ApiOperation(value = "Delete BankInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<BankInfo> deleteBankInfo(@PathVariable("id") String id) {

		BankInfo bankInfo = bankInfoService.selectByPrimaryKey(id);
		if (bankInfo == null) {
			return new ResponseRestEntity<BankInfo>(HttpRestStatus.NOT_FOUND);
		}
		bankInfo.setDeleteFlag(1);
		bankInfoService.updateByPrimaryKeySelective(bankInfo);
		
		//删除日志开始
		BankInfo delBean = new BankInfo();
		delBean.setBankId(id);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<BankInfo>(HttpRestStatus.NO_CONTENT);
	}

}