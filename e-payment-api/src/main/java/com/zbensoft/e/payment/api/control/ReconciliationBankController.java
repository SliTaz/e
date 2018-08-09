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
import com.zbensoft.e.payment.api.service.api.ReconciliationBankService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ReconciliationBank;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/reconciliationBank")
@RestController
public class ReconciliationBankController {
	@Autowired
	ReconciliationBankService reconciliationBankService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_REC_R_Q')")
	@ApiOperation(value = "Query ReconciliationBank，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ReconciliationBank>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String reconciliationTime,@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ReconciliationBank reconciliationBank = new ReconciliationBank();
		reconciliationBank.setBankId(id);
		reconciliationBank.setReconciliationTime(reconciliationTime);
		reconciliationBank.setStatus(status);
	
		List<ReconciliationBank> list = new ArrayList<ReconciliationBank>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = reconciliationBankService.selectPage(reconciliationBank);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = reconciliationBankService.selectPage(reconciliationBank);
		}

		int count = reconciliationBankService.count(reconciliationBank);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ReconciliationBank>>(new ArrayList<ReconciliationBank>(),HttpRestStatus.NOT_FOUND);
		}
		List<ReconciliationBank> listNew = new ArrayList<ReconciliationBank>();
		for(ReconciliationBank bean:list){
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ReconciliationBank>>(listNew, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_REC_R_Q')")
	@ApiOperation(value = "Query ReconciliationBank", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ReconciliationBank> selectByPrimaryKey(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime) {
		ReconciliationBank bean = new ReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ReconciliationBank reconciliationBank = reconciliationBankService.selectByPrimaryKey(bean);
		
		if (reconciliationBank == null) {
			return new ResponseRestEntity<ReconciliationBank>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ReconciliationBank>(reconciliationBank, HttpRestStatus.OK);
	}

/*	@ApiOperation(value = "Add ReconciliationBank", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBank(@RequestBody ReconciliationBank reconciliationBank,
			UriComponentsBuilder ucBuilder) {
		
		reconciliationBank.setReconciliationBankId(IDGenerate.generateCommTwo(IDGenerate.RECONCILIATION_BATCH));

		reconciliationBankService.insert(reconciliationBank);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/reconciliationBank/{id}")
				.buildAndExpand(reconciliationBank.getReconciliationBankId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}*/

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Edit ReconciliationBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}", method = RequestMethod.PUT)
	public ResponseRestEntity<ReconciliationBank> updateBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@RequestBody ReconciliationBank reconciliationBank) {

		ReconciliationBank bean = new ReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ReconciliationBank currentBank = reconciliationBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ReconciliationBank>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

	
		currentBank.setStatus(reconciliationBank.getStatus());

		reconciliationBankService.updateByPrimaryKey(currentBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ReconciliationBank>(currentBank, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Edit Part ReconciliationBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ReconciliationBank> updateBankSelective(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime,
			@RequestBody ReconciliationBank reconciliationBank) {

		ReconciliationBank bean = new ReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ReconciliationBank currentBank = reconciliationBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ReconciliationBank>(HttpRestStatus.NOT_FOUND);
		}
		reconciliationBank.setStatus(reconciliationBank.getStatus());

		reconciliationBankService.updateByPrimaryKeySelective(reconciliationBank);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, reconciliationBank,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ReconciliationBank>(currentBank, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Delete ReconciliationBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ReconciliationBank> deleteBank(@PathVariable("bankId") String bankId,@PathVariable("reconciliationTime") String reconciliationTime) {

		ReconciliationBank bean = new ReconciliationBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		
		ReconciliationBank reconciliationBank = reconciliationBankService.selectByPrimaryKey(bean);
		if (reconciliationBank == null) {
			return new ResponseRestEntity<ReconciliationBank>(HttpRestStatus.NOT_FOUND);
		}

		reconciliationBankService.deleteByPrimaryKey(bean);

		return new ResponseRestEntity<ReconciliationBank>(HttpRestStatus.NO_CONTENT);
	}
}