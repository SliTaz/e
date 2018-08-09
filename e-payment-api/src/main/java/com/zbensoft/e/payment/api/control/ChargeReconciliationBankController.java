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
import com.zbensoft.e.payment.api.service.api.ChargeReconciliationBankService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ChargeReconciliationBank;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/chargeReconciliationBank")
@RestController
public class ChargeReconciliationBankController {
	@Autowired
	ChargeReconciliationBankService chargeReconciliationBankService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query ChargeReconciliationBank，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ChargeReconciliationBank>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String reconciliationTime,@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ChargeReconciliationBank chargeReconciliationBank = new ChargeReconciliationBank();
		chargeReconciliationBank.setBankId(id);
		chargeReconciliationBank.setReconciliationTime(reconciliationTime);
		chargeReconciliationBank.setStatus(status);
	
		List<ChargeReconciliationBank> list = new ArrayList<ChargeReconciliationBank>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = chargeReconciliationBankService.selectPage(chargeReconciliationBank);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = chargeReconciliationBankService.selectPage(chargeReconciliationBank);
		}

		int count = chargeReconciliationBankService.count(chargeReconciliationBank);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ChargeReconciliationBank>>(new ArrayList<ChargeReconciliationBank>(),HttpRestStatus.NOT_FOUND);
		}
		List<ChargeReconciliationBank> listNew = new ArrayList<ChargeReconciliationBank>();
		for(ChargeReconciliationBank bean:list){
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ChargeReconciliationBank>>(listNew, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query ChargeReconciliationBank", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ChargeReconciliationBank> selectByPrimaryKey(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime) {
		ChargeReconciliationBank bean = new ChargeReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ChargeReconciliationBank chargeReconciliationBank = chargeReconciliationBankService.selectByPrimaryKey(bean);
		
		if (chargeReconciliationBank == null) {
			return new ResponseRestEntity<ChargeReconciliationBank>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ChargeReconciliationBank>(chargeReconciliationBank, HttpRestStatus.OK);
	}

/*	@ApiOperation(value = "Add ChargeReconciliationBank", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBank(@RequestBody ChargeReconciliationBank chargeReconciliationBank,
			UriComponentsBuilder ucBuilder) {
		
		chargeReconciliationBank.setChargeReconciliationBankId(IDGenerate.generateCommTwo(IDGenerate.RECONCILIATION_BATCH));

		chargeReconciliationBankService.insert(chargeReconciliationBank);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/chargeReconciliationBank/{id}")
				.buildAndExpand(chargeReconciliationBank.getChargeReconciliationBankId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}*/

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit ChargeReconciliationBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}", method = RequestMethod.PUT)
	public ResponseRestEntity<ChargeReconciliationBank> updateBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@RequestBody ChargeReconciliationBank chargeReconciliationBank) {

		ChargeReconciliationBank bean = new ChargeReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ChargeReconciliationBank currentBank = chargeReconciliationBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ChargeReconciliationBank>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

	
		currentBank.setStatus(chargeReconciliationBank.getStatus());

		chargeReconciliationBankService.updateByPrimaryKey(currentBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ChargeReconciliationBank>(currentBank, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Edit Part ChargeReconciliationBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ChargeReconciliationBank> updateBankSelective(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@RequestBody ChargeReconciliationBank chargeReconciliationBank) {

		ChargeReconciliationBank bean = new ChargeReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ChargeReconciliationBank currentBank = chargeReconciliationBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ChargeReconciliationBank>(HttpRestStatus.NOT_FOUND);
		}
		chargeReconciliationBank.setStatus(chargeReconciliationBank.getStatus());

		chargeReconciliationBankService.updateByPrimaryKeySelective(chargeReconciliationBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, chargeReconciliationBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ChargeReconciliationBank>(currentBank, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_C_E')")
	@ApiOperation(value = "Delete ChargeReconciliationBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ChargeReconciliationBank> deleteBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime) {

		ChargeReconciliationBank bean = new ChargeReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ChargeReconciliationBank chargeReconciliationBank = chargeReconciliationBankService.selectByPrimaryKey(bean);
		if (chargeReconciliationBank == null) {
			return new ResponseRestEntity<ChargeReconciliationBank>(HttpRestStatus.NOT_FOUND);
		}

		chargeReconciliationBankService.deleteByPrimaryKey(bean);

		return new ResponseRestEntity<ChargeReconciliationBank>(HttpRestStatus.NO_CONTENT);
	}
}