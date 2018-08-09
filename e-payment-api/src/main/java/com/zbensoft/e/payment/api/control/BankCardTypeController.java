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
import com.zbensoft.e.payment.api.service.api.BankCardTypeService;
import com.zbensoft.e.payment.db.domain.BankCardType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/bankCardType")
@RestController
public class BankCardTypeController {
	@Autowired
	BankCardTypeService bankCardTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_PAYMENT_B_I_Q') or hasRole('R_BUYER_B_C_Q') or hasRole('R_BUYER_B_C_E') or hasRole('R_SELLER_B_C_Q') or hasRole('R_SELLER_B_C_E')")
	@ApiOperation(value = "Query BankCardType，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<BankCardType>> selectPage(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start,@RequestParam(required = false) String length) {
		BankCardType bankInfo = new BankCardType();
		bankInfo.setBankCardTypeId(id);
		bankInfo.setName(name);
		bankInfo.setDeleteFlag(0);
		int count = bankCardTypeService.count(bankInfo);
		if (count == 0) {
			return new ResponseRestEntity<List<BankCardType>>(new ArrayList<BankCardType>(), HttpRestStatus.NOT_FOUND);
		}
		List<BankCardType> list = null;
		// 分页 start
				if (start != null && length != null) {// 需要进行分页
					/*
					 * 第一个参数是第几页；第二个参数是每页显示条数。
					 */
					int pageNum = PageHelperUtil.getPageNum(start, length);
					int pageSize = PageHelperUtil.getPageSize(start, length);
					PageHelper.startPage(pageNum, pageSize);
					list = bankCardTypeService.selectPage(bankInfo);
					// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
					// System.out.println("list.size:"+list.size());

				} else {
					list = bankCardTypeService.selectPage(bankInfo);
				}

				// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<BankCardType>>(new ArrayList<BankCardType>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<BankCardType>>(list, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_C_T_Q')")
	@ApiOperation(value = "Query BankCardType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<BankCardType> selectByPrimaryKey(@PathVariable("id") Integer id) {
		BankCardType bankInfo = bankCardTypeService.selectByPrimaryKey(id);
		if (bankInfo == null) {
			return new ResponseRestEntity<BankCardType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<BankCardType>(bankInfo, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_C_T_E')")
	@ApiOperation(value = "Add BankCardType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBankInfo(@Valid @RequestBody BankCardType bankInfo, BindingResult result, UriComponentsBuilder ucBuilder) {
		//bankInfo.setBankId(System.currentTimeMillis()+"");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		if (bankCardTypeService.isExist(bankInfo)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		bankInfo.setDeleteFlag(0);
		bankCardTypeService.insert(bankInfo);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, bankInfo,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/bankCardType/{id}").buildAndExpand(bankInfo.getBankCardTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("bankInfo.create.created.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_C_T_E')")
	@ApiOperation(value = "Edit BankCardType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<BankCardType> updateBankInfo(@PathVariable("id") Integer id, @Valid @RequestBody BankCardType bankInfo, BindingResult result) {

		BankCardType currentBankInfo = bankCardTypeService.selectByPrimaryKey(id);

		if (currentBankInfo == null) {
			return new ResponseRestEntity<BankCardType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentBankInfo.setName(bankInfo.getName());
		currentBankInfo.setDeleteFlag(bankInfo.getDeleteFlag());
		currentBankInfo.setRemark(bankInfo.getRemark());
		bankCardTypeService.updateByPrimaryKey(currentBankInfo);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBankInfo,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<BankCardType>(currentBankInfo, HttpRestStatus.OK,localeMessageSourceService.getMessage("bankInfo.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_C_T_E')")
	@ApiOperation(value = "Edit Part BankCardType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<BankCardType> updateBankInfoSelective(@PathVariable("id") Integer id, @RequestBody BankCardType bankInfo) {

		BankCardType currentBankInfo = bankCardTypeService.selectByPrimaryKey(id);

		if (currentBankInfo == null) {
			return new ResponseRestEntity<BankCardType>(HttpRestStatus.NOT_FOUND);
		}
		currentBankInfo.setBankCardTypeId(id);
		currentBankInfo.setName(bankInfo.getName());
		currentBankInfo.setDeleteFlag(bankInfo.getDeleteFlag());
		currentBankInfo.setRemark(bankInfo.getRemark());
		bankCardTypeService.updateByPrimaryKeySelective(bankInfo);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, bankInfo,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<BankCardType>(currentBankInfo, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_B_C_T_E')")
	@ApiOperation(value = "Delete BankCardType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<BankCardType> deleteBankInfo(@PathVariable("id") Integer id) {
		BankCardType bankInfo = bankCardTypeService.selectByPrimaryKey(id);
		if (bankInfo == null) {
			return new ResponseRestEntity<BankCardType>(HttpRestStatus.NOT_FOUND);
		}
		bankInfo.setDeleteFlag(1);
		bankCardTypeService.updateByPrimaryKeySelective(bankInfo);
		
		//删除日志开始
		BankCardType delBean = new BankCardType();
		delBean.setBankCardTypeId(id);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<BankCardType>(HttpRestStatus.NO_CONTENT);
	}

}