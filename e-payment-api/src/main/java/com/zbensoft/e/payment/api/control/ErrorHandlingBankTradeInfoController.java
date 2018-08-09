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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankTradeInfoService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/errorHandlingBankTradeInfo")
@RestController
public class ErrorHandlingBankTradeInfoController {
	@Autowired
	ErrorHandlingBankTradeInfoService errorHandlingBankTradeInfoService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_REC_R_Q')")
	@ApiOperation(value = "Query ErrorHandlingBankTradeInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ErrorHandlingBankTradeInfo>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String reconciliationTime,@RequestParam(required = false) String refNo,
			@RequestParam(required = false) String vid,
			@RequestParam(required = false) String transactionDate,
			@RequestParam(required = false) String currencyType,
			@RequestParam(required = false) String transactionAmount,
			@RequestParam(required = false) String transactionType,
			@RequestParam(required = false) String channelType,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		vid = CommonFun.getRelVid(vid);
		ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo = new ErrorHandlingBankTradeInfo();
		errorHandlingBankTradeInfo.setBankId(id);
		errorHandlingBankTradeInfo.setReconciliationTime(reconciliationTime);
		errorHandlingBankTradeInfo.setRefNo(refNo);
	
		List<ErrorHandlingBankTradeInfo> list = new ArrayList<ErrorHandlingBankTradeInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = errorHandlingBankTradeInfoService.selectPage(errorHandlingBankTradeInfo);

		} else {
			list = errorHandlingBankTradeInfoService.selectPage(errorHandlingBankTradeInfo);
		}

		int count = errorHandlingBankTradeInfoService.count(errorHandlingBankTradeInfo);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ErrorHandlingBankTradeInfo>>(new ArrayList<ErrorHandlingBankTradeInfo>(),HttpRestStatus.NOT_FOUND);
		}
		List<ErrorHandlingBankTradeInfo> listNew = new ArrayList<ErrorHandlingBankTradeInfo>();
		for(ErrorHandlingBankTradeInfo bean:list){
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ErrorHandlingBankTradeInfo>>(listNew, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_REC_R_Q')")
	@ApiOperation(value = "Query ErrorHandlingBankTradeInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ErrorHandlingBankTradeInfo> selectByPrimaryKey(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime
			,@PathVariable("refNo") String refNo) {
		ErrorHandlingBankTradeInfo bean = new ErrorHandlingBankTradeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo = errorHandlingBankTradeInfoService.selectByPrimaryKey(bean);
		
		if (errorHandlingBankTradeInfo == null) {
			return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(errorHandlingBankTradeInfo, HttpRestStatus.OK);
	}


	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Edit ErrorHandlingBankTradeInfo", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<ErrorHandlingBankTradeInfo> updateBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo,
			@RequestBody ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo) {

		ErrorHandlingBankTradeInfo bean = new ErrorHandlingBankTradeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankTradeInfo currentBank = errorHandlingBankTradeInfoService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

	
		currentBank.setVid(errorHandlingBankTradeInfo.getVid());
		currentBank.setTransactionDate(errorHandlingBankTradeInfo.getTransactionDate());
		currentBank.setCurrencyType(errorHandlingBankTradeInfo.getCurrencyType());
		currentBank.setTransactionAmount(errorHandlingBankTradeInfo.getTransactionAmount());
		currentBank.setTransactionType(errorHandlingBankTradeInfo.getTransactionType());
		currentBank.setChannelType(errorHandlingBankTradeInfo.getChannelType());
		errorHandlingBankTradeInfoService.updateByPrimaryKey(currentBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(currentBank, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Edit Part ErrorHandlingBankTradeInfo", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ErrorHandlingBankTradeInfo> updateBankSelective(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo,
			@RequestBody ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo) {

		ErrorHandlingBankTradeInfo bean = new ErrorHandlingBankTradeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankTradeInfo currentBank = errorHandlingBankTradeInfoService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(HttpRestStatus.NOT_FOUND);
		}


		errorHandlingBankTradeInfoService.updateByPrimaryKeySelective(errorHandlingBankTradeInfo);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, errorHandlingBankTradeInfo,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(currentBank, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Delete ErrorHandlingBankTradeInfo", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ErrorHandlingBankTradeInfo> deleteBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo) {

		ErrorHandlingBankTradeInfo bean = new ErrorHandlingBankTradeInfo();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo = errorHandlingBankTradeInfoService.selectByPrimaryKey(bean);
		if (errorHandlingBankTradeInfo == null) {
			return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(HttpRestStatus.NOT_FOUND);
		}

		errorHandlingBankTradeInfoService.deleteByPrimaryKey(bean);

		return new ResponseRestEntity<ErrorHandlingBankTradeInfo>(HttpRestStatus.NO_CONTENT);
	}
}