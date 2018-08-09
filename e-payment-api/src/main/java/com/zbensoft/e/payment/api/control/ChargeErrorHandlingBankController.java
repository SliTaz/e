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
import com.zbensoft.e.payment.api.service.api.ChargeErrorHandlingBankService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ChargeErrorHandlingBank;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/chargeErrorHandlingBank")
@RestController
public class ChargeErrorHandlingBankController {
	@Autowired
	ChargeErrorHandlingBankService chargeErrorHandlingBankService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query ChargeErrorHandlingBank，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ChargeErrorHandlingBank>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String reconciliationTime,@RequestParam(required = false) String refNo,
			@RequestParam(required = false) String tradeSeq,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
		chargeErrorHandlingBank.setBankId(id);
		chargeErrorHandlingBank.setReconciliationTime(reconciliationTime);
		chargeErrorHandlingBank.setRefNo(refNo);
		chargeErrorHandlingBank.setTradeSeq(tradeSeq);
	
		List<ChargeErrorHandlingBank> list = new ArrayList<ChargeErrorHandlingBank>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = chargeErrorHandlingBankService.selectPage(chargeErrorHandlingBank);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = chargeErrorHandlingBankService.selectPage(chargeErrorHandlingBank);
		}

		int count = chargeErrorHandlingBankService.count(chargeErrorHandlingBank);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ChargeErrorHandlingBank>>(new ArrayList<ChargeErrorHandlingBank>(),HttpRestStatus.NOT_FOUND);
		}
		List<ChargeErrorHandlingBank> listNew = new ArrayList<ChargeErrorHandlingBank>();
		for(ChargeErrorHandlingBank bean:list){
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ChargeErrorHandlingBank>>(listNew, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query ChargeErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ChargeErrorHandlingBank> selectByPrimaryKey(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime
			,@PathVariable("refNo") String refNo) {
		ChargeErrorHandlingBank bean = new ChargeErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ChargeErrorHandlingBank chargeErrorHandlingBank = chargeErrorHandlingBankService.selectByPrimaryKey(bean);
		
		if (chargeErrorHandlingBank == null) {
			return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ChargeErrorHandlingBank>(chargeErrorHandlingBank, HttpRestStatus.OK);
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
	@ApiOperation(value = "Edit ChargeErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<ChargeErrorHandlingBank> updateBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo,
			@RequestBody ChargeErrorHandlingBank chargeErrorHandlingBank) {

		ChargeErrorHandlingBank bean = new ChargeErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ChargeErrorHandlingBank currentBank = chargeErrorHandlingBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

	
		currentBank.setTradeSeq(chargeErrorHandlingBank.getTradeSeq());
		currentBank.setHanndlingResult(chargeErrorHandlingBank.getHanndlingResult());
		currentBank.setReason(chargeErrorHandlingBank.getReason());
		chargeErrorHandlingBankService.updateByPrimaryKey(currentBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ChargeErrorHandlingBank>(currentBank, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit Part ChargeErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ChargeErrorHandlingBank> updateBankSelective(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo,
			@RequestBody ChargeErrorHandlingBank chargeErrorHandlingBank) {

		ChargeErrorHandlingBank bean = new ChargeErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ChargeErrorHandlingBank currentBank = chargeErrorHandlingBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}


		chargeErrorHandlingBankService.updateByPrimaryKeySelective(chargeErrorHandlingBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, chargeErrorHandlingBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ChargeErrorHandlingBank>(currentBank, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Delete ChargeErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ChargeErrorHandlingBank> deleteBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo) {

		ChargeErrorHandlingBank bean = new ChargeErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ChargeErrorHandlingBank chargeErrorHandlingBank = chargeErrorHandlingBankService.selectByPrimaryKey(bean);
		if (chargeErrorHandlingBank == null) {
			return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}

		chargeErrorHandlingBankService.deleteByPrimaryKey(bean);

		return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.NO_CONTENT);
	}
	// 差错处理
	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "error the specified govUser", notes = "")
	@RequestMapping(value = "/error/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<ChargeErrorHandlingBank> error(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@PathVariable("refNo") String refNo) {

		ChargeErrorHandlingBank bean = new ChargeErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);
		
		ChargeErrorHandlingBank chargeErrorHandlingBank = chargeErrorHandlingBankService.selectByPrimaryKey(bean);
		if (chargeErrorHandlingBank == null) {
			return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}	
		chargeErrorHandlingBankService.updateByPrimaryKey(chargeErrorHandlingBank);
		//修改日志

		return new ResponseRestEntity<ChargeErrorHandlingBank>(HttpRestStatus.OK);
	}
}