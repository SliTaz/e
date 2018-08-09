package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankChargeInfoService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankChargeInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/errorHandlingBankChargeInfo")
@RestController
public class ErrorHandlingBankChargeInfoController {
	@Autowired
	ErrorHandlingBankChargeInfoService errorHandlingBankChargeInfoService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query ErrorHandlingBankChargeInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ErrorHandlingBankChargeInfo>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String reconciliationTime,@RequestParam(required = false) String refNo,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ErrorHandlingBankChargeInfo chargeErrorHandlingBank = new ErrorHandlingBankChargeInfo();
		chargeErrorHandlingBank.setBankId(id);
		chargeErrorHandlingBank.setReconciliationTime(reconciliationTime);
		chargeErrorHandlingBank.setRefNo(refNo);
	
		List<ErrorHandlingBankChargeInfo> list = new ArrayList<ErrorHandlingBankChargeInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = errorHandlingBankChargeInfoService.selectPage(chargeErrorHandlingBank);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = errorHandlingBankChargeInfoService.selectPage(chargeErrorHandlingBank);
		}

		int count = errorHandlingBankChargeInfoService.count(chargeErrorHandlingBank);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ErrorHandlingBankChargeInfo>>(new ArrayList<ErrorHandlingBankChargeInfo>(),HttpRestStatus.NOT_FOUND);
		}
		List<ErrorHandlingBankChargeInfo> listNew = new ArrayList<ErrorHandlingBankChargeInfo>();
		for(ErrorHandlingBankChargeInfo bean:list){
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ErrorHandlingBankChargeInfo>>(listNew, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query ErrorHandlingBankChargeInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ErrorHandlingBankChargeInfo> selectByPrimaryKey(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime
			,@PathVariable("refNo") String refNo) {
		ErrorHandlingBankChargeInfo bean = new ErrorHandlingBankChargeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankChargeInfo chargeErrorHandlingBank = errorHandlingBankChargeInfoService.selectByPrimaryKey(bean);
		
		if (chargeErrorHandlingBank == null) {
			return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(chargeErrorHandlingBank, HttpRestStatus.OK);
	}

/*	@ApiOperation(value = "Add ChargeErrorHandlingBank", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBank(@RequestBody ChargeErrorHandlingBank chargeErrorHandlingBank,
			UriComponentsBuilder ucBuilder) {
		
		chargeErrorHandlingBank.setChargeErrorHandlingBankId(IDGenerate.generateCommTwo(IDGenerate.RECONCILIATION_BATCH));

		chargeErrorHandlingBankService.insert(chargeErrorHandlingBank);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/chargeErrorHandlingBank/{id}")
				.buildAndExpand(chargeErrorHandlingBank.getChargeErrorHandlingBankId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}*/

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit ErrorHandlingBankChargeInfo", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<ErrorHandlingBankChargeInfo> updateBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo,
			@RequestBody ErrorHandlingBankChargeInfo chargeErrorHandlingBank) {

		ErrorHandlingBankChargeInfo bean = new ErrorHandlingBankChargeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankChargeInfo currentBank = errorHandlingBankChargeInfoService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

	
		currentBank.setVid(chargeErrorHandlingBank.getVid());
		currentBank.setTransactionDate(chargeErrorHandlingBank.getTransactionDate());
		currentBank.setCurrencyType(chargeErrorHandlingBank.getCurrencyType());
		currentBank.setTransactionAmount(chargeErrorHandlingBank.getTransactionAmount());
		currentBank.setTransactionType(chargeErrorHandlingBank.getTransactionType());
		currentBank.setChannelType(chargeErrorHandlingBank.getChannelType());
		errorHandlingBankChargeInfoService.updateByPrimaryKey(currentBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(currentBank, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit Part ErrorHandlingBankChargeInfo", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ErrorHandlingBankChargeInfo> updateBankSelective(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo,
			@RequestBody ErrorHandlingBankChargeInfo chargeErrorHandlingBank) {

		ErrorHandlingBankChargeInfo bean = new ErrorHandlingBankChargeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankChargeInfo currentBank = errorHandlingBankChargeInfoService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(HttpRestStatus.NOT_FOUND);
		}


		errorHandlingBankChargeInfoService.updateByPrimaryKeySelective(chargeErrorHandlingBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, chargeErrorHandlingBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(currentBank, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Delete ErrorHandlingBankChargeInfo", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ErrorHandlingBankChargeInfo> deleteBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo) {

		ErrorHandlingBankChargeInfo bean = new ErrorHandlingBankChargeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankChargeInfo chargeErrorHandlingBank = errorHandlingBankChargeInfoService.selectByPrimaryKey(bean);
		if (chargeErrorHandlingBank == null) {
			return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(HttpRestStatus.NOT_FOUND);
		}

		errorHandlingBankChargeInfoService.deleteByPrimaryKey(bean);

		return new ResponseRestEntity<ErrorHandlingBankChargeInfo>(HttpRestStatus.NO_CONTENT);
	}
}